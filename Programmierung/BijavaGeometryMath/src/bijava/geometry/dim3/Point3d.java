package bijava.geometry.dim3;
import bijava.geometry.*;

public class Point3d extends javax.vecmath.Point3d implements EuclideanPoint<Point3d> {
    /**
     * Constructs and initializes a Point3d from the specified xyz coordinates.
     */
    public Point3d(double x, double y, double z) {
        super(x, y, z);
    }
    /**
     * Constructs and initializes a Point3d to (0,0).
     */
    public Point3d() {
        super();
    }
    /**
     * Constructs and initializes a Point2d from the specified Point2d.
     */
    public Point3d(Point3d p) {
        super(p);
    }
    
    /**
     * Computes the distance between this point and point p1.
     */
    public double distance(Point3d point) {
        return super.distance(point);
    }
    
    public double scalarProduct(Point3d point) throws FalseSpaceDimensionException{
        return x*point.x+y*point.y+z*point.z;
    }
    
    public double norm(){
        return Math.sqrt(x*x+y*y+z*z);
    }
    
    public int dim(){
        return 3;
    }
    public double getCoord(int i){
        if(i<0 || i>=3) throw new IndexOutOfBoundsException("bad coordinate index "+i);
        switch(i){
            case 0: return x;
            case 1: return y;
            case 2: return z;
        }
        return Double.NaN;
    }
    public void setCoord(int i,double d){
        if(i<0 || i>=3) throw new IndexOutOfBoundsException("bad coordinate index "+i);
        switch(i){
            case 0: x=d; break;
            case 1: y=d; break;
            case 2: z=d; break;
        }
    }
    
    public double[] getCoords(){
        double[] coords=new double[3];
        coords[0]=x;
        coords[0]=y;
        coords[0]=z;
        return coords;
    }
    
    @Override
    public Point3d add(Point3d point){
        return new Point3d(x+point.x,y+point.y,z+point.z);
    }
//    
//    public final Point3d add(VectorPoint p) {
//        if (p instanceof Point3d) return add((Point3d)p);
//        return null;
//    }
//    
//    public final Point3d add(LinearPoint p) {
//        if (p instanceof Point3d) return add((Point3d)p);
//        return null;
//    }
    
    public Point3d sub(Point3d point){
        return new Point3d(x-point.x,y-point.y,z-point.z);
    }
    
//    public final Point3d sub(VectorPoint p) {
//        if (p instanceof Point3d) return sub((Point3d)p);
//        return null;
//    }
//    
//    public final Point3d sub(LinearPoint p) {
//        if (p instanceof Point3d) return sub((Point3d)p);
//        return null;
//    }
    
    public Point3d mult(double scalar){
        return new Point3d(scalar*x,scalar*y,scalar*z);
    }
}
