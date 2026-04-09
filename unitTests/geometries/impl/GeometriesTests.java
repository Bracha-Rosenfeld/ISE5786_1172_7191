package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import geometries.api.Intersectable;
import geometries.impl.*;
import java.util.List;

/**
 * Unit tests for class {@link Geometries}.
 */
class GeometriesTests {

    /**
     * Test method for {@link Geometries#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Shapes for the tests
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
        Sphere sphere = new Sphere(new Point(0, 0, 3), 1);
        Triangle triangle = new Triangle(new Point(1, 1, 5), new Point(-1, 1, 5), new Point(0, -1, 5));

        Geometries geometries = new Geometries(plane, sphere, triangle);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Some geometries are intersected (but not all)
        // Ray starts inside the sphere, going up. Intersects the rest of the sphere (1 pt) and the triangle (1 pt) = 2 points.
        Ray rayEP = new Ray(new Point(0, 0, 2.5), new Vector(0, 0, 1));
        List<Point> resultEP = geometries.findIntersections(rayEP);
        assertNotNull(resultEP, "Must be not null");
        assertEquals(2, resultEP.size(), "Wrong number of points for some geometries intersected");

        // =============== Boundary Values Tests ==================

        // BV01: Empty collection
        Geometries emptyGeometries = new Geometries();
        assertNull(emptyGeometries.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1))),
                "Empty geometries collection should return null");

        // BV02: No geometry is intersected
        // Ray goes in the X direction, completely missing all shapes
        Ray rayBV02 = new Ray(new Point(0, 0, -1), new Vector(1, 0, 0));
        assertNull(geometries.findIntersections(rayBV02), "No geometry intersected should return null");

        // BV03: Only one geometry is intersected
        // Ray starts above the sphere, going up. Intersects only the triangle.
        Ray rayBV03 = new Ray(new Point(0, 0, 4.5), new Vector(0, 0, 1));
        List<Point> resultBV03 = geometries.findIntersections(rayBV03);
        assertNotNull(resultBV03, "Must be not null");
        assertEquals(1, resultBV03.size(), "Wrong number of points for one geometry intersected");

        // BV04: All geometries are intersected
        // Ray starts below the plane, going up. Intersects plane (1), sphere (2), triangle (1) = 4 points.
        Ray rayBV04 = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        List<Point> resultBV04 = geometries.findIntersections(rayBV04);
        assertNotNull(resultBV04, "Must be not null");
        assertEquals(4, resultBV04.size(), "Wrong number of points for all geometries intersected");
    }
}