package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Tube represents an infinite tube in 3D space, defined by a radius and an axis ray.
 * @author Dan Zilberstein
 */
public class Tube extends RadialGeometry {
    /** The axis ray of the tube */
    protected final Ray _axis;

    /**
     * Constructor to initialize a tube with a radius and an axis ray.
     * @param radius the radius value
     * @param axis   the axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        return null; // To be implemented in the next stage
    }
}