package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.ArrayList;
import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Cylinder represents a finite cylinder in 3D space.
 */
public final class Cylinder extends Tube {
    private final double _height;

    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = new ArrayList<>();
        Vector va = _axis.direction();
        Point p0 = _axis.origin();
        Point pTop = _axis.getPoint(_height);
        Vector v = ray.direction();
        Point pr0 = ray.origin();

        // 1. חיתוך עם המעטפת (Tube)
        List<Point> tubePoints = super.findIntersections(ray);
        if (tubePoints != null) {
            for (Point p : tubePoints) {
                // הגנה מפני וקטור אפס: אם הנקודה היא p0, ה-t הוא 0
                double t;
                if (p.equals(p0)) t = 0;
                else t = alignZero(p.subtract(p0).dotProduct(va));

                if (t > 0 && t < _height) {
                    result.add(p);
                }
            }
        }

        // 2. חיתוך עם הבסיסים
        double nv = va.dotProduct(v);
        if (!isZero(nv)) { // הקרן לא מקבילה לבסיס
            // בסיס תחתון
            double tB = alignZero(va.dotProduct(p0.subtract(pr0)) / nv);
            if (tB > 0) {
                Point p = ray.getPoint(tB);
                // בדיקה אם בתוך הרדיוס: אם p=p0 המרחק 0, אחרת מחשבים
                double distSq = p.equals(p0) ? 0 : p.distanceSquared(p0);
                if (alignZero(distSq - _radius * _radius) <= 0) {
                    // סינון השקה מקבילה (Parallel on Envelope)
                    if (alignZero(distSq - _radius * _radius) < 0 || !isZero(v.subtract(va.scale(v.dotProduct(va))).lengthSquared()))
                        result.add(p);
                }
            }

            // בסיס עליון
            double tT = alignZero(va.dotProduct(pTop.subtract(pr0)) / nv);
            if (tT > 0) {
                Point p = ray.getPoint(tT);
                double distSq = p.equals(pTop) ? 0 : p.distanceSquared(pTop);
                if (alignZero(distSq - _radius * _radius) <= 0) {
                    if (alignZero(distSq - _radius * _radius) < 0 || !isZero(v.subtract(va.scale(v.dotProduct(va))).lengthSquared()))
                        result.add(p);
                }
            }
        }

        if (result.isEmpty()) return null;

        // 3. ניקוי כפילויות (במיוחד בפינות)
        List<Point> finalResult = new ArrayList<>();
        for (Point p : result) {
            if (!finalResult.contains(p)) finalResult.add(p);
        }

        if (finalResult.isEmpty()) return null;

        // 4. מיון לפי מרחק מהקרן
        finalResult.sort((pt1, pt2) -> Double.compare(pt1.distanceSquared(pr0), pt2.distanceSquared(pr0)));

        return finalResult;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();
        if (point.equals(p0)) return v.scale(-1);

        Vector p0ToPoint;
        try {
            p0ToPoint = point.subtract(p0);
        } catch (IllegalArgumentException e) {
            return v.scale(-1);
        }

        double t = alignZero(v.dotProduct(p0ToPoint));
        if (isZero(t)) return v.scale(-1);
        if (isZero(t - _height)) return v;
        return point.subtract(p0.add(v.scale(t))).normalize();
    }
}