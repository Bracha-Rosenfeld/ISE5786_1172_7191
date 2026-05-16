package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * A simple ray tracer implementation.
 * It computes the color based on the closest intersection point and geometry.
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a simple ray tracer for the given scene.
     *
     * @param scene the scene to render
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // Find the intersections of the ray with the scene geometries
        var intersections = _scene.geometries.calcIntersections(ray);

        // If there are no intersections, return the background color
        if (intersections == null) {
            return _scene.background;
        }

        // Find the closest intersection and calculate its color (מעבירים גם את הקרן)
        return calcColor(ray.findClosestIntersection(intersections), ray);
    }

    /**
     * Calculates the color at a specific intersection point.
     * Adds the geometry's emission color to the ambient light intensity and local effects.
     *
     * @param intersection the intersection object containing the geometry and point
     * @param ray the ray to trace
     * @return the calculated color
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        // If the dot product nv is zero, no illumination from this side
        if (!preprocessIntersection(intersection, ray.direction())) {
            return _scene.ambientLight.getIntensity().add(intersection.geometry.getEmission());
        }

        // Final color = Ambient + Emission + Local Effects (Diffuse + Specular)
        return _scene.ambientLight.getIntensity()
                .add(intersection.geometry.getEmission())
                .add(calcLocalEffects(intersection));
    }

    /**
     * Calculates the local shading effects (diffuse + specular) for all light sources.
     * @param intersection the intersection point data (cached)
     * @return the total color from local effects
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = Color.BLACK;
        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                Color lightIntensity = lightSource.getIntensity(intersection.point);
                // שרשור חיבורי הצבע - קודם דיפוזי ואז אספקלרי
                color = color.add(lightIntensity.scale(calcDiffuse(intersection)))
                        .add(lightIntensity.scale(calcSpecular(intersection)));
            }
        }
        return color;
    }

    /**
     * Calculates the diffuse reflection component.
     * @param intersection the processed intersection cache
     * @return the diffuse reflection Double3 triad
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.geometry.getMaterial().kD.scale(Math.abs(intersection.nl));
    }

    /**
     * Calculates the specular reflection component using the Phong reflection model.
     * @param intersection the processed intersection cache
     * @return the specular reflection Double3 triad
     */
    private Double3 calcSpecular(Intersection intersection) {
        // r = l - 2 * (l ∙ n) * n
        Vector r = intersection.l.subtract(intersection.n.scale(2 * intersection.nl)).normalize();

        // minusVR = -v ∙ r
        double minusVR = primitives.Util.alignZero(-intersection.v.dotProduct(r));
        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        return intersection.geometry.getMaterial().kS.scale(
                Math.pow(minusVR, intersection.geometry.getMaterial().nShininess)
        );
    }
}