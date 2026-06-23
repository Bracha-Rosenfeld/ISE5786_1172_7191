package geometries.impl;

import geometries.api.Intersectable;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite class representing a collection of geometries.
 */
public final class Geometries extends Intersectable {
    private final List<Intersectable> _geometries = new ArrayList<>();

    public Geometries() {
        // מתחילים ללא קופסה. תחושב רק על פי דרישה.
        minX = minY = minZ = Double.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
    }

    public Geometries(Intersectable... geometries) {
        this();
        add(geometries);
    }

    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
        // הערה חשובה: לא מעדכנים כאן את ה-AABB כדי לחסוך ביצועים. מתעדכן רק דרך setBvhIsOn.
    }

    /**
     * Updates the bounding box based on the children geometries.
     */
    private void updateBoundingBox() {
        minX = minY = minZ = Double.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
        for (Intersectable geo : _geometries) {
            if (geo.getMinX() < this.minX) this.minX = geo.getMinX();
            if (geo.getMaxX() > this.maxX) this.maxX = geo.getMaxX();
            if (geo.getMinY() < this.minY) this.minY = geo.getMinY();
            if (geo.getMaxY() > this.maxY) this.maxY = geo.getMaxY();
            if (geo.getMinZ() < this.minZ) this.minZ = geo.getMinZ();
            if (geo.getMaxZ() > this.maxZ) this.maxZ = geo.getMaxZ();
        }
    }

    @Override
    public Intersectable setBvhIsOn(boolean bvhIsOn) {
        super.setBvhIsOn(bvhIsOn);
        for (Intersectable geo : _geometries) {
            geo.setBvhIsOn(bvhIsOn);
        }
        // אם מנגנון ההאצה נדלק, רק עכשיו נחשב את גבולות הקופסה!
        if (bvhIsOn) {
            updateBoundingBox();
        }
        return this;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> intersections = null;
        for (Intersectable geometry : _geometries) {
            List<Intersection> geoIntersections = geometry.calcIntersections(ray, maxDistance);
            if (geoIntersections != null) {
                if (intersections == null) {
                    intersections = new ArrayList<>();
                }
                intersections.addAll(geoIntersections);
            }
        }
        return intersections;
    }

    /**
     * Builds an automatic Bounding Volume Hierarchy (BVH) tree.
     */
    public void buildHierarchy() {
        if (_geometries.size() <= 2) {
            return;
        }

        // אם בונים היררכיה, חובה שיהיו גבולות קופסה מעודכנים כדי שנדע לפצל
        if (!this.bvhIsOn) {
            updateBoundingBox();
        }

        double sizeX = this.maxX - this.minX;
        double sizeY = this.maxY - this.minY;
        double sizeZ = this.maxZ - this.minZ;

        int axis = 0;
        if (sizeY > sizeX && sizeY > sizeZ) axis = 1;
        else if (sizeZ > sizeX && sizeZ > sizeY) axis = 2;

        final int splitAxis = axis;
        _geometries.sort((g1, g2) -> {
            double center1 = 0, center2 = 0;
            if (splitAxis == 0) {
                center1 = (g1.getMinX() + g1.getMaxX()) / 2.0;
                center2 = (g2.getMinX() + g2.getMaxX()) / 2.0;
            } else if (splitAxis == 1) {
                center1 = (g1.getMinY() + g1.getMaxY()) / 2.0;
                center2 = (g2.getMinY() + g2.getMaxY()) / 2.0;
            } else {
                center1 = (g1.getMinZ() + g1.getMaxZ()) / 2.0;
                center2 = (g2.getMinZ() + g2.getMaxZ()) / 2.0;
            }
            return Double.compare(center1, center2);
        });

        int mid = _geometries.size() / 2;
        Geometries left = new Geometries();
        Geometries right = new Geometries();

        for (int i = 0; i < mid; i++) left.add(_geometries.get(i));
        for (int i = mid; i < _geometries.size(); i++) right.add(_geometries.get(i));

        left.buildHierarchy();
        right.buildHierarchy();

        _geometries.clear();

        left.setBvhIsOn(this.bvhIsOn);
        right.setBvhIsOn(this.bvhIsOn);

        this.add(left, right);

        if (this.bvhIsOn) {
            updateBoundingBox();
        }
    }

    /**
     * Flattens the hierarchy, removing any nested Geometries and placing all basic
     * shapes into a single flat list. Used for measuring baseline performance.
     */
    public void flatten() {
        List<Intersectable> flatList = new ArrayList<>();
        flattenHelper(this, flatList);

        _geometries.clear();
        _geometries.addAll(flatList);

        if (this.bvhIsOn) {
            updateBoundingBox();
        }
    }

    /**
     * Recursive helper to extract all geometries from nested structures.
     */
    private void flattenHelper(Geometries geometries, List<Intersectable> flatList) {
        for (Intersectable geo : geometries._geometries) {
            if (geo instanceof Geometries) {
                flattenHelper((Geometries) geo, flatList); // שולף מתוך תיקיות פנימיות
            } else {
                flatList.add(geo); // גוף רגיל נכנס לרשימה
            }
        }
    }
}