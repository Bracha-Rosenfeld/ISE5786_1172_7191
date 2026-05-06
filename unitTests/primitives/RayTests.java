package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

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

    /**
     * Test method for {@link Ray#getPoint(double)}.
     */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 0, 0), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==============
        // EP01: t is positive (t > 0)
        assertEquals(new Point(2, 0, 0), ray.getPoint(1), "ERROR: getPoint() with positive t yields wrong result");

        // EP02: t is negative (t < 0)
        assertEquals(new Point(0, 0, 0), ray.getPoint(-1), "ERROR: getPoint() with negative t yields wrong result");

        // =============== Boundary Values Tests ==================
        // BV01: t is zero (t = 0)
        assertEquals(new Point(1, 0, 0), ray.getPoint(0), "ERROR: getPoint() with t=0 yields wrong result");
    }

    /**
     * Test method for {@link primitives.Ray#findClosestPoint(List)}.
     */
    @Test
    void testFindClosestPoint() {
        Ray ray = new Ray(new Point(0, 0, 10), new Vector(1, 10, -100));
        Point p1 = new Point(1, 1, -100);
        Point p2 = new Point(-1, 1, -99);
        Point p3 = new Point(0, 2, -10);

        // ============ Equivalence Partitions Tests ==============
        // TC01: The closest point is in the middle of the list
        List<Point> list1 = List.of(p1, p3, p2);
        assertEquals(p3, ray.findClosestPoint(list1), "Closest point should be the middle one");

        // =============== Boundary Values Tests ==================
        // TC10: The list is null
        assertNull(ray.findClosestPoint(null), "Should return null for a null list");

        // TC11: The closest point is the first element in the list
        List<Point> list2 = List.of(p3, p1, p2);
        assertEquals(p3, ray.findClosestPoint(list2), "Closest point should be the first one");

        // TC12: The closest point is the last element in the list
        List<Point> list3 = List.of(p1, p2, p3);
        assertEquals(p3, ray.findClosestPoint(list3), "Closest point should be the last one");
    }
}