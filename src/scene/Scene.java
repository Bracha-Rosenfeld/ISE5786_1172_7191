package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * Represents a scene to be rendered.
 * This class is a Passive Data Structure (PDS) that holds all scene attributes.
 */
public final class Scene {

    /**
     * The name of the scene
     */
    public final String name;

    /**
     * The background color of the scene, initialized to BLACK
     */
    public Color background = Color.BLACK;

    /**
     * The ambient light of the scene, initialized to NONE
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * The geometric objects in the scene, initialized to an empty collection
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructs a Scene with the given name.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the background color of the scene.
     *
     * @param background the background color
     * @return the Scene object itself for method chaining
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight the ambient light
     * @return the Scene object itself for method chaining
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometric objects of the scene.
     *
     * @param geometries the collection of geometries
     * @return the Scene object itself for method chaining
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}