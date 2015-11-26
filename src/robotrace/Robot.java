package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.util.Random;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    Random rn = new Random();
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
    private final Material material;
    
    float[] centerColor = {.7f, .7f, .7f, 1.0f};
    float[] outerColor = {rn.nextFloat() * .8f + .2f, rn.nextFloat() * .8f + .2f, rn.nextFloat() * .8f + .2f, 1.0f};

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material
        /* add other parameters that characterize this robot */) {
        this.material = material;

        // code goes here ...
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) { 
        gl.glPushMatrix();
        drawLeg(gl, glut, stickFigure, tAnim);
        gl.glPopMatrix();
    }
    
    public void drawArm(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        
        gl.glPushMatrix();
        gl.glScalef(1, 1, 4);
        unitTriangularPrism(gl, true);
        gl.glPopMatrix();
       
    }
    
    public void drawLeg(GL2 gl, GLUT glut, boolean stickFigure, float tAnim) {
        drawFeet(gl, glut, .2f, .3f, stickFigure, tAnim);
        
        gl.glPushMatrix();
            // Leg creation here
        gl.glPopMatrix();
    }
    
    public void drawFeet(GL2 gl, GLUT glut, float height, float width, boolean stickFigure, float tAnim) {        
        // Create the base. First the ones along the x-axis.
        gl.glPushMatrix();
            gl.glTranslatef(-.5f * width, .5f * width, 0.0f);
            gl.glRotatef(90f, 0f, 1f, 0f);
            drawPart(gl, glut, width / 10, width, stickFigure);
            gl.glTranslatef(0.0f, -width, 0.0f);
            drawPart(gl, glut, width / 10, width, stickFigure);
        gl.glPopMatrix();
            
        // Create the second half of the base, the two along the y-axis.
        gl.glPushMatrix();
            gl.glTranslatef(-.5f * width, .5f * width, 0.0f);
            gl.glRotatef(90f, 1f, 0f, 0f);
            gl.glRotatef(90f, 0f, 0f, 1f);
            drawPart(gl, glut, width / 10, width, stickFigure);
            gl.glTranslatef(0.0f, -width, 0.0f);
            drawPart(gl, glut, width / 10, width, stickFigure);
        gl.glPopMatrix();
        
        
        float o = height;
        float a = (float) Math.sqrt(2 * Math.pow(width / 2, 2));
        float s = (float) (Math.sqrt(Math.pow(o, 2) + Math.pow(a, 2)));
        float angle = (float) (Math.atan(a / o) * 180 / Math.PI);
        
        for(int i = 0; i < 360; i += 90) {
            gl.glPushMatrix();
                gl.glRotatef(i, 0.0f, 0.0f, 1.0f);
                gl.glTranslatef(-.5f * width, -.5f * width, 0f);
                gl.glRotatef(45, 0f, 0f, 1f);
                gl.glRotatef(angle, 0f, 1f, 0f);
                drawPart(gl, glut, width / 10, s, stickFigure);
            gl.glPopMatrix();
        }
    }
    
    public void drawPart(GL2 gl, GLUT glut, float centerWidth, float length, float[] rgbaCenter, float[] rgbaOuter, boolean stickFigure) {
        if(stickFigure) {
            gl.glLineWidth(4.0f);
            gl.glColor3i(1, 1, 1); // Set wireFrame color to white
            
            gl.glBegin(gl.GL_LINES);
                gl.glVertex3f(0f, 0f, 0f);
                gl.glVertex3f(0f, 0f, length);
            gl.glEnd();
        } else {
            // Prevents the center of getting wider than the function will support (function designed to allow up to .5f).
            if(centerWidth > length / 2) {
                //centerWidth = length / 2;
            }
            
            float cylinderExceed = 1.2f; // As a factor (so 1.0f means that it will fit exactly).
            float cylinderRadius = (float) Math.sqrt(2 * Math.pow(centerWidth / 2, 2)) * cylinderExceed; // Pythagoras on the 'radius' of the cube.
            float cylinderHeight = length - centerWidth * 2;
            
            float[] centerColor = rgbaCenter;
            float[] outerColor = rgbaOuter;
            
            if(!stickFigure) {
                gl.glPushMatrix();
                // Draw the center.
                gl.glPushMatrix();
                RobotRace.setMaterial(gl, centerColor, 20, "metal");
                gl.glTranslatef(0f, 0f, length / 2);
                gl.glScalef(centerWidth / length, centerWidth / length, 1f);
                glut.glutSolidCube(length);
                gl.glPopMatrix();
                
                // Draw the outer cylinder.
                gl.glPushMatrix();
                RobotRace.setMaterial(gl, outerColor, 10, "plastic");
                gl.glTranslatef(0f, 0f, length / 2 - cylinderHeight / 2); // Move local axis to start position of the cylinder.
                glut.glutSolidCylinder(cylinderRadius, cylinderHeight, 50, 10);
                gl.glPopMatrix();
                gl.glPopMatrix();
            }
        }
    }
    
    // Wrapper for drawPart.
    public void drawPart(GL2 gl, GLUT glut, float centerWidth, float length, boolean stickFigure) {
        drawPart(gl, glut, centerWidth, length, centerColor, outerColor, stickFigure);
    }
    
    private void unitTriangularPrism(GL2 gl, boolean solid){
    // back endcap;
    gl.glBegin(solid ? gl.GL_TRIANGLES : gl.GL_LINES);
    gl.glNormal3f(0f, 0f, 1f);
    gl.glVertex3f(1f, 0f, 0f);
    gl.glVertex3f(0f, 0f, 0f);
    gl.glVertex3f(0f, 1f, 0f);
    gl.glEnd();

    // front endcap
    gl.glBegin(solid ? gl.GL_TRIANGLES : gl.GL_LINES);
    gl.glNormal3f(0f, 0f, 1f);
    gl.glVertex3f(1f, 0f, 1f);
    gl.glVertex3f(0f, 0f, 1f);
    gl.glVertex3f(0f, 1f, 1f);
    gl.glEnd();

    
    // bottom
    gl.glBegin(solid ? gl.GL_QUADS : gl.GL_LINES);
    gl.glNormal3f(0f, -1f, 0f);
    gl.glVertex3f(0f, 0f, 0f);
    gl.glVertex3f(1f, 0f, 0f);
    gl.glVertex3f(1f, 0f, 1f);
    gl.glVertex3f(0f, 0f, 1f);
    gl.glEnd();
    
    

    // back
    gl.glBegin(solid ? gl.GL_QUADS : gl.GL_LINES);
    gl.glNormal3f(-1f, 0f, 0f);
    gl.glVertex3f(0f, 0f, 0f);
    gl.glVertex3f(0f, 1f, 0f);
    gl.glVertex3f(0f, 1f, 1f);
    gl.glVertex3f(0f, 0f, 1f);
    gl.glEnd();
    

    // top
    gl.glBegin(solid ? gl.GL_QUADS : gl.GL_LINES);
    gl.glNormal3f(1f, 1f, 0f);
    gl.glVertex3f(0f, 1f, 0f);
    gl.glVertex3f(1f, 0f, 0f);
    gl.glVertex3f(1f, 0f, 1f);
    gl.glVertex3f(0f, 1f, 1f);
    gl.glEnd();
    }
}