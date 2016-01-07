package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import static java.lang.Math.*;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of the terrain.
 */
class Terrain {
    int amount_x = 100;
    int amount_y = 100;
    float size_x = 40;
    float size_y = 40;
    
    float[][] heights = new float[amount_x][amount_y];
    /**
     * Can be used to set up a display list.
     */
    public Terrain() {
        for(int x = 0; x < amount_x; x++) {
            float x_cor = xCoordinate(x);
            for(int y = 0; y < amount_y; y++) {
                float y_cor = yCoordinate(y);
                heights[x][y] = heightAt(x_cor, y_cor);
            }
        }
    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        gl.glBegin(gl.GL_TRIANGLE_STRIP);
        for(int y = 0; y < amount_y - 1; y++) {
            for(int x = 0; x < amount_x; x++) {
                drawPoint(gl, x, y);
                drawPoint(gl, x, y+1);
            }
        }
        gl.glEnd();
    }

    public void drawPoint(GL2 gl, int x, int y) {
        gl.glVertex3d(xCoordinate(x), yCoordinate(y), heights[x][y]);
    }
    
    public float xCoordinate(int x) {
        return ((2 * x / (float) amount_x) - 1f) * size_x; // Calculate the x coordinate.
    }
    
    public float yCoordinate(int y) {
        return ((2 * y / (float) amount_y) - 1f) * size_y; // Calculate the y coordinate.
    }
    
    /**
     * Computes the elevation of the terrain at (x, y).
     */
    public float heightAt(float x, float y) {
        return (float) (0.6 * cos(0.3 * x + 0.2 * y) + 0.4 * cos(x - 0.5 * y));
    }
}
