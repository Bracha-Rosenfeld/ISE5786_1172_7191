package geometries.api;

/**
 * Abstract class RadialGeometry is the base class for all geometric objects
 * that have a radius.
 * @author Dan Zilberstein
 */
public abstract class RadialGeometry extends Geometry {
    /** The radius of the radial geometry */
    protected final double _radius;
    /** The squared radius to save calculations later (DRY) */
    protected final double _radiusSquared;

    /**
     * Constructor to initialize RadialGeometry with a radius.
     * It initializes both the radius and its square.
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        this._radius = radius;
        this._radiusSquared = radius * radius;
    }
}