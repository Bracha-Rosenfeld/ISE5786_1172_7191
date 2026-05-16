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
    /** Diffuse attenuation factor */
    public Double3 kD = Double3.ZERO;
    /** Specular attenuation factor */
    public Double3 kS = Double3.ZERO;
    /** Shininess factor (determines the size of the specular highlight) */
    public int nShininess = 0;

    /**
     * Sets the diffuse attenuation factor.
     * @param kD the diffuse factor as a Double3
     * @return the Material object itself for method chaining
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the diffuse attenuation factor.
     * @param kD the diffuse factor as a scalar double
     * @return the Material object itself for method chaining
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the specular attenuation factor.
     * @param kS the specular factor as a Double3
     * @return the Material object itself for method chaining
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the specular attenuation factor.
     * @param kS the specular factor as a scalar double
     * @return the Material object itself for method chaining
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the shininess factor of the material.
     * @param nShininess the shininess factor
     * @return the Material object itself for method chaining
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}