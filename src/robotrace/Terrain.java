package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import static java.lang.Math.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    int texture;
    
    float[][] heights = new float[amount_v + 1][amount_u + 1]; // Stores all the heights for all the different point.
    
    /**
     * Pre-computes the height for all points.
     */
    public Terrain() {
        for(int v = 0; v < amount_v + 1; v++) {
            float x = xCoordinate(v);
            for(int u = 0; u < amount_u + 1; u++) {
                float y = yCoordinate(u);
                heights[v][u] = heightAt(x, y);
            }
        }
    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        RobotRace.setMaterial(gl, new float[] {1f, 1f, 1f, 1f}, 100f, "metal");
        
        // Enable 1D texturing and bind the 1D terrain texture.
        gl.glDisable(gl.GL_TEXTURE_2D);
        gl.glEnable(gl.GL_TEXTURE_1D);
        
        gl.glBindTexture(gl.GL_TEXTURE_1D, RobotRace.terrainTexture);
        gl.glTexParameteri(gl.GL_TEXTURE_1D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP);
        
        // Draw the entire terrain strip by strip.
        for(int u = 0; u < amount_u; u++) {            
            // Initialise the start values.
            Vector oldPoint1 = Coordinate(0, u);
            Vector oldPoint2 = Coordinate(0, u + 1);
            
            double scaledHeight = (oldPoint2.z() + 1) / 2;
            gl.glTexCoord1d(scaledHeight);
            
            // Draw one strip of triangles.
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int v = 0; v < amount_v + 1; v++) {
                Vector point1 = Coordinate(v, u);       // Get the coordinate of the first point.
                Vector point2 = Coordinate(v, u + 1);   // Get the coordinate of the second point.
                
                Vector diagonal = point1.subtract(oldPoint2);           // The line from oldP2 to P1.
                Vector newHorizontal = point2.subtract(point1);         // The line from P1 to P2.
                Vector oldHorizontal = oldPoint2.subtract(oldPoint1);   // The line from oldP1 to oldP2.
                
                Vector normal1 = diagonal.cross(oldHorizontal.normalized()); // The normal for the first triangle.
                Vector normal2 = diagonal.cross(newHorizontal).normalized(); // The normal for the second triangle.
                
                drawTriangle(gl, normal1, point1);
                drawTriangle(gl, normal2, point2);
                
                oldPoint1 = point1;          // Store the value of point1, which will be used by the next calculation.
                oldPoint2 = point2;          // Store the value of point2, which will be used by the next calculation.
            }
            
            gl.glEnd();
        }
        
        
        
        gl.glDisable(gl.GL_TEXTURE_1D);
        
        //draw trees
             float width = 3;
             float height = 10;
             gl.glPushMatrix();
             gl.glTranslatef(3.5f, 4f, 0.5f);
             drawTree(gl,glu,glut,width,height);
             gl.glTranslatef(13f,13f, 0);
             width +=1;
             height +=2;
             drawTree(gl,glu,glut,(float)width,height);
             gl.glTranslatef(-34f,0f, -0.2f);
             drawTree(gl,glu,glut,(float)width,height);
             gl.glPopMatrix();
        /** 
         * Generates limit amount of transparant layers on top of each other.
         * Generating a more smooth transition from ground to water on the edges,
         * although still far from realistic, since this method doesn't take
         * the distance looked through the water into account, but only the
         * depth. The effect is most visible on the corners of the terrain with water.
         *
         * Looking at it from below will not make it visible - except at the edges 
         * from some angles - since it is rendered bottom to top, independent from
         * the position of the camera. Resulting in the depth-buffer preventing
         * the upper layers from being drawn.
        */
        for(int i = 0, limit = 40; i < limit; i++) {
            gl.glPushMatrix();
            
            double layerHeight = -abs(pow(1f / (-((double) i / (double) limit) - 1f), 3)  + .25f * cos(tAnim / 1.5f)); // Use -|1/(-(i/lim)-1)| for the height of the layer, and a sinusoid with tAnim to let the water breath.
            gl.glTranslatef(xCoordinate(0), yCoordinate(0), (float) layerHeight); // Move local coordinate system to the right position.
            
            RobotRace.setMaterial(gl, 
                    new float[] {0.2f, 0.2f, .2f, 1.2f / (float) limit},  // Set the color and transparity of (one layer of) water.
                    1f * ((float) (limit - i) / (float) limit),          
                    "plastic");
            
            
            
            // Draw the water as one large plane.
            gl.glBegin(gl.GL_QUADS);
                gl.glNormal3f(0f, 0f, 1f);
                gl.glVertex3d(0f, 0f, 0f);
                gl.glVertex3d(0f, size_y, 0f);
                gl.glVertex3d(size_x, size_y, 0f);
                gl.glVertex3d(size_x, 0f, 0f);
            gl.glEnd();
            
            gl.glPopMatrix();
        }
        
        
        
    }
    
    public void drawTree(GL2 gl,GLU glu, GLUT glut, float width, float height){
        
        
        RobotRace.setMaterial(gl, 0.41f, 0.12f, 0.0f, 1.0f, 10, "plastic");
        gl.glPushMatrix();
            glut.glutSolidCylinder((float)width/10, (float)height/10, 30, 30);
            gl.glTranslatef(0, 0, 1);
            RobotRace.setMaterial(gl, 0f, 1.0f, 0f, 1.0f, 10, "plastic");
            glut.glutSolidCone((float)width/2, (float)height/6, 20,2);
            gl.glTranslatef(0, 0, 0.8f);
            glut.glutSolidCone((float)width/2.6, (float)height/7, 20,2);
            gl.glTranslatef(0, 0, 0.8f);
            glut.glutSolidCone((float)width/4, (float)height/9, 20,2);
        gl.glPopMatrix();
        
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
    
    public void drawTriangle(GL2 gl, Vector normal, Vector newPoint) {
        // Texture mapping.
        double scaledHeight = (newPoint.z() + 1.01f) / 2.02f; // Height on the texture {0, 1} (in stead of [0,1], because this resulted in (semi) transparant parts on the extreems).
        gl.glTexCoord1d(scaledHeight); // Map the new point to the 1D texture.
        
        // Set the normal.
        setNormal(gl, normal);
        
        // Draw the line to the new point.
        drawLineSegment(gl, newPoint);
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
     * Draws a line part from the previous line end to the specified newPoint.
     * 
     * @param GL2       The JOGL 2 object.
     * @param Vector    The newPoint where the line should be drawn to. 
     */
    public void drawLineSegment(GL2 gl, Vector newPoint) {
        gl.glVertex3d(newPoint.x(), newPoint.y(), newPoint.z());
    }
    
    /**
     * Calculates the coordinate of the v-th point.
     * 
     * @param float     The value of v.
     * @return float    The value of the corresponding v-coordinate.
     */
    public float xCoordinate(float v) {
        return ((v / (float) amount_v) - .5f) * size_x; // Calculate the v coordinate.
    }
    
    /**
     * Calculates the coordinate of the u-th point.
     * 
     * @param float     The value of u.
     * @return float    The value of the corresponding u-coordinate.
     */
    public float yCoordinate(float u) {
        return ((u / (float) amount_u) - .5f) * size_y; // Calculate the u coordinate.
    }
    
    /**
     * Computes the elevation of the terrain at (v, u).
     */
    public float heightAt(float v, float u) {
        return (float) (0.8 * cos(0.1 * v + 0.1 * u) * sin(.3 * v) + 0.2 * cos(.2 * v - 0.5 * u) * sin(.4 * u));
    }
}
