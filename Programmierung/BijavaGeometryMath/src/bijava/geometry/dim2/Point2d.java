package bijava.geometry.dim2;
import bijava.geometry.*;
import bijava.geometry.dimN.PointNd;

//==========================================================================//
/** The class "Point2d" describe ....
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class Point2d extends javax.vecmath.Point2d  implements Cloneable, EuclideanPoint<Point2d> {
    
    public static final Point2d INFINITY = new Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    /**
     * Constructs and initializes a Point2d from the specified xy coordinates.
     */
    public Point2d(double x, double y) {
        super(x, y);
    }
    /**
     * Constructs and initializes a Point2d to (0,0).
     */
    public Point2d() {
        super();
    }
    /**
     * Constructs and initializes a Point2d from the specified Point2d.
     */
    public Point2d(Point2d p) {
        super(p);
    }
    
    @Override
    public Point2d clone() {
        return new Point2d(this);
    }
    
    /**
     * Computes the distance between this point and point p1.
     */
    public double distance(Point2d p1) {
        return super.distance(p1);
    }
    
    /**
     * Gets the distance vector to a point p.
     * 
     * @param p point.
     * @return distance vector to <code>other</code>.
     */
    public Vector2d distanceVector(Point2d p) {
        return new Vector2d(p.x - x, p.y - y);
    }
    
    /* kreuzprodukt */
    double cross(Point2d p) { return x * p.y - y * p.x; }
    
    /* quadrat der norm */
    public double norm2() { return x*x + y*y; }
    
    public double norm(){
        return Math.sqrt(x*x+y*y);
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
    
    public Point2d add(Point2d point){
        return new Point2d(x+point.x,y+point.y);
    }
    
    public Point2d sub(Point2d p){
        return new Point2d(x-p.x,y-p.y);
    }
    
    public Point2d mult(double scalar){
        return new Point2d(scalar*x,scalar*y);
    }

    /**
     * Computes the scalarproduct between this point and point p1.
     */
    public double scalarProduct(Point2d point) throws FalseSpaceDimensionException {
        return this.x*point.x+this.y*point.y;
    }
//
    public void translate(Point2d point) throws FalseSpaceDimensionException {
        this.x+=point.x;
        this.y+=point.y;
    }

    public PointNd toPointNd() {
        return new PointNd(x, y);
    }
}
