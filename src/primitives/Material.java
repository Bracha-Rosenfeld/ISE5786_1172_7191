package primitives;

/**
 * Passive Data Structure (PDS) representing the material properties of a geometry.
 */
public class Material {
    /**
     * Ambient light attenuation factor
     */
    public Double3 kA = Double3.ONE;

    /**
     * Sets the ambient attenuation factor kA using a Double3 value.
     *
     * @param kA the kA value to set
     * @return this material object (Builder pattern)
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the ambient attenuation factor kA using a double value for all components.
     *
     * @param kA the kA value to set
     * @return this material object (Builder pattern)
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}