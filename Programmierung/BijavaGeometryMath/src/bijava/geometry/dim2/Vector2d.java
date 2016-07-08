package bijava.geometry.dim2;
import bijava.geometry.*;

//==========================================================================//
/** The class "Vector2d" describe ....
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class Vector2d extends javax.vecmath.Vector2d implements VectorPoint {
    /**
     * Constructs and initializes a Vector2d from the specified xy coordinates.
     */
    public Vector2d(double x, double y) {
        super(x, y);
    }
    /**
     * Constructs and initializes a Vector3d to (0,0).
     */
    public Vector2d() {
        super();
    }
    /**
     * Constructs and initializes a Vector3d from the specified Vector3d.
     */
    public Vector2d(Vector2d v) {
        super(v);
    }
    public int dim(){
        return 2;
    }
    public double getCoord(int i){
        if(i<0 || i>=2) throw new IndexOutOfBoundsException("bad coordinate index "+i);
        if(i==0) return x;
        else return y;
    }
    public void setCoord(int i,double d){
        if(i<0 || i>=2) throw new IndexOutOfBoundsException("bad coordinate index "+i);
        if(i==0) x=d;
        else y=d;
    }
    
    public double[] getCoords(){
        double[] coords=new double[2];
        coords[0]=x;
        coords[1]=y;
        return coords;
    }
    
    /**
     * return a new element of linear space as the sum of itself and the specified vector
     */
    public VectorPoint add(VectorPoint p){
        Vector2d point = (Vector2d) p;
        return new Vector2d(x+point.x,y+point.y);
    }
    /**
     * return a new vector as the sum of itself and the specified vector
     */
    public Vector2d add(Vector2d point){
        return new Vector2d(x+point.x,y+point.y);
    }
    
    public VectorPoint sub(VectorPoint p){
        Vector2d point = (Vector2d) p;
        return new Vector2d(x-point.x,y-point.y);
    }
    public Vector2d sub(Vector2d point){
        return new Vector2d(x-point.x,y-point.y);
    }
    public Vector2d mult(double scalar){
        return new Vector2d(scalar*x,scalar*y);
    }

    public LinearPoint add(LinearPoint point) {
        if(point instanceof Vector2d) return add((Vector2d) point);
        return null;
    }

    public LinearPoint sub(LinearPoint point) {
        if(point instanceof Vector2d) return sub((Vector2d) point);
        return null;
    }

    public static double length(double dx, double dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double angleDeg(double dx, double dy) {
        return Math.toDegrees(angleRad(dx, dy));
    }

    public static double angleRad(double dx, double dy) {
        double length = length(dx, dy);
        return (dy < 0.) ? (Math.toRadians(360.) - Math.acos(dx / length)) : Math.acos(dx / length);
    }
}
