package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for class {@link Plane}.
 */
class PlaneTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for wrong normal calculation */
    private static final String ERROR_NORMAL = "ERROR: getNormal() wrong result";
    /** Error message for failing to throw exception */
    private static final String ERROR_EXCEPTION = "ERROR: Expected exception was not thrown";

    /** Reference point for plane tests */
    private static final Point P1 = new Point(0, 0, 1);
    /** Second point for plane tests */
    private static final Point P2 = new Point(1, 0, 0);
    /** Third point for plane tests */
    private static final Point P3 = new Point(0, 1, 0);

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Correct plane construction with 3 non-collinear points
        assertDoesNotThrow(() -> new Plane(P1, P2, P3), "ERROR: Failed to construct a valid plane");

        // =============== Boundary Values Tests ==================

        // BV01: Two points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P3), ERROR_EXCEPTION);

        // BV02: Three points are on the same line
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
                ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Plane plane = new Plane(P1, P2, P3);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Normal at a general point on the plane (not the reference point)
        // Verify the normal is normalized (length = 1)
        Vector normal = plane.getNormal(new Point(0.5, 0.5, 0));
        assertEquals(1, normal.length(), DELTA, "ERROR: Normal is not normalized");

        // Verify the normal is orthogonal to an edge on the plane
        Vector edge = P2.subtract(P1);
        assertEquals(0, normal.dotProduct(edge), DELTA, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: Normal at the reference point of the plane
        assertDoesNotThrow(() -> plane.getNormal(P1), "ERROR: getNormal at reference point failed");
        assertEquals(normal, plane.getNormal(P1), ERROR_NORMAL);
    }
}