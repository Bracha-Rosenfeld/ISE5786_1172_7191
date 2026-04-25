package renderer;

import primitives.Util.*;
import primitives.*;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

/**
 * Camera class representing a point of view in the 3D scene.
 * It handles the creation of rays through the view plane pixels.
 * * @author Project Assistant
 */
public class Camera implements Cloneable {
    // Camera location and orientation vectors [cite: 42]
    private Point _p0;
    private Vector _vTo;
    private Vector _vUp;
    private Vector _vRight;

    // View plane physical dimensions and distance [cite: 42]
    private double _width;
    private double _height;
    private double _distance;

    // View plane resolution (defaulting to 1)
    private int _nX = 1;
    private int _nY = 1;

    // Computed helper fields to save repetitive calculations
    private Point _vpCenter;
    private double _pixelWidth;
    private double _pixelHeight;

    /**
     * Private default constructor for Camera[cite: 46].
     */
    private Camera() {}

    /**
     * Static method to get a new Builder instance[cite: 47].
     * * @return a new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through the center of a specific pixel.
     *
     * @param j pixel's column index (X axis)
     * @param i pixel's row index (Y axis)
     * @return a new ray from the camera through the pixel center
     */
    public Ray constructRay(int j, int i) {
        Point pIJ = _vpCenter;

        double xJ = (j - (_nX - 1) / 2d) * _pixelWidth;
        double yI = -(i - (_nY - 1) / 2d) * _pixelHeight;

        if (!isZero(xJ)) {
            pIJ = pIJ.add(_vRight.scale(xJ));
        }

        if (!isZero(yI)) {
            pIJ = pIJ.add(_vUp.scale(yI));
        }

        return new Ray(_p0, pIJ.subtract(_p0));
    }

    /**
     * Builder class for creating Camera objects using the Builder pattern[cite: 38].
     */
    public static class Builder {
        // Final camera object to be populated [cite: 54]
        private final Camera _camera = new Camera();

        private Point _target;
        /**
         * Sets the camera's location[cite: 55].
         * * @param location the camera's position
         * @return the builder instance
         */
        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        /**
         * Sets the camera's orientation using forward and up vectors[cite: 56].
         * * @param to direction vector towards the scene
         * @param up general up direction vector
         * @return the builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            _camera._vTo = to;
            _camera._vUp = up;
            return this;
        }

        /**
         * Sets the camera's orientation towards a target point[cite: 56].
         * * @param target the point the camera is looking at
         * @param up     general up direction vector
         * @return the builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            this._target = target;
            _camera._vUp = up;
            return this;
        }

        /**
         * Sets the camera's orientation towards a target point with default Y-axis up[cite: 56].
         * * @param target the point the camera is looking at
         * @return the builder instance
         */
         public Builder setDirection(Point target) {
            this._target = target;
            _camera._vUp = new Vector(0, 1, 0);
            return this;
         }

        /**
         * Sets the physical size of the view plane[cite: 57].
         * * @param width  physical width
         * @param height physical height
         * @return the builder instance
         */
        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets the distance between the camera and the view plane[cite: 57].
         * * @param distance the distance value
         * @return the builder instance
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane[cite: 57].
         * * @param nX number of pixels in a row
         * @param nY number of pixels in a column
         * @return the builder instance
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Finalizes the camera construction with specific order of checks[cite: 65].
         * * @return a ready-to-use Camera object (cloned)
         * @throws MissingResourceException if mandatory data is missing
         * @throws IllegalArgumentException if data is invalid
         */
        public Camera build() {
            // Must follow this specific order
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            try {
                // Return a clone of the internal camera object
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        /**
         * Validates that resolution values are positive[cite: 74].
         */
        private void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }

        /**
         * Validates location and orientation, and computes vRight[cite: 76, 78].
         */
        private void checkLocationAndDirection() {
            if (_camera._p0 == null)
                throw new MissingResourceException("Missing camera location", "Camera", "Location");
            if (_camera._vUp == null)
                throw new MissingResourceException("Missing up vector", "Camera", "Up Vector");
            if (_camera._vTo == null && _target == null)
                throw new MissingResourceException("Missing direction or target", "Camera", "Direction");
            if (_camera._vTo == null) {
                _camera._vTo = _target.subtract(_camera._p0);
            }

            _camera._vTo = _camera._vTo.normalize();

            try {
                _camera._vRight = _camera._vTo.crossProduct(_camera._vUp).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel");
            }

            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo);
        }

        /**
         * Validates view plane dimensions and distance, and computes helper fields[cite: 78, 85].
         */
        private void checkViewPlane() {
            if (_camera._width <= 0 || _camera._height <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            if (_camera._distance <= 0)
                throw new IllegalArgumentException("View plane distance must be positive");

            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
        }
    }
}