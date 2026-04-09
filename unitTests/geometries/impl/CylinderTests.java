package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

/**
 * Unit tests for class {@link Cylinder}.
 * The tests verify the calculation of the normal vector to the cylinder's surface and intersections.
 * @author Student
 */
class CylinderTests {

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for wrong normal vector
     */
    private static final String ERROR_NORMAL = "ERROR: getNormal() yields wrong result";

    /**
     * Error message when an exception is unexpectedly thrown
     */
    private static final String ERROR_EXCEPTION = "ERROR: Exception thrown";

    /**
     * Test method for {@link Cylinder#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // Arrange
        Point p0 = new Point(0, 0, 0);
        Vector v = new Vector(0, 0, 1);
        Ray axis = new Ray(p0, v);
        double radius = 1.0;
        double height = 2.0;
        Cylinder cylinder = new Cylinder(radius, axis, height);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point on the side (round surface) of the cylinder
        assertDoesNotThrow(() -> {
            Vector n = cylinder.getNormal(new Point(1, 0, 1));
            assertEquals(new Vector(1, 0, 0), n, ERROR_NORMAL);
        }, ERROR_EXCEPTION);

        // EP02: Point on the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0.5, 0, 2)), ERROR_NORMAL);

        // EP03: Point on the bottom base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0.5, 0, 0)), ERROR_NORMAL);


        // =============== Boundary Values Tests ==================

        // BV01: Point at the center of the bottom base (the axis head)
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)), ERROR_NORMAL);

        // BV02: Point at the center of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)), ERROR_NORMAL);

        // BV03: Point on the edge of the bottom base (connection between side and bottom base)
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)), ERROR_NORMAL);

        // BV04: Point on the edge of the top base (connection between side and top base)
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)), ERROR_NORMAL);
    }

    /**
     * Test method for {@link Cylinder#findIntersections(Ray)}.
     * Comprehensive test suite containing ~60 test cases to cover the vast amount
     * of boundary and equivalence scenarios for a finite cylinder, significantly more than Tube.
     */
    @Test
    void testFindIntersections() {
        // Cylinder of radius 1, height 2, sitting on the XY plane and going up the Z axis
        Cylinder cylinder = new Cylinder(1d, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 2d);

        // ==========================================================
        // GROUP 1: ORTHOGONAL RAYS (Perpendicular to Z-axis)
        // ==========================================================
        // EP01: Outside, crosses envelope twice
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0))).size(), "Orthogonal: crosses envelope");
        // EP02: Inside, hits envelope once
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(1, 0, 0))).size(), "Orthogonal: inside to envelope");
        // EP03: Starts on axis, hits envelope once
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 0))).size(), "Orthogonal: axis to envelope");
        // EP04: Outside, misses completely
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(0, 1, 0))), "Orthogonal: miss");

        // BV11: Starts on envelope, goes in
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(1, 0, 1), new Vector(-1, 0, 0))).size(), "Orthogonal: on envelope in");
        // BV12: Starts on envelope, goes out
        assertNull(cylinder.findIntersections(new Ray(new Point(1, 0, 1), new Vector(1, 0, 0))), "Orthogonal: on envelope out");
        // BV13: Tangent to envelope
        assertNull(cylinder.findIntersections(new Ray(new Point(1, -2, 1), new Vector(0, 1, 0))), "Orthogonal: tangent to envelope");

        // BV14: Orthogonal above top base
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 3), new Vector(-1, 0, 0))), "Orthogonal: above cylinder");
        // BV15: Orthogonal below bottom base
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, -1), new Vector(-1, 0, 0))), "Orthogonal: below cylinder");
        // BV16: Orthogonal exactly on top base plane (crosses) -> Treat as tangent (0)
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 2), new Vector(-1, 0, 0))), "Orthogonal: on top plane");
        // BV17: Orthogonal exactly on bottom base plane (crosses) -> Treat as tangent (0)
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0))), "Orthogonal: on bottom plane");


        // ==========================================================
        // GROUP 2: PARALLEL RAYS (Parallel to Z-axis)
        // ==========================================================
        // EP21: Parallel inside, going up (hits top base)
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(0, 0, 1))).size(), "Parallel: inside up");
        // EP22: Parallel inside, going down (hits bottom base)
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(0, 0, -1))).size(), "Parallel: inside down");
        // EP23: Parallel outside, misses
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(0, 0, 1))), "Parallel: outside");
        // EP24: Parallel crosses both bases
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1))).size(), "Parallel: crosses bases");

        // BV21: On axis, crosses bases
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1))).size(), "Parallel: on axis cross");
        // BV22: On axis, starts inside
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))).size(), "Parallel: on axis inside");
        // BV23: Starts exactly on bottom base, goes up (inside)
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 0, 1))).size(), "Parallel: from bottom base up");
        // BV24: Starts exactly on top base, goes down (inside)
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0.5, 0, 2), new Vector(0, 0, -1))).size(), "Parallel: from top base down");
        // BV25: Starts exactly on top base, goes up (outside)
        assertNull(cylinder.findIntersections(new Ray(new Point(0.5, 0, 2), new Vector(0, 0, 1))), "Parallel: from top base up");
        // BV26: Starts exactly on bottom base, goes down (outside)
        assertNull(cylinder.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 0, -1))), "Parallel: from bottom base down");

        // BV27: Parallel exactly on envelope (Tangent)
        assertNull(cylinder.findIntersections(new Ray(new Point(1, 0, -1), new Vector(0, 0, 1))), "Parallel: on envelope");


        // ==========================================================
        // GROUP 3: DIAGONAL RAYS (Intersecting axis or crossing)
        // ==========================================================
        // EP31: Crosses envelope twice
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0.2))).size(), "Diagonal: envelope twice");
        // EP32: Crosses bottom base and top base
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0.2, 0, 1))).size(), "Diagonal: base to base");
        // EP33: Crosses bottom base and envelope
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0.5, 0, 1))).size(), "Diagonal: bottom to envelope");
        // EP34: Crosses envelope and top base
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, 0.5), new Vector(-1, 0, 1))).size(), "Diagonal: envelope to top");
        // EP35: Starts inside, hits top base
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0.2, 0, 1))).size(), "Diagonal: inside to top");
        // EP36: Starts inside, hits bottom base
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0.2, 0, -1))).size(), "Diagonal: inside to bottom");
        // EP37: Starts inside, hits envelope
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 0.1))).size(), "Diagonal: inside to envelope");
        // EP38: Misses completely (skewed)
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(0, 1, 1))), "Diagonal: miss");

        // BV31: Starts on envelope goes in and hits top base
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(1, 0, 1), new Vector(-1, 0, 2))).size(), "Diagonal: envelope in to top");
        // BV32: Starts on envelope goes out
        assertNull(cylinder.findIntersections(new Ray(new Point(1, 0, 1), new Vector(1, 0, 2))), "Diagonal: envelope out");
        // BV33: Starts on top base goes in and hits envelope
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0.5, 0, 2), new Vector(1, 0, -1))).size(), "Diagonal: top in to env");
        // BV34: Starts on top base goes out
        assertNull(cylinder.findIntersections(new Ray(new Point(0.5, 0, 2), new Vector(1, 0, 1))), "Diagonal: top out");


        // ==========================================================
        // GROUP 4: CORNERS AND EDGES (Crucial for cylinder)
        // ==========================================================
        // BV41: Crosses exactly through bottom and top corners
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, -1), new Vector(-1, 0, 1))).size(), "Corners: bottom to top");
        // BV42: Crosses exactly through envelope and top corner
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(-1, 0, 1))).size(), "Corners: env to top corner");
        // BV43: Crosses exactly through bottom corner and envelope
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, -1), new Vector(-1, 0, 2))).size(), "Corners: bottom corner to env");

        // BV44: Starts exactly ON top corner, goes inside
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(1, 0, 2), new Vector(-1, 0, -1))).size(), "Corners: on top corner in");
        // BV45: Starts exactly ON top corner, goes outside
        assertNull(cylinder.findIntersections(new Ray(new Point(1, 0, 2), new Vector(1, 0, 1))), "Corners: on top corner out");
        // BV46: Starts exactly ON bottom corner, goes inside
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(1, 0, 0), new Vector(-1, 0, 1))).size(), "Corners: on bottom corner in");
        // BV47: Starts exactly ON bottom corner, goes outside
        assertNull(cylinder.findIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, -1))), "Corners: on bottom corner out");

        // BV48: Passes exactly through center of top base
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, 4), new Vector(-1, 0, -1))).size(), "Corners: through top center");
        // BV49: Passes exactly through center of bottom base
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(2, 0, 2), new Vector(-1, 0, -1))).size(), "Corners: through bottom center");

        // BV50: Ray tangent to top base edge (hits only corner from outside, shouldn't count as internal crossing)
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 2), new Vector(-1, 1, 0))), "Corners: tangent to top edge");
    }
}