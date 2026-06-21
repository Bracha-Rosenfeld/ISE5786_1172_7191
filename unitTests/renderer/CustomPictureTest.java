package renderer;

import geometries.impl.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * סצנת חדר Cornell Box מתקדמת (בסגנון "דיורמה" ישרה ואסתטית).
 * שימוש בעדשת "טלה-פוטו" (VpDistance גבוה) כדי למנוע עיוותי פרספקטיבה,
 * מה שגורם לקירות החדר להיראות ישרים לחלוטין למרות שהם חתוכים.
 */
public class CustomPictureTest {

    /**
     * פונקציית עזר להוספת צללית של ציפור (צורה של V אסתטי מ-2 משולשים)
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

    @Test
    public void customPicture() {
        Scene scene = new Scene("Beautiful Room with Spheres Tower")
                .setBackground(new Color(170, 220, 255))
                .setAmbientLight(new AmbientLight(new Color(255, 255, 255), new Double3(0.05)));

        // ── 1. חומרים וצבעים ─────────────
        Material matMatte = new Material().setKD(0.6).setKS(0.1).setShininess(10);
        Material matFloor = new Material().setKD(0.5).setKS(0.5).setKR(0.25).setShininess(50);

        // מתכת חלקה ומשתקפת לחלוטין עבור הקובייה (אפקט מראה)
        Material matSolidMetal = new Material().setKD(0.05).setKS(0.95).setKR(0.95).setShininess(100);

        Material matGlass = new Material().setKD(0.1).setKS(0.8).setKT(0.6).setKR(0.1).setShininess(100);
        Material matCurtain = new Material().setKD(0.6).setKS(0.1).setShininess(5);

        Color colLeftWall = new Color(110, 30, 30);
        Color colRightWall = new Color(30, 110, 30);
        Color colCeiling = new Color(150, 150, 150);
        Color colBackWall = new Color(150, 150, 150);
        Color colFloor = new Color(30, 30, 35);

        // ── 2. קירות החדר (חתוכים ב-Z=90 כדי לייצר קופסה ישרה) ────────────
        scene.geometries.add(
                new Polygon(new Point(-80, 0, 90), new Point(80, 0, 90), new Point(80, 0, -80), new Point(-80, 0, -80))
                        .setEmission(colFloor).setMaterial(matFloor),
                new Polygon(new Point(-80, 100, -80), new Point(80, 100, -80), new Point(80, 100, 90), new Point(-80, 100, 90))
                        .setEmission(colCeiling).setMaterial(matMatte),
                new Polygon(new Point(-80, 0, -80), new Point(-80, 100, -80), new Point(-80, 100, 90), new Point(-80, 0, 90))
                        .setEmission(colLeftWall).setMaterial(matMatte),
                new Polygon(new Point(80, 0, 90), new Point(80, 100, 90), new Point(80, 100, -80), new Point(80, 0, -80))
                        .setEmission(colRightWall).setMaterial(matMatte)
        );

        // ── 3. הקיר האחורי והחלון ─────────────────────────────────────────
        scene.geometries.add(
                new Polygon(new Point(-80, 0, -80), new Point(80, 0, -80), new Point(80, 25, -80), new Point(-80, 25, -80))
                        .setEmission(colBackWall).setMaterial(matMatte),
                new Polygon(new Point(-80, 85, -80), new Point(80, 85, -80), new Point(80, 100, -80), new Point(-80, 100, -80))
                        .setEmission(colBackWall).setMaterial(matMatte),
                new Polygon(new Point(-80, 25, -80), new Point(-45, 25, -80), new Point(-45, 85, -80), new Point(-80, 85, -80))
                        .setEmission(colBackWall).setMaterial(matMatte),
                new Polygon(new Point(45, 25, -80), new Point(80, 25, -80), new Point(80, 85, -80), new Point(45, 85, -80))
                        .setEmission(colBackWall).setMaterial(matMatte)
        );

        Color frameColor = new Color(40, 20, 10);
        scene.geometries.add(
                new Cylinder(1.5, new Ray(new Point(-45, 25, -80), new Vector(1, 0, 0)), 90).setEmission(frameColor).setMaterial(matMatte),
                new Cylinder(1.5, new Ray(new Point(-45, 85, -80), new Vector(1, 0, 0)), 90).setEmission(frameColor).setMaterial(matMatte),
                new Cylinder(1.5, new Ray(new Point(-45, 25, -80), new Vector(0, 1, 0)), 60).setEmission(frameColor).setMaterial(matMatte),
                new Cylinder(1.5, new Ray(new Point(45, 25, -80), new Vector(0, 1, 0)), 60).setEmission(frameColor).setMaterial(matMatte)
        );

        // ── 4. וילונות זיג-זג מציאותיים ──────────────────────────────────
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

        // ── 5. במת תצוגה עגולה ───────────────────────────────────────────
        // הבמה קודמה ל-Z=65 (הרדיוס 22, לכן מגיעה ל-Z=87 ועדיין בתוך החדר)
        scene.geometries.add(
                new Cylinder(22, new Ray(new Point(0, 0, 65), new Vector(0, 1, 0)), 5)
                        .setEmission(new Color(20, 20, 25)).setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30))
        );

        // ── 6. פירמידת כדורי זכוכית בקדמת הבמה ───────────────────────────
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
            // הותאם ל-Z=65
            double startZ = -(n - 1) * r + 65;
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

        // ── 7. קוביית פלדה כבדה ואטומה (כעת עם אפקט מראה מתכתי) ───────────
        Point cA = new Point(45, 0, 25),  cB = new Point(65, 0, 25);
        Point cC = new Point(65, 0, 5),   cD = new Point(45, 0, 5);
        Point cE = new Point(45, 25, 25), cF = new Point(65, 25, 25);
        Point cG = new Point(65, 25, 5),  cH = new Point(45, 25, 5);

        Color solidMetalColor = new Color(80, 85, 90);
        scene.geometries.add(
                new Polygon(cA, cB, cF, cE).setEmission(solidMetalColor).setMaterial(matSolidMetal),
                new Polygon(cB, cC, cG, cF).setEmission(solidMetalColor).setMaterial(matSolidMetal),
                new Polygon(cC, cD, cH, cG).setEmission(solidMetalColor).setMaterial(matSolidMetal),
                new Polygon(cD, cA, cE, cH).setEmission(solidMetalColor).setMaterial(matSolidMetal),
                new Polygon(cE, cF, cG, cH).setEmission(solidMetalColor).setMaterial(matSolidMetal)
        );

        Point pApex = new Point(55, 45, 15);
        Color pyrColor = new Color(10, 80, 255).scale(0.4);
        scene.geometries.add(
                new Triangle(cE, cF, pApex).setEmission(pyrColor).setMaterial(matGlass),
                new Triangle(cF, cG, pApex).setEmission(pyrColor).setMaterial(matGlass),
                new Triangle(cG, cH, pApex).setEmission(pyrColor).setMaterial(matGlass),
                new Triangle(cH, cE, pApex).setEmission(pyrColor).setMaterial(matGlass)
        );

        // ── 8. צילינדר עם כדור אנרגיה ────────────────────────────────────
        scene.geometries.add(
                new Cylinder(6, new Ray(new Point(-45, 0, 30), new Vector(0, 1, 0)), 15)
                        .setEmission(new Color(20, 20, 20)).setMaterial(matMatte),
                new Sphere(new Point(-45, 23, 30), 8)
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
        scene.lights.add(new PointLight(new Color(255, 180, 50), new Point(-45, 23, 30)).setKl(0.001).setKq(0.0001));
        scene.lights.add(new SpotLight(new Color(200, 220, 255), new Point(0, 95, 100), new Vector(0, -1, -0.6)).setKl(0.0001).setKq(0.00001));

        // ── 11. המצלמה (עדשת טלה-פוטו ליצירת דיורמה ישרה לגמרי!) ──────────
        Camera.getBuilder()
                .setLocation(new Point(0, 50, 450))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(400)
                // הוקטן מ-220 ל-180 - יגרום לזום-אין שמקטין את המסגרת הכחולה וחושף יותר חדר
                .setVpSize(180, 180)
                .setResolution(1000, 1000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("Cornell_Box_Final_Straight_Diorama");
    }
}