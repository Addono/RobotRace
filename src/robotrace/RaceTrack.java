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
        if (controlPoints == null) {
            float trackWidth = laneWidth * 4;
            float sideWidth = 1.4f;
            float trackBottom = -1f;
            
            RobotRace.setMaterial(gl, .9f, .2f, 0f, 1f, 30f, "metal");
            
            // Draw the horizontal plane of the track.
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                
                gl.glNormal3f(0f, 0f, 1f);
                gl.glVertex3d(
                    point.x(),
                    point.y(),
                    point.z()
                );
                
                gl.glNormal3f(0f, 0f, 1f);
                gl.glVertex3d(
                    point.x() + tangentLine.scale(trackWidth).x(),
                    point.y() + tangentLine.scale(trackWidth).y(),
                    point.z() + tangentLine.scale(trackWidth).z()
                );
            }
            gl.glEnd();
            
            RobotRace.setMaterial(gl, .5f, .15f, 0f, 1f, 100f, "metal");
            
            
            // Draw inner diagonal side.
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                
                gl.glVertex3d(
                    point.x(),
                    point.y(),
                    point.z()
                );
                
                gl.glVertex3d(
                    point.x() + tangentLine.scale(-sideWidth).x(),
                    point.y() + tangentLine.scale(-sideWidth).y(),
                    trackBottom
                );
            }
            gl.glEnd();
            
            // Draw outer diagonal side.
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                
                gl.glVertex3d(
                    point.add(tangentLine.scale(trackWidth)).x(),
                    point.add(tangentLine.scale(trackWidth)).y(),
                    point.add(tangentLine.scale(trackWidth)).z()
                );
                
                gl.glVertex3d(
                    point.add(tangentLine.scale(trackWidth + sideWidth)).x(),
                    point.add(tangentLine.scale(trackWidth + sideWidth)).y(),
                    trackBottom
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
            return getPoint(t).add(
                    getLaneTangent(0, t).cross(Vector.Z).normalized().scale(laneWidth * (.5f + lane))
            );
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
            return getTangent(t);
        } else {
            return Vector.O; // <- code goes here
        }
    }

    /**
     * Returns a point on the test track at 0 <= t < 1.
     */
    private Vector getPoint(double t) {
        return new Vector(
            10 * Math.cos(2 * Math.PI * t),
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
        ).normalized();
    }
    
    /**
     * Returns a point on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     * 
     *       1
     *      1  1
     *    1   2  1
     *   1  3  3  1
     * P = P0 * t^3 + P1 * t^2 * (1 - t) + P2 * t * (1-t)^2 + P3 * (1 - t)^3
     */
    private Vector getCubicBezierPoint(double t, Vector P0, Vector P1,
                                                 Vector P2, Vector P3) {
        return P0.scale(t*t*t)
                .add(P1.scale(t * t * (1 - t)))
                .add(P2.scale(t * Math.pow(1 - t, 2)))
                .add(P3.scale(Math.pow(1 - t, 3)));
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
