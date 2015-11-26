package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
    private final Material material;
    
    float[] centerColor = {.7f, .7f, .7f, 1.0f};
    float[] outerColor = {1.0f, 1.0f, 0.0f, 1.0f};

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
        //gl.glTranslatef(2, 0, 0);
        //drawPart(gl, glut, .13f, centerColor, outerColor, stickFigure);
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
        gl.glPushMatrix();
            //gl.glScalef(.2f, .2f, .2f);
            gl.glTranslatef(.5f, .5f, .5f);
            gl.glRotatef(90f, 0f, 1f, 0f);
            drawPart(gl, glut, .04f, .25f, centerColor, outerColor, stickFigure);
        gl.glPopMatrix();
    }
    
    public void drawPart(GL2 gl, GLUT glut, float centerWidth, float length, float[] rgbaCenter, float[] rgbaOuter, boolean stickFigure) {
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
                // Draw the 
                gl.glPushMatrix();
                    RobotRace.setMaterial(gl, centerColor, 20, "metal");
                    gl.glTranslatef(0f, 0f, length / 2);
                    gl.glScalef(centerWidth / length, centerWidth / length, 1f);
                    glut.glutSolidCube(length);
                gl.glPopMatrix();
                
                // Draw the cylinder
                gl.glPushMatrix();
                    RobotRace.setMaterial(gl, outerColor, 10, "plastic");
                    gl.glTranslatef(0f, 0f, length / 2 - cylinderHeight / 2); // Move local axis to start position of the cylinder.
                    glut.glutSolidCylinder(cylinderRadius, cylinderHeight, 50, 10);
                gl.glPopMatrix();
            gl.glPopMatrix();
        }
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