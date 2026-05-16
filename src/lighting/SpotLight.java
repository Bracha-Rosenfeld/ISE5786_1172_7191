package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a spot light source (e.g., a flashlight).
 * It is a point light with a specific direction of illumination and optional narrow beam support.
 * * @author פרויקט מבוא להנדסת תוכנה
 */
public class SpotLight extends PointLight {

    /** The direction of the spotlight beam */
    private final Vector _direction;

    /** The concentration factor for a narrow beam spotlight (bonus functionality) */
    private double _narrowBeam = 1.0;

    /**
     * Constructs a spot light with given intensity, position, and direction.
     * The direction vector is normalized upon initialization.
     *
     * @param intensity the intensity color of the light
     * @param position  the position of the light source
     * @param direction the direction vector of the light beam
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        _direction = direction.normalize();
    }

    /**
     * Sets the narrow beam concentration factor for the spotlight beam (bonus feature).
     *
     * @param narrowBeam the concentration factor power
     * @return the current SpotLight object (for chaining)
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this._narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double dirDotL = _direction.dotProduct(l);

        // If the point is behind the spotlight, there is no light contribution
        if (dirDotL <= 0) {
            return Color.BLACK;
        }

        // Apply the narrow beam factor using Math.pow (if _narrowBeam is 1, it has no effect)
        double factor = Math.pow(dirDotL, _narrowBeam);

        // Multiply the base point light intensity by the calculated factor
        return super.getIntensity(p).scale(factor);
    }
}