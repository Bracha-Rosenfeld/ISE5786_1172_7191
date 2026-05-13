package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Interface-like abstract class representing geometries that can be intersected by a ray.
 * Implements the Non-Virtual Interface (NVI) pattern for calculating intersections.
 */
public abstract class Intersectable {

    /**
     * Passive Data Structure representing an intersection point with its specific geometry.
     */
    /**
     * Passive Data Structure representing an intersection point with its specific geometry.
     */
    public static class Intersection {
        /**
         * The geometry that was intersected
         */
        public final Geometry geometry;
        /**
         * The point of intersection
         */
        public final Point point;
        /**
         * The material of the intersected geometry
         */
        public final Material material; // הוספנו את שדה החומר

        /**
         * Constructs an Intersection object.
         *
         * @param geometry the intersected geometry
         * @param point    the point of intersection
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
     * Finds all intersections between the geometry and a given ray.
     * This method implements the NVI pattern by calling the protected helper method.
     *
     * @param ray the ray intersecting the geometry
     * @return a list of intersections, or null if there are no intersections
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Helper method to calculate intersections, to be implemented by subclasses.
     *
     * @param ray the ray intersecting the geometry
     * @return a list of intersections, or null if there are no intersections
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Finds all intersection points between the geometry and a given ray.
     * Retained for backward compatibility.
     *
     * @param ray the ray intersecting the geometry
     * @return a list of intersection points, or null if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream().map(inter -> inter.point).toList();
    }
}