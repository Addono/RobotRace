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
    int amount_x = 200;
    int amount_y = 200;
    float size_x = 40;
    float size_y = 40;
    
    float[][] heights = new float[amount_x][amount_y]; // Stores all the heights for all the different point.
    
    /**
     * Pre-computes the height for all points.
     * 
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
        RobotRace.setMaterial(gl, new float[] {.8f, .8f, .8f, 1f}, 20, "metal");
        
        
        for(int y = 0; y < amount_y - 1; y++) {
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            Vector oldPoint1 = new Vector(0,0,0);
            Vector oldPoint2 = new Vector(0,0,0);
            for(int x = 0; x < amount_x; x++) {
                Vector point1 = Coordinate(x, y);
                Vector point2 = Coordinate(x, y + 1);
                
                Vector diagonal = point1.subtract(oldPoint2);           // The line from oldP2 to P1.
                Vector newHorizontal = point2.subtract(point1);         // The line from P1 to P2.
                Vector oldHorizontal = oldPoint2.subtract(oldPoint1);   // The line from oldP1 to oldP2.
                
                Vector normal1 = diagonal.cross(oldHorizontal.normalized()); // The normal for the first triangle.
                Vector normal2 = diagonal.cross(newHorizontal).normalized(); // The normal for the second triangle.
                
                setNormal(gl, normal1);
                drawLineSegment(gl, point1); // Draw the first line segment, creating a new triangle.
                
                setNormal(gl, normal2);
                drawLineSegment(gl, point2); // Draw the second line segment, creating a new triangle.
                
                oldPoint1 = point1;
                oldPoint2 = point2;
            }
            gl.glEnd();
        }
    }
    
    public void setNormal(GL2 gl, Vector normal) {
        gl.glNormal3d(normal.x(), normal.y(), normal.z());
    }

    public void drawLineSegment(GL2 gl, Vector point) {
        gl.glVertex3d(point.x(), point.y(), point.z());
    }
    
    public Vector Coordinate(int x, int y) {
        return new Vector(
                xCoordinate(x),
                yCoordinate(y),
                heights[x][y]
        );
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
