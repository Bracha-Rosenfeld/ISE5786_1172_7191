package primitives;

import java.util.*;

import geometries.api.Intersectable.Intersection;

/**
 * Class Ray represents a semi-line in 3D space.
 *
 * @author Dan Zilberstein
 */
public final class Ray {
    /** Constant for moving the ray origin to avoid self-intersection */
    private static final double DELTA = 0.1;

    private final Point _origin;
    private final Vector _direction;

    /**
     * Constructor to initialize Ray with an origin point and a direction vector.
     *
     * @param origin    the starting point
     * @param direction the direction vector
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Constructor that shifts the ray origin by DELTA along the normal to avoid self-intersection.
     *
     * @param origin    the original starting point
     * @param direction the direction of the ray
     * @param normal    the normal vector at the surface
     */
    public Ray(Point origin, Vector direction, Vector normal) {
        double nv = normal.dotProduct(direction);
        Vector delta = normal.scale(nv > 0 ? DELTA : -DELTA);
        this._origin = origin.add(delta);
        this._direction = direction.normalize();
    }

    /**
     * @return the origin point
     */
    public Point origin() {
        return _origin;
    }

    /**
     * @return the normalized direction vector
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Calculates a point on the ray's line at a given distance from the ray's origin.
     * Uses the formula: P = P0 + t * v
     *
     * @param t the distance from the ray's origin
     * @return the calculated point
     */
    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException e) {
            // Catching the case where scaling by t creates a ZERO vector (which throws an exception)
            return _origin;
        }
    }


    /**
     * Finds the closest intersection to the ray's origin from a given list of intersections.
     *
     * @param intersections list of intersections to check
     * @return the closest intersection, or null if the list is null or empty
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        Intersection closest = null;
        double minDistanceSq = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            // Check distance from ray origin to the intersection point
            double distanceSq = intersection.point.distanceSquared(_origin);
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closest = intersection;
            }
        }

        return closest;
    }


    /**
     * Finds the closest point to the ray's origin from a given list of points.
     * Uses findClosestIntersection to maintain DRY principle.
     *
     * @param points list of points to check
     * @return the closest point, or null if the list is null or empty
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(points.stream()
                .map(point -> new Intersection(null, point))
                .toList()).point;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other)
                && _origin.equals(other._origin)
                && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        return 31 * _origin.hashCode() + _direction.hashCode();
    }

    @Override
    public String toString() {
        return "Ray: origin=" + _origin + ", direction=" + _direction;
    }
}