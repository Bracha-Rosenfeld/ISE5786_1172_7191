package renderer;

import primitives.Util.*;
import primitives.*;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import static primitives.Util.isZero;
import static primitives.Util.alignZero;

/**
 * Camera class representing a point of view in the 3D scene.
 * It handles the creation of rays through the view plane pixels.
 * @author Project Assistant
 */
public class Camera implements Cloneable {
    // Camera location and orientation vectors
    private Point _p0;
    private Vector _vTo;
    private Vector _vUp;
    private Vector _vRight;

    // View plane physical dimensions and distance
    private double _width;
    private double _height;
    private double _distance;

    /** Image writer for the camera */
    private ImageWriter _imageWriter;
    /** Ray tracer for the camera */
    private RayTracerBase _rayTracer;

    // View plane resolution (defaulting to 1)
    private int _nX = 1;
    private int _nY = 1;

    // Computed helper fields to save repetitive calculations
    private Point _vpCenter;
    private double _pixelWidth;
    private double _pixelHeight;

    /** Amount of threads to use for rendering image by the camera */
    private int threadsCount = 0;
    /** Amount of threads to spare for Java VM threads */
    private static final int SPARE_THREADS = 2;
    /** Debug print interval in seconds (for progress percentage) */
    private double printInterval = 0;
    /** Pixel manager for supporting multi-threading and debug print */
    private PixelManager pixelManager;

    // --- Depth of Field Fields ---
    /** The distance from the camera to the focal plane */
    private double focalDistance = 0.0;
    /** The sampling grid generator for the aperture (Depth of Field) */
    private Blackboard apertureBlackboard = null;

    /**
     * Private default constructor for Camera.
     */
    private Camera() {
    }

    /**
     * Static method to get a new Builder instance.
     * @return a new Builder instance
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
     * Constructs a beam of rays for a specific pixel to simulate Depth of Field.
     * If the aperture is not set or its resolution is 1, returns a single ray.
     * * @param j pixel's column index
     * @param i pixel's row index
     * @return a list of rays passing through the pixel and converging at the focal point
     */
    public List<Ray> constructRayBeam(int j, int i) {
        Ray centralRay = constructRay(j, i);

        // If DoF is turned off or only 1 sample is requested, return the central ray
        if (apertureBlackboard == null || focalDistance == 0.0) {
            return List.of(centralRay);
        }

        List<Double3> apertureOffsets = apertureBlackboard.generatePoints();
        if (apertureOffsets == null || apertureOffsets.size() <= 1) {
            return List.of(centralRay);
        }

        // Calculate the distance to the focal plane along the central ray.
        // _vTo is the camera's forward direction.
        double nv = alignZero(centralRay.direction().dotProduct(_vTo));
        if (isZero(nv)) {
            return List.of(centralRay); // Edge case protection
        }

        double t = focalDistance / nv;
        Point focalPoint = centralRay.getPoint(t);

        List<Ray> beam = new LinkedList<>();
        // Generate rays from the aperture to the focal point
        for (Double3 offset : apertureOffsets) {
            Point pStart = _p0;
            if (!isZero(offset._d1())) {
                pStart = pStart.add(_vRight.scale(offset._d1()));
            }
            if (!isZero(offset._d2())) {
                pStart = pStart.add(_vUp.scale(offset._d2()));
            }

            beam.add(new Ray(pStart, focalPoint.subtract(pStart)));
        }

        return beam;
    }

    /**
     * Builder class for creating Camera objects using the Builder pattern.
     */
    public static class Builder {
        // Final camera object to be populated
        private final Camera _camera = new Camera();

        private Point _target;

        /**
         * Sets the camera's location.
         * @param location the camera's position
         * @return the builder instance
         */
        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        /**
         * Sets the camera's orientation using forward and up vectors.
         * @param to direction vector towards the scene
         * @param up general up direction vector
         * @return the builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            _camera._vTo = to;
            _camera._vUp = up;
            return this;
        }

        /**
         * Sets the camera's orientation towards a target point.
         * @param target the point the camera is looking at
         * @param up general up direction vector
         * @return the builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            this._target = target;
            _camera._vUp = up;
            return this;
        }

        /**
         * Sets the camera's orientation towards a target point with default Y-axis up.
         * @param target the point the camera is looking at
         * @return the builder instance
         */
        public Builder setDirection(Point target) {
            this._target = target;
            _camera._vUp = new Vector(0, 1, 0);
            return this;
        }

