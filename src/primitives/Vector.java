package primitives;

import static primitives.Util.isZero;

/**
 * Class Vector represents a direction and magnitude in 3D space.
 * It inherits from Point.
 * @author Dan Zilberstein
 */
public class Vector extends Point {
    /** Constants for the axis vectors */
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructor to initialize Vector based on three double values.
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     * @throws IllegalArgumentException if the vector is (0,0,0)
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (isZero(x) && isZero(y) && isZero(z))
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
    }

    /**
     * Constructor to initialize Vector based on a Double3 object.
     * @param xyz Double3 value
     * @throws IllegalArgumentException if the vector is (0,0,0)
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
    }

    /**
     * Scalar multiplication (scaling) of a vector.
     * @param rhs scaling factor
     * @return a new Vector scaled by rhs
     */
    public Vector scale(double rhs) {
        return new Vector(_xyz.scale(rhs));
    }

    /**
     * Adds another vector to this vector.
     * @param other the vector to add
     * @return a new Vector representing the sum
     */
    public Vector add(Vector other) {
        return new Vector(_xyz.add(other._xyz));
    }

    /**
     * Dot product between two vectors.
     * @param other the other vector
     * @return the scalar product
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1() +
                _xyz._d2() * other._xyz._d2() +
                _xyz._d3() * other._xyz._d3();
    }

    /**
     * Cross product between two vectors.
     * @param other the other vector
     * @return a new Vector orthogonal to both
     */
    public Vector crossProduct(Vector other) {
        double x = _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2();
        double y = _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3();
        double z = _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1();
        return new Vector(x, y, z);
    }

    public double lengthSquared() {
        return dotProduct(this);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vector normalize() {
        return scale(1d / length());
    }

    @Override
    public String toString() {
        return "Vector" + _xyz;
    }
}