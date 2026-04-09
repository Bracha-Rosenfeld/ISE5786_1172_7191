package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

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

    /**
     * Test method for {@link Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============
        // EP01: Ray's line intersects the plane (1 point)
        Ray rayEP1 = new Ray(new Point(1, 0, 0), new Vector(-1, 0, 1));
        List<Point> resultEP1 = plane.findIntersections(rayEP1);
        assertNotNull(resultEP1, "Must be not null");
        assertEquals(1, resultEP1.size(), "Wrong number of points");
        assertEquals(new Point(0, 0, 1), resultEP1.get(0), "Ray crosses plane");

        // EP02: Ray's line does not intersect the plane (0 points)
        Ray rayEP2 = new Ray(new Point(1, 0, 2), new Vector(1, 0, 1));
        assertNull(plane.findIntersections(rayEP2), "Ray's line is outside the plane");


        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray is parallel to the plane
        // BV01: Ray is included in the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 1, 0))),
                "Ray is included in the plane");

        // BV02: Ray is not included in the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(0, 1, 0))),
                "Ray is parallel to the plane");

        // **** Group 2: Ray is orthogonal to the plane
        // BV03: Ray starts before the plane
        Ray rayBV3 = new Ray(new Point(1, 1, 0), new Vector(0, 0, 1));
        List<Point> resultBV3 = plane.findIntersections(rayBV3);
        assertNotNull(resultBV3, "Must be not null");
        assertEquals(1, resultBV3.size(), "Wrong number of points");
        assertEquals(new Point(1, 1, 1), resultBV3.get(0), "Ray is orthogonal and starts before plane");

        // BV04: Ray starts in the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 0, 1))),
                "Ray is orthogonal and starts in plane");

        // BV05: Ray starts after the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(0, 0, 1))),
                "Ray is orthogonal and starts after plane");

        // **** Group 3: Ray is neither orthogonal nor parallel
        // BV06: Ray begins on the plane (but not at the reference point)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                "Ray begins on the plane");

        // BV07: Ray begins at the reference point of the plane
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 1))),
                "Ray begins at the reference point");
    }
}