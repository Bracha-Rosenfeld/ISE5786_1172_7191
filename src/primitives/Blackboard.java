package primitives;

import java.util.LinkedList;
import java.util.List;

/**
 * The Blackboard class represents a 2D target area for multiple sampling (super-sampling).
 * It is responsible for generating a grid of sample points (offsets) around a center point (0,0).
 * @author Project Assistant
 */
public class Blackboard {
    /** The physical width and height of the target area */
    private double size;
    /** The resolution of the grid (number of points in one row/column) */
    private int resolution;
    /** Flag to determine if the grid points should be jittered (randomized within their cell) */
    private boolean isJittered = false;

    /**
     * Private constructor to enforce Builder pattern.
     */
    private Blackboard() {
    }

    /**
     * @return a new Builder instance for Blackboard
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Generates a list of 2D offset points forming a grid centered at (0,0).
     * The grid distributes the points evenly across the target area, with optional jittering.
     * @return a list of 2D points (represented as Double3 where Z is 0) representing the offsets
     */
    public List<Double3> generatePoints() {
        List<Double3> points = new LinkedList<>();

        // If resolution is 1, return a single point at the center
        if (resolution == 1) {
            points.add(Double3.ZERO);
            return points;
        }

        // Calculate the size of each small square cell in the grid
        double cellSize = size / resolution;

        // Start coordinates from the bottom-left corner of the grid
        double start = -(size - cellSize) / 2.0;

        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                // Calculate the exact center of the current cell
                double xOffset = start + j * cellSize;
                double yOffset = start + i * cellSize;

                // If jittered is enabled, move the point randomly within the boundaries of the current cell
                if (isJittered) {
                    // Math.random() returns [0, 1). We shift it to [-0.5, 0.5) and scale by cellSize
                    xOffset += (Math.random() - 0.5) * cellSize;
                    yOffset += (Math.random() - 0.5) * cellSize;
                }

                points.add(new Double3(xOffset, yOffset, 0));
            }
        }
        return points;
    }

    /**
     * Builder class for Blackboard.
     */
    public static class Builder {
        private final Blackboard blackboard = new Blackboard();

        /**
         * Sets the physical size of the sampling area.
         * @param size the size of the area
         * @return the builder instance
         */
        public Builder setSize(double size) {
            if (size <= 0)
                throw new IllegalArgumentException("Size must be positive");
            blackboard.size = size;
            return this;
        }

        /**
         * Sets the resolution (number of rows/columns) of the sampling grid.
         * The total number of points generated will be resolution * resolution.
         * @param resolution number of points in a row/column
         * @return the builder instance
         */
        public Builder setResolution(int resolution) {
            if (resolution < 1)
                throw new IllegalArgumentException("Resolution must be at least 1");
            blackboard.resolution = resolution;
            return this;
        }

        /**
         * Sets whether the grid should use jittered sampling.
         * @param isJittered true for jittered sampling, false for strict grid
         * @return the builder instance
         */
        public Builder setJittered(boolean isJittered) {
            blackboard.isJittered = isJittered;
            return this;
        }

        /**
         * Builds the final Blackboard object.
         * @return a ready-to-use Blackboard instance
         */
        public Blackboard build() {
            if (blackboard.size == 0)
                throw new IllegalStateException("Size must be initialized");
            if (blackboard.resolution == 0)
                throw new IllegalStateException("Resolution must be initialized");

            // Return a new instance to keep the object immutable
            Blackboard clone = new Blackboard();
            clone.size = blackboard.size;
            clone.resolution = blackboard.resolution;
            clone.isJittered = blackboard.isJittered;
            return clone;
        }
    }
}