package renderer;

import geometries.api.Intersectable;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Vector;
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
    /**
     * Pre-processes intersection data by caching the normal, view vector, and their dot product.
     * @param intersection the intersection to process
     * @param v the view ray direction vector
     * @return true if the dot product nv is not zero, false otherwise
     */
    protected boolean preprocessIntersection(Intersectable.Intersection intersection, Vector v) {
        intersection.n = intersection.geometry.getNormal(intersection.point);
        intersection.v = v;
        intersection.nv = primitives.Util.alignZero(intersection.n.dotProduct(v));
        return intersection.nv != 0;
    }

    /**
     * Pre-processes light source data for the intersection by caching the light vector and nl dot product.
     * @param intersection the intersection to process
     * @param lightSource the external light source
     * @return true if the light and camera are on the same side of the surface
     */
    protected boolean preprocessLightSource(Intersectable.Intersection intersection, LightSource lightSource) {
        intersection.l = lightSource.getL(intersection.point);
        intersection.nl = primitives.Util.alignZero(intersection.n.dotProduct(intersection.l));
        return primitives.Util.compareSign(intersection.nv, intersection.nl);
    }
}