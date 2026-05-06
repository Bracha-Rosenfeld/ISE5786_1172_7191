package lighting;

import primitives.Color;

/**
 * Represents ambient lighting in the scene.
 * The ambient light is a fixed-intensity and fixed-color light source
 * that affects all objects in the scene equally.
 */
public final class AmbientLight {

    /**
     * The intensity of the ambient light
     */
    private final Color _intensity;

    /**
     * Constant representing no ambient light (black color)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an ambient light with the specified color intensity.
     *
     * @param intensity the color intensity of the light
     */
    public AmbientLight(Color intensity) {
        _intensity = intensity;
    }

    /**
     * Gets the intensity of the ambient light.
     *
     * @return the color intensity
     */
    public Color getIntensity() {
        return _intensity;
    }
}