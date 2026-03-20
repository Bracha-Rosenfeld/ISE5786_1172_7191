package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for class {@link Cylinder}.
 * The tests verify the calculation of the normal vector to the cylinder's surface. [cite: 76]
 * * @author Student
 */
class CylinderTests {

    /** Delta value for accuracy when comparing double values. [cite: 32, 136] */
    private static final double DELTA = 1e-6;

    /** Error message for wrong normal vector [cite: 41] */
    private static final String ERROR_NORMAL = "ERROR: getNormal() yields wrong result";

    /** Error message when an exception is unexpectedly thrown [cite: 41, 141] */
    private static final String ERROR_EXCEPTION = "ERROR: Exception thrown";

    /**
     * Test method for {@link Cylinder#getNormal(Point)}. [cite: 76]
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

        // EP01: Point on the side (round surface) of the cylinder [cite: 72]
        // The normal should be orthogonal to the axis
        assertDoesNotThrow(() -> {
            Vector n = cylinder.getNormal(new Point(1, 0, 1));
            assertEquals(new Vector(1, 0, 0), n, ERROR_NORMAL);
        }, ERROR_EXCEPTION);

        // EP02: Point on the top base [cite: 71]
        // The normal should be equal to the axis direction vector
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0.5, 0, 2)), ERROR_NORMAL);

        // EP03: Point on the bottom base [cite: 71]
        // The normal should be opposite to the axis direction vector
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0.5, 0, 0)), ERROR_NORMAL);


        // =============== Boundary Values Tests ==================

        // BV01: Point at the center of the bottom base (the axis head) [cite: 74]
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)), ERROR_NORMAL);

        // BV02: Point at the center of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)), ERROR_NORMAL);

        // BV03: Point on the edge of the bottom base (connection between side and bottom base)
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)), ERROR_NORMAL);

        // BV04: Point on the edge of the top base (connection between side and top base)
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)), ERROR_NORMAL);
    }
}