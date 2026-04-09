package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import java.util.List;

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
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        double nv = _normal.dotProduct(v);

        // If the ray is parallel to the plane (nv == 0), there are no intersections.
        if (Util.isZero(nv)) {
            return null;
        }

        // Ray origin cannot be exactly the plane's reference point
        // because creating a vector from identical points throws an exception.
        if (_point.equals(p0)) {
            return null;
        }

        // t = (n * (Q - P0)) / (n * v)
        Vector qMinusP0 = _point.subtract(p0);
        double nQMinusP0 = _normal.dotProduct(qMinusP0);
        double t = Util.alignZero(nQMinusP0 / nv);

        // We only return points strictly in the ray's direction (t > 0).
        // The origin point itself is not included.
        if (t <= 0) {
            return null;
        }

        // Create the list strictly when an intersection is found
        return List.of(ray.getPoint(t));
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