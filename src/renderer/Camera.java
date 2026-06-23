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
 * Camera class representing a point of view in the 3D scene environment.
 * It handles the creation of primary rays and distributed ray beams through
 * the view plane pixels, supporting multi-threading and Depth of Field effects.
 */
public class Camera implements Cloneable {
    /** The position point of the camera in the 3D space. */
    private Point _p0;
    /** The forward orientation vector pointing from the camera toward the scene. */
    private Vector _vTo;
    /** The upward orientation vector defining the vertical top orientation of the camera. */
    private Vector _vUp;
    /** The rightward orientation vector orthogonal to both _vTo and _vUp. */
    private Vector _vRight;

    /** The physical horizontal width of the view plane. */
    private double _width;
    /** The physical vertical height of the view plane. */
    private double _height;
    /** The orthogonal distance between the camera position point and the view plane. */
    private double _distance;

    /** Image writer implementation instance assigned for handling actual file rendering output. */
    private ImageWriter _imageWriter;
    /** Ray tracer instance responsible for finding geometric intersections and calculating shading colors. */
    private RayTracerBase _rayTracer;

    /** View plane horizontal resolution representing total number of pixel columns. */
    private int _nX = 1;
    /** View plane vertical resolution representing total number of pixel rows. */
    private int _nY = 1;

    /** Calculated physical center point location of the view plane grid. */
    private Point _vpCenter;
    /** Precomputed physical width of a single pixel unit in the view plane grid. */
    private double _pixelWidth;
    /** Precomputed physical height of a single pixel unit in the view plane grid. */
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
    /** * The distance from the camera's location to the focal plane.
     */
    private double focalDistance = 0.0;
    /** * The blackboard assistant used for generating point offsets on the aperture window
     * to simulate depth of field distributed ray tracing.
     */
    private Blackboard apertureBlackboard = null;

    /**
     * Private default constructor to prevent direct unconfigured instantiation
     * and enforce structural assembly using the static Builder pattern.
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
     * Constructs a distributed beam of scattered rays focused across a target point
     * on the focal plane, simulating physical Depth of Field blur.
     *
     * @param j the vertical column location index inside the active view plane pixel matrix
     * @param i the horizontal row location index inside the active view plane pixel matrix
     * @return an unmodifiable List collection of sampled Rays targeting the computed point of focus
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
     * Builder class for creating Camera objects using the fluid Builder pattern.
     * Manages all semantic variable configurations, verification checks, and safety rules.
     */
    public static class Builder {
        // Final camera object to be populated
        private final Camera _camera = new Camera();

        private Point _target;

        /**
         * Sets the spatial positioning coordinates where the focal lens assembly resides inside the universe.
         * * @param location the localized point reference representing the new position
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        /**
         * Sets orientation vectors explicitly determining forward view angle and absolute upward tilt direction.
         * * @param to the forward-facing look vector extending toward the world coordinate space
         * @param up the vertical structural vector mapping the upward frame rotation bounds
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setDirection(Vector to, Vector up) {
            _camera._vTo = to;
            _camera._vUp = up;
            return this;
        }

        /**
         * Computes forward alignment automatically by pointing the view matrix directly
         * at a targeted space point.
         * * @param target the spatial target coordinate position to track
         * @param up     the upward structural vector mapping the vertical layout bounds
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setDirection(Point target, Vector up) {
            this._target = target;
            _camera._vUp = up;
            return this;
        }

        /**
         * Computes forward alignment automatically by pointing the view matrix directly
         * at a targeted space point, defaulting to a vertical Y-axis up orientation (0,1,0).
         * * @param target the spatial target coordinate position to track
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setDirection(Point target) {
            this._target = target;
            _camera._vUp = new Vector(0, 1, 0);
            return this;
        }

        /**
         * Sets the physical dimensions bounding the active flat projection view window frame.
         * * @param width  the horizontal physical size value representing view plane width
         * @param height the vertical physical size value representing view plane height
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets the distance value between the camera point origin and the viewport screen.
         * * @param distance the length value representing the focal viewport clearance distance
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Configures the grid matrix scale representing total vertical rows and horizontal pixel columns.
         * * @param nX total absolute horizontal subdivisions count mapping width resolution
         * @param nY total absolute vertical subdivisions count mapping height resolution
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Configures the multi-threaded execution parameters used during image rendering.
         * * @param threads the thread count parameter limit, where negative configurations trigger smart auto-scaling
         * @return the active internal Builder instance for structural step chaining
         * @throws IllegalArgumentException if the parameter bounds drop below a value of -2
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
         * Configures a real-time terminal logging console reporting interval step value tracking render progression.
         * * @param interval the duration window measured in seconds between subsequent logs
         * @return the active internal Builder instance for structural step chaining
         * @throws IllegalArgumentException if the provided numeric span falls below zero
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("interval parameter must be non-negative");
            _camera.printInterval = interval;
            return this;
        }

        /**
         * Sets the precise distance separating the lens apparatus from the crisp plane of perfect focus.
         * * @param focalDistance the absolute geometric distance setting the focal layer alignment depth
         * @return the active internal Builder instance for structural step chaining
         * @throws IllegalArgumentException if the distance parameter configuration is negative
         */
        public Builder setFocalDistance(double focalDistance) {
            if (focalDistance < 0)
                throw new IllegalArgumentException("Focal distance cannot be negative");
            _camera.focalDistance = focalDistance;
            return this;
        }

