package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dim2.*;
import bijava.geometry.dimN.VectorNd;

/**
 *
 * @author milbradt
 */
public abstract class AbstractVectorFunction2d implements VectorFunction2d, LinearPoint<AbstractVectorFunction2d>{
    
    /**Funktionswerte der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    @Override
    public abstract VectorNd getValue(Point2d p);

    
    public final AbstractVectorFunction2d add(final VectorFunction2d f1){
        return new AbstractVectorFunction2d(){
            public VectorNd getValue(Point2d p){
                return AbstractVectorFunction2d.this.getValue(p).add(f1.getValue(p));
            }
        };
    }
    public final AbstractVectorFunction2d add( final AbstractVectorFunction2d g){
        return add((VectorFunction2d)g);
    }
    
    public final AbstractVectorFunction2d sub(final VectorFunction2d f1){
        return new AbstractVectorFunction2d(){
            public VectorNd getValue(Point2d p){
                return AbstractVectorFunction2d.this.getValue(p).sub(f1.getValue(p));
            }
        };
    }
    public final AbstractVectorFunction2d sub( final AbstractVectorFunction2d g){
        return sub((VectorFunction2d)g);
    }
    
    public AbstractVectorFunction2d mult(final double scalar){
        return new AbstractVectorFunction2d(){
            public VectorNd getValue(Point2d p){
                return AbstractVectorFunction2d.this.getValue(p).mult(scalar);
            }
        };
    }
    
}
