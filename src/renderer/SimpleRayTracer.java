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
 * A simple ray tracer implementation.
 */
class SimpleRayTracer extends RayTracerBase {

    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

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
     * מתודת עזר לחישוב החיתוך הקרוב ביותר לראשית הקרן
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        return ray.findClosestIntersection(intersections);
    }

    private Color calcColor(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, ray, k);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, ray.direction(), level, k));
    }

    private Color calcLocalEffects(Intersection intersection, Ray ray, Double3 k) {
        if (!preprocessIntersection(intersection, ray.direction())) {
            return _scene.ambientLight.getIntensity().add(intersection.geometry.getEmission());
        }

        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {

                // חישוב מקדם השקיפות (כמה אור עובר דרך הגופים שבדרך)
                Double3 ktr = transparency(intersection, lightSource);

                // אם עוצמת האור שעוברת כפול המקדם הכללי גדולה מהמינימום, נוסיף אותה לצבע
                if (!ktr.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
                    // מכפילים את עוצמת האור המקורית במקדם השקיפות (ktr) שמצאנו
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);

                    color = color.add(lightIntensity.scale(calcDiffuse(intersection)))
                            .add(lightIntensity.scale(calcSpecular(intersection)));
                }
            }
        }
        return color;
    }

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

    private Color calcGlobalEffect(Ray ray, int level, Double3 kx, Double3 kkx) {
        Intersection closest = findClosestIntersection(ray);
        if (closest == null) {
            return _scene.background.scale(kx);
        }
        return calcColor(closest, ray, level - 1, kkx).scale(kx);
    }

    private Ray constructReflectedRay(Point point, Vector v, Vector n) {
        double vn = v.dotProduct(n);
        if (primitives.Util.isZero(vn)) {
            return new Ray(point, v, n);
        }
        Vector r = v.subtract(n.scale(2 * vn)).normalize();
        return new Ray(point, r, n);
    }

    private Ray constructRefractedRay(Point point, Vector v, Vector n) {
        return new Ray(point, v, n);
    }

    /**
     * פונקציה שמחשבת כמה אור מצליח להגיע לנקודה ממקור האור
     * (אור שעובר דרך גופים שקופים מונחת בהתאם למקדם השקיפות שלהם)
     */
    private Double3 transparency(Intersection intersection, LightSource lightSource) {
        Vector lightDirection = lightSource.getL(intersection.point).scale(-1);
        Ray lightRay = new Ray(intersection.point, lightDirection, intersection.n);

        double lightDistance = lightSource.getDistance(intersection.point);
        List<Intersection> intersections = _scene.geometries.calcIntersections(lightRay, lightDistance);

        if (intersections == null) {
            return Double3.ONE; // אין חסימות, כל האור עובר
        }

        Double3 ktr = Double3.ONE;
        for (Intersection geo : intersections) {
            ktr = ktr.product(geo.geometry.getMaterial().kT);
            if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                return Double3.ZERO; // האור נחסם לגמרי
            }
        }

        return ktr;
    }

    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.geometry.getMaterial().kD.scale(Math.abs(intersection.nl));
    }

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