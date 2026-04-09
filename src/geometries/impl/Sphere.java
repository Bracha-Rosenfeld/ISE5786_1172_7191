package geometries.impl;

import geometries.api.RadialGeometry;
import primitives.*;

import java.util.List;
import static primitives.Util.alignZero;

/**
 * Class Sphere represents a sphere in 3D space.
 * @author Dina Black and Bracha Rosenfeld
 */
public final class Sphere extends RadialGeometry {
    /** The center point of the sphere */
    private final Point _center;

    /**
     * Constructor to initialize a sphere with a center point and a radius.
     * @param center the center point
     * @param radius the radius value
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Special case: ray starts exactly at the center of the sphere
        if (_center.equals(p0)) {
            return List.of(ray.getPoint(_radius));
        }

        Vector u = _center.subtract(p0);

        // tm = v * u
        double tm = alignZero(v.dotProduct(u));

        // Calculate dSquared and align it to 0 before taking the square root to avoid NaN
        double dSquared = alignZero(u.lengthSquared() - tm * tm);
        double d = Math.sqrt(dSquared);

        // If d >= radius, there are no intersections.
        // Tangent case (d == radius) returns null per requirements.
        if (alignZero(d - _radius) >= 0) {
            return null;
        }

        // th = sqrt(r^2 - d^2)
        double th = alignZero(Math.sqrt(_radius * _radius - dSquared));

        // Calculate the distances to the intersection points
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // We only care about intersections strictly IN FRONT of the ray (t > 0)
        // Since t1 <= t2, we can simplify the logic:
        if (t1 > 0) {
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        }
        if (t2 > 0) {
            return List.of(ray.getPoint(t2));
        }

        return null;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", " + super.toString();
    }
}