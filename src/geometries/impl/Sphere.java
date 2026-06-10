package geometries.impl;

import geometries.api.RadialGeometry;
import primitives.*;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.alignZero;

/**
 * Class Sphere represents a sphere in 3D space.
 *
 * @author Dina Black and Bracha Rosenfeld
 */
public final class Sphere extends RadialGeometry {
    /** The center point of the sphere */
    private final Point _center;

    /**
     * Constructor to initialize a sphere with a center point and a radius.
     *
     * @param center the center point
     * @param radius the radius value
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Special case: ray starts exactly at the center of the sphere
        if (_center.equals(p0)) {
            // Check if the radius (the distance) is within maxDistance
            if (alignZero(_radius - maxDistance) <= 0) {
                return List.of(new Intersection(this, ray.getPoint(_radius)));
            }
            return null;
        }

        Vector u = _center.subtract(p0);
        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);

        // If dSquared is negative (due to floating point issues), make it 0
        if (dSquared < 0) dSquared = 0;
        double d = Math.sqrt(dSquared);

        // If d >= radius, there are no intersections.
        if (alignZero(d - _radius) >= 0) {
            return null;
        }

        double th = alignZero(Math.sqrt(_radius * _radius - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        List<Intersection> result = null;

        // Check if t1 is strictly positive and within maxDistance
        if (t1 > 0 && alignZero(t1 - maxDistance) <= 0) {
            result = new ArrayList<>();
            result.add(new Intersection(this, ray.getPoint(t1)));
        }

        // Check if t2 is strictly positive and within maxDistance
        if (t2 > 0 && alignZero(t2 - maxDistance) <= 0) {
            if (result == null) {
                result = new ArrayList<>();
            }
            result.add(new Intersection(this, ray.getPoint(t2)));
        }

        return result;
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