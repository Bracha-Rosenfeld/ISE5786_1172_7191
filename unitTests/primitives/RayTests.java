package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Ray}.
 * The tests verify the correct functionality of the ray creation.
 */
class RayTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /**
     * Test method for {@link Ray#Ray(Point, Vector)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Verify that the constructor normalizes the direction vector
        Vector direction = new Vector(0, 3, 4); // Length is 5
        Ray ray = new Ray(new Point(1, 2, 3), direction);

        assertEquals(1, ray.direction().length(), DELTA,
                "ERROR: Ray direction is not normalized");

        assertEquals(new Vector(0, 0.6, 0.8), ray.direction(),
                "ERROR: Ray direction is not correctly normalized");
    }
}