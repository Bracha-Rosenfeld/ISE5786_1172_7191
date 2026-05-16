package lighting;

import primitives.Color;

/**
 * Abstract base class for all light components in a scene.
 * Stores the basic intensity of the light source.
 * * @author פרויקט מבוא להנדסת תוכנה
 */
abstract class Light {
    /** The intensity color of the light */
   final protected Color _intensity;

    /**
     * Protected constructor to initialize the light intensity.
     * * @param intensity the intensity color of the light
     */
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Getter for the original light intensity.
     * * @return the intensity color of the light
     */
    public Color getIntensity() {
        return _intensity;
    }
}