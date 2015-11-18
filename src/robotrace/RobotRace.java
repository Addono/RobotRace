package robotrace;

import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;
import java.awt.event.*;
import static javax.media.opengl.GL.GL_LINES;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 * 
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 * 
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards;
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 * 
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the same folder as this
 * file. These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 * 
 * gl.glColor3f(1f, 1f, 1f);
 * track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd(); 
 * 
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */
public class RobotRace extends Base {
    
    /** Array of the four robots. */
    private final Robot[] robots;
    
    /** Instance of the camera. */
    private final Camera camera;
    
    /** Instance of the race track. */
    private final RaceTrack[] raceTracks;
    
    /** Instance of the terrain. */
    private final Terrain terrain;
    
    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */
    public RobotRace() {
        
        // Create a new array of four robots
        robots = new Robot[4];
        
        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD
            /* add other parameters that characterize this robot */);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE
            /* add other parameters that characterize this robot */);
        
        // Initialize the camera
        camera = new Camera();
        
        // Initialize the race tracks
        raceTracks = new RaceTrack[5];
        
        // Test track
        raceTracks[0] = new RaceTrack();
        
        // O-track
        raceTracks[1] = new RaceTrack(new Vector[] {
            /* add control points like:
            new Vector(10, 0, 1), new Vector(10, 5, 1), new Vector(5, 10, 1),
            new Vector(..., ..., ...), ...
            */
        });
        
        // L-track
        raceTracks[2] = new RaceTrack(new Vector[] { 
            /* add control points */
        });
        
        // C-track
        raceTracks[3] = new RaceTrack(new Vector[] { 
            /* add control points */
        });
        
        // Custom track
        raceTracks[4] = new RaceTrack(new Vector[] { 
           /* add control points */
        });
        
        // Initialize the terrain
        terrain = new Terrain();
        
        // Set the initial start location of the camera.
        gs.cnt = Vector.O;
    }
    
    /*
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {
		
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
		
	// Normalize normals.
        gl.glEnable(GL_NORMALIZE);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
		
        // Try to load four textures, add more if you like.
        track = loadTexture("track.jpg");
        brick = loadTexture("brick.jpg");
        head  = loadTexture("head.jpg");
        torso = loadTexture("torso.jpg");
        
        gl.glShadeModel(GL_SMOOTH);
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        
        float whiteColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, whiteColor, 0);
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, new float[]{0.1f, 0.1f, 0.1f, 1f}, 0);
    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);
        
        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        double aspectRatio = gs.w / gs.h;
        
        double xFOV = 2 * Math.atan((gs.vWidth / 2) / gs.vDist); // Calculate FOV.
        xFOV = xFOV * 180 / Math.PI; // Convert FOV from radians to degrees.
        
        double yFOV = xFOV / aspectRatio; // Convert xFOV to yFOV y using the aspect ratio of the screen.
        glu.gluPerspective(yFOV, aspectRatio, 0.1 * gs.vDist, 10 * gs.vDist);
        
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
               
        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        camera.update(gs, robots[0]);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z()
        );
    }
    
    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
        // Background color.
        gl.glClearColor(0f, 0f, 0f, 0f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        
        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }
        
        // Get the position and direction of the first robot.
        robots[0].position = raceTracks[gs.trackNr].getLanePoint(0, 0);
        robots[0].direction = raceTracks[gs.trackNr].getLaneTangent(0, 0);
        
        // Draw the first robot.
        robots[0].draw(gl, glu, glut, false, gs.tAnim);
        
        // Draw the race track.
        raceTracks[gs.trackNr].draw(gl, glu, glut);
        
        // Draw the terrain.
        terrain.draw(gl, glu, glut);
        
        float cameraLightPos[] = {
            (float) camera.eye.x(), // Get x coordinate of the camera.
            (float) camera.eye.y(), // Get y coordinate of the camera.
            (float) camera.eye.z(), // Get z coordinate of the camera.
            1.0f                    // It's a local position.
        };
        float pinkColor[] = {1.0f, 0.5f, 0.5f, 1.0f};
        
        gl.glLightfv(GL_LIGHT0, GL_POSITION, cameraLightPos, 0);        
        
        gl.glLineWidth(1f);
        gl.glColor3f(0f, 0f, 0f);
        
        gl.glPushMatrix();
        
        // Move in x-direction.
        gl.glTranslatef(2f, 0f, 0f);
        
        // Rotate 30 degrees, around z-axis.
        gl.glRotatef(30f, 0f, 0f, 0f);
        
        // Scale in z-direction.
        gl.glScalef(1f, 1f, 2f);

        // Translated, rotated, scaled box.
        setMaterial(pinkColor, 0f, "plastic");
        glut.glutSolidCylinder(1f, 1f, 50, 10);
        
        gl.glPopMatrix();
        
        // Create the floor.
        setMaterial(new float[]{0.2f, .2f, .2f, 1f}, 10f, "plastic");
        gl.glBegin(GL_POLYGON);
            gl.glNormal3f(0f, 0f, 1f);
            gl.glVertex3f(100f, 100f, 0f);
            gl.glVertex3f(100f, -100f, 0f);
            gl.glVertex3f(-100f, -100f, 0f);
            gl.glVertex3f(-100f, 100f, 0f);
        gl.glEnd();
    }
    
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        // Define all the colors.
        float yellow[] = {1.0f, 1.0f, 0.0f, 1.0f};
        float red[] = {1.0f, 0.0f, 0.0f, 1.0f};
        float green[] = {0.0f, 1.0f, 0.0f, 1.0f};
        float blue[] = {0.0f, 0.0f, 1.0f, 1.0f};
        
        // Define shininess axis frame.
        float shininess = 10f;
        
        // Define material axis frame.
        String materialType = "metal";
        
        // Create the yellow box in the center.
        setMaterial(yellow, shininess, materialType);
        glut.glutSolidCube(.12f);
        
        // Create the red arrow.
        gl.glPushMatrix();
        gl.glRotatef(90f, 0f, 1f, 0f);
        setMaterial(red, shininess, materialType);
        createArrow(1f);
        gl.glPopMatrix();
        
        // Create the green arrow.
        gl.glPushMatrix();
        setMaterial(green, shininess, materialType);
        gl.glRotatef(270f, 1f, 0f, 0f);
        createArrow(1f);
        gl.glPopMatrix();
        
        // Create the blue arrow.
        setMaterial(blue, 10f, materialType);
        createArrow(1f);
        
        // Creates a little sphere where the camera focusses.
        gl.glPushMatrix();
        setMaterial(.3f, .3f, .3f, 1f, shininess, materialType);
        gl.glTranslated(gs.cnt.x(),gs.cnt.y(),gs.cnt.z());
        glut.glutSolidSphere(.05f,10,10);
        gl.glPopMatrix();
    }

    /*
    * @description - Creates the model of an arrow at the local axis.
    * @param lengt - Length of the arrow.
    */
    public void createArrow(float length) {
        int coneSides = 35;
        float coneRadius = length * .05f;
        
        gl.glPushMatrix();
        
        glut.glutSolidCylinder(.02f * length, .9f * length, 15, 1);
       
        gl.glTranslatef(0f, 0f, .9f * length);
        gl.glBegin(GL_POLYGON);
            double stepSize = 2 * Math.PI / coneSides;
            for(int i = 0; i < coneSides; i++) {
                gl.glNormal3f(0f, 0f, -1f);
                gl.glVertex3d(coneRadius * Math.cos(i * stepSize), coneRadius * Math.sin(i * stepSize), 0f);
            }
        gl.glEnd();
        glut.glutSolidCone(coneRadius,.1f * length, coneSides, 1);
        
        gl.glPopMatrix();
    }
    
    /*
    * @description               Sets the lights properties according to the given settings.
    * @param float r             Amount of red in the color of the object.
    * @param float g             Amount of green in the color of the object.
    * @param float b             Amount of blue in the color of the object.
    * @param float a             Opacity of the object.
    * @param float shininess     Shininess of the object
    * @param String materialType Type of the material, contains two types: plastic and metal.
    * @return                    Void
    */
    public void setMaterial(float r, float g, float b, float a, float shininess, String materialType) {
        float ambientDecrease = 2f;
        float diffuseDecrease = 10f;
        
        float[] ambientColor = {r / ambientDecrease, g / ambientDecrease, b / ambientDecrease, a};
        float[] diffuseColor = {r / diffuseDecrease, g / diffuseDecrease, b / diffuseDecrease, a};
        float[] specularColor;
        
        switch(materialType.toLowerCase()) {
            case "metal":
                specularColor = diffuseColor;
                break;
            case "plastic":
                float white = (r + g + b) / (3 * diffuseDecrease); // Dividing by three might be more accurate, but gives ugly results.
                specularColor = new float[]{white, white, white, 1.0f};
                break;
            default:
                specularColor = new float[]{0f, 0f, 0f, 0f};
        }
        
        //specularColor = new float[]{.1f, .1f, .1f, 1.0f};
        
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, diffuseColor, 0);
        gl.glMaterialfv(GL_FRONT, GL_AMBIENT, ambientColor, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, specularColor, 0);
        gl.glMaterialf(GL_FRONT, GL_SHININESS, shininess);
    }
    
    // Wrapper of setMaterial.
    public void setMaterial(float[] rgba, float shininess, String material) {
        setMaterial(rgba[0], rgba[1], rgba[2], rgba[3], shininess, material);
    }
    
    // Wrapper of setMaterial.
    public void setMaterial(float r, float g, float b, float shininess, String material) {
        setMaterial(r, g, b, 1.0f, shininess, material);
    }
    
    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    } 
}
