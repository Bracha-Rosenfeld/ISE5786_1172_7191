package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Point}.
 * @author Your Name
 */
class PointTests {

    /** Delta value for accuracy when comparing double values[cite: 32]. */
    private static final double DELTA = 1e-6;

    /** Error message for wrong distance calculation. */
    private static final String ERROR_DISTANCE = "ERROR: Distance calculation is wrong";

    /** Error message for expected exception. */
    private static final String ERROR_EXCEPTION = "ERROR: Expected exception was not thrown";

    /** First point for tests. */
    private static final Point P1 = new Point(1, 2, 3);

    /** Second point for tests. */
    private static final Point P2 = new Point(2, 4, 6);

    /** Third point for tests. */
    private static final Point P3 = new Point(2, 4, 5);

    /**
     * Test method for {@link Point#subtract(Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Simple subtraction returning a valid vector
        assertEquals(new Vector(1, 2, 3), P2.subtract(P1), "ERROR: Point - Point does not work correctly");

        // =============== Boundary Values Tests ==================

        // BV01: Subtracting a point from itself must throw an exception due to Zero Vector [cite: 34, 47]
        assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Point#add(Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Simple addition of a vector to a point
        assertEquals(P2, P1.add(new Vector(1, 2, 3)), "ERROR: Point + Vector does not work correctly");

        // =============== Boundary Values Tests ==================
        // No specific boundary values for Point addition in this context.
    }

    /**
     * Test method for {@link Point#distanceSquared(Point)}.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Squared distance between two different points
        assertEquals(9.0, P1.distanceSquared(P3), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================

        // BV01: Squared distance from a point to itself
        assertEquals(0.0, P1.distanceSquared(P1), DELTA, ERROR_DISTANCE);
    }

    /**
     * Test method for {@link Point#distance(Point)}.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Distance between two different points
        assertEquals(3.0, P1.distance(P3), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================

        // BV01: Distance from a point to itself
        assertEquals(0.0, P1.distance(P1), DELTA, ERROR_DISTANCE);
    }
}