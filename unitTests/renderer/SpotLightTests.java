package renderer;


import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link SpotLight} class.
 * @author פרויקט מבוא להנדסת תוכנה
 */
class SpotLightTests {

    /**
     * Test method for {@link SpotLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        SpotLight light = new SpotLight(new Color(1000, 1000, 1000), new Point(0, 0, 0), new Vector(0, 0, 1))
                .setKc(1).setKl(0).setKq(0);

        // TC01: Point exactly in the direction of the spot beam (dot product = 1)
        Point p1 = new Point(0, 0, 10);
        assertEquals(new Color(1000, 1000, 1000), light.getIntensity(p1),
                "Wrong intensity directly in front of spotlight");

        // TC02: Point behind the spotlight (dot product <= 0)
        Point p2 = new Point(0, 0, -10);
        assertEquals(Color.BLACK, light.getIntensity(p2),
                "Wrong intensity behind the spotlight (should be black)");
    }

    /**
     * Test method for {@link SpotLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        SpotLight light = new SpotLight(new Color(100, 150, 200), new Point(0, 0, 0), new Vector(1, 0, 0));
        Point p = new Point(0, 0, 10);

        // TC01: Direction for SpotLight is exactly the same as PointLight (from position to point)
        assertEquals(new Vector(0, 0, 1), light.getL(p),
                "Wrong direction for SpotLight");
    }
}