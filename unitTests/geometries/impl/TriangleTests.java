package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

/**
 * Unit tests for class {@link Triangle}.
 * @author Your Name
 */
class TriangleTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for wrong normal calculation */
    private static final String ERROR_NORMAL = "ERROR: getNormal() wrong result";

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // Arrange
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Triangle triangle = new Triangle(p1, p2, p3);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Test getNormal for a point inside the triangle
        Point p = new Point(1d/3, 1d/3, 1d/3);
        Vector result = triangle.getNormal(p);

        // 1. Ensure the normal is normalized (length = 1)
        assertEquals(1, result.length(), DELTA, "ERROR: Triangle normal is not normalized");

        // 2. Ensure the normal is orthogonal to all edges
        assertEquals(0, result.dotProduct(p2.subtract(p1)), DELTA, ERROR_NORMAL);
        assertEquals(0, result.dotProduct(p3.subtract(p2)), DELTA, ERROR_NORMAL);

        // 3. Optional: check against expected value
        Vector expected = new Vector(1, 1, 1).normalize();
        assertEquals(expected, result, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // No specific boundary cases for getNormal in Triangle according to requirements
    }

    /**
     * Test method for {@link Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(new Point(0, 1, 0), new Point(1, -1, 0), new Point(-1, -1, 0));

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects inside the triangle (1 point)
        List<Point> resultEP01 = triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1)));
        assertNotNull(resultEP01, "Must be not null");
        assertEquals(1, resultEP01.size(), "Wrong number of points");
        assertEquals(new Point(0, 0, 0), resultEP01.get(0), "Ray crosses inside triangle");

        // EP02: Ray intersects outside against an edge (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(2, 0, -1), new Vector(0, 0, 1))),
                "Ray crosses outside against edge");

        // EP03: Ray intersects outside against a vertex (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 2, -1), new Vector(0, 0, 1))),
                "Ray crosses outside against vertex");

        // =============== Boundary Values Tests ==================

        // BV01: Ray intersects on an edge (0 points)
        // Midpoint of edge (0,1,0) to (1,-1,0) is (0.5, 0, 0)
        assertNull(triangle.findIntersections(new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1))),
                "Ray crosses on edge");

        // BV02: Ray intersects in a vertex (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 1, -1), new Vector(0, 0, 1))),
                "Ray crosses in vertex");

        // BV03: Ray intersects on an edge continuation (0 points)
        // Continuing the edge (0,1,0) to (1,-1,0) further down to y=-3 gives x=2
        assertNull(triangle.findIntersections(new Ray(new Point(2, -3, -1), new Vector(0, 0, 1))),
                "Ray crosses on edge continuation");
    }
}
