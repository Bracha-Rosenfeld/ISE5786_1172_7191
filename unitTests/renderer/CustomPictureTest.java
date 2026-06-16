package renderer;

import geometries.impl.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * סצנה גיאומטרית מודרנית - גרסת פירמידה תלת-ממדית מובהקת.
 * בנייה מתמטית מחדש של הפירמידה מאחור כדי להבטיח נפח 3D אמיתי וחד.
 */
public class CustomPictureTest {

    @Test
    public void customPicture() {
        // יצירת סצנה עם רקע ניטרלי ותאורת סביבה
        Scene scene = new Scene("Ultimate 3D Pyramid Fixed")
                .setBackground(new Color(20, 22, 35))
                .setAmbientLight(new AmbientLight(new Color(255, 255, 255), new Double3(0.05)));

        // ── חומרים צבעוניים ומחזירי אור ─────────────────────────────────────────
        Material matFloor    = new Material().setKD(0.5).setKS(0.5).setKR(0.25).setShininess(60);
        Material matMetalGlass = new Material().setKD(0.15).setKS(0.95).setKR(0.85).setShininess(140);
        Material matSolidYel  = new Material().setKD(0.85).setKS(0.4).setShininess(50);
        Material matSolidCyan = new Material().setKD(0.7).setKS(0.5).setShininess(70);
        Material matPyramid   = new Material().setKD(0.85).setKS(0.6).setShininess(90); // ברק גבוה להדגשת קווי ה-3D
        Material matForeground = new Material().setKD(0.7).setKS(0.5).setShininess(60);

        // *** בנייה מתמטית מחדש: פירמידה תלת-ממדית (3D) אמיתית ומסיבית הכי מאחורה (Z = -480) ***
        Point pApex           = new Point(-50, 85, -480);  // קודקוד עליון גבוה ומרכזי
        Point pBaseFrontLeft  = new Point(-100, -60, -420); // נקודה קדמית שמאלית (קרובה יותר)
        Point pBaseFrontRight = new Point(-20, -60, -440);  // נקודה קדמית ימנית (יוצרת את חזית הפירמידה)
        Point pBaseBack       = new Point(-60, -60, -540);  // נקודה אחורית עמוקה (יוצרת את הנפח והעובי הצידי)

        scene.geometries.add(

                // 1. רצפה רפלקטיבית מוארת
                new Plane(new Point(0, -60, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(30, 35, 50))
                        .setMaterial(matFloor),


                // 2. גופים אחוריים - מסודרים בשכבות עומק ברורות ────────────────────────

                // *** פירמידה תלת-ממדית (3D) סגורה בעלת נפח פיזי בולט ***
                // פאה שמאל-קדמית (פונה שמאלה וקדימה)
                new Triangle(pApex, pBaseFrontLeft, pBaseFrontRight)
                        .setEmission(new Color(240, 40, 90)) // ורוד ניאון בהיר ומואר
                        .setMaterial(matPyramid),

                // פאה ימין-אחורית (פונה ימינה ואחורה)
                new Triangle(pApex, pBaseFrontRight, pBaseBack)
                        .setEmission(new Color(150, 20, 85)) // סגול-מגנטה כהה יותר (אזור צל חלקי)
                        .setMaterial(matPyramid),

                // פאה שמאל-אחורית (הפאה הנסתרת/המוצלת)
                new Triangle(pApex, pBaseBack, pBaseFrontLeft)
                        .setEmission(new Color(95, 10, 50)) // בורדו עמוק וכהה (צל מלא)
                        .setMaterial(matPyramid),

                // פאת בסיס תחתונה לסגירה הרמטית של נפח ה-3D
                new Triangle(pBaseFrontLeft, pBaseFrontRight, pBaseBack)
                        .setEmission(new Color(70, 5, 35))
                        .setMaterial(matPyramid),

                // הכדור המתכתי - ממוקם בימין האמצעי (X = 60), לפני הפירמידה ומאחורי הגליל (Z = -320)
                new Sphere(new Point(60, -15, -320), 45)
                        .setEmission(new Color(10, 45, 115))
                        .setMaterial(matMetalGlass),

                // הגליל הצהוב - מיושר לחלוטין, מורחק בצד ימין הקיצוני (X = 130, Z = -200)
                new Cylinder(35, new Ray(new Point(130, -60, -200), new Vector(0, 1, 0)), 65)
                        .setEmission(new Color(240, 180, 20))
                        .setMaterial(matSolidYel),

                // פאת קצה עליונה מיושרת לגליל הצהוב למראה נפח סגור ומקצועי
                new Polygon(
                        new Point(130, 5, -165),
                        new Point(165, 5, -200),
                        new Point(130, 5, -235),
                        new Point(95, 5, -200)
                ).setEmission(new Color(255, 200, 40)).setMaterial(matSolidYel),

                // אלמנט מרכז-שמאל: פלטה/דיסק טורקיז בקדמה היחסית של הגופים האחוריים (Z = -160)
                new Cylinder(12, new Ray(new Point(-55, -60, -160), new Vector(0, 1, 0)), 35)
                        .setEmission(new Color(120, 60, 160))
                        .setMaterial(matSolidCyan),

                // אלמנט רקע משלים: עמוד צילינדר גבוה ודק בצד שמאל הקיצוני (Z = -300)
                new Cylinder(130, new Ray(new Point(-130, -60, -300), new Vector(0, 1, 0)), 12)
                        .setEmission(new Color(20, 180, 180))
                        .setMaterial(matSolidCyan),


                // 3. גופים קטנים בקדמת המסך (Foreground) ──────────────────
                // קרובים, מופרדים ומייצרים צלליות הצטלבויות חדות

                // כדור קטן בצד שמאל קרוב
                new Sphere(new Point(-30, -48, -75), 11)
                        .setEmission(new Color(220, 60, 60))
                        .setMaterial(matForeground),

                // גליל קטן ונמוך מיושר בצד ימין קרוב
                new Cylinder(10, new Ray(new Point(35, -60, -70), new Vector(0, 1, 0)), 15)
                        .setEmission(new Color(40, 200, 120))
                        .setMaterial(matForeground)
        );

        // ── מערך תאורה ממוקד להדגשת נפח 3D וצללים מוצלבים ──────────────────
        scene.lights.add(new DirectionalLight(
                new Color(140, 140, 170),
                new Vector(-1, -1.2, -1)));

        // תאורת פוינט חזקה משמאל שדואגת להצליל את הפאה האחורית של הפירמידה
        scene.lights.add(new PointLight(
                new Color(255, 60, 180),
                new Point(-190, 45, -180))
                .setKl(0.0002).setKq(0.00003));

        scene.lights.add(new SpotLight(
                new Color(100, 255, 255),
                new Point(10, 180, -90),
                new Vector(-0.1, -1, -0.1))
                .setKl(0.0001).setKq(0.00002)
                .setNarrowBeam(1.2));

        // ── מצלמה בפרספקטיבה גבוהה החושפת את השכבות והנפח ──────────────────
        Camera.getBuilder()
                .setLocation(new Point(15, 65, 250))
                .setDirection(new Vector(-0.05, -0.25, -1), new Vector(0, 1, 0))
                .setVpDistance(145)
                .setVpSize(200, 200)
                .setResolution(1000, 1000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("brightModernScene3D");
    }
}