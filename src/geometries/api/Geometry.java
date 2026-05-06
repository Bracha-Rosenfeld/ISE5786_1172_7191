package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Interface Geometry is the basic interface for all geometric objects
 * in the 3D space.
 *
 * @author Dan Zilberstein
 */
public abstract class Geometry extends Intersectable {
    /**
     * Calculates the normal vector to the geometry at a given point.
     *
     * @param point the point on the geometry to calculate the normal at
     * @return the normalized normal vector
     */
    public abstract Vector getNormal(Point point);
}