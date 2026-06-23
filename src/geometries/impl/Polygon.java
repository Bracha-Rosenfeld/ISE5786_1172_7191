package geometries.impl;

import geometries.api.Geometry;
import geometries.api.Intersectable;
import primitives.*;

import java.util.List;
/**
 * Class representing a flat convex polygon in a 3D space.
 * The polygon is defined by an ordered list of vertices that must all lie on the same plane
 * and form a convex shape. Inherits from {@link Geometry}.
 * * @author Computer Graphics Course
 */
public class Polygon extends Geometry {
    /** The ordered list of vertices defining the polygon boundaries. */
    protected final List<Point> _vertices;
    /** The supporting plane in which the polygon resides. */
    protected final Plane _plane;
    /** The total number of vertices forming the polygon. */
    private final int _size;
    /**
     * Constructor to initialize a polygon with a series of vertices.
     * Validates that there are at least 3 vertices, all vertices lie on the same plane,
     * and that the polygon is convex and ordered.
     * * @param vertices An ordered array/varargs of points representing the polygon's vertices.
     * @throws IllegalArgumentException If vertices count is less than 3.
     * @throws IllegalArgumentException If vertices do not lie on the same plane.
     * @throws IllegalArgumentException If vertices are not ordered properly or form a non-convex shape.
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        _vertices = List.of(vertices);
        _size = vertices.length;

        _plane = new Plane(vertices[0], vertices[1], vertices[2]);

        if (_size == 3) return;

        Vector n = _plane.getNormal(vertices[0]);
        Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            if (!Util.isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }
    /**
     * Enables or disables BVH (Bounding Box) mechanism for this polygon.
     * When turned on, it scans all vertices to calculate the exact structural minimum
     * and maximum X, Y, Z coordinates for the Axis-Aligned Bounding Box (AABB).
     * * @param bvhIsOn true to turn BVH on, false to turn it off.
     * @return The updated Polygon instance itself for method chaining.
     */
    @Override
    public Intersectable setBvhIsOn(boolean bvhIsOn) {
        super.setBvhIsOn(bvhIsOn);
        // חישוב הקופסה רק לפי דרישה
        if (bvhIsOn) {
            minX = minY = minZ = Double.POSITIVE_INFINITY;
            maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;

            for (Point p : _vertices) {
                double x = getX(p);
                double y = getY(p);
                double z = getZ(p);

                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
                if (z < minZ) minZ = z;
                if (z > maxZ) maxZ = z;
            }
        }
        return this;
    }
    /**
     * Finds the intersection points between a given ray and the polygon, bounded by a maximum distance.
     * It first finds the intersection with the supporting plane, and then checks whether
     * the intersection point lies within the boundaries of the polygon using vector math.
     * * @param ray         The ray intersecting the polygon.
     * @param maxDistance The maximum valid distance threshold for the intersection.
     * @return A list containing a single {@link Intersection} point if it lies inside, or null otherwise.
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> planeIntersections = _plane.calcIntersections(ray, maxDistance);

        if (planeIntersections == null) {
            return null;
        }

        Point point = planeIntersections.getFirst().point;
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Vector v1 = _vertices.get(_size - 1).subtract(p0);
        Vector v2 = _vertices.get(0).subtract(p0);
        Vector n = v1.crossProduct(v2);

        double vn = Util.alignZero(v.dotProduct(n));

        if (vn == 0) {
            return null;
        }

        boolean isPositive = vn > 0;

        for (int i = 1; i < _size; ++i) {
            v1 = v2;
            v2 = _vertices.get(i).subtract(p0);
            n = v1.crossProduct(v2);
            vn = Util.alignZero(v.dotProduct(n));

            if (vn == 0 || (vn > 0) != isPositive) {
                return null;
            }
        }

        return List.of(new Intersection(this, point));
    }
    /**
     * Gets the normal vector to the polygon's surface at a given point.
     * Delegates the calculation to the supporting plane.
     * * @param point The point on the polygon surface to find the normal at.
     * @return The normal vector orthogonal to the polygon surface.
     */
    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }
}