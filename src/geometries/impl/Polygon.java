package geometries.impl;

import primitives.*;

import java.util.List;

//import geometries.api.Geometry;
import geometries.api.Geometry;
import primitives.*;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices.
 * All vertices must lie in the same plane and be arranged along the
 * polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 * @author Dina Black and Bracha Rosenfeld
 */
public class Polygon extends Geometry {
   /** Ordered list of polygon vertices */
   protected final List<Point> _vertices;
   /** Plane containing the polygon */
   protected final Plane _plane;
   /** Number of vertices */
   private final int           _size;

   /**
    * Constructs a convex polygon from ordered vertices.
    * <p>
    * The vertices must:
    * </p>
    * <ul>
    * <li>Contain at least three points</li>
    * <li>Be ordered along the polygon edge path</li>
    * <li>Lie in the same plane</li>
    * <li>Form a convex polygon</li>
    * </ul>
    * @param  vertices                 polygon vertices in edge order
    * @throws IllegalArgumentException if the vertices do not form a valid convex
    *                                  polygon
    */
   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
      _vertices = List.of(vertices);
      _size     = vertices.length;

      // Create the supporting plane using the first three vertices.
      // The plane stores the constant normal of the polygon.
      _plane    = new Plane(vertices[0], vertices[1], vertices[2]);
      if (_size == 3) return; // no need for more tests for a Triangle

      Vector  n        = _plane.getNormal(vertices[0]);
      // Subtracting identical vertices would create a zero vector (illegal)
      Vector  edge1    = vertices[_size - 1].subtract(vertices[_size - 2]);
      Vector  edge2    = vertices[0].subtract(vertices[_size - 1]);

      // Cross product of consecutive edges determines orientation.
      // All edge pairs must produce the same sign relative to the normal,
      // otherwise the polygon is concave or vertices are unordered.
      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
      for (var i = 1; i < _size; ++i) {
         // Test that the point is in the same plane as calculated originally
         if (!Util.isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
         // Test the consequent edges have
         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
            throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
      }
   }
   @Override
   public List<Point> findIntersections(Ray ray) {
      // 1. Intersect with the plane containing the polygon
      List<Point> planeIntersections = _plane.findIntersections(ray);

      // If the ray doesn't intersect the plane, it definitely doesn't intersect the polygon
      if (planeIntersections == null) {
         return null;
      }

      Point p0 = ray.origin();
      Vector v = ray.direction();

      // We check the first edge separately to determine the initial sign (positive or negative)
      Vector v1 = _vertices.get(_size - 1).subtract(p0);
      Vector v2 = _vertices.get(0).subtract(p0);
      Vector n = v1.crossProduct(v2);

      double vn = Util.alignZero(v.dotProduct(n));

      // If the ray intersects exactly on an edge or vertex, return null per requirements
      if (vn == 0) {
         return null;
      }

      // Determine the expected sign for all subsequent checks
      boolean isPositive = vn > 0;

      // Loop over the rest of the edges
      for (int i = 1; i < _size; ++i) {
         v1 = v2;
         v2 = _vertices.get(i).subtract(p0);
         n = v1.crossProduct(v2);
         vn = Util.alignZero(v.dotProduct(n));

         // If vn is 0, it's on an edge/vertex. If the sign differs, it's outside.
         if (vn == 0 || (vn > 0) != isPositive) {
            return null;
         }
      }

      // If we passed all checks, the point is strictly inside the polygon
      return planeIntersections;
   }
   @Override
   public Vector getNormal(Point point) { return _plane.getNormal(point); }
}
