package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dim3.*;
import bijava.geometry.dimN.VectorNd;

/**
 *
 * @author berthold
 */
public abstract class AbstractVectorFunction3d implements VectorFunction3d, LinearPoint<AbstractVectorFunction3d> {
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract VectorNd getValue(Point3d p);
    
    
    public final AbstractVectorFunction3d add(final VectorFunction3d f1){
            return new AbstractVectorFunction3d(){
                public VectorNd getValue(Point3d p){
                    return AbstractVectorFunction3d.this.getValue(p).add(f1.getValue(p));
                }
            };
    }
    public final AbstractVectorFunction3d add( final AbstractVectorFunction3d g){
        return add((VectorFunction3d)g);
    }
    
    public final AbstractVectorFunction3d sub(final VectorFunction3d f1){
            return new AbstractVectorFunction3d(){
                public VectorNd getValue(Point3d p){
                    return AbstractVectorFunction3d.this.getValue(p).sub(f1.getValue(p));
                }
            };
    }
    public final AbstractVectorFunction3d sub( final AbstractVectorFunction3d g){
        return sub((VectorFunction3d)g);
    }
    
    public AbstractVectorFunction3d mult(final double scalar){
        return new AbstractVectorFunction3d(){
            public VectorNd getValue(Point3d p){
                return AbstractVectorFunction3d.this.getValue(p).mult(scalar);
            }
        };
    }
    
}
