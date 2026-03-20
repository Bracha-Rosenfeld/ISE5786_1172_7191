package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link Tube}.
 */
class TubeTests {

    /**
     * Test method for {@link Tube#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Tube tube = new Tube(1, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));
        Vector expectedNormal = new Vector(1, 0, 0);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Normal at a point opposite the axis ray
        assertEquals(expectedNormal, tube.getNormal(new Point(1, 0, 1)),
                "ERROR: Tube normal is wrong when point is opposite the axis ray");

        // EP02: Normal at a point opposite the back of the axis ray
        assertEquals(expectedNormal, tube.getNormal(new Point(1, 0, -1)),
                "ERROR: Tube normal is wrong when point is opposite the back of the axis ray");

        // =============== Boundary Values Tests ==================
        // BV01: Normal at a point opposite the head of the axis ray (the origin of the ray)
        assertEquals(expectedNormal, tube.getNormal(new Point(1, 0, 0)),
                "ERROR: Tube normal is wrong when point is opposite the head of the axis ray");
    }
}