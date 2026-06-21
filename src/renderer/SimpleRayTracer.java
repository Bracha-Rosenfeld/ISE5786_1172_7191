package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import java.util.List;
import primitives.Point;

/**
 * A simple ray tracer implementation responsible for tracing rays through a scene
 * and computing the color of the intersection points using the Phong lighting model,
 * shadows, reflections, and refractions.
 * * @author Project Assistant
 */
class SimpleRayTracer extends RayTracerBase {

    /** Maximum level of recursion for global effects calculations. */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /** Minimum attenuation factor threshold to continue recursion. */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /** Initial attenuation factor triad (1,1,1). */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructs a SimpleRayTracer with a specified scene.
     *
     * @param scene the scene to be rendered
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        Intersection closest = findClosestIntersection(ray);
        if (closest == null) {
            return _scene.background;
        }
        return calcColor(closest, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }

    /**
     * Finds the closest intersection point of a ray with the scene geometries.
     *
     * @param ray the ray being cast
     * @return the closest intersection point, or null if no intersections occur
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        return ray.findClosestIntersection(intersections);
    }

    /**
     * Combines local and global lighting effects recursively to calculate the point's color.
     *
     * @param intersection the intersection point data
     * @param ray          the ray that caused the intersection
     * @param level        the current recursion depth level
     * @param k            the current cumulative attenuation factor
     * @return the calculated color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, ray, k);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, ray.direction(), level, k));
    }

    /**
     * Computes the local lighting effects (Emission, Ambient, Diffuse, Specular) at a point.
     *
     * @param intersection the intersection point data
     * @param ray          the ray that caused the intersection
     * @param k            the current cumulative attenuation factor
     * @return the color contribution from local elements
     */
    private Color calcLocalEffects(Intersection intersection, Ray ray, Double3 k) {
        if (!preprocessIntersection(intersection, ray.direction())) {
            return _scene.ambientLight.getIntensity().add(intersection.geometry.getEmission());
        }

        // FIX: Incorporating Ambient Light scaled by kA into the initial color base
        Color ambient = _scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA);
        Color color = intersection.geometry.getEmission().add(ambient);

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                Double3 ktr = transparency(intersection, lightSource);

                if (!ktr.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);

                    color = color.add(lightIntensity.scale(calcDiffuse(intersection)))
                            .add(lightIntensity.scale(calcSpecular(intersection)));
                }
            }
        }
        return color;
    }

    /**
     * Computes the global lighting effects (Reflection and Refraction) recursively.
     *
     * @param intersection the intersection point data
     * @param v            the direction vector of the incoming ray
     * @param level        the current recursion depth level
     * @param k            the current cumulative attenuation factor
     * @return the color contribution from global reflections and refractions
     */
    private Color calcGlobalEffects(Intersection intersection, Vector v, int level, Double3 k) {
        Color color = Color.BLACK;
        Double3 kr = intersection.geometry.getMaterial().kR;
        Double3 kkr = kr.product(k);
        if (!kkr.isLowerThan(MIN_CALC_COLOR_K)) {
            Ray reflectedRay = constructReflectedRay(intersection.point, v, intersection.n);
            color = color.add(calcGlobalEffect(reflectedRay, level, kr, kkr));
        }

        Double3 kt = intersection.geometry.getMaterial().kT;
        Double3 kkt = kt.product(k);
        if (!kkt.isLowerThan(MIN_CALC_COLOR_K)) {
            Ray refractedRay = constructRefractedRay(intersection.point, v, intersection.n);
            color = color.add(calcGlobalEffect(refractedRay, level, kt, kkt));
        }
        return color;
    }

    /**
     * Helper method to trace a single global recursive ray and scale its intensity.
     *
     * @param ray   the reflection or refraction ray
     * @param level the current recursion depth level
     * @param kx    the material factor for this specific effect (kR or kT)
     * @param kkx   the combined cumulative attenuation factor
     * @return the color contribution of the specific global ray path
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 kx, Double3 kkx) {
        Intersection closest = findClosestIntersection(ray);
        if (closest == null) {
            return _scene.background.scale(kx);
        }
        return calcColor(closest, ray, level - 1, kkx).scale(kx);
    }

    /**
     * Constructs a reflected ray from an intersection point.
     *
     * @param point the intersection point
     * @param v     the incoming direction vector
     * @param n     the surface normal vector at the point
     * @return the constructed reflection ray
     */
    private Ray constructReflectedRay(Point point, Vector v, Vector n) {
        double vn = v.dotProduct(n);
        if (primitives.Util.isZero(vn)) {
            return new Ray(point, v, n);
        }
        Vector r = v.subtract(n.scale(2 * vn)).normalize();
        return new Ray(point, r, n);
    }

    /**
     * Constructs a refracted ray from an intersection point.
     *
     * @param point the intersection point
     * @param v     the incoming direction vector
     * @param n     the surface normal vector at the point
     * @return the constructed refraction ray
     */
    private Ray constructRefractedRay(Point point, Vector v, Vector n) {
        return new Ray(point, v, n);
    }

    /**
     * Computes the transparency/shadow factor between a light source and an intersection point.
     *
     * @param intersection the intersection point data
     * @param lightSource  the light source being checked
     * @return the transparency multiplier triad representing light occlusion
     */
    private Double3 transparency(Intersection intersection, LightSource lightSource) {
        Vector lightDirection = lightSource.getL(intersection.point).scale(-1);
        Ray lightRay = new Ray(intersection.point, lightDirection, intersection.n);

        double lightDistance = lightSource.getDistance(intersection.point);
        List<Intersection> intersections = _scene.geometries.calcIntersections(lightRay, lightDistance);

        if (intersections == null) {
            return Double3.ONE;
        }

        Double3 ktr = Double3.ONE;
        for (Intersection geo : intersections) {
            ktr = ktr.product(geo.geometry.getMaterial().kT);
            if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                return Double3.ZERO;
            }
        }

        return ktr;
    }

    /**
     * Calculates the diffuse color factor according to Lambert's cosine law.
     *
     * @param intersection the intersection point data
     * @return the diffuse reflection factor triad
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.geometry.getMaterial().kD.scale(Math.abs(intersection.nl));
    }

    /**
     * Calculates the specular color factor according to the Phong model.
     *
     * @param intersection the intersection point data
     * @return the specular reflection factor triad
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(intersection.n.scale(2 * intersection.nl)).normalize();
        double minusVR = primitives.Util.alignZero(-intersection.v.dotProduct(r));
        if (minusVR <= 0) {
            return Double3.ZERO;
        }
        return intersection.geometry.getMaterial().kS.scale(
                Math.pow(minusVR, intersection.geometry.getMaterial().nShininess)
        );
    }
}