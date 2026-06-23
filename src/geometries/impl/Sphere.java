package geometries.impl;

import geometries.api.RadialGeometry;
import geometries.api.Intersectable;
import primitives.*;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.alignZero;

public final class Sphere extends RadialGeometry {
    private final Point _center;

    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Intersectable setBvhIsOn(boolean bvhIsOn) {
        super.setBvhIsOn(bvhIsOn);
        // חישוב הקופסה מתבצע אך ורק אם מנגנון ההאצה הודלק
        if (bvhIsOn) {
            double x = getX(_center);
            double y = getY(_center);
            double z = getZ(_center);

            this.minX = x - _radius;
            this.maxX = x + _radius;
            this.minY = y - _radius;
            this.maxY = y + _radius;
            this.minZ = z - _radius;
            this.maxZ = z + _radius;
        }
        return this;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        if (_center.equals(p0)) {
            if (alignZero(_radius - maxDistance) <= 0) {
                return List.of(new Intersection(this, ray.getPoint(_radius)));
            }
            return null;
        }

        Vector u = _center.subtract(p0);
        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);

        if (dSquared < 0) dSquared = 0;
        double d = Math.sqrt(dSquared);

        if (alignZero(d - _radius) >= 0) {
            return null;
        }

        double th = alignZero(Math.sqrt(_radius * _radius - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        List<Intersection> result = null;

        if (t1 > 0 && alignZero(t1 - maxDistance) <= 0) {
            result = new ArrayList<>();
            result.add(new Intersection(this, ray.getPoint(t1)));
        }

        if (t2 > 0 && alignZero(t2 - maxDistance) <= 0) {
            if (result == null) {
                result = new ArrayList<>();
            }
            result.add(new Intersection(this, ray.getPoint(t2)));
        }

        return result;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", " + super.toString();
    }
}