package renderer;

import geometries.impl.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Test class for rendering a custom 3D scene showcasing all engine capabilities:
 * Shadows, Reflections, Refractions, and Partial Shadows.
 */
public class CustomPictureTest {

    @Test
    public void customPicture() {
        // שמי חורף חשוכים
        Scene scene = new Scene("Snow Globe")
                .setBackground(new Color(10, 10, 30))
                .setAmbientLight(new AmbientLight(new Color(255, 255, 255), new Double3(0.1)));

        // הגדרת חומרים
        Material matSnow = new Material().setKD(0.8).setKS(0.2).setShininess(30);
        Material matGlass = new Material().setKD(0.0).setKS(0.9).setKT(0.85).setKR(0.1).setShininess(100);
        Material matWood = new Material().setKD(0.7).setKS(0.1).setShininess(10);
        Material matMirror = new Material().setKD(0.1).setKS(0.8).setKR(0.6).setShininess(60);
        Material matBlack = new Material().setKD(0.9).setKS(0.1).setShininess(10);
        Material matOrange = new Material().setKD(0.9).setKS(0.1).setShininess(10);

        scene.geometries.add(
                // 1. רצפת קרח משקפת (מראה)
                new Plane(new Point(0, -80, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(20, 20, 30))
                        .setMaterial(matMirror),

                // 2. מעמד עץ לכדור הבדולח
                new Cylinder(60, new Ray(new Point(0, -80, -200), new Vector(0, 1, 0)), 10)
                        .setEmission(new Color(60, 30, 10))
                        .setMaterial(matWood),

                // 3. כדור הזכוכית השקוף שעוטף את הכל
                new Sphere(new Point(0, -10, -200), 60)
                        .setEmission(new Color(0, 5, 10))
                        .setMaterial(matGlass),

                // 4. איש השלג (בתוך הזכוכית) - כדור תחתון
                new Sphere(new Point(0, -50, -200), 20)
                        .setEmission(new Color(220, 220, 220))
                        .setMaterial(matSnow),

                // כדור אמצעי
                new Sphere(new Point(0, -18, -200), 15)
                        .setEmission(new Color(230, 230, 230))
                        .setMaterial(matSnow),

                // כדור עליון (ראש)
                new Sphere(new Point(0, 5, -200), 10)
                        .setEmission(new Color(255, 255, 255))
                        .setMaterial(matSnow),

                // 5. עיניים (כדורים שחורים קטנים)
                new Sphere(new Point(3, 8, -191), 1.5).setEmission(Color.BLACK).setMaterial(matBlack),
                new Sphere(new Point(-3, 8, -191), 1.5).setEmission(Color.BLACK).setMaterial(matBlack),

                // 6. כפתורים על הבטן
                new Sphere(new Point(0, -12, -186), 1.5).setEmission(Color.BLACK).setMaterial(matBlack),
                new Sphere(new Point(0, -20, -186), 1.5).setEmission(Color.BLACK).setMaterial(matBlack),
                new Sphere(new Point(0, -28, -185), 1.5).setEmission(Color.BLACK).setMaterial(matBlack),

                // 7. אף מגזר (משולש כתום)
                new Triangle(new Point(0, 5, -190), new Point(2, 3, -190), new Point(0, 4, -175))
                        .setEmission(new Color(200, 100, 0))
                        .setMaterial(matOrange)
        );

        // הוספת תאורה
        scene.lights.add(new PointLight(new Color(200, 220, 255), new Point(100, 100, 0))
                .setKl(0.00001).setKq(0.000001));
        scene.lights.add(new SpotLight(new Color(255, 255, 255), new Point(0, 20, 100), new Vector(0, -0.2, -1))
                .setKl(0.0001).setKq(0.00001));

        // בניית המצלמה והרצת הרינדור
        Camera.getBuilder()
                .setLocation(new Point(0, -10, 150)) // מיקום המצלמה
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)) // מסתכלת ישר קדימה
                .setVpDistance(150)
                .setVpSize(200, 200)
                .setResolution(1000, 1000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("snowGlobePicture");
    }
    }