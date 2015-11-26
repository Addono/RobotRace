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
        
        gl = RobotRace.setMaterial(gl, new float[]{0.5f, .5f, 1f, 1f}, 10f, "plastic");
        
        gl.glPushMatrix();
        gl.glTranslatef(2, 0, 0);
        drawPart(gl, glut, .13f, stickFigure);
        gl.glPopMatrix();
        
        
    }
    
    
    public void drawArm(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        
        gl.glPushMatrix();
        gl.glScalef(1, 1, 4);
        unitTriangularPrism(gl, true);
        gl.glPopMatrix();
       
    }
    
    public void drawPart(GL2 gl, GLUT glut, float centerWidth, boolean stickFigure) {
        if(centerWidth > .45f) {
            centerWidth = .45f;
        }
        
        float cylinderExceed = 1.2f; // As a factor (so 1.0f means that it will fit exactly).
        float cylinderRadius = (float) Math.sqrt(2 * Math.pow(centerWidth / 2, 2)) * cylinderExceed; // Pythagoras on the 'radius' of the cube.
        float cylinderHeight = 1f - 2 * centerWidth;
        
        float[] centerColor = {.6f, .6f, .6f, 1.0f};
        float[] outerColor = {1f, 0f, 0f, 1.0f};
        
        if(!stickFigure) {
            gl.glPushMatrix();
                // Draw the 
                gl.glPushMatrix();
                    RobotRace.setMaterial(gl, centerColor, 20, "metal");
                    gl.glTranslatef(0f, 0f, 0.5f);
                    gl.glScalef(centerWidth, centerWidth, 1f);
                    glut.glutSolidCube(1f);
                gl.glPopMatrix();
                
                // Draw the cylinder
                gl.glPushMatrix();
                    RobotRace.setMaterial(gl, outerColor, 10, "plastic");
                    gl.glTranslatef(0f, 0f, .5f - cylinderHeight / 2); // Move local axis to start position of the cylinder.
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