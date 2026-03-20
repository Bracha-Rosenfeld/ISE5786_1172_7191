package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Cylinder represents a finite cylinder in 3D space.
 */
public final class Cylinder extends Tube {
    private final double _height;

    /**
     * Constructor to initialize a cylinder.
     * @param radius the radius
     * @param axis   the axis ray
     * @param height the height
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }

    @Override
    public String toString() {
        return "Cylinder: " + super.toString() + ", height=" + _height;
    }
}