package robotrace;

import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;
import java.awt.event.*;
import javax.media.opengl.GL2;

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
    
    int ambientLight = GL_LIGHT2;
    int lightSource1 = GL_LIGHT1;
    int cameraLight = GL_LIGHT3;
    
    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */
    public RobotRace() {
        
        // Create a new array of four robots
        robots = new Robot[4];
        
        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD, 2f);
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER, 1.7f);
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD, 2.4f);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE, 1.9f);
        
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
        
        // Enable lighting.
        gl.glShadeModel(GL_SMOOTH);
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(lightSource1); // Local moving light source.
        gl.glEnable(cameraLight);
        gl.glEnable(ambientLight); // Ambient light source
        
        // Set the properties of lightsource 1.
        float whiteColor[] = { 1.0f, 1.0f, 1.0f, 1f };
        gl.glLightfv(lightSource1, GL_DIFFUSE, whiteColor, 0);
        gl.glLightfv(lightSource1, GL_SPECULAR, whiteColor, 0);
        
        // Set the properties of the ambient lightsource.
        float ambientIntensity = .3f;
        gl.glLightfv(ambientLight, GL_AMBIENT, new float[] {ambientIntensity, ambientIntensity, ambientIntensity, 1f}, 0);
        
        // Set the properties of the camera lightsource.
        gl.glLightfv(cameraLight, GL_DIFFUSE, whiteColor, 0);
        gl.glLightfv(cameraLight, GL_SPECULAR, whiteColor, 0);
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
        double aspectRatio = (double) gs.w / (double) gs.h; // Calculate the screens aspect ratio.
        
        double xFOV = 2 * Math.atan(((double) gs.vWidth / 2) / gs.vDist); // Calculate FOV.
        xFOV = xFOV * 180 / Math.PI; // Convert FOV from radians to degrees.
        
        double yFOV = xFOV / aspectRatio; // Convert xFOV to yFOV y using the aspect ratio of the screen.
        
        glu.gluPerspective(yFOV, aspectRatio, 0.1 * gs.vDist, 10 * gs.vDist); // Set the perspective.
        
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
               
        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        if(gs.camMode == 3){
            camera.update(gs, robots[3]);
        } else {
            camera.update(gs, robots[0]);
        }
        
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
        
        // Draw all the robots on their position on the racetrack.
        float robotSpeed[] = {10, 15, 25, 28}; // Array containing the speed of all the robots.
        for(int i = 0; i < 4; i++) {
            float timeScale = robotSpeed[i];
            robots[i].position = raceTracks[gs.trackNr].getLanePoint(i, gs.tAnim / timeScale);
            robots[i].direction = raceTracks[gs.trackNr].getLaneTangent(i, gs.tAnim / timeScale);
            
            Vector defaultDir = new Vector(0f, -1f, 0f); // Point towards the front side of the robot.
            Vector rotationVector = defaultDir.cross(robots[i].direction).normalized();
            
            // Calculate the angle of rotation around the rotationVector. 
            double angle = Math.acos(robots[i].direction.normalized().dot(defaultDir.normalized()));
            gl.glPushMatrix();
            gl.glTranslated(robots[i].position.x(), robots[i].position.y(), robots[i].position.z());
            gl.glRotated((angle * 180) / Math.PI, rotationVector.x(), rotationVector.y(), rotationVector.z());
            glut.glutSolidCube(.5f);
            robots[i].draw(gl, glu, glut, gs.showStick, gs.tAnim); // Draw the i-th robot.
            gl.glPopMatrix();
        }
        
        // Draw the race track.
        raceTracks[gs.trackNr].draw(gl, glu, glut);
        
        // Draw the terrain.
        terrain.draw(gl, glu, glut);
        
        
        // Set the ambient light of the scene.
        double offset = 10 * (Math.PI / 180); // Calculate offset in radians.
        double theta = gs.theta - offset; // Apply the offset to theta.
        double phi = gs.phi + offset; // Apply the offset to phi.
        
        float[] ambientLightDir = { // Calculate the direction of the ambient light.
            (float) (Math.cos(theta) * Math.cos(phi)),
            (float) (Math.sin(theta) * Math.cos(phi)),
            (float) (Math.sin(phi)),
            0.0f
        };
        
        gl.glLightfv(cameraLight, GL_POSITION, ambientLightDir, 0); // Set the direction of the ambient light.
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
        String materialType = "plastic";
        
        // Create the yellow box in the center.
        setMaterial(yellow, shininess, materialType);
        glut.glutSolidSphere(.12f, 10, 10);
        
        // Create the red arrow.
        gl.glPushMatrix();
        gl.glRotatef(90f, 0f, 1f, 0f); // Rotate to get the global x axis alligned with the local z axis.
        setMaterial(red, shininess, materialType);
        createArrow(1f);
        gl.glPopMatrix();
        
        // Create the green arrow.
        gl.glPushMatrix();
        setMaterial(green, shininess, materialType);
        gl.glRotatef(270f, 1f, 0f, 0f); // Rotate to get the global y axis alligned with the local z axis.
        createArrow(1f);
        gl.glPopMatrix();
        
        // Create the blue arrow.
        setMaterial(blue, 10f, materialType);
        createArrow(1f);
        
        // Creates a little sphere where the camera focusses.
        gl.glPushMatrix();
        gl = setMaterial(gl, .3f, .3f, .3f, 1f, shininess, materialType);
        gl.glTranslated(gs.cnt.x(),gs.cnt.y(),gs.cnt.z());
        glut.glutSolidSphere(.05f,10,10);
        gl.glPopMatrix();
    }

    /**
    * @description  Creates the model of an arrow pointing upward along the local z axis.
    * @param lengt  Length of the arrow.
    * @return       Void
    */
    public void createArrow(float length) {
        int coneSides = 35;
        float coneRadius = length * .05f;
        
        gl.glPushMatrix();
        
            // Draw the shaft of the arrow.
        glut.glutSolidCylinder(.02f * length, .9f * length, 15, 1);
       
            // Draw the cone on top.
        // Move local axis to the position of the base and center of the cone.
        gl.glTranslatef(0f, 0f, .9f * length);
        
        // Draws the base of the cone (a circle).
        gl.glBegin(GL_POLYGON);
            double stepSize = 2 * Math.PI / coneSides;
            for(int i = 0; i < coneSides; i++) {
                gl.glNormal3f(0f, 0f, -1f); // Sets normal of the base of the cone.
                gl.glVertex3d(coneRadius * Math.cos(i * stepSize), coneRadius * Math.sin(i * stepSize), 0f);
            }
        gl.glEnd();
        
        //Draws the cone itself.
        glut.glutSolidCone(coneRadius,.1f * length, coneSides, 1);
        
        gl.glPopMatrix();
    }
    
    /**
    * @description                      Sets the lights properties according to the given settings.
    * @parameter float r                Amount of red in the color of the object.
    * @parameter float g                Amount of green in the color of the object.
    * @parameter float b                Amount of blue in the color of the object.
    * @parameter float a                Opacity of the object.
    * @parameter float shininess        Shininess of the object
    * @parameter String materialType    Type of the material, contains two types: plastic and metal.
    * @return                           Void
    */
    public static GL2 setMaterial(GL2 gl, float r, float g, float b, float a, float shininess, String materialType) {
        float ambientDecrease = 2f;
        float diffuseDecrease = 1.7f;
        
        float[] ambientColor = {r / ambientDecrease, g / ambientDecrease, b / ambientDecrease, a};
        float[] diffuseColor = {r / diffuseDecrease, g / diffuseDecrease, b / diffuseDecrease, a};
        float[] specularColor;
        
        switch(materialType.toLowerCase()) {
            case "metal":
                specularColor = diffuseColor;
                break;
            case "plastic":
                float white = (r + g + b) / (diffuseDecrease); // Dividing by three might be more accurate, but gives ugly results.
                specularColor = new float[]{white, white, white, 1.0f};
                break;
            default:
                specularColor = new float[] {0f, 0f, 0f, 0f};
        }
        
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, diffuseColor, 0);
        gl.glMaterialfv(GL_FRONT, GL_AMBIENT, ambientColor, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, specularColor, 0);
        gl.glMaterialf(GL_FRONT, GL_SHININESS, shininess);
        
        return gl;
    }
    
    // Static wrapper of setMaterial.
    public static GL2 setMaterial(GL2 gl, float[] rgba, float shininess, String material) {
        return setMaterial(gl, rgba[0], rgba[1], rgba[2], rgba[3], shininess, material);
    }
    
    // Wrapper of setMaterial.
    public void setMaterial(float[] rgba, float shininess, String material) {
        gl = setMaterial(gl, rgba[0], rgba[1], rgba[2], rgba[3], shininess, material);
    }
    
    // Wrapper of setMaterial.
    public void setMaterial(float r, float g, float b, float shininess, String material) {
        gl = setMaterial(gl, r, g, b, 1.0f, shininess, material);
    }
    
    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }
    
    public static void logVector(Vector vector) {
        System.out.println("Vector: "  + vector.x() + "\t" + vector.y() + "\t" + vector.z() + "\t" + vector.length());
    }
}