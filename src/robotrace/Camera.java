package robotrace;

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
        
        // Get the 
        float vectorTransScalar = transitionScalar(gs.tAnim - switchTime, 2f);
        
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
            return t * t * (3 * (1 - t) + t);
        } else {
            return 1;
        }
    }

    /**
     * Computes eye, center, and up, based on the camera's default mode.
     */
    private void setDefaultMode(GlobalState gs) {
        // code goes here ...
        
        // Set the center of the camera.
        newCenter = gs.cnt;
        
        // Calculate the direction in which the camera is pointing.
        Vector lookDirection = new Vector(
                Math.cos(gs.theta) * Math.cos(gs.phi),
                Math.sin(gs.theta) * Math.cos(gs.phi),
                Math.sin(gs.phi)
        );
        Vector V = lookDirection.scale(gs.vDist);
        
        newEye = center.add(V);
    }

    /**
     * Computes eye, center, and up, based on the helicopter mode.
     * The camera should focus on the robot.
     */
    private void setHelicopterMode(GlobalState gs, Robot focus) {
        // code goes here ...
        newCenter = focus.position;
        newCenter.z += 2;
        newEye = newCenter.add(focus.direction.scale(gs.vDist));
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode.
     * The camera should focus on the robot.
     */
    private void setMotorCycleMode(GlobalState gs, Robot focus) {
        // code goes here ...
        
        newCenter = focus.position.add(newUp.scale(2f));
        newEye = newCenter.add(focus.direction.cross(newUp).scale(3f*0.5*gs.vDist));
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        // code goes here ...
        
        newCenter = focus.position.add(newUp.scale(2f));
        newEye = newCenter.add(focus.direction.scale(-0.5f));
        
    }
    
    /**
     * Computes eye, center, and up, based on the auto mode.
     * The above modes are alternated.
     */
    private void setAutoMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }
    
    double toRadians(double angle) {
        return angle * (Math.PI / 180);
    }
}
