package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a directional light source (e.g., the sun).
 * The light has a fixed direction and uniform intensity everywhere,
 * with no attenuation over distance.
 * * @author פרויקט מבוא להנדסת תוכנה
 */
public class DirectionalLight extends Light implements LightSource {

    /** The direction of the light */
    private final Vector _direction;

    /**
     * Constructs a directional light with given intensity and direction.
     * The direction vector is normalized upon initialization.
     *
     * @param intensity the intensity color of the light
     * @param direction the direction vector of the light
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        _direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return super.getIntensity();
    }

    @Override
    public Vector getL(Point p) {
        return _direction;
    }
}