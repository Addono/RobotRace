package robotrace;

import java.util.Random;

/**
 * Implementation of a camera with a position and orientation. 
 */
class Camera {

    /** The position of the camera. */
    public Vector eye = new Vector(3f, 5f, 4f);
    private Vector oldEye = eye;
    private Vector newEye = eye;

    /** The point to which the camera is looking. */
    public Vector center = Vector.O;
    private Vector oldCenter = center;
    private Vector newCenter = center;

    /** The up vector. */
    public Vector up = Vector.Z;
    private Vector oldUp = up;
    private Vector newUp = up;
    
    private int oldCamMode = -1;
    private float switchTime = 0;
    private float vectorTransScalar = 0;
    
    private int autoModeState = 1;
    
    Random rn = new Random();
    
    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
     */
    public void update(GlobalState gs, Robot focus) {
        // Check if the camera mode was switched.
        if(gs.camMode != oldCamMode) {
            switchTime = gs.tAnim; // Store the last time the camera mode was switched.
            
            // Store the old vectors.
            oldEye = eye;
            oldCenter = center;
            oldUp = up;
        }
        
        switch (gs.camMode) {
            // Helicopter mode
            case 1:
                setHelicopterMode(gs, focus);
                break;
                
            // Motor cycle mode    
            case 2:
                setMotorCycleMode(gs, focus);
                break;
                
            // First person mode    
            case 3:
                setFirstPersonMode(gs, focus);
                break;
                
            // Auto mode    
            case 4:
                setAutoMode(gs, focus);
                break;
                
            // Default mode    
            default:
                setDefaultMode(gs);
        }
        
        // Get the vector transition scalar.
        vectorTransScalar = transitionScalar(gs.tAnim - switchTime, 2f);
        
        eye = newEye.scale(vectorTransScalar).add(oldEye.scale(1 - vectorTransScalar));
        center = newCenter.scale(vectorTransScalar).add(oldCenter.scale(1 - vectorTransScalar));
        up = newUp.scale(vectorTransScalar).add(oldUp.scale(1 - vectorTransScalar));
        
        oldCamMode = gs.camMode;
    }
    
    /**
     * Calculates the progress of the camera transition, used to get a smooth
     * transition from camera to camera.
     * 
     * @param The difference in time between now and the previous
    */
    private float transitionScalar(float deltaT, float maxLength) {
        float t = deltaT / maxLength;
        if(t < 1 && t >= 0) {
            return t * t * (3 * (1 - t) + t); // Some math.
        } else {
            return 1;
        }
    }

    /**
     * Computes eye, center, and up, based on the camera's default mode.
     */
    private void setDefaultMode(GlobalState gs) {
        // Calculate the direction in which the camera is pointing.
        Vector lookDirection = new Vector(
                Math.cos(gs.theta) * Math.cos(gs.phi),
                Math.sin(gs.theta) * Math.cos(gs.phi),
                Math.sin(gs.phi)
        );
        
        Vector V = lookDirection.scale(gs.vDist);

        // Set the up direction of the camera.
        newUp       =  new Vector(0f, 0f, 1f);
        newCenter   =  gs.cnt;
        newEye      =  gs.cnt.add(V);
    }

    /**
     * Computes eye, center, and up, based on the helicopter mode.
     * The camera should focus on the robot.
     */
    private void setHelicopterMode(GlobalState gs, Robot focus) {
        newUp       =  focus.feetToHead.cross(focus.direction); // Up vector can't be feetToHead, since it will be parallel to the look direction.
        newCenter   =  focus.position;
        newEye      =  focus.position.add( // Place the camera above the robot.
                focus.feetToHead.scale(gs.vDist)
        );
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode.
     * The camera should focus on the robot.
     */
    private void setMotorCycleMode(GlobalState gs, Robot focus) {
        newUp       =  focus.feetToHead;
        newCenter   =  focus.position.add( // Move the center upwards so it will focus on the body.
                focus.feetToHead.scale(1.4f) 
        );
        newEye      =  newCenter.add( // Place the camera beside the robot.
                focus.direction.cross(focus.feetToHead).scale(1.5f * gs.vDist)
        );
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        newUp       =  focus.feetToHead;
        newCenter   =  focus.position.add( // Move the center upwards so it will be at head height.
                focus.feetToHead.scale(2f)
        );
        newEye      =  newCenter.add( // Place the camera a bit in front of the robot, to prevent that the camera is inside the robot.
                focus.direction.scale(-0.5f)
        );
        
    }
    
    /**
     * Computes eye, center, and up, based on the auto mode.
     * The above modes are alternated.
     */
    private void setAutoMode(GlobalState gs, Robot focus) {
        // Check if a new state should be selected.
        if(vectorTransScalar == 1 && rn.nextFloat() < 0.0005 * (gs.tAnim - switchTime)) { // Chance to switch increases while more times passes.
            int oldAutoModeState = autoModeState;
            do {
                autoModeState = rn.nextInt(3) + 1; // Get a random integer in the range [1, 3]
            } while (autoModeState == oldAutoModeState);
            
            switchTime = gs.tAnim; // Store the last time the camera mode was switched.

            // Store the old vectors.
            oldEye = eye;
            oldCenter = center;
            oldUp = up;
        }
        
        // Update the camera with the appropriate state.
        switch(autoModeState) {
            // Helicopter mode
            case 1:
                setHelicopterMode(gs, focus);
                break;

            // Motor cycle mode    
            case 2:
                setMotorCycleMode(gs, focus);
                break;

            // First person mode    
            case 3:
                setFirstPersonMode(gs, focus);
                break;

            // Auto mode    
            case 4:
                setAutoMode(gs, focus);
                break;

            // Default mode    
            default:
                setDefaultMode(gs);
        }
    }
    
    double toRadians(double angle) {
        return angle * (Math.PI / 180);
    }
}
