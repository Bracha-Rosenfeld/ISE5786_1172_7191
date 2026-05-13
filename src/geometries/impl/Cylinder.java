package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a finite cylinder in 3D space.
 * The cylinder is defined by a radius, an axis ray, and a height.
 */
public final class Cylinder extends Tube {
    /**
     * The height of the finite cylinder
     */
    private final double _height;

    /**
     * Constructs a finite cylinder with a given radius, axis, and height.
     *
     * @param radius the radius of the cylinder
     * @param axis   the axis ray of the cylinder
     * @param height the height of the cylinder along the axis
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Check if the point is exactly at the center of the bottom base
        if (point.equals(p0)) return v.scale(-1);

        // Calculate the projection 't' of the vector from p0 to the point onto the axis
        double t = alignZero(v.dotProduct(point.subtract(p0)));

        // If t is 0, the point is on the bottom base
        if (isZero(t)) return v.scale(-1);

        // If t is equal to the height, the point is on the top base
        if (isZero(t - _height)) return v;

        // Otherwise, the point is on the envelope, use the Tube's getNormal
        return super.getNormal(point);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point pr0 = ray.origin();
        Vector v = ray.direction();
        Vector va = _axis.direction();
        Point p0 = _axis.origin();
        Point pTop = _axis.getPoint(_height);

        double nv = alignZero(va.dotProduct(v));

        // Check if the ray is parallel (or anti-parallel) to the cylinder's axis
        boolean isParallel = isZero(1.0 - Math.abs(nv));

        Point p1 = null;
        Point p2 = null;

        // 1. Intersect with the infinite tube (envelope)
        List<Intersection> tubeIntersections = super.calcIntersectionsHelper(ray);
        if (tubeIntersections != null) {
            for (Intersection inter : tubeIntersections) {
                Point p = inter.point;
                double t = 0;
                if (!p.equals(p0)) {
                    t = alignZero(va.dotProduct(p.subtract(p0)));
                }
                // Take only points strictly inside the envelope (excluding edges to prevent duplicates)
                if (t > 0 && t < _height) {
                    if (p1 == null) p1 = p;
                    else p2 = p;
                }
            }
        }

        // 2. Intersect with the bases (caps)
        if (!isZero(nv)) {
            // Bottom base
            if (!pr0.equals(p0)) {
                double tB = alignZero(va.dotProduct(p0.subtract(pr0)) / nv);
                if (tB > 0) {
                    Point pB = ray.getPoint(tB);
                    double dSqB = alignZero(pB.distanceSquared(p0) - _radius * _radius);
                    // Accept if strictly inside the base (< 0) or exactly on the corner (== 0) only if not parallel
                    if (dSqB < 0 || (dSqB == 0 && !isParallel)) {
                        if (p1 == null) p1 = pB;
                        else p2 = pB;
                    }
                }
            }

            // Top base
            if (!pr0.equals(pTop)) {
                double tT = alignZero(va.dotProduct(pTop.subtract(pr0)) / nv);
                if (tT > 0) {
                    Point pT = ray.getPoint(tT);
                    double dSqT = alignZero(pT.distanceSquared(pTop) - _radius * _radius);
                    if (dSqT < 0 || (dSqT == 0 && !isParallel)) {
                        if (p1 == null) p1 = pT;
                        else p2 = pT;
                    }
                }
            }
        }

        // 3. Sort and return results without unnecessary allocations
        if (p1 == null) return null;
        if (p2 == null) return List.of(new Intersection(this, p1));

        if (alignZero(p1.distanceSquared(pr0) - p2.distanceSquared(pr0)) > 0) {
            return List.of(new Intersection(this, p2), new Intersection(this, p1));
        }
        return List.of(new Intersection(this, p1), new Intersection(this, p2));
    }
}