package geometries.api;

import primitives.*;


/**
 * Interface Geometry is the basic interface for all geometric objects
 * in the 3D space.
 *
 * @author Dan Zilberstein
 */
public abstract class Geometry extends Intersectable {
    /** Emission light color */
    private Color _emission = Color.BLACK;

    /**
     * Getter for emission color
     * @return the emission color
     */
    public Color getEmission() { return _emission; }

    /**
     * Setter for emission color (Builder pattern)
     * @param emission the emission color to set
     * @return the geometry object itself
     */
    public Geometry setEmission(Color emission) {
        _emission = emission;
        return this;
    }
    /**
     * Calculates the normal vector to the geometry at a given point.
     *
     * @param point the point on the geometry to calculate the normal at
     * @return the normalized normal vector
     */
    public abstract Vector getNormal(Point point);
}