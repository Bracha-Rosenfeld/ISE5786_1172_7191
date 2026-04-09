package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

/**
 * Unit tests for class {@link Sphere}.
 * @author Your Name
 */
class SphereTests {

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for wrong normal calculation
     */
    private static final String ERROR_NORMAL = "ERROR: getNormal() wrong result";

    /**
     * Point (2,1,1) used as center for the sphere to avoid (0,0,0)
     */
    private static final Point P211 = new Point(2, 1, 1);

    /**
     * Sphere used in most tests (Center: 2,1,1, Radius: 1)
     */
    private static final Sphere SPHERE = new Sphere(P211, 1d);

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // Arrange
        Sphere sphere = new Sphere(new Point(1, 1, 1), 1.0);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Test getNormal for a point on the sphere surface
        Point p = new Point(1, 1, 2);
        Vector normal = sphere.getNormal(p);

        // 1. Ensure the normal is normalized (length is 1)
        assertEquals(1, normal.length(), DELTA, "ERROR: Sphere normal is not normalized");

        // 2. Ensure the result is the expected vector
        Vector expected = new Vector(0, 0, 1);
        assertEquals(expected, normal, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // No special boundary cases for Sphere.getNormal(Point) per requirements.
    }

    /**
     * Test method for {@link Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Point p01 = new Point(0, 1, 1);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray's line is outside the sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(p01, new Vector(1, 1, 0))), "Ray's line out of sphere");

        // EP02: Ray starts before and crosses the sphere (2 points)
        Point p1 = new Point(1.0651530771650466, 1.355051025721682, 1);
        Point p2 = new Point(2.53484692283495, 1.844948974278318, 1);
        List<Point> resultEP2 = SPHERE.findIntersections(new Ray(p01, new Vector(3, 1, 0)));
        assertNotNull(resultEP2, "Must be not null");
        assertEquals(2, resultEP2.size(), "Wrong number of points");
        // The points must be ordered by distance from the ray origin
        assertEquals(List.of(p1, p2), resultEP2, "Ray crosses sphere");

        // EP03: Ray starts inside the sphere (1 point)
        List<Point> resultEP3 = SPHERE.findIntersections(new Ray(new Point(1.5, 1, 1), new Vector(1, 0, 0)));
        assertNotNull(resultEP3, "Must be not null");
        assertEquals(1, resultEP3.size(), "Wrong number of points");
        assertEquals(List.of(new Point(3, 1, 1)), resultEP3, "Ray starts inside");

        // EP04: Ray starts after the sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(4, 1, 1), new Vector(1, 0, 0))), "Ray starts after sphere");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // BV11: Ray starts at sphere and goes inside (1 point)
        List<Point> resultBV11 = SPHERE.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0.5, 0)));
        assertNotNull(resultBV11, "Must be not null");
        assertEquals(1, resultBV11.size(), "Wrong number of points");

        // BV12: Ray starts at sphere and goes outside (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(1, 1, 1), new Vector(-1, -0.5, 0))), "Ray starts at sphere and goes outside");

        // **** Group 2: Ray's line goes through the center
        // BV21: Ray starts before the sphere (2 points)
        List<Point> resultBV21 = SPHERE.findIntersections(new Ray(new Point(0, 1, 1), new Vector(1, 0, 0)));
        assertNotNull(resultBV21, "Must be not null");
        assertEquals(2, resultBV21.size(), "Wrong number of points");

        // BV22: Ray starts at sphere and goes inside (1 point)
        List<Point> resultBV22 = SPHERE.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0)));
        assertNotNull(resultBV22, "Must be not null");
        assertEquals(1, resultBV22.size(), "Wrong number of points");

        // BV23: Ray starts inside (1 point)
        List<Point> resultBV23 = SPHERE.findIntersections(new Ray(new Point(1.5, 1, 1), new Vector(1, 0, 0)));
        assertNotNull(resultBV23, "Must be not null");
        assertEquals(1, resultBV23.size(), "Wrong number of points");

        // BV24: Ray starts at the center (1 point)
        List<Point> resultBV24 = SPHERE.findIntersections(new Ray(P211, new Vector(1, 0, 0)));
        assertNotNull(resultBV24, "Must be not null");
        assertEquals(1, resultBV24.size(), "Wrong number of points");

        // BV25: Ray starts at sphere and goes outside (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(3, 1, 1), new Vector(1, 0, 0))), "Ray starts at sphere and goes outside");

        // BV26: Ray starts after sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(4, 1, 1), new Vector(1, 0, 0))), "Ray starts after sphere");

        // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
        // BV31: Ray starts before the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(1, 2, 1), new Vector(1, 0, 0))), "Tangent line, ray before tangent point");

        // BV32: Ray starts at the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(2, 2, 1), new Vector(1, 0, 0))), "Tangent line, ray at tangent point");

        // BV33: Ray starts after the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(3, 2, 1), new Vector(1, 0, 0))), "Tangent line, ray after tangent point");

        // **** Group 4: Special cases
        // BV41: Ray's line is outside, ray is orthogonal to ray start to sphere's center line
        assertNull(SPHERE.findIntersections(new Ray(new Point(0, 1, 1), new Vector(0, 1, 0))), "Orthogonal to center line, outside");

        // BV42: Ray's starts inside, ray is orthogonal to ray start to sphere's center line
        List<Point> resultBV42 = SPHERE.findIntersections(new Ray(new Point(1.5, 1, 1), new Vector(0, 1, 0)));
        assertNotNull(resultBV42, "Must be not null");
        assertEquals(1, resultBV42.size(), "Wrong number of points");
    }
}