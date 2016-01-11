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
    }

    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, Texture track, Texture brick) {
        if (controlPoints == null) {
            float trackWidth = laneWidth * 4;
            float sideWidth = 1.4f;
            float trackBottom = -1f;
            
            RobotRace.setMaterial(gl, 1f, 1f, 1f, 1f, 10f, "metal");

            gl.glEnable(GL_TEXTURE_2D);
            track.bind(gl);
            
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
            
            // Draw the horizontal plane of the track.
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            gl.glNormal3f(0f, 0f, 1f);
            float length = 0;
            float scale = laneWidth * 4;
            Vector oldPoint = points.get(0);
            
            // Create a strip of all points.
            for(int i = 0; i < points.size(); i++) {
                // Retrieve the upcoming point and tangent line from the ArrayList.
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                
                // Calculate how long this part of the track is.
                length += point.subtract(oldPoint).length() / scale;
                
                gl.glTexCoord2f(length, 0f);
                gl.glVertex3d(
                    point.x(),
                    point.y(),
                    point.z()
                );
                
                gl.glTexCoord2f(length, trackWidth / scale); // Set the upper right position on the texture.
                gl.glVertex3d(
                    point.x() + tangentLine.scale(trackWidth).x(),
                    point.y() + tangentLine.scale(trackWidth).y(),
                    point.z() + tangentLine.scale(trackWidth).z()
                );
                
                oldPoint = point; // Save this point, so it can be used in the next cycle.
            }
            gl.glEnd();
            
           
            RobotRace.setMaterial(gl, 1f, 1f, 1f, 1f, 10f, "metal");
            
            // Draw inner diagonal side.
            brick.bind(gl);
            
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
            
            oldPoint = points.get(0);
            length = 0;
            scale = 1f;
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            for(int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                Vector tangentLine = tangentLines.get(i);
                Vector normal = point.subtract(oldPoint).cross(tangentLine);
                
                gl.glNormal3d(normal.x(), normal.y(), normal.z());
                
                // Calculate how long this part of the track is.
                length += point.subtract(oldPoint).length() / scale;
                
                gl.glTexCoord2f(0f, length);
                gl.glVertex3d(
                    point.x(),
                    point.y(),
                    point.z()
                );
                
                gl.glTexCoord2f(sideWidth / scale, length);
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
                length += point.subtract(oldPoint).length() / scale;
                
                gl.glTexCoord2f(0f, length);
                gl.glVertex3d(
                    point.add(tangentLine.scale(trackWidth)).x(),
                    point.add(tangentLine.scale(trackWidth)).y(),
                    point.add(tangentLine.scale(trackWidth)).z()
                );
                
                gl.glTexCoord2f(sideWidth / scale, length);
                gl.glVertex3d(
                    point.add(tangentLine.scale(trackWidth + sideWidth)).x(),
                    point.add(tangentLine.scale(trackWidth + sideWidth)).y(),
                    trackBottom
                );
                
                oldPoint = point;
            }
            gl.glEnd();
        } else {
            //gl.glBegin(gl.GL_LINE_STRIP);
            
            int amount = 100;
            
            /*
            int limit = (controlPoints.length) / 3;
            int partsEachCurve =  amount / limit;
            
            // 
            for(int i = 0; i < limit; i++) {
                int start = i * 3;
                Vector[] points = {
                    controlPoints[start],
                    controlPoints[start+1],
                    controlPoints[start+2],
                    controlPoints[(start+3) % controlPoints.length]
                };
                
                // Draw this curve.
                for(int part = 0; part <= partsEachCurve; part++) {
                    Vector point = getCubicBezierPoint((float) part / (float) partsEachCurve, points);
                    gl.glVertex3d(point.x, point.y, point.z);
                }
            }*/
            gl.glBegin(gl.GL_TRIANGLE_STRIP);
            
            Vector oldPoint1 = getLanePoint(-.5f, 0);
            Vector oldPoint2 = getLanePoint(3f, 0);
            
            for(int i = 0; i <= amount; i++) {        
                Vector point1 = getLanePoint(-.5f, ((double) i / (double) amount) % 1);// Get the coordinate of the first point.
                Vector point2 = getLanePoint(3f, ((double) i / (double) amount) % 1);  // Get the coordinate of the second point.
                
                Vector diagonal = point1.subtract(oldPoint2);           // The line from oldP2 to P1.
                Vector newHorizontal = point2.subtract(point1);         // The line from P1 to P2.
                Vector oldHorizontal = oldPoint2.subtract(oldPoint1);   // The line from oldP1 to oldP2.
                
                Vector normal1 = diagonal.cross(oldHorizontal.normalized()); // The normal for the first triangle.
                Vector normal2 = diagonal.cross(newHorizontal).normalized(); // The normal for the second triangle.
                
                drawTriangle(gl, normal1, point1);
                drawTriangle(gl, normal2, point2);
                
                oldPoint1 = point1;          // Store the value of point1, which will be used by the next calculation.
                oldPoint2 = point2;          // Store the value of point2, which will be used by the next calculation.
            }
            
            gl.glEnd();
        }
    }
    
    public void drawTriangle(GL2 gl, Vector normal, Vector newPoint) {
        // Texture mapping.
        double scaledHeight = (newPoint.z() + 1.01f) / 2.02f; // Height on the texture {0, 1} (in stead of [0,1], because this resulted in (semi) transparant parts on the extreems).
        gl.glTexCoord1d(scaledHeight); // Map the new point to the 1D texture.
        
        // Set the normal.
        setNormal(gl, normal);
        
        // Draw the line to the new point.
        drawLineSegment(gl, newPoint);
    }
    
    /**
     * Sets the normal vector of the upcoming triangles.
     * 
     * @param GL2     The JOGL 2 object.
     * @param Vector  The normal vector which should be applied.
     */
    public void setNormal(GL2 gl, Vector normal) {
        gl.glNormal3d(normal.x(), normal.y(), normal.z());
    }
    
    /**
     * Draws a line part from the previous line end to the specified newPoint.
     * 
     * @param GL2       The JOGL 2 object.
     * @param Vector    The newPoint where the line should be drawn to. 
     */
    public void drawLineSegment(GL2 gl, Vector newPoint) {
        gl.glVertex3d(newPoint.x(), newPoint.y(), newPoint.z());
    }
    
    /**
     *  Stores all points of the track in an ArrayList. To prevent re-doing
     *  computations.
     */
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
    public Vector getLanePoint(float lane, double t) {
        if (null == controlPoints) {
            return getPoint(t).add(
                    getLaneTangent(0, t).cross(Vector.Z).normalized().scale(laneWidth * (.5f + lane))
            );
        } else {
            int amountOfSplines = controlPoints.length / 3; // Get the amount of splines to be drawn.
            double timePerSpline= 1 / (double) amountOfSplines;
            
            int selectedSpline  = (int) Math.floor(t * amountOfSplines);
            double relativeT    = (t % timePerSpline) * amountOfSplines;
            int startPoint      = 3 * selectedSpline;
            
            Vector point        = getCubicBezierPoint(relativeT,
                    controlPoints[startPoint],
                    controlPoints[startPoint+1],
                    controlPoints[startPoint+2],
                    controlPoints[(startPoint+3) % controlPoints.length]
            );
            
            Vector tangent      = getLaneTangent(lane, t);
            
            return point.add(tangent.cross(Vector.Z).normalized().scale((lane + .5f) * laneWidth)); // <- code goes here
        }
    }
    
    public Vector getLanePoint(int lane, double t) {
        return getLanePoint((float) lane, t);
    }
    
    public Vector getLaneTangent(int lane, double t) {
        return getLaneTangent((float) lane, t);
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    public Vector getLaneTangent(float lane, double t) {
        if (null == controlPoints) {
            return getTangent(t);
        } else {
            int amountOfSplines = controlPoints.length / 3; // Get the amount of splines to be drawn.
            double timePerSpline= 1 / (double) amountOfSplines;
            
            int selectedSpline  = (int) Math.floor(t * amountOfSplines);
            double relativeT    = (t % timePerSpline) * amountOfSplines;
            int startPoint      = 3 * selectedSpline;
            
            return getCubicBezierTangent(relativeT,
                    controlPoints[startPoint],
                    controlPoints[startPoint+1],
                    controlPoints[startPoint+2],
                    controlPoints[(startPoint+3) % (controlPoints.length)]
            );
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
        return P0.scale(Math.pow(t, 3d))
                .add(P1.scale(3f * t * t * (1f - t)))
                .add(P2.scale(3f * t * Math.pow(1 - t, 2d)))
                .add(P3.scale(Math.pow(1f - t, 3d)));
    }
    
    private Vector getCubicBezierPoint(double t, Vector[] P) {
        return getCubicBezierPoint(t, P[0], P[1], P[2], P[3]);
    }
    
    /**
     * Returns a tangent on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     * P = 3 *P0 * t^2 + P1 * (6 * (1 - t) * t - 3 * t^2) + P2 * (3 * (1 - t)^2 - 6 * (1 - t) * t) + -3 * P3 * (1 - t)^2
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
                                                   Vector P2, Vector P3) {
        return  P0.scale(3 * t * t)
                .add(P1.scale((6 - 9 * t)* t))
                .add(P2.scale(9 * t * t - 12 * t + 3))
                .add(P3.scale((6 - 3 * t) * t - 3));
    }
}