package bijava.geometry.dim3;
import bijava.geometry.*;

public class Vector3d extends javax.vecmath.Vector3d implements VectorPoint<Vector3d> {
    /**
     * Constructs and initializes a Vector3d from the specified xyz coordinates.
     */
    public Vector3d(double x, double y, double z) {
        super(x, y, z);
    }
    /**
     * Constructs and initializes a Vector3d to (0,0,0).
     */
    public Vector3d() {
        super();
    }
    /**
     * Constructs and initializes a Vector3d from the specified Vector3d.
     */
    public Vector3d(Vector3d v) {
        super(v);
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
    
    public VectorPoint add(VectorPoint p){
        Vector3d point = (Vector3d) p;
        return new Vector3d(x+point.x,y+point.y,z+point.z);
    }
    
    public Vector3d add(Vector3d point){
        return new Vector3d(x+point.x,y+point.y,z+point.z);
    }
    
//    public final Vector3d add(LinearPoint p) {
//        if (p instanceof Vector3d) return add((Vector3d)p);
//        return null;
//    }
    
    public VectorPoint sub(VectorPoint p){
        Vector3d point = (Vector3d) p;
        return new Vector3d(x-point.x,y-point.y,z-point.z);
    }
    
    public Vector3d sub(Vector3d point){
        return new Vector3d(x-point.x,y-point.y,z-point.z);
    }
    
//    public final Vector3d sub(LinearPoint p) {
//        if (p instanceof Vector3d) return sub((Vector3d)p);
//        return null;
//    }
    
    public Vector3d mult(double scalar){
        return new Vector3d(scalar*x,scalar*y,scalar*z);
    }
}
