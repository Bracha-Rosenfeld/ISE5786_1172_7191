package renderer;


import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link PointLight} class.
 * @author פרויקט מבוא להנדסת תוכנה
 */
class PointLightTests {

    /**
     * Test method for {@link PointLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        PointLight light = new PointLight(new Color(1000, 1000, 1000), new Point(0, 0, 0))
                .setKc(1).setKl(0).setKq(0);
        Point p = new Point(0, 0, 10);

        // TC01: Point light without attenuation (kC=1, kL=0, kQ=0)
        assertEquals(new Color(1000, 1000, 1000), light.getIntensity(p),
                "Wrong intensity with no attenuation");

        // TC02: Point light with constant and linear attenuation
        light.setKc(1).setKl(0.1).setKq(0);
        // Distance is 10. Attenuation = 1 + 0.1*10 = 2. Expected intensity = 1000 / 2 = 500
        assertEquals(new Color(500, 500, 500), light.getIntensity(p),
                "Wrong intensity with linear attenuation");
    }

    /**
     * Test method for {@link PointLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        PointLight light = new PointLight(new Color(100, 150, 200), new Point(0, 0, 0));
        Point p = new Point(0, 0, 10);

        // TC01: Point light direction is from light position to the point
        assertEquals(new Vector(0, 0, 1), light.getL(p),
                "Wrong direction for PointLight");
    }
}
