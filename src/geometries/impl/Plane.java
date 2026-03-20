package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Class Plane represents a plane in 3D space.
 */
public final class Plane extends Geometry {
    private final Point  _point;
    private final Vector _normal;

    /**
     * Constructor to initialize a plane from three points.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;
        // Calculation of the normal: (p2-p1) x (p3-p1)
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        _normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructor to initialize a plane from a point and a normal vector.
     * @param point  a point on the plane
     * @param normal the normal vector
     */
    public Plane(Point point, Vector normal) {
        _point  = point;
        _normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }
}