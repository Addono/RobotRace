package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of a race track that is made from Bezier segments.
 */
class RaceTrack {
    
    /** The trackWidth of one lane. The total trackWidth of the track is 4 * laneWidth. */
    private final static float laneWidth = 1.22f;

    /** Array with 3N control points, where N is the number of segments. */
    private Vector[] controlPoints = null;
    
    /**
     * Constructor for the default track.
     */
    public RaceTrack() {
        calculateTrackPoints();
    }
    
    ArrayList<Vector> points = new ArrayList<Vector>();
    ArrayList<Vector> tangentLines = new ArrayList<Vector>();
    
    /**
     * Constructor for a spline track.
     */
    public RaceTrack(Vector[] controlPoints) {
        this.controlPoints = controlPoints;
        calculateTrackPoints();
    }

    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        if (null == controlPoints) {
            int parts = 50;
            float trackWidth = 1.22f * 4;
            
            RobotRace.setMaterial(gl, .8f, .2f, 0f, 1f, 1f, "plastic");
            
            // Draw the horizontal plane of the track.
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int i = 0; i < points.size(); i++) {
                gl.glNormal3f(0f, 0f, 1f);
                gl.glVertex3d(
                    points.get(i).x(),
                    points.get(i).y(),
                    points.get(i).z()
                );
                
                gl.glNormal3f(0f, 0f, 1f);
                gl.glVertex3d(points.get(i).x() + tangentLines.get(i).scale(trackWidth).x(),
                    points.get(i).y() + tangentLines.get(i).scale(trackWidth).y(),
                    points.get(i).z() + tangentLines.get(i).scale(trackWidth).z()
                );
            }
            gl.glEnd();
        } else {
            // draw the spline track
        }
    }
    
    private void calculateTrackPoints() {
        int parts = 100;
        for(int i = 0; i <= parts; i++) {
            float t = (float) i / parts;
            
            // Calculate the inner point on the inner ring.
            points.add(getPoint(t));
            
            // Calculate the point on the outer ring, by taking the vector orthogonal to the tangent line and de z vector.
            tangentLines.add(getTangent(t).cross(Vector.Z).normalized());
        }
    }
    
    /**
     * Returns the center of a lane at 0 <= t < 1.
     * Use this method to find the position of a robot on the track.
     */
    public Vector getLanePoint(int lane, double t) {
        if (null == controlPoints) {
            return Vector.O; // <- code goes here
        } else {
            return Vector.O; // <- code goes here
        }
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t) {
        if (null == controlPoints) {
            return Vector.O; // <- code goes here
        } else {
            return Vector.O; // <- code goes here
        }
    }

    /**
     * Returns a point on the test track at 0 <= t < 1.
     */
    private Vector getPoint(double t) {
        return new Vector(
            (10 * Math.cos(2 * Math.PI * t)),
            14 * Math.sin(2 * Math.PI * t),
            1.0f
        );
    }

    /**
     * Returns a tangent on the test track at 0 <= t < 1.
     */
    private Vector getTangent(double t) {
        return new Vector(
            -20 * Math.PI * Math.sin(2 * Math.PI * t),
            28 * Math.PI * Math.cos(2 * Math.PI * t),
            0.0f
        );
    }
    
    /**
     * Returns a point on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     */
    private Vector getCubicBezierPoint(double t, Vector P0, Vector P1,
                                                 Vector P2, Vector P3) {
        return Vector.O; // <- code goes here
    }
    
    /**
     * Returns a tangent on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
                                                   Vector P2, Vector P3) {
        return Vector.O; // <- code goes here
    }
}
