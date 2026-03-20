package geometries.impl;

import geometries.api.RadialGeometry;
import primitives.Point;
import primitives.Vector;

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
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", " + super.toString();
    }
}