package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

/**
 * Unit tests for class {@link Tube}.
 */
class TubeTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;
    /** Error message for wrong normal calculation */
    private static final String ERROR_NORMAL = "ERROR: getNormal() wrong result";

    @Test
    void testGetNormal() {
        Ray axisRay = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        Tube tube = new Tube(1.0, axisRay);
        Vector expectedNormal = new Vector(1, 0, 0);

        // EP01: Test getNormal at a point on the tube's surface
        Vector n1 = tube.getNormal(new Point(2, 2, 4));
        assertEquals(1, n1.length(), DELTA, "ERROR: Tube normal is not normalized");
        assertEquals(0, n1.dotProduct(axisRay.direction()), DELTA, "ERROR: Normal is not orthogonal to the axis");
        assertEquals(expectedNormal, n1, ERROR_NORMAL);

        // BV01: Test getNormal at a point on the tube's surface against the axis ray's head
        Vector n3 = tube.getNormal(new Point(2, 2, 3));
        assertEquals(1, n3.length(), DELTA, "ERROR: Tube normal is not normalized");
        assertEquals(0, n3.dotProduct(axisRay.direction()), DELTA, "ERROR: Normal is not orthogonal to the axis");
        assertEquals(expectedNormal, n3, ERROR_NORMAL);
    }

    /**
     * Test method for {@link Tube#findIntersections(primitives.Ray)}.
     * Contains exactly 35 test cases updated to avoid (0,0,0) [cite: 301-302].
     */
    @Test
    void testFindIntersections() {
        Tube tube = new Tube(1d, new Ray(new Point(1, 1, 1), new Vector(0, 0, 1)));

        // ==========================================================
        // GROUP 1: Orthogonal Rays (90 degrees to the Z-axis)
        // ==========================================================
        // TC11: Outside crossing (2 points)
        List<Point> res11 = tube.findIntersections(new Ray(new Point(3, 1, 1), new Vector(-1, 0, 0)));
        assertNotNull(res11, "Must be not null");
        assertEquals(2, res11.size(), "Orthogonal outside crossing");

        // TC12: Inside crossing (1 point)
        List<Point> res12 = tube.findIntersections(new Ray(new Point(1.5, 1, 1), new Vector(1, 0, 0)));
        assertNotNull(res12, "Must be not null");
        assertEquals(1, res12.size(), "Orthogonal inside crossing");

        // TC13: Origin on axis (1 point)
        List<Point> res13 = tube.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0)));
        assertNotNull(res13, "Must be not null");
        assertEquals(1, res13.size(), "Orthogonal from axis");

        // TC14: Outside missing (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(3, 1, 1), new Vector(0, 1, 0))), "Orthogonal missing");

        // TC15: Outside tangent (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, -1, 1), new Vector(0, 1, 0))), "Orthogonal tangent");

        // TC16: Surface pointing in (1 point)
        List<Point> res16 = tube.findIntersections(new Ray(new Point(2, 1, 1), new Vector(-1, 0, 0)));
        assertNotNull(res16, "Must be not null");
        assertEquals(1, res16.size(), "Orthogonal surface pointing in");

        // TC17: Surface pointing out (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 1, 1), new Vector(1, 0, 0))), "Orthogonal surface out");

        // TC18: Surface tangent (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 1, 1), new Vector(0, 1, 0))), "Orthogonal surface tangent");

        // ==========================================================
        // GROUP 2: Parallel Rays (0 or 180 degrees to the Z-axis)
        // ==========================================================
        // TC21: Parallel outside (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(3, 1, 1), new Vector(0, 0, 1))), "Parallel outside");

        // TC22: Parallel inside (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1.5, 1, 1), new Vector(0, 0, 1))), "Parallel inside");

        // TC23: Parallel on axis (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 0, 1))), "Parallel on axis");

        // TC24: Parallel on surface (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 1, 1), new Vector(0, 0, 1))), "Parallel on surface");

        // TC25: Parallel opposite outside (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(3, 1, 1), new Vector(0, 0, -1))), "Parallel opposite outside");

        // TC26: Parallel opposite inside (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1.5, 1, 1), new Vector(0, 0, -1))), "Parallel opposite inside");

        // TC27: Parallel opposite on axis (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 0, -1))), "Parallel opposite on axis");

        // TC28: Parallel opposite on surface (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 1, 1), new Vector(0, 0, -1))), "Parallel opposite on surface");

        // ==========================================================
        // GROUP 3: Acute/Obtuse Angles Intersecting the Axis Line
        // ==========================================================
        // TC31: Outside crossing (2 points)
        List<Point> res31 = tube.findIntersections(new Ray(new Point(3, 1, -1), new Vector(-1, 0, 1)));
        assertNotNull(res31, "Must be not null");
        assertEquals(2, res31.size(), "Intersecting axis outside");

        // TC32: Inside crossing (1 point)
        List<Point> res32 = tube.findIntersections(new Ray(new Point(1.5, 1, 0.5), new Vector(1, 0, 1)));
        assertNotNull(res32, "Must be not null");
        assertEquals(1, res32.size(), "Intersecting axis inside");

        // TC33: From axis crossing (1 point)
        List<Point> res33 = tube.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 1)));
        assertNotNull(res33, "Must be not null");
        assertEquals(1, res33.size(), "Intersecting axis from center");

        // TC34: Surface pointing in (1 point)
        List<Point> res34 = tube.findIntersections(new Ray(new Point(2, 1, 0), new Vector(-1, 0, 1)));
        assertNotNull(res34, "Must be not null");
        assertEquals(1, res34.size(), "Intersecting axis surface in");

        // TC35: Surface pointing out (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 1, 2), new Vector(1, 0, 1))), "Intersecting axis surface out");

        // TC36: Opposite angle outside (2 points)
        List<Point> res36 = tube.findIntersections(new Ray(new Point(3, 1, 3), new Vector(-1, 0, -1)));
        assertNotNull(res36, "Must be not null");
        assertEquals(2, res36.size(), "Opposite intersecting axis outside");

        // TC37: Opposite angle inside (1 point)
        List<Point> res37 = tube.findIntersections(new Ray(new Point(1.5, 1, 1.5), new Vector(1, 0, -1)));
        assertNotNull(res37, "Must be not null");
        assertEquals(1, res37.size(), "Opposite intersecting axis inside");

        // TC38: Opposite angle from axis (1 point)
        List<Point> res38 = tube.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, -1)));
        assertNotNull(res38, "Must be not null");
        assertEquals(1, res38.size(), "Opposite intersecting from axis");

        // TC39: Opposite angle surface in (1 point)
        List<Point> res39 = tube.findIntersections(new Ray(new Point(2, 1, 2), new Vector(-1, 0, -1)));
        assertNotNull(res39, "Must be not null");
        assertEquals(1, res39.size(), "Opposite intersecting surface in");

        // TC3A: Opposite angle surface out (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, -1))), "Opposite intersecting surface out");

        // ==========================================================
        // GROUP 4: Skew Rays (Neither parallel nor intersecting axis)
        // ==========================================================
        // TC41: Skew outside crossing (2 points)
        List<Point> res41 = tube.findIntersections(new Ray(new Point(3, 1.5, -1), new Vector(-1, 0, 1)));
        assertNotNull(res41, "Must be not null");
        assertEquals(2, res41.size(), "Skew outside crossing");

        // TC42: Skew inside crossing (1 point)
        List<Point> res42 = tube.findIntersections(new Ray(new Point(1.5, 1.5, 0.5), new Vector(1, 0, 1)));
        assertNotNull(res42, "Must be not null");
        assertEquals(1, res42.size(), "Skew inside crossing");

        // TC43: Skew tangent (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, -1, -1), new Vector(0, 1, 1))), "Skew tangent");

        // TC44: Skew missing completely (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(3, 3, -1), new Vector(-1, 0, 1))), "Skew missing");

        // TC45: Skew surface pointing in (1 point)
        List<Point> res45 = tube.findIntersections(new Ray(new Point(2, 1, 0), new Vector(-1, 1, 1)));
        assertNotNull(res45, "Must be not null");
        assertEquals(1, res45.size(), "Skew surface pointing in");

        // TC46: Skew surface pointing out (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 1, 1))), "Skew surface pointing out");

        // TC47: Skew opposite outside crossing (2 points)
        List<Point> res47 = tube.findIntersections(new Ray(new Point(3, 1.5, 3), new Vector(-1, 0, -1)));
        assertNotNull(res47, "Must be not null");
        assertEquals(2, res47.size(), "Skew opposite crossing");

        // TC48: Skew opposite missing (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(3, 3, 3), new Vector(-1, 0, -1))), "Skew opposite missing");

        // TC49: Skew opposite tangent (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, -1, 3), new Vector(0, 1, -1))), "Skew opposite tangent");
    }
}