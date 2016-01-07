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
    int amount_v = 200;
    int amount_u = 200;
    float size_x = 40;
    float size_y = 40;
    
    float[][] heights = new float[amount_v][amount_u]; // Stores all the heights for all the different point.
    
    /**
     * Pre-computes the height for all points.
     */
    public Terrain() {
        for(int v = 0; v < amount_v; v++) {
            float x = xCoordinate(v);
            for(int u = 0; u < amount_u; u++) {
                float y = yCoordinate(u);
                heights[v][u] = heightAt(x, y);
            }
        }
    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        RobotRace.setMaterial(gl, new float[] {.8f, .8f, .8f, 1f}, 20, "metal");
        
        
        for(int u = 0; u < amount_u - 1; u++) {
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            Vector oldPoint1 = new Vector(0,0,0);
            Vector oldPoint2 = new Vector(0,0,0);
            for(int v = 0; v < amount_v; v++) {
                Vector point1 = Coordinate(v, u);       // Get the coordinate of the first point.
                Vector point2 = Coordinate(v, u + 1);   // Get the coordinate of the second point.
                
                Vector diagonal = point1.subtract(oldPoint2);           // The line from oldP2 to P1.
                Vector newHorizontal = point2.subtract(point1);         // The line from P1 to P2.
                Vector oldHorizontal = oldPoint2.subtract(oldPoint1);   // The line from oldP1 to oldP2.
                
                Vector normal1 = diagonal.cross(oldHorizontal.normalized()); // The normal for the first triangle.
                Vector normal2 = diagonal.cross(newHorizontal).normalized(); // The normal for the second triangle.
                
                setNormal(gl, normal1);      // Set the normal for the first triangle to be drawn.
                drawLineSegment(gl, point1); // Draw the first line segment, creating a new triangle.
                
                setNormal(gl, normal2);      // Set the normal for the second normal to be drawn.
                drawLineSegment(gl, point2); // Draw the second line segment, creating a new triangle.
                
                oldPoint1 = point1;          // Store the value of point1, which will be used by the next calculation.
                oldPoint2 = point2;          // Store the value of point2, which will be used by the next calculation.
            }
            gl.glEnd();
        }
    }
    
    /**
     * Sets the normal vector of the upcoming triangles.
     * 
     * @param GL2     The JOGL 2 object.
     * @param Vector  The normal vector which should be applied.
     */
    public void setNormal(GL2 gl, Vector normal) {
        gl.glNormal3d(normal.x(), normal.y(), normal.z());
    }
    
    /**
     * Draws a line part from the previous line end to the specified point.
     * 
     * @param GL2       The JOGL 2 object.
     * @param Vector    The point where the line should be drawn to. 
     */
    public void drawLineSegment(GL2 gl, Vector point) {
        gl.glVertex3d(point.x(), point.y(), point.z());
    }
    
    /**
     * Calculates a coordinate for a point. Uses the heights array for the z
     * coordinate.
     * 
     * @param int       The v-th point of which the coordinates should be returned.
     * @param int       The u-th point of which the coordinates should be returned.     
     * @return Vector   The coordinates of the point.
     */
    public Vector Coordinate(int v, int u) {
        return new Vector(
                xCoordinate(v),
                yCoordinate(u),
                heights[v][u]
        );
    }
    
    /**
     * Calculates the coordinate of the v-th point.
     * 
     * @param float     The value of v.
     * @return float    The value of the corresponding v-coordinate.
     */
    public float xCoordinate(float v) {
        return ((2 * v / (float) amount_v) - 1f) * size_x; // Calculate the v coordinate.
    }
    
    /**
     * Calculates the coordinate of the u-th point.
     * 
     * @param float     The value of u.
     * @return float    The value of the corresponding u-coordinate.
     */
    public float yCoordinate(float u) {
        return ((2 * u / (float) amount_u) - 1f) * size_y; // Calculate the u coordinate.
    }
    
    /**
     * Computes the elevation of the terrain at (v, u).
     */
    public float heightAt(float v, float u) {
        return (float) (0.6 * cos(0.3 * v + 0.2 * u) + 0.4 * cos(v - 0.5 * u));
    }
}
