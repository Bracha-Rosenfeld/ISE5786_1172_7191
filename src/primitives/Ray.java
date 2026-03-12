package primitives;

/**
 * Class Ray represents a semi-line in 3D space, defined by an origin point and a direction vector.
 * The direction vector is always normalized.
 * @author Dan Zilberstein
 */
public class Ray {
    /** The origin point of the ray */
    private final Point  _origin;
    /** The normalized direction vector of the ray */
    private final Vector _direction;

    /**
     * Constructor to initialize Ray with an origin point and a direction vector.
     * The direction vector is automatically normalized.
     * @param origin    the starting point
     * @param direction the direction vector
     */
    public Ray(Point origin, Vector direction) {
        _origin    = origin;
        _direction = direction.normalize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other)
                && _origin.equals(other._origin)
                && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        return 31 * _origin.hashCode() + _direction.hashCode();
    }

    @Override
    public String toString() {
        return "Ray: origin=" + _origin + ", direction=" + _direction;
    }

    /**
     * Getter for the origin point.
     * @return the origin point
     */
    public Point origin() { return _origin; }

    /**
     * Getter for the direction vector.
     * @return the normalized direction vector
     */
    public Vector direction() { return _direction; }
}