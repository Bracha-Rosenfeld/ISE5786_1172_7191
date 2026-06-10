package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface for external light sources in a scene.
 * Defines methods for calculating light intensity and direction at a specific point.
 * * @author פרויקט מבוא להנדסת תוכנה
 */
public interface LightSource {

    /**
     * Calculates the light intensity at a specific point in the scene.
     *
     * @param p the illuminated point
     * @return the color intensity of the light at the given point
     */
    Color getIntensity(Point p);

    /**
     * Calculates the normalized direction vector from the light source to the illuminated point.
     *
     * @param p the illuminated point
     * @return the normalized direction vector (L)
     */
    Vector getL(Point p);
    /**
     * Gets the distance from the light source to a given point.
     *
     * @param p the point to calculate the distance to
     * @return the distance
     */
    double getDistance(Point p);
}