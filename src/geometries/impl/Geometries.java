package geometries.impl;

import geometries.api.Intersectable;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite class representing a collection of geometric shapes.
 * This class implements the Composite Design Pattern and extends {@link Intersectable}.
 * It provides mechanisms for updating spatial bounding boxes, building an automated
 * Bounding Volume Hierarchy (BVH) tree, and flattening nested structures to optimize
 * ray tracing intersection performance.
 */
public final class Geometries extends Intersectable {
    /** The list of individual or nested geometries contained in this collection. */
    private final List<Intersectable> _geometries = new ArrayList<>();
    /**
     * Default constructor.
     * Initializes an empty geometry collection with an uninitialized bounding box
     * (boundaries set to infinite inverse values, to be calculated upon activation).
     */
    public Geometries() {
        // מתחילים ללא קופסה. תחושב רק על פי דרישה.
        minX = minY = minZ = Double.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
    }
    /**
     * Constructor that initializes the collection with a given set of geometries.
     * * @param geometries A variable number of geometry objects to add to the collection.
     */
    public Geometries(Intersectable... geometries) {
        this();
        add(geometries);
    }
    /**
     * Adds one or more geometric shapes to the collection.
     * Note: Bounding box properties are not updated immediately for performance optimization.
     * * @param geometries A variable number of geometry objects to be added.
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
        // הערה חשובה: לא מעדכנים כאן את ה-AABB כדי לחסוך ביצועים. מתעדכן רק דרך setBvhIsOn.
    }

    /**
     * Recalculates and updates the composite bounding box coordinates
     * by enclosing all the individual bounding boxes of the children geometries.
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
     * Builds an automatic, balanced Bounding Volume Hierarchy (BVH) tree structure.
     * It recursively splits the elements into midpoints along the longest axis
     * of the bounding volume to reduce ray intersection complexity.
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
     * Flattens the collection hierarchy by unpacking all nested Geometries structures,
     * leaving only base geometric shapes in a unified linear list.
     * This is useful for performance benchmarking comparisons.
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
     * Recursive internal helper method to extract all deep basic shapes from nested
     * composite groups and aggregate them into a linear target list.
     * * @param geometries The current composite Geometries instance to evaluate.
     * @param flatList   The destination list gathering the basic shapes.
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