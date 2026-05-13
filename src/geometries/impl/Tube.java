package geometries.impl;

import geometries.api.RadialGeometry;
import primitives.*;

import java.util.List;

import static primitives.Util.*; // <-- הוספנו ייבוא סטטי

/**
 * Class Tube represents an infinite tube in 3D space, defined by a radius and an axis ray.
 *
 * @author Dina Black and Bracha Rosenfeld
 */
public class Tube extends RadialGeometry {
    /**
     * The axis ray of the tube
     */
    protected final Ray _axis;

    /**
     * Constructor to initialize a tube with a radius and an axis ray.
     *
     * @param radius the radius value
     * @param axis   the axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();
        Vector p0ToPoint = point.subtract(p0);
        double t = v.dotProduct(p0ToPoint);

        // שימוש ב-isZero נקי
        Point o = isZero(t) ? p0 : p0.add(v.scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Vector v = ray.direction();
        Vector va = _axis.direction();
        Point p0 = ray.origin();
        Point pa = _axis.origin();

        // Calculate v - (v * va) * va
        double vDotVa = alignZero(v.dotProduct(va));
        Vector vecA = null;
        try {
            Vector vMinusVaScale = isZero(vDotVa) ? v : v.subtract(va.scale(vDotVa));
            vecA = vMinusVaScale;
        } catch (IllegalArgumentException e) {
            // v is parallel to va. If the ray is parallel to the tube axis, there are no intersections.
            return null;
        }

        double a = alignZero(vecA.lengthSquared());
        double b = 0;
        double c = 0;

        Vector deltaP = null;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            // p0 == pa
        }

        if (deltaP != null) {
            double dpDotVa = alignZero(deltaP.dotProduct(va));
            Vector vecB = null;
            try {
                vecB = isZero(dpDotVa) ? deltaP : deltaP.subtract(va.scale(dpDotVa));
            } catch (IllegalArgumentException e) {
                // deltaP is parallel to va (vecB is zero vector)
            }

            if (vecB != null) {
                b = alignZero(2 * vecA.dotProduct(vecB));
                c = alignZero(vecB.lengthSquared() - _radius * _radius);
            } else {
                c = alignZero(-_radius * _radius);
            }
        } else {
            c = alignZero(-_radius * _radius);
        }

        // Calculate the discriminant: DELTA = b^2 - 4ac
        double discriminant = alignZero(b * b - 4 * a * c);

        // If discriminant <= 0, there are no intersections (tangents return null per requirements)
        if (discriminant <= 0) {
            return null;
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = alignZero((-b - sqrtDiscriminant) / (2 * a));
        double t2 = alignZero((-b + sqrtDiscriminant) / (2 * a));

        if (t1 > 0 && t2 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t1)),
                    new Intersection(this, ray.getPoint(t2)));
        }
        if (t1 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t1)));
        }
        if (t2 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t2)));
        }

        return null;
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", " + super.toString();
    }
}