        /**
         * Sets the physical size of the view plane.
         * @param width  physical width
         * @param height physical height
         * @return the builder instance
         */
        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets the distance between the camera and the view plane.
         * @param distance the distance value
         * @return the builder instance
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane.
         * @param nX number of pixels in a row
         * @param nY number of pixels in a column
         * @return the builder instance
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Set multi-threading
         * @param threads number of threads.
         * @return builder object itself
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                _camera.threadsCount = cores <= 2 ? 1 : cores;
            } else {
                _camera.threadsCount = threads;
            }
            return this;
        }

        /**
         * Set debug printing interval.
         * @param interval printing interval in seconds.
         * @return builder object itself
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("interval parameter must be non-negative");
            _camera.printInterval = interval;
            return this;
        }

        /**
         * Sets the focal distance for Depth of Field.
         * @param focalDistance the distance to the focal plane
         * @return the builder instance
         */
        public Builder setFocalDistance(double focalDistance) {
            if (focalDistance < 0)
                throw new IllegalArgumentException("Focal distance cannot be negative");
            _camera.focalDistance = focalDistance;
            return this;
        }

        /**
         * Sets the blackboard used to generate points on the aperture window.
         * @param blackboard the blackboard instance
         * @return the builder instance
         */
        public Builder setApertureBlackboard(Blackboard blackboard) {
            _camera.apertureBlackboard = blackboard;
            return this;
        }

        /**
         * Sets the ray tracer for the camera.
         * @param scene the scene to render
         * @param type  the type of ray tracer to use
         * @return the builder itself
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                _camera._rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type");
            }
            return this;
        }

        /**
         * Finalizes the camera construction with specific order of checks.
         * @return a ready-to-use Camera object (cloned)
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            if (_camera._width == 0 || _camera._height == 0)
                throw new MissingResourceException("Missing resolution data", "Camera", "resolution");
            _camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
            if (_camera._rayTracer == null) {
                setRayTracer(new Scene("default"), RayTracerType.SIMPLE);
            }
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        private void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }

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

    /**
     * Renders the image by casting rays through all pixels.
     * @return the camera itself
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(_nY, _nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    private Camera renderImageNoThreads() {
        for (int i = 0; i < _nY; ++i) {
            for (int j = 0; j < _nX; ++j) {
                castRay(_nX, _nY, j, i);
            }
        }
        return this;
    }

    private Camera renderImageStream() {
        IntStream.range(0, _nY).parallel()
                .forEach(i -> IntStream.range(0, _nX).parallel()
                        .forEach(j -> castRay(_nX, _nY, j, i)));
        return this;
    }

    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        int threadsToRun = threadsCount;
        while (threadsToRun-- > 0) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null) {
                    castRay(_nX, _nY, pixel.col(), pixel.row());
                }
            }));
        }
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {}

        return this;
    }

    /**
     * Casts a beam of rays through a specific pixel, calculates their average color,
     * and writes it to the image. Updates the pixel manager if active.
     */
    private void castRay(int nX, int nY, int j, int i) {
        List<Ray> beam = constructRayBeam(j, i);

        Color color = Color.BLACK;
        for (Ray ray : beam) {
            color = color.add(_rayTracer.traceRay(ray));
        }

        // Average the color across all rays in the beam
        color = color.reduce(beam.size());

        _imageWriter.writePixel(j, i, color);

        if (pixelManager != null) {
            pixelManager.pixelDone(); // חובה לדווח למנהל הפיקסלים על סיום העבודה
        }
    }

    /**
     * Adds a grid to the image.
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < _nY; i++) {
            for (int j = 0; j < _nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    /**
     * Delegates image writing to the image writer.
     */
    public void writeToImage(String fileName) {
        _imageWriter.writeToImage(fileName);
    }
}