package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Cylinder represents a finite cylinder in 3D space.
 * It inherits from Tube and adds a height component. [cite: 162, 163]
 */
public final class Cylinder extends Tube {
    /** The height of the cylinder [cite: 164, 165, 303] */
    private final double _height;

    /**
     * Constructor to initialize a cylinder with radius, axis ray, and height. [cite: 113, 115]
     * * @param radius the radius of the cylinder [cite: 126]
     * @param axis   the axis ray of the cylinder [cite: 156]
     * @param height the height of the cylinder [cite: 165]
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis); // Initializing Tube fields [cite: 115]
        _height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        return null; // To be implemented in the next stage [cite: 172, 173]
    }

    @Override
    public String toString() {
        return "Cylinder: " + super.toString() + ", height=" + _height;
    }
}