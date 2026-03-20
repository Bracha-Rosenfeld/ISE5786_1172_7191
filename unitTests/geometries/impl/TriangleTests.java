package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for class {@link Triangle}.
 */
class TriangleTests {

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Triangle triangle = new Triangle(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0));
        Vector expectedNormal = new Vector(1, 1, 1).normalize();

        // ============ Equivalence Partitions Tests ==============
        // EP01: Normal at a general point on the triangle
        Point pointOnTriangle = new Point(1d/3, 1d/3, 1d/3); // Center of the triangle
        assertEquals(expectedNormal, triangle.getNormal(pointOnTriangle),
                "ERROR: Triangle normal calculation is wrong");
    }
}