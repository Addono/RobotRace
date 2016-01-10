package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import java.util.Random;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import static java.lang.Math.*;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_TEXTURE_2D;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    Random rn = new Random();
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);
    
    public Vector feetToHead = new Vector(0, 0, 1);

    /** The material from which this robot is built. */
    private final Material material;
    
    float[] centerColor;
    float[] outerColor;
    float stretchedHeight;
    String outerType;
    Texture part;
    
    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material, float stretchedHeight) {
        this.material = material;
        this.centerColor = material.centerColor;
        this.outerColor = material.outerColor;
        this.stretchedHeight = stretchedHeight;
        this.outerType = material.outerType;
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) { 
        float feetHeight = stretchedHeight / 20;
        float feetWidth = stretchedHeight / 10;
        
        float legDistance = stretchedHeight / 10;
        float legPartHeight = stretchedHeight * 14 / 60;
        float rightKneeAngle = (float) (10f * (2f + cos(tAnim*10)));
        float leftKneeAngle = (float) (10f * (2f + sin(tAnim*10)));
        float legAngle = (float) (25f * (sin(tAnim*10)));
        
        
        float legAngleRad = legAngle*(float)Math.PI/180f;
        float legHeight  = (float) (2 * legPartHeight * Math.cos(rightKneeAngle * Math.PI / 180)) - legPartHeight/ 10;
        float lowerBodyHeight = stretchedHeight / 10;
        float bodyHeight = stretchedHeight * 3 / 10;
  
        
        float elbowAngle = (float) (10f * (2f + Math.cos(tAnim*10)));
        float armAngle = (float) (25f * (Math.sin(tAnim*10)));
        
        gl.glPushMatrix();
        // Draw the feets and legs.
        
                float legHeight1  = (float) (2 * legPartHeight * Math.cos(rightKneeAngle * Math.PI / 180)) - legPartHeight/ 10;
                float legHeight2  = (float) (2 * legPartHeight * Math.cos(leftKneeAngle * Math.PI / 180)) - legPartHeight/ 10;
                legHeight = max(legHeight1,legHeight2);
                
            gl.glPushMatrix();
                 if(legHeight2>legHeight1){
                        gl.glTranslatef(0f, 0f, legHeight2-legHeight1);
                     }
                gl.glTranslatef(0f, -1*(float)Math.sin(legAngleRad)*(feetHeight+legHeight) , ((feetHeight+legHeight)-(float)Math.cos(legAngleRad)*(feetHeight+legHeight)));
                gl.glRotatef(legAngle, -1f, 0f, 0f);
                gl.glTranslatef(legDistance, 0f, 0f);
                drawTriangle(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
                gl.glTranslatef(0.0f, 0.0f, feetHeight);
                drawLimb(gl, glut, legPartHeight, rightKneeAngle, legAngle, stickFigure, tAnim);
            gl.glPopMatrix();
            gl.glPushMatrix();
                if(legHeight1>legHeight2){
                    gl.glTranslatef(0f, 0f, legHeight1-legHeight2);
                }
                gl.glTranslatef(0f, -1*(float)Math.sin(-1*legAngleRad)*(feetHeight+legHeight) , ((feetHeight+legHeight)-(float)Math.cos(-1*legAngleRad)*(feetHeight+legHeight)));
                gl.glRotatef(-1*legAngle, -1f, 0f, 0f);
                gl.glTranslatef(-1*legDistance, 0f, 0f);
                drawTriangle(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
                gl.glTranslatef(0.0f, 0.0f, feetHeight);
                drawLimb(gl, glut, legPartHeight, leftKneeAngle, -1*legAngle, stickFigure, tAnim);
                
            gl.glPopMatrix();
            
        
        gl.glPopMatrix();
        
        // Draw the lower body.
        gl.glPushMatrix();
            gl.glTranslatef(0f,0 , legHeight + feetHeight);
            drawLowerbody(gl,glu,glut,legDistance, lowerBodyHeight, stickFigure, tAnim);
        gl.glPopMatrix();
        
        // Draw the upper body.
        gl.glPushMatrix();
            gl.glTranslatef(0, 0, legHeight + feetHeight + lowerBodyHeight);
            drawBody(gl,glu,glut,legDistance, lowerBodyHeight, bodyHeight, stickFigure, tAnim);
        gl.glPopMatrix();
        
        // Draw the texture
        gl.glPushMatrix();
            gl.glTranslatef(legDistance*1.5f,legDistance*-0.5f, legHeight + feetHeight + lowerBodyHeight);
            drawTexture(gl, glu, glut, legDistance);
        gl.glPopMatrix();
        
        // Draw the head.
        gl.glPushMatrix();
            gl.glTranslatef(0, 0, legHeight + feetHeight + lowerBodyHeight);
            drawHead(gl,glu,glut,legDistance, lowerBodyHeight, feetHeight, feetWidth, bodyHeight, stickFigure, tAnim);
        gl.glPopMatrix();
        
        // Draw the arms.
        gl.glPushMatrix();
            // Draw both arms individually.
            for(int i = 1; i >= -1; i -= 2) {
                gl.glScalef( i, 1.0f, 1.0f);
                gl.glPushMatrix();
                    gl.glTranslatef(legDistance * 1.5f, 0.0f, bodyHeight + lowerBodyHeight + legHeight);
                    gl.glRotatef(90f, 0.0f, 1.0f, 0.0f);
                    drawTriangle(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
                    gl.glRotatef(90f, 0.0f, 1.0f, 0.0f);
                    gl.glRotatef(180f, 0.0f, 0.0f, 1.0f);
                    gl.glRotatef(i * armAngle, 1.0f, 0.0f, 0.0f);
                    gl.glTranslatef(feetHeight, 0.0f, feetHeight * -0.5f);
                    drawLimb(gl, glut, legPartHeight, elbowAngle, legAngle, stickFigure, tAnim);
                gl.glPopMatrix();
            }
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
            gl.glRotatef(90f, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(90f, 0.0f, 0.0f, 1.0f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
            gl.glTranslatef(0.0f, -legDistance*3, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
        gl.glPopMatrix();
        
        
        // Create horizontal bar which is connected to the legs.
        gl.glPushMatrix();
            gl.glTranslatef(legDistance, 0, height);
            gl.glRotatef(-90, 0, 1, 0);
            
            drawPart(gl, glut, shapeWidth, legDistance*2, stickFigure);
        gl.glPopMatrix();
        
        
        // Create diagonals between the three horizontal bars.
        float o = height;
        float a = (float) Math.sqrt(2 * Math.pow(legDistance / 2, 2));
        float s = (float) (Math.sqrt(Math.pow(o, 2) + Math.pow(a, 2)));
        float angle = (float) (Math.atan(a / o) * 180 / Math.PI);
        
        // Create two bars per side. Left side is a mirrored version of the right side.
        for(int j = 1; j >= -1; j -= 2){
            gl.glPushMatrix();
            gl.glScalef(j, 1.0f, 1.0f);
            gl.glTranslatef(legDistance, 0, 0);
            
            // Create two diagonals on one side.
            for(int i = 90; i < 270; i += 90) {
                gl.glPushMatrix();
                    gl.glRotatef(i, 0.0f, 0.0f, 1.0f);
                    gl.glTranslatef(-.5f * legDistance, -.5f * legDistance, 0f);
                    gl.glRotatef(45, 0.0f, 0.0f, 1.0f);
                    gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
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
        
        // Draw the vertical parts of the body.
        gl.glPushMatrix();
            gl.glTranslatef(legDistance * 1.5f, legDistance * 0.5f, 0);
            drawPart(gl, glut, shapeWidth, BodyHeight, stickFigure);
            gl.glTranslatef(0, -legDistance, 0);
            drawPart(gl, glut, shapeWidth, BodyHeight, stickFigure);
            gl.glTranslatef(-2 * legDistance * 1.5f, 0.0f, 0.0f);
            drawPart(gl, glut, shapeWidth, BodyHeight, stickFigure);
            gl.glTranslatef(0, legDistance, 0);
            drawPart(gl, glut, shapeWidth, BodyHeight, stickFigure);
        gl.glPopMatrix();
        
        // Draw the three upper horizontal parts.
        gl.glPushMatrix();
            gl.glTranslatef(-bodyWidth, .5f * legDistance, BodyHeight);
            gl.glRotatef(90f, 0.0f, 1.0f, 0.0f);
            // Draw the part in the back.
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
            // Draw the part where the head on rests.
            gl.glTranslatef(0.0f, -legDistance, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
            // Draw the part in the front.
            gl.glTranslatef(0.0f, legDistance/2, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance*3, stickFigure);
        gl.glPopMatrix();
            
        // Draw the two vertical parts (the shoulders).
        gl.glPushMatrix();
            gl.glTranslatef(-bodyWidth, .5f * legDistance, BodyHeight);
            gl.glRotatef(90f, 1f, 0f, 0f);
            gl.glRotatef(90f, 0f, 0f, 1f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
            gl.glTranslatef(0.0f, -3 * legDistance, 0.0f);
            drawPart(gl, glut, shapeWidth, legDistance, stickFigure);
        gl.glPopMatrix();
        
    }
    
    public void drawHead(GL2 gl, GLU glu, GLUT glut, float legDistance, float height, float feetHeight, float feetWidth, float bodyHeight, boolean stickFigure, float tAnim){
        float shapeWidth = legDistance / 8;
        
        gl.glPushMatrix();
            gl.glTranslatef(0, 0, bodyHeight);     
            gl.glTranslatef(legDistance / 2, legDistance / 2, feetHeight);
            
            // Draw all the four vertical lines.
            gl.glPushMatrix();
                drawPart(gl, glut, shapeWidth, legDistance / 2, stickFigure);
                gl.glTranslatef(-legDistance, 0, 0);
                drawPart(gl, glut, shapeWidth, legDistance / 2, stickFigure);
                gl.glTranslatef(0, -legDistance, 0);
                drawPart(gl, glut, shapeWidth, legDistance / 2, stickFigure);
                gl.glTranslatef(legDistance, 0, 0);
                drawPart(gl, glut, shapeWidth, legDistance / 2, stickFigure);
            gl.glPopMatrix();
            
            // Draw the upper triangle of the robot's head.
            gl.glTranslatef(-legDistance / 2, -legDistance / 2, feetHeight);
            drawTriangle(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
            
            // Draw the lower triangle of the robot's head.
            gl.glTranslatef(0, 0, -feetHeight);
            gl.glRotatef(180, 1, 0, 0);
            drawTriangle(gl, glut, feetHeight, feetWidth, stickFigure, tAnim);
        gl.glPopMatrix();
    }
    
    public void drawTexture(GL2 gl, GLU glu, GLUT glut, float legDistance){
        
        part = RobotRace.getPart();
        
        float scale = 1f;
        
        gl.glEnable(GL_TEXTURE_2D);
        //remove "//" for textures below
        part.bind(gl);
        
        gl.glBegin(gl.GL_QUADS);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glTexCoord2d(legDistance * 3f*scale, 0);
        gl.glVertex3d(-legDistance * 3f, 0, 0);
        gl.glTexCoord2d(legDistance * 3f*scale, legDistance * 3f*scale);
        gl.glVertex3d(-legDistance * 3f, 0, legDistance * 3f);
        gl.glTexCoord2d(0, legDistance * 3f*scale);
        gl.glVertex3d(0, 0, legDistance * 3f);
        gl.glEnd(); 
        
        part.disable(gl);
        
        
    }
    
    public float drawLimb(GL2 gl, GLUT glut, float partSize, float angle, float legAngle, boolean stickFigure, float tAnim) {
        
        gl.glPushMatrix();
            gl.glPushMatrix();
                // Draw the lower part of the leg.
                gl.glRotatef(angle, 1f, 0f, 0f);
                drawPart(gl, glut, partSize / 10, partSize, stickFigure);
                
                // Draw the upper part of the leg.
                gl.glTranslatef(0.0f, 0.0f, partSize * 9 / 10); // Move to the top of the lower leg.
                gl.glRotatef(- 2 * angle, 1f, 0f, 0f); // Rotate the the other direction.
                drawPart(gl, glut, partSize / 10, partSize, stickFigure);
            gl.glPopMatrix();
        gl.glPopMatrix();
        
        return (float) (2 * partSize * Math.cos(angle * Math.PI / 180)) - partSize / 10; // Return the height of the leg.
    }
    
    public float drawTriangle(GL2 gl, GLUT glut, float height, float width, boolean stickFigure, float tAnim) {        
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
    
    /*
     * @description     Base shape for the robot, consisting of a cylinder - called outer - with a square - called center - on each side.
     */
    
    
    public void drawPart(GL2 gl, GLUT glut, float height, float width, float[] rgbaCenter, float[] rgbaOuter, boolean stickFigure) {
        if(stickFigure) { // Check if it should be drawn as a stick figure.
            gl.glLineWidth(2.5f);
            RobotRace.setMaterial(gl, 1.0f, 1.0f, 1.0f, 1.0f, 10, "plastic");
            
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
                        RobotRace.setMaterial(gl, outerColor, 10, outerType);
                        gl.glTranslatef(0f, 0f, width / 2 - cylinderHeight / 2); // Move local axis to start position of the cylinder.
                        glut.glutSolidCylinder(cylinderRadius, cylinderHeight, 16, 2);
                    gl.glPopMatrix();
                gl.glPopMatrix();
            }
        }
    }
    
    // Wrapper for drawPart.
    public void drawPart(GL2 gl, GLUT glut, float centerWidth, float length, boolean stickFigure) {
        drawPart(gl, glut, centerWidth, length, centerColor, outerColor, stickFigure);
    }
}