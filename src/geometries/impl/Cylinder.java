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
        Point p0 = _axis.origin();
        Vector v = _axis.direction();
        if (point.equals(p0)) {
            return v.scale(-1);
        }

        Vector p0ToPoint = point.subtract(p0);
        double t = v.dotProduct(p0ToPoint);
        if (primitives.Util.isZero(t)) {
            return v.scale(-1);
        }
        if (primitives.Util.isZero(t - _height)) {
            return v;
        }
        Point o = p0.add(v.scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    public String toString() {
        return "Cylinder: " + super.toString() + ", height=" + _height;
    }
}