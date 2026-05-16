package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * Represents ambient lighting in the scene.
 * The ambient light is a fixed-intensity and fixed-color light source
 * that affects all objects in the scene equally.
 * * @author פרויקט מבוא להנדסת תוכנה
 */
public class AmbientLight extends Light {

    /**
     * Constant representing no ambient light (black color)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK, Double3.ZERO);

    /**
     * Default constructor that initializes the ambient light to black (no light).
     */
    public AmbientLight() {
        super(Color.BLACK);
    }

    /**
     * Constructs an ambient light source with a given intensity and attenuation factor.
     * Calculates the final ambient light intensity (Ia = I0 * ka).
     *
     * @param i0 the original intensity color of the light
     * @param ka the attenuation factor represented as a Double3 triad
     */
    public AmbientLight(Color i0, Double3 ka) {
        super(i0.scale(ka));
    }

    /**
     * Constructs an ambient light source with a given intensity and attenuation factor.
     * Calculates the final ambient light intensity (Ia = I0 * ka).
     *
     * @param i0 the original intensity color of the light
     * @param ka the attenuation factor represented as a double scalar
     */
    public AmbientLight(Color i0, double ka) {
        super(i0.scale(ka));
    }

    /**
     * Constructs an ambient light with the specified calculated color intensity.
     *
     * @param ia the calculated intensity color of the ambient light
     */
    public AmbientLight(Color ia) {
        super(ia);
    }
}