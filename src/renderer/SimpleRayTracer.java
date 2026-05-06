package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * A simple ray tracer implementation.
 * It computes the color based on the closest intersection point.
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a simple ray tracer for the given scene.
     * @param scene the scene to render
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    Color traceRay(Ray ray) {
        // Find the intersections of the ray with the scene geometries
        List<Point> intersections = _scene.geometries.findIntersections(ray);

        // If there are no intersections, return the background color
        if (intersections == null || intersections.isEmpty()) {
            return _scene.background;
        }

        // Find the closest intersection point
        Point closestPoint = ray.findClosestPoint(intersections);

        // Return the color computed at the intersection point
        return calcColor(closestPoint);
    }

    /**
     * Calculates the color at a specific intersection point.
     * At this stage, it only returns the ambient light intensity.
     * @param point the intersection point
     * @return the calculated color
     */
    private Color calcColor(Point point) {
        return _scene.ambientLight.getIntensity();
    }
}