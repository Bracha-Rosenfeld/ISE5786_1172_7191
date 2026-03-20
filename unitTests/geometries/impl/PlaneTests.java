package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for class {@link Plane}.
 * The tests verify plane creation and normal calculation.
 */
class PlaneTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for expected exception */
    private static final String ERROR_EXCEPTION = "ERROR: Expected exception was not thrown";
    /** Error message for wrong normal calculation */
    private static final String ERROR_NORMAL = "ERROR: Normal calculation is wrong";

    /** First point for tests */
    private static final Point P1 = new Point(0, 0, 1);
    /** Second point for tests */
    private static final Point P2 = new Point(1, 0, 0);
    /** Third point for tests */
    private static final Point P3 = new Point(0, 1, 0);

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Correct plane construction with 3 points not on the same line
        assertDoesNotThrow(() -> new Plane(P1, P2, P3),
                "ERROR: Failed constructing a correct plane");

        // =============== Boundary Values Tests ==================
        // BV01: Two points coincide
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(P1, P1, P3), ERROR_EXCEPTION);

        // BV02: Three points are on the same line
        // Points (1,2,3), (2,4,6), (3,6,9) are on the same line starting from origin
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(1, 2, 3), new Point(2, 4, 6), new Point(3, 6, 9)),
                ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormalPoint() {
        Plane plane = new Plane(P1, P2, P3);
        // The expected normal is the normalized vector of (1, 1, 1)
        Vector expectedNormal = new Vector(1, 1, 1).normalize();

        // ============ Equivalence Partitions Tests ==============
        // EP01: Normal at a general point on the plane (not the reference point)
        Point pointOnPlane = new Point(0.5, 0.5, 0); // Point between P2 and P3
        assertEquals(expectedNormal, plane.getNormal(pointOnPlane), ERROR_NORMAL);
        assertEquals(1, plane.getNormal(pointOnPlane).length(), DELTA, "ERROR: Normal is not normalized");

        // =============== Boundary Values Tests ==================
        // BV01: Normal at the reference point of the plane (P1)
        assertEquals(expectedNormal, plane.getNormal(P1), ERROR_NORMAL);
    }
}