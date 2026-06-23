package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Interface-like abstract class representing geometries that can be intersected by a ray.
 * Implements the Non-Virtual Interface (NVI) pattern for calculating intersections.
 */
public abstract class Intersectable {

    // --- BVH / AABB (Axis-Aligned Bounding Box) Fields ---
    protected double minX = Double.NEGATIVE_INFINITY;
    protected double maxX = Double.POSITIVE_INFINITY;
    protected double minY = Double.NEGATIVE_INFINITY;
    protected double maxY = Double.POSITIVE_INFINITY;
    protected double minZ = Double.NEGATIVE_INFINITY;
    protected double maxZ = Double.POSITIVE_INFINITY;

    // --- Getters for Bounding Box coordinates ---
    public double getMinX() { return minX; }
    public double getMaxX() { return maxX; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }
    public double getMinZ() { return minZ; }
    public double getMaxZ() { return maxZ; }

    /** Flag to turn BVH on/off for this geometry */
    protected boolean bvhIsOn = false;

    /**
     * Enables or disables BVH (Bounding Box) improvement.
     * @param bvhIsOn true to turn on, false to turn off
     * @return the object itself for chaining
     */
    public Intersectable setBvhIsOn(boolean bvhIsOn) {
        this.bvhIsOn = bvhIsOn;
        return this;
    }

    // --- הטריק המתמטי לקבלת קואורדינטות ללא שינוי מחלקת Point ---
    private static final Vector X_AXIS = new Vector(1, 0, 0);
    private static final Vector Y_AXIS = new Vector(0, 1, 0);
    private static final Vector Z_AXIS = new Vector(0, 0, 1);

    protected double getX(Point p) {
        return p.equals(Point.ZERO) ? 0 : p.subtract(Point.ZERO).dotProduct(X_AXIS);
    }

    protected double getY(Point p) {
        return p.equals(Point.ZERO) ? 0 : p.subtract(Point.ZERO).dotProduct(Y_AXIS);
    }

    protected double getZ(Point p) {
        return p.equals(Point.ZERO) ? 0 : p.subtract(Point.ZERO).dotProduct(Z_AXIS);
    }
    // -------------------------------------------------------------

    public static class Intersection {
        public final Geometry geometry;
        public final Point point;
        public final Material material;

        public Vector n;
        public Vector v;
        public double nv;
        public Vector l;
        public double nl;

        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry == null ? new Material() : geometry.getMaterial();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return (obj instanceof Intersection other)
                    && this.geometry == other.geometry
                    && this.point.equals(other.point);
        }

        @Override
        public String toString() {
            return "Intersection{geometry=" + geometry + ", point=" + point + "}";
        }
    }

    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Finds all intersections between the geometry and a given ray, up to a maximum distance.
     * This method implements the NVI pattern by calling the protected helper method.
     */
    public final List<Intersection> calcIntersections(Ray ray, double maxDistance) {
        // אם מערכת הקופסאות מופעלת והקרן מחטיאה את הקופסה לחלוטין - נדלג על הצורה!
        if (bvhIsOn && !checkBoundingBox(ray)) {
            return null;
        }
        return calcIntersectionsHelper(ray, maxDistance);
    }

    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance);

    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream().map(inter -> inter.point).toList();
    }

    /**
     * Checks if a given ray intersects the geometric bounding box (AABB).
     * Uses the "Slab Method" for fast ray-box intersection.
     */
    protected boolean checkBoundingBox(Ray ray) {
        Point p0 = ray.origin();
        Vector dir = ray.direction();

        double p0X = getX(p0);
        double p0Y = getY(p0);
        double p0Z = getZ(p0);

        double dirX = getX(dir);
        double dirY = getY(dir);
        double dirZ = getZ(dir);

        double tMin = (minX - p0X) / dirX;
        double tMax = (maxX - p0X) / dirX;
        if (dirX < 0) { double temp = tMin; tMin = tMax; tMax = temp; }

        double tyMin = (minY - p0Y) / dirY;
        double tyMax = (maxY - p0Y) / dirY;
        if (dirY < 0) { double temp = tyMin; tyMin = tyMax; tyMax = temp; }

        if ((tMin > tyMax) || (tyMin > tMax)) return false;

        if (tyMin > tMin) tMin = tyMin;
        if (tyMax < tMax) tMax = tyMax;

        double tzMin = (minZ - p0Z) / dirZ;
        double tzMax = (maxZ - p0Z) / dirZ;
        if (dirZ < 0) { double temp = tzMin; tzMin = tzMax; tzMax = temp; }

        if ((tMin > tzMax) || (tzMin > tMax)) return false;

        if (tzMax < tMax) tMax = tzMax;

        return tMax > 0;
    }
}