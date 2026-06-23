package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Interface-like abstract class representing geometries that can be intersected by a ray.
 * Implements the Non-Virtual Interface (NVI) pattern for calculating intersections.
 * Supports Axis-Aligned Bounding Box (AABB) and Bounding Volume Hierarchy (BVH)
 * for performance optimization during ray-object intersection tests.
 * * @author Computer Graphics Course
 */
public abstract class Intersectable {

// --- BVH / AABB (Axis-Aligned Bounding Box) Fields ---

    /** The minimum X coordinate boundary of the bounding box. Default is negative infinity. */
    protected double minX = Double.NEGATIVE_INFINITY;
    /** The maximum X coordinate boundary of the bounding box. Default is positive infinity. */
    protected double maxX = Double.POSITIVE_INFINITY;
    /** The minimum Y coordinate boundary of the bounding box. Default is negative infinity. */
    protected double minY = Double.NEGATIVE_INFINITY;
    /** The maximum Y coordinate boundary of the bounding box. Default is positive infinity. */
    protected double maxY = Double.POSITIVE_INFINITY;
    /** The minimum Z coordinate boundary of the bounding box. Default is negative infinity. */
    protected double minZ = Double.NEGATIVE_INFINITY;
    /** The maximum Z coordinate boundary of the bounding box. Default is positive infinity. */
    protected double maxZ = Double.POSITIVE_INFINITY;

    // --- Getters for Bounding Box coordinates ---
    /**
     * Gets the minimum X coordinate of the bounding box.
     * @return the minX boundary value
     */
    public double getMinX() { return minX; }
    /**
     * Gets the maximum X coordinate of the bounding box.
     * @return the maxX boundary value
     */
    public double getMaxX() { return maxX; }
    /**
     * Gets the minimum Y coordinate of the bounding box.
     * @return the minY boundary value
     */
    public double getMinY() { return minY; }
    /**
     * Gets the maximum Y coordinate of the bounding box.
     * @return the maxY boundary value
     */
    public double getMaxY() { return maxY; }
    /**
     * Gets the minimum Z coordinate of the bounding box.
     * @return the minZ boundary value
     */
    public double getMinZ() { return minZ; }
    /**
     * Gets the maximum Z coordinate of the bounding box.
     * @return the maxZ boundary value
     */
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
    /** Constant vector representing the direction of the X axis for coordinate projection. */
    private static final Vector X_AXIS = new Vector(1, 0, 0);
    /** Constant vector representing the direction of the Y axis for coordinate projection. */
    private static final Vector Y_AXIS = new Vector(0, 1, 0);
    /** Constant vector representing the direction of the Z axis for coordinate projection. */
    private static final Vector Z_AXIS = new Vector(0, 0, 1);
    /**
     * Helper method to extract the X coordinate of a Point using vector math projection.
     * @param p The point to evaluate
     * @return The X coordinate value as a double
     */
    protected double getX(Point p) {
        return p.equals(Point.ZERO) ? 0 : p.subtract(Point.ZERO).dotProduct(X_AXIS);
    }
    /**
     * Helper method to extract the Y coordinate of a Point using vector math projection.
     * @param p The point to evaluate
     * @return The Y coordinate value as a double
     */
    protected double getY(Point p) {
        return p.equals(Point.ZERO) ? 0 : p.subtract(Point.ZERO).dotProduct(Y_AXIS);
    }
    /**
     * Helper method to extract the Z coordinate of a Point using vector math projection.
     * @param p The point to evaluate
     * @return The Z coordinate value as a double
     */
    protected double getZ(Point p) {
        return p.equals(Point.ZERO) ? 0 : p.subtract(Point.ZERO).dotProduct(Z_AXIS);
    }
    // -------------------------------------------------------------
    /**
     * Inner static class representing a point of intersection between a ray and a geometry.
     * Holds reference to the target geometry, the exact point in 3D space, and material traits,
     * alongside pre-calculated lighting vectors for shading.
     */
    public static class Intersection {
        /** The specific concrete geometry component that was hit by the ray. */
        public final Geometry geometry;
        /** The precise spatial point where the intersection takes place. */
        public final Point point;
        /** The material characteristics of the intersected geometry used for lighting computation. */
        public final Material material;
        /** The normal vector to the geometry surface at the intersection point. */
        public Vector n;
        /** The view direction vector (ray direction from the source camera/previous intersection). */
        public Vector v;
        /** The dot product of the normal vector 'n' and the view vector 'v' (n * v). */
        public double nv;
        /** The direction vector from the light source towards the intersection point. */
        public Vector l;
        /** The dot product of the normal vector 'n' and the light vector 'l' (n * l). */
        public double nl;
        /**
         * Constructor for an Intersection instance.
         * * @param geometry The intersected geometry object
         * @param point    The spatial point of intersection
         */
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
    /**
     * Calculates all intersections between a ray and the geometry with no distance limitation.
     * Wraps the primary calculation logic.
     * * @param ray The casting ray
     * @return A list of Intersection objects, or null if none are found
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Finds all intersections between the geometry and a given ray, up to a maximum distance.
     * This method implements the NVI pattern by calling the protected helper method.
     *
     * @param ray         The ray intersecting the geometry
     * @param maxDistance The maximum valid distance from the ray origin to the intersection point
     * @return A list of valid Intersections within range, or null if none exist or box is missed
     */
    public final List<Intersection> calcIntersections(Ray ray, double maxDistance) {
        // אם מערכת הקופסאות מופעלת והקרן מחטיאה את הקופסה לחלוטין - נדלג על הצורה!
        if (bvhIsOn && !checkBoundingBox(ray)) {
            return null;
        }
        return calcIntersectionsHelper(ray, maxDistance);
    }
    /**
     * Abstract helper method implemented by specific shape subclasses to perform actual
     * ray-geometry intersection math calculations.
     * * @param ray         The ray intersecting the geometry
     * @param maxDistance The maximum valid distance threshold
     * @return A list of internal Intersections within range, or null if none exist
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance);
    /**
     * Finds and extracts only the structural 3D points of intersection without geometry/shading metadata.
     * * @param ray The casting ray
     * @return A list of raw Point items, or null if no intersections are found
     */
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