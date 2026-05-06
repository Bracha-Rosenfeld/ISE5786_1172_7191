package scene;

import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import primitives.Color;

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

                        switch (shapeType) {
                            case "sphere":
                                primitives.Point center = parsePoint(shape.getAttribute("center"));
                                double radius = Double.parseDouble(shape.getAttribute("radius"));
                                scene.geometries.add(new geometries.impl.Sphere(center, radius));
                                break;

                            case "triangle":
                                primitives.Point p0 = parsePoint(shape.getAttribute("p0"));
                                primitives.Point p1 = parsePoint(shape.getAttribute("p1"));
                                primitives.Point p2 = parsePoint(shape.getAttribute("p2"));
                                scene.geometries.add(new geometries.impl.Triangle(p0, p1, p2));
                                break;
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
     *
     * @param colorStr the RGB string (e.g., "255 191 191")
     * @return the Color object
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
     *
     * @param pointStr the coordinate string (e.g., "0 0 -100")
     * @return the Point object
     */
    private static primitives.Point parsePoint(String pointStr) {
        String[] parts = pointStr.split("\\s+");
        return new primitives.Point(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }
}