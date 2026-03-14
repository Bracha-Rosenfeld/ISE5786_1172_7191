package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Cylinder represents a finite cylinder in 3D space.
 * It inherits from Tube and adds a height component.
 * @author Dina Black and Bracha Rosenfeld
 */
public class Cylinder extends Tube {
    /** The height of the cylinder */
    private final double _height;

    /**
     * Constructor to initialize a cylinder with radius, axis ray, and height.
     * @param radius the radius of the cylinder
     * @param axis   the axis ray of the cylinder
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    /**
     * Getter for the height of the cylinder.
     * @return the height
     */
    public double getHeight() {
        return _height;
    }

    @Override
    public Vector getNormal(Point point) {
        return null; // To be implemented in the next stage
    }
}