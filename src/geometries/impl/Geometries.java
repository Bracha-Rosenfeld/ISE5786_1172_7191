
package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite class representing a collection of geometries.
 * Implements the Intersectable interface.
 */
public class Geometries extends Intersectable {

    /** List of geometries in the collection */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Default constructor for an empty collection of geometries.
     */
    public Geometries() {
    }

    /**
     * Constructor that accepts a varying number of geometries.
     * @param geometries zero or more geometries to add to the collection
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds zero or more geometries to the collection.
     * @param geometries the geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> intersections = null;

        // Iterate over all geometries using delegation
        for (Intersectable geometry : _geometries) {
            List<Point> geoIntersections = geometry.findIntersections(ray);

            if (geoIntersections != null) {
                // Lazy initialization: create the list only when the first intersection is found
                if (intersections == null) {
                    intersections = new ArrayList<>();
                }
                intersections.addAll(geoIntersections);
            }
        }

        return intersections;
    }
}