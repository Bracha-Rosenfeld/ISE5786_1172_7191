package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Class Plane represents a plane in 3D space.
 * @author Dina Black and Bracha Rosenfeld
 */
public class Plane extends Geometry {
    /** A point on the plane */
    private final Point  _point;
    /** The normal vector to the plane */
    private final Vector _normal;

    /**
     * Constructor to initialize a plane from three points.
     * For now, it only stores the first point.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point  = p1;
        _normal = null; // To be implemented later
    }

    /**
     * Constructor to initialize a plane from a point and a normal vector.
     * The normal vector is normalized.
     * @param point  a point on the plane
     * @param normal the normal vector
     */
    public Plane(Point point, Vector normal) {
        _point  = point;
        _normal = normal.normalize(); // [cite: 145]
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal; // [cite: 146]
    }
}