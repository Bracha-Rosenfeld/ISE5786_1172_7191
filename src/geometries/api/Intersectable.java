
package geometries.api;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Interface-like abstract class representing geometries that can be intersected by a ray.
 * All geometric shapes and composite geometries that can calculate their intersections
 * with a 3D ray should implement this class.
 */
public abstract class Intersectable {

    /**
     * Finds all intersection points between the geometry and a given ray.
     *
     * @param ray the ray intersecting the geometry
     * @return a list of intersection points, or null if there are no intersections
     */
    public abstract List<Point> findIntersections(Ray ray);
}
