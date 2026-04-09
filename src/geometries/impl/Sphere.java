package geometries.impl;

import geometries.api.RadialGeometry;
import primitives.*;


import java.util.List;

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
        super(radius); // [cite: 115, 126]
        _center = center;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Special case: ray starts exactly at the center of the sphere
        // To avoid creating a ZERO vector in subtraction, we handle it explicitly.
        if (_center.equals(p0)) {
            return List.of(ray.getPoint(_radius));
        }

        Vector u = _center.subtract(p0);

        // tm = v * u
        double tm = Util.alignZero(v.dotProduct(u));

        // d = sqrt(|u|^2 - tm^2)
        double d = Util.alignZero(Math.sqrt(u.lengthSquared() - tm * tm));

        // If d >= radius, there are no intersections.
        // Note: Tangent case (d == radius) returns null per requirements (0 points).
        if (d >= _radius) {
            return null;
        }

        // th = sqrt(r^2 - d^2)
        double th = Util.alignZero(Math.sqrt(_radius * _radius - d * d));

        // Calculate the distances to the intersection points
        double t1 = Util.alignZero(tm - th);
        double t2 = Util.alignZero(tm + th);

        // We only care about intersections strictly IN FRONT of the ray (t > 0)
        if (t1 > 0 && t2 > 0) {
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        }

        // Using ternary operators is recommended for simple logic (KISS principle)
        if (t1 > 0) {
            return List.of(ray.getPoint(t1));
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