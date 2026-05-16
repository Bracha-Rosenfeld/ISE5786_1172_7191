package renderer;

import lighting.DirectionalLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link DirectionalLight} class.
 * @author פרויקט מבוא להנדסת תוכנה
 */
class DirectionalLightTests {

    /**
     * Test method for {@link DirectionalLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        DirectionalLight light = new DirectionalLight(new Color(100, 150, 200), new Vector(0, 0, -1));
        Point p = new Point(1, 2, 3);

        // TC01: Directional light intensity should be identical everywhere
        assertEquals(new Color(100, 150, 200), light.getIntensity(p),
                "Wrong intensity for DirectionalLight");
    }

    /**
     * Test method for {@link DirectionalLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        DirectionalLight light = new DirectionalLight(new Color(100, 150, 200), new Vector(0, 0, -1));
        Point p = new Point(1, 2, 3);

        // TC01: Directional light direction should be identical everywhere and normalized
        assertEquals(new Vector(0, 0, -1), light.getL(p),
                "Wrong direction for DirectionalLight");
    }
}