        /**
         * Attaches a customizable blackboard helper script to handle localized distributed aperture pattern generation.
         * * @param blackboard the parameterized sampling grid template map containing matrix configuration rules
         * @return the active internal Builder instance for structural step chaining
         */
        public Builder setApertureBlackboard(Blackboard blackboard) {
            _camera.apertureBlackboard = blackboard;
            return this;
        }

        /**
         * Attaches a targeted rendering engine implementation subclass module to process trace lookups.
         * * @param scene the active global universe model environment holding shapes and lights
         * @param type  the enum structural variation tracking the style of pixel processing
         * @return the active internal Builder instance for structural step chaining
         * @throws IllegalArgumentException if an unknown or unmapped RayTracer pattern configuration is supplied
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
         * Validates all configured internal variables, sets up derived helper properties,
         * and returns a ready-to-use cloned instance of the constructed Camera object.
         * * @return a complete, verified Camera instance copy
         * @throws MissingResourceException if mandatory properties like dimensions or positions are unconfigured
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

        /**
         * Verifies the resolution configurations to ensure matrix divisions remain positive.
         * * @throws IllegalArgumentException if horizontal or vertical bounds equal or fall below zero
         */
        private void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }

        /**
         * Validates geographic coordinates, aligns forward directions, and constructs
         * an orthogonal coordinate system matrix tracking rightward and upward direction lines.
         * * @throws MissingResourceException if critical orientation structures are unassigned
         * @throws IllegalArgumentException if look coordinates conflict or describe parallel vectors
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
         * Validates physical bounds sizing and derives helper fields such as center alignment
         * coordinates and exact fractional pixel grid step spans.
         * * @throws IllegalArgumentException if view window metric proportions measure negative or zero
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
        /**
         * Turns on the Bounding Volume Hierarchy (BVH) improvement and builds the tree.
         * @return the builder itself
         */
        public Builder enableBVH() {
            // הגישה לסצנה תלויה באיך שהיא נשמרת אצלכם ב-Builder או ב-RayTracer.
            // בהנחה שה-RayTracer שלכם שומר את הסצנה והיא נגישה:
            if (_camera._rayTracer instanceof renderer.SimpleRayTracer rt) {
                rt._scene.geometries.setBvhIsOn(true);
                rt._scene.geometries.buildHierarchy();
            } else if (_camera._rayTracer instanceof renderer.RayTracerBase rt) {
                rt._scene.geometries.setBvhIsOn(true);
                rt._scene.geometries.buildHierarchy();
            }
            return this;
        }

        /**
         * Alias for enableBVH, used for backward compatibility in tests.
         * @return the builder itself
         */
        public Builder enableCBR() {
            return enableBVH();
        }
    }

    /**
     * Iterates across the grid coordinates, deploying ray computations
     * based on the chosen thread routing configuration.
     * * @return the modified Camera instance itself
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(_nY, _nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }
    /**
     * Executes the rendering logic using a basic, single-threaded nested loop
     * across all pixel coordinates sequentially.
     * * @return the camera instance reference once the processing steps wrap up
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < _nY; ++i) {
            for (int j = 0; j < _nX; ++j) {
                castRay(_nX, _nY, j, i);
            }
        }
        return this;
    }

    /**
     * Leverages parallel Java Streams to execute automatic multi-threaded calculation loops
     * across the view plane grid.
     * * @return the camera instance reference once processing wraps up
     */
    private Camera renderImageStream() {
        IntStream.range(0, _nY).parallel()
                .forEach(i -> IntStream.range(0, _nX).parallel()
                        .forEach(j -> castRay(_nX, _nY, j, i)));
        return this;
    }

    /**
     * Instantiates and manages raw, low-level Java Thread instances to process pixels
     * concurrently via a shared PixelManager worker pipeline.
     * * @return the camera instance reference once all thread processes exit safely
     */
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
     * Coordinates the generation of ray targets passing through a precise pixel coordinate slot,
     * averages color responses returned from the active tracer engine, and commits the result
     * to the file layout tracker.
     * * @param nX total horizontal viewport width matrix resolution divisions
     * @param nY total vertical viewport height matrix resolution divisions
     * @param j  the relative column coordinate position map tracking screen width placement
     * @param i  the relative row coordinate position map tracking screen height placement
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
     * Superimposes a visible uniform reference grid layer across the rendered output data array
     * for verification and structural analysis.
     * * @param interval the specific mathematical step gap count defining grid line intervals
     * @param color    the programmatic color profile applied to the grid lines
     * @return the active modified Camera instance reference
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
     * Delegates file compilation and memory writing steps to the internal image writer mechanism
     * to save the picture output to disk.
     * * @param fileName the final filename path label assigned to the generated file output
     */
    public void writeToImage(String fileName) {
        _imageWriter.writeToImage(fileName);
    }
}