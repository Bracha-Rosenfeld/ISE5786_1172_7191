package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Class Triangle represents a triangle in 3D space.
 * It inherits from Polygon.
 * @author Dina Black and Bracha Rosenfeld
 */
public final class Triangle extends Polygon {
    /**
     * Constructor to initialize a triangle with its three vertices.
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public Vector getNormal(Point point) {
        // Triangle is a Polygon, so it should return the polygon's normal
        return super.getNormal(point);
    }

    @Override
    public String toString() {
        return "Triangle: " + super.toString();
    }
}