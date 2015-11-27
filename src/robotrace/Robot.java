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
    
    float[] centerColor;
    float[] outerColor;
    
    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material
        /* add other parameters that characterize this robot */) {
        this.material = material;
        this.centerColor = material.centerColor;
        this.outerColor = material.outerColor;
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) { 
        float feetHeight = .1f;
        float feetWidth = .2f;
        
        float legDistance = .2f;
        float legPartHeight = .35f;
        float legHeight=0;
        float lowerBodyHeight = 0.2f;
        float bodyHeight = legDistance*2.5f;
        
        gl.glPushMatrix();
        // Draw the feets and legs.
        for(int i = -1; i <= 1; i += 2) {
            gl.glPushMatrix();
                gl.glTranslatef(i * legDistance, 0f, 0f);
                drawFeet(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
                gl.glTranslatef(0.0f, 0.0f, feetHeight);
                legHeight = drawLeg(gl, glut, legPartHeight, stickFigure, tAnim);
            gl.glPopMatrix();
        }
        
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0f,0 , legHeight + feetHeight);
        drawLowerbody(gl,glu,glut,legDistance, lowerBodyHeight,stickFigure, tAnim);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, legHeight+feetHeight+lowerBodyHeight);
        drawBody(gl,glu,glut,legDistance, lowerBodyHeight, bodyHeight ,stickFigure, tAnim);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, legHeight+feetHeight+lowerBodyHeight);
        drawHead(gl,glu,glut,legDistance, lowerBodyHeight, feetHeight, feetWidth, bodyHeight, stickFigure, tAnim);
        gl.glPopMatrix();
        
    }
    
    public void drawLowerbody(GL2 gl, GLU glu, GLUT glut, float legDistance, float height, boolean stickFigure, float tAnim){
        
        float bodyWidth = (1.5f *legDistance);
        float shapeWidth = legDistance / 8;
        
        gl.glPushMatrix();
        gl.glRotatef(180, 1, 0, 0);
        gl.glTranslatef(0, 0, -height);
        // Create the base. First the ones along the x-axis.
        gl.glPushMatrix();
            gl.glTranslatef(-bodyWidth, .5f * legDistance, 0.0f);
            gl.glRotatef(90f, 0f, 1f, 0f);
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
            gl.glTranslatef(0.0f, -legDistance, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
        gl.glPopMatrix();
            
        // Create the second half of the base, the two along the y-axis.
        gl.glPushMatrix();
            gl.glTranslatef(-bodyWidth, .5f * legDistance, 0.0f);
            gl.glRotatef(90f, 1f, 0f, 0f);
            gl.glRotatef(90f, 0f, 0f, 1f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
            gl.glTranslatef(0.0f, -legDistance*3, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
        gl.glPopMatrix();
        
        
        //creat main line
        
        gl.glPushMatrix();
            gl.glTranslatef(legDistance, 0, height);
            gl.glRotatef(-90, 0, 1, 0);
            
            drawPart(gl, glut, shapeWidth, legDistance*2, stickFigure);
        gl.glPopMatrix();
        
        
        //creat triangles
        
        float o = height;
        float a = (float) Math.sqrt(2 * Math.pow(legDistance / 2, 2));
        float s = (float) (Math.sqrt(Math.pow(o, 2) + Math.pow(a, 2)));
        float angle = (float) (Math.atan(a / o) * 180 / Math.PI);
        
        for(int j=0; j<=180 ; j+=180){
        gl.glPushMatrix();
        gl.glRotatef(j, 0, 0, 1);
        gl.glTranslatef(legDistance, 0, 0);
        for(int i = 90; i < 270; i += 90) {
            gl.glPushMatrix();
                gl.glRotatef(i, 0.0f, 0.0f, 1.0f);
                gl.glTranslatef(-.5f * legDistance, -.5f * legDistance, 0f);
                gl.glRotatef(45, 0f, 0f, 1f);
                gl.glRotatef(angle, 0f, 1f, 0f);
                drawPart(gl, glut, shapeWidth, s, stickFigure);
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }
        gl.glPopMatrix();
    }
    
    public void drawBody(GL2 gl, GLU glu, GLUT glut, float legDistance, float height, float BodyHeight, boolean stickFigure, float tAnim){
        
        float bodyWidth = (1.5f *legDistance);
        float shapeWidth = legDistance / 8;
        float bodyHeight = legDistance*2.5f;
        
        
        gl.glPushMatrix();
        gl.glTranslatef(legDistance*1.5f, legDistance*0.5f, 0);
        drawPart(gl, glut, shapeWidth, bodyHeight, stickFigure);
        gl.glTranslatef(0, -legDistance, 0);
        drawPart(gl, glut, shapeWidth, bodyHeight, stickFigure);
        gl.glTranslatef(-2*legDistance*1.5f, 0, 0);
        drawPart(gl, glut, shapeWidth, bodyHeight, stickFigure);
        gl.glTranslatef(0, legDistance, 0);
        drawPart(gl, glut, shapeWidth, bodyHeight, stickFigure);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
            gl.glTranslatef(-bodyWidth, .5f * legDistance, bodyHeight);
            gl.glRotatef(90f, 0f, 1f, 0f);
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
            gl.glTranslatef(0.0f, -legDistance, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
            gl.glTranslatef(0.0f, legDistance/2, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
        gl.glPopMatrix();
            
        // Create the second half of the base, the two along the y-axis.
        gl.glPushMatrix();
            gl.glTranslatef(-bodyWidth, .5f * legDistance, bodyHeight);
            gl.glRotatef(90f, 1f, 0f, 0f);
            gl.glRotatef(90f, 0f, 0f, 1f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
            gl.glTranslatef(0.0f, -legDistance*3, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
        gl.glPopMatrix();
        
    }
    
    public void drawHead(GL2 gl, GLU glu, GLUT glut, float legDistance, float height, float feetHeight, float feetWidth, float bodyHeight, boolean stickFigure, float tAnim){
        
        float bodyWidth = (1.5f *legDistance);
        float shapeWidth = legDistance / 8;
        
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, bodyHeight);     
        gl.glTranslatef(legDistance/2, legDistance/2, feetHeight);
        gl.glPushMatrix();
        drawPart(gl, glut, shapeWidth, legDistance/2, stickFigure);
        gl.glTranslatef(-legDistance, 0, 0);
        drawPart(gl, glut, shapeWidth, legDistance/2, stickFigure);
        gl.glTranslatef(0, -legDistance, 0);
        drawPart(gl, glut, shapeWidth, legDistance/2, stickFigure);
        gl.glTranslatef(legDistance, 0, 0);
        drawPart(gl, glut, shapeWidth, legDistance/2, stickFigure);
        gl.glPopMatrix();
        drawPart(gl, glut, shapeWidth, legDistance/2, stickFigure);
        gl.glTranslatef(-legDistance/2, -legDistance/2, feetHeight);
        drawFeet(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
        gl.glTranslatef(0, 0, -feetHeight);
        gl.glRotatef(180, 1, 0, 0);
        drawFeet(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
        gl.glPopMatrix();
    }
    
    
    public void drawArm(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0.5f);
        gl.glScalef(1, 1, 4);
        unitTriangularPrism(gl, true);
        gl.glPopMatrix();
       
    }
    
    public float drawLeg(GL2 gl, GLUT glut, float partSize, boolean stickFigure, float tAnim) {
        float angle = 15f;
        
        gl.glPushMatrix();

            gl.glPushMatrix();
                gl.glRotatef(angle, 1f, 0f, 0f);
                drawPart(gl, glut, partSize / 10, partSize, stickFigure);
                
                gl.glTranslatef(0.0f, 0.0f, partSize * 9 / 10); // Move to the top of the lower leg.
                gl.glRotatef(- 2 * angle, 1f, 0f, 0f); // 
                drawPart(gl, glut, partSize / 10, partSize, stickFigure);
            gl.glPopMatrix();
        gl.glPopMatrix();
        
        return (float) (2 * partSize * Math.cos(angle * Math.PI / 180)) - partSize / 10;
    }
    
    public float drawFeet(GL2 gl, GLUT glut, float height, float width, boolean stickFigure, float tAnim) {        
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
        
        
        // Draw the diagonal parts.
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
        
        return height;
    }
    
    public void drawPart(GL2 gl, GLUT glut, float height, float width, float[] rgbaCenter, float[] rgbaOuter, boolean stickFigure) {
        if(stickFigure) {
            gl.glLineWidth(4.0f);
            gl.glColor3i(1, 1, 1); // Set wireFrame color to white
            
            gl.glBegin(gl.GL_LINES);
                gl.glVertex3f(0f, 0f, 0f);
                gl.glVertex3f(0f, 0f, width);
            gl.glEnd();
        } else {
            // Prevents the center of getting wider than the function will support (function designed to allow up to .5f).
            if(height > width / 2) {
                //centerWidth = width / 2;
            }
            
            float cylinderExceed = 1.2f; // As a factor (so 1.0f means that it will fit exactly).
            float cylinderRadius = (float) Math.sqrt(2 * Math.pow(height / 2, 2)) * cylinderExceed; // Pythagoras on the 'radius' of the cube.
            float cylinderHeight = width - height * 2;
            
            float[] centerColor = rgbaCenter;
            float[] outerColor = rgbaOuter;
            
            if(!stickFigure) {
                gl.glPushMatrix();
                // Draw the center.
                gl.glPushMatrix();
                RobotRace.setMaterial(gl, centerColor, 20, "metal");
                gl.glTranslatef(0f, 0f, width / 2);
                gl.glScalef(height / width, height / width, 1f);
                glut.glutSolidCube(width);
                gl.glPopMatrix();
                
                // Draw the outer cylinder.
                gl.glPushMatrix();
                RobotRace.setMaterial(gl, outerColor, 10, "plastic");
                gl.glTranslatef(0f, 0f, width / 2 - cylinderHeight / 2); // Move local axis to start position of the cylinder.
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