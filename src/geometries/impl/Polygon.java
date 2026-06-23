package geometries.impl;

import geometries.api.Geometry;
import geometries.api.Intersectable;
import primitives.*;

import java.util.List;

public class Polygon extends Geometry {
    protected final List<Point> _vertices;
    protected final Plane _plane;
    private final int _size;

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

    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }
}