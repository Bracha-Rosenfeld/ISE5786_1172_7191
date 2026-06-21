package renderer;

import geometries.impl.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Advanced Cornell Box room scene test class (Diorama style).
 * It creates a 3D rendered picture with advanced materials, lighting,
 * architectural elements, and geometric constructs.
 */
public class CustomPictureTest {
    /**
     * Adds a simple 3D bird model made of two triangles to the given scene.
     *
     * @param scene the scene to add the bird geometry to
     * @param x     the X coordinate for the bird's head position
     * @param y     the Y coordinate for the bird's head position
     * @param z     the Z coordinate for the bird's head position
     * @param size  the scaling factor for the bird's wings and body
     */
    private void addBird(Scene scene, double x, double y, double z, double size) {
        Point head = new Point(x, y, z);
        Point tail = new Point(x, y, z + 4 * size);
        Point leftWing = new Point(x - 6 * size, y + 2 * size, z + 2 * size);
        Point rightWing = new Point(x + 6 * size, y + 2 * size, z + 2 * size);

        Material birdMat = new Material().setKD(0.8).setKS(0.2).setShininess(10);
        Color birdColor = new Color(15, 15, 15);

        scene.geometries.add(
                new Triangle(head, leftWing, tail).setEmission(birdColor).setMaterial(birdMat),
                new Triangle(head, rightWing, tail).setEmission(birdColor).setMaterial(birdMat)
        );
    }

