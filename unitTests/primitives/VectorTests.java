package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Vector}.
 * The tests verify all math operations and edge cases like zero vectors.
 */
class VectorTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** First vector for tests */
    private static final Vector V1 = new Vector(1, 2, 3);
    /** Second vector for tests, orthogonal to V1 */
    private static final Vector V2 = new Vector(0, 3, -2);
    /** Third vector for tests, opposite direction to V1 */
    private static final Vector V3 = new Vector(-1, -2, -3);

    /** Error message for wrong math calculations */
    private static final String ERROR_MATH = "ERROR: Math calculation is wrong";
    /** Error message for expected exception */
    private static final String ERROR_EXCEPTION = "ERROR: Expected exception was not thrown";

    /**
     * Test method for {@link Vector#add(Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple addition of two vectors
        assertEquals(new Vector(1, 5, 1), V1.add(V2), ERROR_MATH);

        // =============== Boundary Values Tests ==================
        // BV01: Addition resulting in zero vector throws exception
        assertThrows(IllegalArgumentException.class, () -> V1.add(V3), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#subtract(Point)}.
     * We must test subtract here even though it is implemented in Point.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple subtraction of two vectors
        assertEquals(new Vector(1, -1, 5), V1.subtract(V2), ERROR_MATH);

        // =============== Boundary Values Tests ==================
        // BV01: Subtraction of a vector from itself throws exception (Zero Vector)
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Scaling vector by a positive scalar
        assertEquals(new Vector(2, 4, 6), V1.scale(2), ERROR_MATH);

        // EP02: Scaling vector by a negative scalar
        assertEquals(new Vector(-2, -4, -6), V1.scale(-2), ERROR_MATH);

        // =============== Boundary Values Tests ==================
        // BV01: Scaling vector by zero throws exception
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Dot product of two vectors
        assertEquals(-14, V1.dotProduct(V3), DELTA, ERROR_MATH);

        // =============== Boundary Values Tests ==================
        // BV01: Dot product of orthogonal vectors is zero
        assertEquals(0, V1.dotProduct(V2), DELTA, ERROR_MATH);
    }

    /**
     * Test method for {@link Vector#crossProduct(Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Cross product of two vectors
        Vector vr = V1.crossProduct(V2);
        assertEquals(new Vector(-13, 2, 3), vr, ERROR_MATH);

        // EP02: Check orthogonality of the cross product result
        assertEquals(0, vr.dotProduct(V1), DELTA, ERROR_MATH);
        assertEquals(0, vr.dotProduct(V2), DELTA, ERROR_MATH);

        // =============== Boundary Values Tests ==================
        // BV01: Cross product of parallel vectors throws exception
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V3), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Squared length of a vector
        assertEquals(14, V1.lengthSquared(), DELTA, ERROR_MATH);
    }

    /**
     * Test method for {@link Vector#length()}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Length of a vector (using a Pythagorean triple vector for exact double)
        Vector v4 = new Vector(0, 3, 4);
        assertEquals(5, v4.length(), DELTA, ERROR_MATH);
    }

    /**
     * Test method for {@link Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Vector normalization result is a unit vector
        Vector v = V1.normalize();
        assertEquals(1, v.lengthSquared(), DELTA, ERROR_MATH);

        // EP02: Normalized vector is parallel to the original vector (cross product throws exception)
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(v),
                "ERROR: Normalized vector is not parallel to the original one");

        // EP03: Normalized vector is in the same direction
        assertTrue(V1.dotProduct(v) > 0, "ERROR: Normalized vector is in opposite direction");
    }
}