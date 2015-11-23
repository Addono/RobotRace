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
        // code goes here ...
        
        gl.glLineWidth(1f);
        gl.glColor3f(0f, 0f, 0f);

        glut.glutWireCube(1f);
        
        gl.glPushMatrix();
        
        gl.glTranslatef(0f,0f,1f);
        
        glut.glutWireCube(.5f);
        
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(2f, 2f, 0f);
        gl.glRotated(180, 0, 0, 1);
        drawArm(gl ,glu,glut,stickFigure, tAnim);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        drawArm(gl ,glu,glut,stickFigure, tAnim);
        gl.glPopMatrix();
    }
    
    
    public void drawArm(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        
        gl.glPushMatrix();
        gl.glScalef(1, 1, 4);
        unitTriangularPrism(gl, true);
        gl.glPopMatrix();
       
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
