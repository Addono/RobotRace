package robotrace;

/**
* Materials that can be used for the robots.
*/
public enum Material {

    /** 
     * Gold material properties.
     * Modify the default values to make it look like gold.
     */
    GOLD (
        new float[] {1.0f, .95f, 0.1f, 1.0f},
        new float[] {.9f, .9f, .9f, 1.0f},
        new float[] {0.8f, 0.8f, 0.8f, 1.0f},
        new float[] {0.0f, 0.0f, 0.0f, 1.0f},
        0f),

    /**
     * Silver material properties.
     * Modify the default values to make it look like silver.
     */
    SILVER (
        new float[] {0.85f, 0.8f, 0.8f, 1.0f},
        new float[] {.3f, .3f, .3f, 1.0f},
        new float[] {0.8f, 0.8f, 0.8f, 1.0f},
        new float[] {0.0f, 0.0f, 0.0f, 1.0f},
        0f),

    /** 
     * Wood material properties.
     * Modify the default values to make it look like wood.
     */
    WOOD (
        new float[] {153f/255f, 51f/255f, 0f, 1.0f},
        new float[] {60f/255f, 15f/255f, 0f, 1.0f},
        new float[] {0.8f, 0.8f, 0.8f, 1.0f},
        new float[] {0.0f, 0.0f, 0.0f, 1.0f},
        0f),

    /**
     * Orange material properties.
     * Modify the default values to make it look like orange.
     */
    ORANGE (
        new float[] {1f, 0.40f, 0.0f, 1.0f},
        new float[] {.7f, .7f, .7f, 1.0f},
        new float[] {0.8f, 0.8f, 0.8f, 1.0f},
        new float[] {0.0f, 0.0f, 0.0f, 1.0f},
        0f);

    float[] outerColor;
    
    float[] centerColor;
    
    /** The diffuse RGBA reflectance of the material. */
    float[] diffuse;

    /** The specular RGBA reflectance of the material. */
    float[] specular;
    
    /** The specular exponent of the material. */
    float shininess;

    /**
     * Constructs a new material with diffuse and specular properties.
     */
    private Material(float[] outerColor, float[] centerColor, float[] diffuse, float[] specular, float shininess) {
        this.outerColor = outerColor;
        this.centerColor = centerColor;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
}
