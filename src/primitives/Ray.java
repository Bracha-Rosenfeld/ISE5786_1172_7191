package primitives;

import java.util.*;

/**
 * Class Ray represents a semi-line in 3D space.
 *
 * @author Dan Zilberstein
 */
public final class Ray {
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
     * Finds the closest point to the ray's origin from a given list of points.
     *
     * @param points list of points to check
     * @return the closest point, or null if the list is null
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null) {
            return null;
        }

        Point closestPoint = null;
        double minDistanceSq = Double.POSITIVE_INFINITY;

        for (Point point : points) {
            double distanceSq = point.distanceSquared(_origin);
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closestPoint = point;
            }
        }

        return closestPoint;
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