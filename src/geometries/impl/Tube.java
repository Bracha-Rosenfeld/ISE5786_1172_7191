package geometries.impl;

import geometries.api.RadialGeometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Tube represents an infinite tube in 3D space, defined by a radius and an axis ray.
 * @author Dina Black and Bracha Rosenfeld
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
        Point p0 = _axis.origin();
        Vector v = _axis.direction();
        Vector p0ToPoint = point.subtract(p0);
        double t = v.dotProduct(p0ToPoint);
        Point o = primitives.Util.isZero(t) ? p0 : p0.add(v.scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", " + super.toString();
    }
}