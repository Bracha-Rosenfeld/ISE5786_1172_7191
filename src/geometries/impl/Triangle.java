package geometries.impl;

import primitives.*;

import java.util.List;

/**
 * Class Triangle represents a triangle in 3D space.
 * It inherits from Polygon.
 *
 * @author Dina Black and Bracha Rosenfeld
 */
public final class Triangle extends Polygon {
    /**
     * Constructor to initialize a triangle with its three vertices.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // 1. Intersect with the plane containing the triangle
        List<Point> planeIntersections = _plane.findIntersections(ray);

        // If the ray doesn't intersect the plane, it definitely doesn't intersect the triangle
        if (planeIntersections == null) {
            return null;
        }

        // 2. Check if the point is inside the triangle boundaries
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Point p1 = _vertices.get(0);
        Point p2 = _vertices.get(1);
        Point p3 = _vertices.get(2);

        // Vectors from the ray origin to the triangle vertices
        Vector v1 = p1.subtract(p0);
        Vector v2 = p2.subtract(p0);
        Vector v3 = p3.subtract(p0);

        // Normals to the planes created by the ray and each edge
        Vector n1 = v1.crossProduct(v2);
        Vector n2 = v2.crossProduct(v3);
        Vector n3 = v3.crossProduct(v1);

        // Calculate dot products and align to zero to handle floating point errors
        double vn1 = Util.alignZero(v.dotProduct(n1));
        double vn2 = Util.alignZero(v.dotProduct(n2));
        double vn3 = Util.alignZero(v.dotProduct(n3));

        // If the ray passes exactly on an edge or vertex, vn will be 0.
        // We do not include edges/vertices in intersections per requirements.
        if (vn1 == 0 || vn2 == 0 || vn3 == 0) {
            return null;
        }

        // The point is inside the triangle strictly if all dot products have the SAME sign
        if ((vn1 > 0 && vn2 > 0 && vn3 > 0) || (vn1 < 0 && vn2 < 0 && vn3 < 0)) {
            return planeIntersections; // Reusing the list from the plane
        }

        // Otherwise, it's outside
        return null;
    }

    @Override
    public String toString() {
        return "Triangle: " + super.toString();
    }
}