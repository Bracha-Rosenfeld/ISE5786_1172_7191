package primitives;

import static primitives.Util.isZero;

/**
 * Class Point is the basic object representing a point with 3 coordinates in the 3D space.
 * * @author Dan Zilberstein
 */
public class Point {
    /** The coordinates of the point */
    protected final Double3 _xyz;

    /** Zero point (0,0,0) */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructor to initialize Point based on three double values.
     * * @param x first number value
     * @param y second number value
     * @param z third number value
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Constructor to initialize Point based on a Double3 object.
     * * @param xyz Double3 value
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other) && _xyz.equals(other._xyz);
    }

    @Override
    public int hashCode() {
        return _xyz.hashCode();
    }

    @Override
    public String toString() {
        return "Point" + _xyz;
    }

    /**
     * Subtracts one point from another to create a vector.
     * * @param other the point to subtract
     * @return a vector from the other point to this point
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    /**
     * Adds a vector to this point to create a new point.
     * * @param vector the vector to add
     * @return a new point
     */
    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * Calculates the squared distance between two points.
     * * @param other the other point
     * @return the squared distance
     */
    public double distanceSquared(Point other) {
        double dx = _xyz._d1() - other._xyz._d1();
        double dy = _xyz._d2() - other._xyz._d2();
        double dz = _xyz._d3() - other._xyz._d3();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the distance between two points.
     * * @param other the other point
     * @return the distance
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }
}