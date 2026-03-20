package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for class {@link Tube}.
 * @author Your Name
 */
class TubeTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for wrong normal calculation */
    private static final String ERROR_NORMAL = "ERROR: getNormal() wrong result";

    /**
     * Test method for {@link Tube#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1.0, axisRay);
        Vector expectedNormal = new Vector(1, 0, 0);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Test getNormal at a point on the tube's surface against the axis ray
        Vector n1 = tube.getNormal(new Point(1, 0, 1));
        assertEquals(1, n1.length(), DELTA, "ERROR: Tube normal is not normalized");
        assertEquals(0, n1.dotProduct(axisRay.direction()), DELTA, "ERROR: Normal is not orthogonal to the axis");
        assertEquals(expectedNormal, n1, ERROR_NORMAL);

        // EP02: Test getNormal at a point on the tube's surface against the back of the axis ray
        Vector n2 = tube.getNormal(new Point(1, 0, -1));
        assertEquals(1, n2.length(), DELTA, "ERROR: Tube normal is not normalized");
        assertEquals(0, n2.dotProduct(axisRay.direction()), DELTA, "ERROR: Normal is not orthogonal to the axis");
        assertEquals(expectedNormal, n2, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: Test getNormal at a point on the tube's surface against the axis ray's head
        Vector n3 = tube.getNormal(new Point(1, 0, 0));
        assertEquals(1, n3.length(), DELTA, "ERROR: Tube normal is not normalized");
        assertEquals(0, n3.dotProduct(axisRay.direction()), DELTA, "ERROR: Normal is not orthogonal to the axis");
        assertEquals(expectedNormal, n3, ERROR_NORMAL);
    }
}