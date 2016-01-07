package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import java.util.ArrayList;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LESS;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;
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
    public void draw(GL2 gl, GLU glu, GLUT glut, Texture track, Texture brick) {
        if (controlPoints == null) {
            float trackWidth = laneWidth * 4;
            float sideWidth = 1.4f;
            float trackBottom = -1f;
            
            RobotRace.setMaterial(gl, 1f, 1f, 1f, 1f, 30f, "metal");

            gl.glEnable(GL_TEXTURE_2D);
            track.bind(gl);
            
            gl.glTexEnvi(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_REPLACE);
            
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
            
            // Draw the horizontal plane of the track.
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            gl.glNormal3f(0f, 0f, 1f);
            float length = 0;
            Vector oldPoint = points.get(0);
            
            // Create a strip of all points.
            for(int i = 0; i < points.size(); i++) {
                // Retrieve the upcoming point and tangent line from the ArrayList.
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                
                // Calculate how long this part of the track is.
                length += point.subtract(oldPoint).length() / 10;
                
                gl.glTexCoord2f(length, 0f);
                gl.glVertex3d(
                    point.x(),
                    point.y(),
                    point.z()
                );
                
                gl.glTexCoord2f(length, trackWidth / 10); // Set the upper right position on the texture.
                gl.glVertex3d(
                    point.x() + tangentLine.scale(trackWidth).x(),
                    point.y() + tangentLine.scale(trackWidth).y(),
                    point.z() + tangentLine.scale(trackWidth).z()
                );
                
                oldPoint = point; // Save this point, so it can be used in the next cycle.
            }
            gl.glEnd();
            
           
            RobotRace.setMaterial(gl, .5f, .15f, 0f, 1f, 100f, "metal");
            
            // Draw inner diagonal side.
            brick.bind(gl);
            
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
            
            oldPoint = points.get(0);
            length = 0;
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                Vector normal = point.subtract(oldPoint).cross(tangentLine);
                
                gl.glNormal3d(normal.x(), normal.y(), normal.z());
                
                // Calculate how long this part of the track is.
                length += point.subtract(oldPoint).length() / 10;
                
                gl.glTexCoord2f(0f, length);
                gl.glVertex3d(
                    point.x(),
                    point.y(),
                    point.z()
                );
                
                gl.glTexCoord2f(sideWidth / 10, length);
                gl.glVertex3d(
                    point.x() + tangentLine.scale(-sideWidth).x(),
                    point.y() + tangentLine.scale(-sideWidth).y(),
                    trackBottom
                );
                
                oldPoint = point;
            }
            gl.glEnd();
            
            // Draw outer diagonal side.
            length = 0;
            oldPoint = points.get(0);
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                Vector normal = point.subtract(oldPoint).cross(tangentLine);
                
                gl.glNormal3d(normal.x(), normal.y(), normal.z());
                
                // Calculate how long this part of the track is.
                length += point.subtract(oldPoint).length() / 20;
                
                gl.glTexCoord2f(0f, length);
                gl.glVertex3d(
                    point.add(tangentLine.scale(trackWidth)).x(),
                    point.add(tangentLine.scale(trackWidth)).y(),
                    point.add(tangentLine.scale(trackWidth)).z()
                );
                
                gl.glTexCoord2f(sideWidth / 10, length);
                gl.glVertex3d(
                    point.add(tangentLine.scale(trackWidth + sideWidth)).x(),
                    point.add(tangentLine.scale(trackWidth + sideWidth)).y(),
                    trackBottom
                );
                
                oldPoint = point;
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
     * P = P0 * t^3 + 3 * P1 * t^2 * (1 - t) + 3 * P2 * t * (1-t)^2 + P3 * (1 - t)^3
     */
    private Vector getCubicBezierPoint(double t, Vector P0, Vector P1,
                                                 Vector P2, Vector P3) {
        return P0.scale(t*t*t)
                .add(P1.scale(3 * t * t * (1 - t)))
                .add(P2.scale(3 * t * Math.pow(1 - t, 2)))
                .add(P3.scale(Math.pow(1 - t, 3)));
    }
    
    /**
     * Returns a tangent on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     * P = 3 *P0 * t^2 + P1 * (6 * (1 - t) * t - 3* t^2) + P2 * (3 * (1 - t)^2 - 6 * (1 - t) * t) + -3 * P3 * (1 - t)^2
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
                                                   Vector P2, Vector P3) {
        return  P0.scale(3 * t * t)
                .add(P1.scale(6 * t * (1 - t)))
                .add(P2.scale(3 * (1 - t) * (1 - t) - 6 * (1 - t) * t))
                .add(P3.scale(-3 * (1 - t) * (1 - t)));
    }
}