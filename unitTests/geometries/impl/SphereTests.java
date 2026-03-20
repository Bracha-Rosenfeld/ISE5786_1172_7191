package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for class {@link Sphere}.
 */
class SphereTests {

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Normal at a general point on the sphere
        assertEquals(new Vector(0, 0, 1), sphere.getNormal(new Point(0, 0, 1)),
                "ERROR: Sphere normal calculation is wrong");
    }
}