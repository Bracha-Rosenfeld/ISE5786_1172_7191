package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import primitives.Point;
import primitives.Vector;

/**
 * Integration tests for Camera and Intersectable geometries.
 * The tests verify the combination of ray construction and intersection calculations.
 * * @author Project Assistant
 */
class CameraIntersectionIntegration {

    /** * Base camera builder with standard 3x3 resolution and distance 1
     * as required by the integration test instructions.
     */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(3, 3)
            .setVpDistance(1)
            .setResolution(3, 3);

    /** Standard camera located at the origin */
    private final Camera camera1 = cameraBuilder.setLocation(Point.ZERO).build();

    /** Camera shifted along the Z-axis for specific test cases */
    private final Camera camera2 = cameraBuilder.setLocation(new Point(0, 0, 0.5)).build();

    /**
     * Helper method to count total intersections between a camera's rays and a geometry.
     * The method iterates through all pixels in a 3x3 view plane.
     * * @param camera   The camera generating the rays
     * @param body     The geometric body to intersect with
     * @param expected The expected total number of intersections
     * @param testName The name of the test for error reporting
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body, int expected, String testName) {
        int count = 0;

        // Use standard 3x3 resolution for integration tests
        int nX = 3;
        int nY = 3;

        // Iterate through all pixels according to resolution
        for (int i = 0; i < nY; ++i) {
            for (int j = 0; j < nX; ++j) {
                // Construct ray and find intersections
                var intersections = body.findIntersections(camera.constructRay(j, i));
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }

        assertEquals(expected, count, "Wrong amount of intersections for: " + testName);
    }

    /**
     * Integration tests for Sphere.
     */
    @Test
    void testCameraRaySphereIntegration() {
        // TC01: Sphere r=1, 2 intersections
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -3), 1), 2, "Sphere TC01");

        // TC02: Large Sphere r=2.5, 18 intersections
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2.5), 2.5), 18, "Sphere TC02");

        // TC03: Medium Sphere r=2, 10 intersections
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2), 2), 10, "Sphere TC03");

        // TC04: Camera inside sphere r=4, 9 intersections
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -1), 4), 9, "Sphere TC04");

        // TC05: Sphere behind camera, 0 intersections
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, 1), 0.5), 0, "Sphere TC05");
    }

    /**
     * Integration tests for Plane.
     */
    @Test
    void testCameraRayPlaneIntegration() {
        // TC01: Plane parallel to VP, 9 intersections
        assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 9, "Plane TC01");

        // TC02: Tilted plane, all rays intersect, 9 intersections
        assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -2), new Vector(0, -0.5, 1)), 9, "Plane TC02");

        // TC03: Tilted plane, some rays do not intersect, 6 intersections
        assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -5), new Vector(0, -1, 1)), 6, "Plane TC03");
    }

    /**
     * Integration tests for Triangle.
     */
    @Test
    void testCameraRayTriangleIntegration() {
        // TC01: Small triangle, 1 intersection
        assertIntersectionsCount(camera1, new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 1, "Triangle TC01");

        // TC02: Tall triangle, 2 intersections
        assertIntersectionsCount(camera1, new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 2, "Triangle TC02");
    }
}