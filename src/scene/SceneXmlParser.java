package scene;

import geometries.api.Geometry;
import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import primitives.Color;
import primitives.Double3;
import primitives.Material;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Parses an XML file to construct a Scene object.
 */
public class SceneXmlParser {

    /**
     * Parses an XML file and generates a Scene.
     *
     * @param sceneName the name to give the generated scene
     * @param filePath  the path to the XML file
     * @return the constructed Scene object
     */
    public static Scene parse(String sceneName, String filePath) {
        Scene scene = new Scene(sceneName);

        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element sceneElement = doc.getDocumentElement();

            // 1. Parse Background Color
            String bgColorStr = sceneElement.getAttribute("background-color");
            if (!bgColorStr.isEmpty()) {
                scene.setBackground(parseColor(bgColorStr));
            }

            // 2. Parse Ambient Light
            NodeList ambientList = doc.getElementsByTagName("ambient-light");
            if (ambientList.getLength() > 0) {
                Element ambientElement = (Element) ambientList.item(0);
                String colorStr = ambientElement.getAttribute("color");
                scene.setAmbientLight(new AmbientLight(parseColor(colorStr)));
            }

            // 3. Parse Geometries (Spheres and Triangles)
            NodeList geometriesList = doc.getElementsByTagName("geometries");
            if (geometriesList.getLength() > 0) {
                Element geometriesElement = (Element) geometriesList.item(0);
                org.w3c.dom.NodeList shapes = geometriesElement.getChildNodes();

                for (int i = 0; i < shapes.getLength(); i++) {
                    if (shapes.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element shape = (Element) shapes.item(i);
                        String shapeType = shape.getTagName();

                        Geometry geom = null;

                        switch (shapeType) {
                            case "sphere":
                                primitives.Point center = parsePoint(shape.getAttribute("center"));
                                double radius = Double.parseDouble(shape.getAttribute("radius"));
                                geom = new geometries.impl.Sphere(center, radius);
                                break;

                            case "triangle":
                                primitives.Point p0 = parsePoint(shape.getAttribute("p0"));
                                primitives.Point p1 = parsePoint(shape.getAttribute("p1"));
                                primitives.Point p2 = parsePoint(shape.getAttribute("p2"));
                                geom = new geometries.impl.Triangle(p0, p1, p2);
                                break;
                        }

                        if (geom != null) {
                            String emissionStr = shape.getAttribute("emission");
                            if (!emissionStr.isEmpty()) {
                                geom.setEmission(parseColor(emissionStr));
                            }

                            String kAStr = shape.getAttribute("kA");
                            if (!kAStr.isEmpty()) {
                                geom.setMaterial(new Material().setKa(parseDouble3(kAStr)));
                            }

                            scene.geometries.add(geom);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return scene;
    }

    /**
     * Helper method to parse a space-separated RGB string into a Color object.
     */
    private static Color parseColor(String colorStr) {
        String[] parts = colorStr.split("\\s+");
        return new Color(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }

    /**
     * Helper method to parse a space-separated string into a Point object.
     */
    private static primitives.Point parsePoint(String pointStr) {
        String[] parts = pointStr.split("\\s+");
        return new primitives.Point(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }

    /**
     * Helper method to parse a space-separated string into a Double3 object.
     * Supports both a single value (e.g., "0.4") and three values (e.g., "0 0.8 0").
     */
    private static Double3 parseDouble3(String str) {
        String[] parts = str.trim().split("\\s+");
        if (parts.length == 1) {
            return new Double3(Double.parseDouble(parts[0]));
        } else if (parts.length == 3) {
            return new Double3(
                    Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2])
            );
        }
        throw new IllegalArgumentException("Invalid Double3 format: " + str);
    }
}