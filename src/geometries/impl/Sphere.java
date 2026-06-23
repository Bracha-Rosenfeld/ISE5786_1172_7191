package geometries.impl;

import geometries.api.RadialGeometry;
import geometries.api.Intersectable;
import primitives.*;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.alignZero;
/**
 * Class representing a three-dimensional sphere in Euclidean space.
 * A sphere is defined by its center point and a radius.
 * Inherits from {@link RadialGeometry}.
 */
public final class Sphere extends RadialGeometry {

    private final Point _center;
    /**
     * Constructor to initialize a sphere with a specific center point and a radius value.
     * * @param center The center point of the sphere.
     * @param radius The radius length of the sphere.
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }
    /**
     * Enables or disables BVH (Bounding Box) mechanism for this sphere.
     * When enabled, it calculates the Axis-Aligned Bounding Box (AABB) boundaries
     * by subtracting and adding the radius to the center's X, Y, and Z coordinates.
     * * @param bvhIsOn true to turn BVH on, false to turn it off.
     * @return The updated Sphere instance itself for method chaining.
     */
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
    /**
     * Finds the intersection points between a given ray and the sphere, bounded by a maximum distance.
     * Uses geometric projection math to calculate whether the ray passes through, misses,
     * or originates inside the sphere.
     * * @param ray         The ray intersecting the sphere.
     * @param maxDistance The maximum valid distance threshold for the intersections.
     * @return A list of valid {@link Intersection} points within range, or null if no intersections occur.
     */
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
    /**
     * Gets the normal vector to the sphere's surface at a given point.
     * The normal vector is calculated by subtracting the center point from
     * the surface point and normalizing the resulting vector.
     * * @param point The point on the sphere surface to find the normal at.
     * @return The normalized normal vector orthogonal to the sphere surface.
     */
    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }
    /**
     * Generates a text representation of the sphere instance properties.
     * * @return String detailing the center and radius information.
     */
    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", " + super.toString();
    }
}