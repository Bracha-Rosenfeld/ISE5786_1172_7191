package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for class {@link Sphere}.
 * @author Your Name
 */
class SphereTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for wrong normal calculation */
    private static final String ERROR_NORMAL = "ERROR: getNormal() wrong result";

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // Arrange
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1.0);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Test getNormal for a point on the sphere surface
        Point p = new Point(0, 0, 1);
        Vector normal = sphere.getNormal(p);

        // 1. Ensure the normal is normalized (length is 1)
        assertEquals(1, normal.length(), DELTA, "ERROR: Sphere normal is not normalized");

        // 2. Ensure the result is the expected vector
        Vector expected = new Vector(0, 0, 1);
        assertEquals(expected, normal, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // No special boundary cases for Sphere.getNormal(Point) per requirements.
    }
}