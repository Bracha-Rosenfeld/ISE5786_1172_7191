package renderer;

import geometries.api.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
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
        // Find the intersections of the ray with the scene geometries using the new NVI method
        var intersections = _scene.geometries.calcIntersections(ray);

        // If there are no intersections, return the background color
        if (intersections == null) {
            return _scene.background;
        }

        // Find the closest intersection and calculate its color
        return calcColor(ray.findClosestIntersection(intersections));
    }

    /**
     * Calculates the color at a specific intersection point.
     * Adds the geometry's emission color to the ambient light intensity.
     *
     * @param intersection the intersection object containing the geometry and point
     * @return the calculated color
     */
    private Color calcColor(Intersection intersection) {
        return _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA) // הכפלה במקדם ההנחתה של החומר
                .add(intersection.geometry.getEmission()); // הוספת צבע הפליטה
    }
}