package primitives;

import static primitives.Util.isZero;

/**
 * Class Vector represents a direction and magnitude in 3D space.
 * It inherits from Point.
 * * @author Dan Zilberstein
 */
public class Vector extends Point {

    /** Constant for the Z-axis vector */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructor to initialize Vector based on three double values.
     * @param x first coordinate
     * @param y second coordinate
     * @param z third coordinate
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
        if (_xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
    }

    @Override
    public String toString() {
        return "Vector" + _xyz;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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
     * @throws IllegalArgumentException if the result is a zero vector
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
        Double3 product = _xyz.product(other._xyz);
        return product._d1() + product._d2() + product._d3();
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

    /**
     * Calculates the squared length of the vector.
     * @return squared length
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Calculates the length of the vector.
     * @return length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector.
     * * @return a new normalized Vector (length 1)
     */
    public Vector normalize() {
        return scale(1d / length());
    }
}