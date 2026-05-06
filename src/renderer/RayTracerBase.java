package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Base abstract class for ray tracers.
 */
abstract class RayTracerBase {

    /**
     * The scene to be traced
     */
    protected final Scene _scene;

    /**
     * Constructs a ray tracer base with the given scene.
     *
     * @param scene the scene to render
     */
    RayTracerBase(Scene scene) {
        _scene = scene;
    }

    /**
     * Traces a ray and calculates the color at the intersection point.
     *
     * @param ray the ray to trace
     * @return the calculated color
     */
    abstract Color traceRay(Ray ray);
}