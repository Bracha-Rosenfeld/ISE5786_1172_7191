package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;
import renderer.ImageWriter;

/**
 * Testing the ImageWriter class.
 */
class ImageWriterTests {

    /** Image width in pixels */
    private static final int NX = 800;
    /** Image height in pixels */
    private static final int NY = 500;
    /** Grid square size in pixels */
    private static final int STEP = 50;

    /** Background color of the image */
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 0); // Yellow
    /** Color of the grid lines */
    private static final Color GRID_COLOR = new Color(255, 0, 0); // Red

    /**
     * Test method for generating a basic grid image.
     */
    @Test
    void testImageWriter() {
        ImageWriter imageWriter = new ImageWriter(NX, NY);

        // A single loop with one nested loop to color all pixels
        for (int i = 0; i < NX; i++) {
            for (int j = 0; j < NY; j++) {
                // Using a ternary operator to avoid code duplication and check if the pixel is on the grid
                imageWriter.writePixel(i, j, i % STEP == 0 || j % STEP == 0 ? GRID_COLOR : BACKGROUND_COLOR);
            }
        }

        // Write the final image to the images folder
        imageWriter.writeToImage("testImageWriter");
    }
}