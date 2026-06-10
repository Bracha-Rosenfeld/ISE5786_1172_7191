package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a point light source (e.g., a light bulb).
 * The light emits from a specific position in all directions,
 * and its intensity attenuates over distance.
 * * @author פרויקט מבוא להנדסת תוכנה
 */
public class PointLight extends Light implements LightSource {

    /** The position of the light source */
    private final Point _position;

    /** Constant attenuation factor */
    private double _kC = 1.0;
    /** Linear attenuation factor */
    private double _kL = 0.0;
    /** Quadratic attenuation factor */
    private double _kQ = 0.0;

    /**
     * Constructs a point light with given intensity and position.
     *
     * @param intensity the intensity color of the light
     * @param position  the position of the light source in the scene
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        _position = position;
    }

    /**
     * Sets the constant attenuation factor.
     *
     * @param kC the constant attenuation factor
     * @return the current PointLight object (for chaining)
     */
    public PointLight setKc(double kC) {
        _kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     *
     * @param kL the linear attenuation factor
     * @return the current PointLight object (for chaining)
     */
    public PointLight setKl(double kL) {
        _kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     *
     * @param kQ the quadratic attenuation factor
     * @return the current PointLight object (for chaining)
     */
    public PointLight setKq(double kQ) {
        _kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double d = p.distance(_position);
        double attenuation = _kC + _kL * d + _kQ * d * d;
        // Using scale(1.0 / attenuation) instead of reduce to handle double values perfectly
        return super.getIntensity().scale(1.0 / attenuation);
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

    @Override
    public double getDistance(Point p) {
        return _position.distance(p);
    }
}