    /**
     * Generates and renders a comprehensive customized picture of a styled room.
     * Sets up advanced illumination sources, complex layered sphere pyramids,
     * curtains, windows, volumetric shapes, and triggers the rendering process.
     */
    @Test
    public void customPicture() {
        Scene scene = new Scene("Beautiful Room Foreground Focus")
                .setBackground(new Color(170, 220, 255))
                .setAmbientLight(new AmbientLight(new Color(255, 255, 255), new Double3(0.05)));

        // ── 1. חומרים וצבעים ─────────────
        Material matMatte = new Material().setKD(0.6).setKS(0.1).setShininess(10);
        Material matFloor = new Material().setKD(0.5).setKS(0.5).setKR(0.25).setShininess(50);

        // מתכת קלאסית ונכונה: צבע בסיס חלש, ברק מאוד גבוה, והשתקפות נעימה (כרום/פלדה)
        Material matSolidMetal = new Material().setKD(0.1).setKS(0.9).setKR(0.7).setShininess(100);

        Material matGlass = new Material().setKD(0.1).setKS(0.8).setKT(0.6).setKR(0.1).setShininess(100);
        Material matCurtain = new Material().setKD(0.6).setKS(0.1).setShininess(5);

        Color colCream = new Color(245, 240, 225);
        Color colCeiling = new Color(150, 150, 150);
        Color colBackWall = new Color(180, 180, 185);
        Color colFloor = new Color(30, 30, 35);

        // ── 2. קירות החדר ────────────
        scene.geometries.add(
                new Polygon(new Point(-90, 0, 500), new Point(90, 0, 500), new Point(90, 0, -80), new Point(-90, 0, -80))
                        .setEmission(colFloor).setMaterial(matFloor),
                new Polygon(new Point(-90, 100, -80), new Point(90, 100, -80), new Point(90, 100, 500), new Point(-90, 100, 500))
                        .setEmission(colCeiling).setMaterial(matMatte),
                new Polygon(new Point(-90, 0, -80), new Point(-90, 100, -80), new Point(-90, 100, 500), new Point(-90, 0, 500))
                        .setEmission(colCream).setMaterial(matMatte),
                new Polygon(new Point(90, 0, 500), new Point(90, 100, 500), new Point(90, 100, -80), new Point(90, 0, -80))
                        .setEmission(colCream).setMaterial(matMatte)
        );

        // ── 3. הקיר האחורי והחלון ─────────────────────────────────────────
        scene.geometries.add(
                new Polygon(new Point(-90, 0, -80), new Point(90, 0, -80), new Point(90, 25, -80), new Point(-90, 25, -80))
                        .setEmission(colBackWall).setMaterial(matMatte),
                new Polygon(new Point(-90, 85, -80), new Point(90, 85, -80), new Point(90, 100, -80), new Point(-90, 100, -80))
                        .setEmission(colBackWall).setMaterial(matMatte),
                new Polygon(new Point(-90, 25, -80), new Point(-45, 25, -80), new Point(-45, 85, -80), new Point(-90, 85, -80))
                        .setEmission(colBackWall).setMaterial(matMatte),
                new Polygon(new Point(45, 25, -80), new Point(90, 25, -80), new Point(90, 85, -80), new Point(45, 85, -80))
                        .setEmission(colBackWall).setMaterial(matMatte)
        );

        Color frameColor = new Color(40, 20, 10);
        scene.geometries.add(
                new Cylinder(1.5, new Ray(new Point(-45, 25, -80), new Vector(1, 0, 0)), 90).setEmission(frameColor).setMaterial(matMatte),
                new Cylinder(1.5, new Ray(new Point(-45, 85, -80), new Vector(1, 0, 0)), 90).setEmission(frameColor).setMaterial(matMatte),
                new Cylinder(1.5, new Ray(new Point(-45, 25, -80), new Vector(0, 1, 0)), 60).setEmission(frameColor).setMaterial(matMatte),
                new Cylinder(1.5, new Ray(new Point(45, 25, -80), new Vector(0, 1, 0)), 60).setEmission(frameColor).setMaterial(matMatte)
        );

        // ── תוספות עיצוביות ────────────────────────

        scene.geometries.add(
                new Polygon(new Point(89.5, 25, 100), new Point(89.5, 75, 100), new Point(89.5, 75, 0), new Point(89.5, 25, 0))
                        .setEmission(new Color(25, 25, 25)).setMaterial(matMatte),
                new Polygon(new Point(89.0, 30, 95), new Point(89.0, 70, 95), new Point(89.0, 70, 5), new Point(89.0, 30, 5))
                        .setEmission(new Color(30, 70, 140)).setMaterial(matMatte)
        );

        // השטיח נשאר מקדימה עם המגדל (Z=180)
        scene.geometries.add(
                new Cylinder(35, new Ray(new Point(0, 0, 180), new Vector(0, 1, 0)), 0.5)
                        .setEmission(new Color(110, 30, 40)).setMaterial(new Material().setKD(0.8).setKS(0.1))
        );

        scene.geometries.add(
                new Sphere(new Point(0, 98, 100), 10)
                        .setEmission(new Color(255, 255, 180))
        );

        // ── 4. וילונות זיג-זג ──────────────────────────────────
        Color curtainColor = new Color(130, 100, 130);
        scene.geometries.add(
                new Cylinder(0.8, new Ray(new Point(-50, 87, -78), new Vector(1, 0, 0)), 100)
                        .setEmission(new Color(60, 60, 60)).setMaterial(new Material().setKD(0.6).setKS(0.6).setKR(0.03).setShininess(30))
        );

        double zFront = -76, zBack = -78.5;
        double yBottomFront = 25, yBottomBack = 28;
        double yTop = 86, step = 3.0;

        for (double x = -45; x < -25; x += step) {
            scene.geometries.add(
                    new Polygon(new Point(x, yBottomFront, zFront), new Point(x + step / 2, yBottomBack, zBack),
                            new Point(x + step / 2, yTop, zBack), new Point(x, yTop, zFront))
                            .setEmission(curtainColor).setMaterial(matCurtain),
                    new Polygon(new Point(x + step / 2, yBottomBack, zBack), new Point(x + step, yBottomFront, zFront),
                            new Point(x + step, yTop, zFront), new Point(x + step / 2, yTop, zBack))
                            .setEmission(curtainColor).setMaterial(matCurtain)
            );
        }

        for (double x = 25; x < 45; x += step) {
            scene.geometries.add(
                    new Polygon(new Point(x, yBottomFront, zFront), new Point(x + step / 2, yBottomBack, zBack),
                            new Point(x + step / 2, yTop, zBack), new Point(x, yTop, zFront))
                            .setEmission(curtainColor).setMaterial(matCurtain),
                    new Polygon(new Point(x + step / 2, yBottomBack, zBack), new Point(x + step, yBottomFront, zFront),
                            new Point(x + step, yTop, zFront), new Point(x + step / 2, yTop, zBack))
                            .setEmission(curtainColor).setMaterial(matCurtain)
            );
        }

        // ── 5. במת תצוגה עגולה (נשארה מקדימה Z=180) ───────────────────────────────────────────
        scene.geometries.add(
                new Cylinder(22, new Ray(new Point(0, 0, 180), new Vector(0, 1, 0)), 5)
                        .setEmission(new Color(20, 20, 25)).setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30))
        );

        // ── 6. פירמידת כדורי זכוכית (נשארה מקדימה Z=180) ───────────────────────────
        Color[] glassColors = {
                new Color(255, 50, 50), new Color(50, 255, 50), new Color(50, 100, 255),
                new Color(255, 255, 50), new Color(255, 50, 255), new Color(50, 255, 255)
        };

        int layers = 5;
        double r = 4.0;
        double dy = r * Math.sqrt(2);
        double yBase = 5 + r;
        int colorIdx = 0;

        for (int l = 0; l < layers; l++) {
            int n = layers - l;
            double startX = -(n - 1) * r;
            double startZ = -(n - 1) * r + 180;
            double currY = yBase + l * dy;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double cx = startX + i * 2 * r;
                    double cz = startZ + j * 2 * r;
                    scene.geometries.add(
                            new Sphere(new Point(cx, currY, cz), r)
                                    .setEmission(glassColors[colorIdx % glassColors.length].scale(0.35))
                                    .setMaterial(matGlass)
                    );
                    colorIdx++;
                }
            }
        }

        // ── 7. קוביית מתכת קלאסית ונכונה - נדחפה עמוק לאחור (Z = -40) ──
        Point cA = new Point(55, 0, -25),  cB = new Point(70, 0, -40);
        Point cC = new Point(55, 0, -55),  cD = new Point(40, 0, -40);
        Point cE = new Point(55, 25, -25), cF = new Point(70, 25, -40);
        Point cG = new Point(55, 25, -55), cH = new Point(40, 25, -40);

        Color solidMetalColor = new Color(20, 20, 20); // צבע בסיס חלש כדי שהמתכת תבלוט
        scene.geometries.add(
                new Polygon(cA, cB, cF, cE).setEmission(solidMetalColor).setMaterial(matSolidMetal),
                new Polygon(cB, cC, cG, cF).setEmission(solidMetalColor).setMaterial(matSolidMetal),
                new Polygon(cC, cD, cH, cG).setEmission(solidMetalColor).setMaterial(matSolidMetal),
                new Polygon(cD, cA, cE, cH).setEmission(solidMetalColor).setMaterial(matSolidMetal)
        );

        Point pApex = new Point(55, 45, -40);
        Color pyrColor = new Color(10, 80, 255).scale(0.4);
        scene.geometries.add(
                new Triangle(cE, cF, pApex).setEmission(pyrColor).setMaterial(matGlass),
                new Triangle(cF, cG, pApex).setEmission(pyrColor).setMaterial(matGlass),
                new Triangle(cG, cH, pApex).setEmission(pyrColor).setMaterial(matGlass),
                new Triangle(cH, cE, pApex).setEmission(pyrColor).setMaterial(matGlass)
        );

        // ── 8. צילינדר עם כדור אנרגיה - נדחף עמוק לאחור (Z = -40) ────────────────────────────────────
        scene.geometries.add(
                new Cylinder(6, new Ray(new Point(-45, 0, -40), new Vector(0, 1, 0)), 15)
                        .setEmission(new Color(20, 20, 20)).setMaterial(matMatte),
                new Sphere(new Point(-45, 23, -40), 8)
                        .setEmission(new Color(255, 180, 30)).setMaterial(new Material().setKD(0.2).setKS(0.8).setKT(0.5).setShininess(100))
        );

        // ── 9. ציפורים ושמש ──────────────────────────────────────────────
        addBird(scene, -15, 65, -120, 1.5);
        addBird(scene, 5, 70, -130, 1.2);
        addBird(scene, 18, 78, -140, 1.8);

        scene.geometries.add(
                new Sphere(new Point(-30, 85, -250), 18)
                        .setEmission(new Color(180, 160, 60)).setMaterial(new Material().setKD(0).setKS(0))
        );

        // ── 10. מערך תאורה מאוזן ─────────────────────────────────────────
        scene.lights.add(new DirectionalLight(new Color(110, 100, 80), new Vector(-0.2, -0.5, 0.8)));
        // מקור האור הנקודתי של כדור האנרגיה עבר אחורה ל-Z=-40
        scene.lights.add(new PointLight(new Color(255, 180, 50), new Point(-45, 23, -40)).setKl(0.001).setKq(0.0001));
        scene.lights.add(new PointLight(new Color(200, 220, 255), new Point(0, 95, 100)).setKl(0.0001).setKq(0.00001));

        // ── 11. המצלמה והרינדור ──────────────────────
        System.out.println("Starting render...");
        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 50, 450))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(400)
                .setVpSize(200, 200)
                .setResolution(800, 800)
                .setMultithreading(-1)
                .setDebugPrint(0.1)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("Cornell_Box_Base_Picture_Perfect");

        long endTime = System.currentTimeMillis();
        System.out.println("Render time: " + (endTime - startTime) / 1000.0 + " seconds");
    }
}