package bijava.geometry.dimN;
import bijava.geometry.*;
import bijava.geometry.dim2.Vector2d;
import bijava.geometry.dim3.Vector3d;



/**
 *
 * @author Milbradt
 */
public class VectorNd<T extends VectorNd> extends AbstractVectorNd<VectorNd>{
    
    /** Creates a new instance of VectorNd with the dimension i*/
    public VectorNd(int i) {
        super(i);
    }
    
    /** Creates a new instance of VectorNd */
    public VectorNd(double[] d) {
        super(d);
    }
    
    /**Umwandlungskonstruktor wird zum Beispiel
     * in der Klasse Derivation2d verwendet
     *
     * @param Vector2d
     * @author pick
     * @version 14.02.2005
     */
    
    public VectorNd(Vector2d v) {
        super(new double[] {v.x,v.y});
    }
    
    /**Umwandlungskonstruktor wird zum Beispiel
     * in der Klasse Derivation3d verwendet
     *
     * @param Vector3d
     * @author pick
     * @version 14.02.2005
     */
    
    public VectorNd(Vector3d v) {
        super(new double[] {v.x,v.y,v.z});
    }
    
    
    /** Creates a new instance of VectorNd */
    public VectorNd(VectorNd vectornd) {
        super(vectornd);
    }

    public VectorNd(double d, double d0) {
        super(new double[] {d,d0});
    }
    
    @Override
    public VectorNd sub(VectorNd p) {
        if( x.length == p.x.length ){
            VectorNd result=new VectorNd(p);
            for(int i=0;i<x.length;i++) result.x[i]-=x[i];
            return result;
        } else return null;
    }
    
    @Override
    public VectorNd mult(double scalar){
        VectorNd result=new VectorNd(this);
            for(int i=0;i<x.length;i++) result.x[i]*=scalar;
        return (T)result;
    }
    
    @Override
    public VectorNd add(VectorNd p) {
        if (x.length == p.x.length) {
            VectorNd result = new VectorNd(p);
            for (int i = 0; i < x.length; i++) {
                result.x[i] += x[i];
            }
            return result;
        } else {
            return null;
        }
    }
}
