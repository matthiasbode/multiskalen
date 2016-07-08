package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dimN.*;

/**
 *
 * @author milbradt
 */
public abstract class AbstractVectorFunctionNd implements VectorFunctionNd, LinearPoint<AbstractVectorFunctionNd> {
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract VectorNd getValue(PointNd p);
    public abstract int getDim();


    public final AbstractVectorFunctionNd add(final VectorFunctionNd f1){
        if(AbstractVectorFunctionNd.this.getDim()!=f1.getDim()) return null;
        return new AbstractVectorFunctionNd(){
            public VectorNd getValue(PointNd p){
                return AbstractVectorFunctionNd.this.getValue(p).add(f1.getValue(p));
            }
            
            public int getDim()
            {
            	return AbstractVectorFunctionNd.this.getDim();
            }
        };
    }

    public final AbstractVectorFunctionNd add( final AbstractVectorFunctionNd f1){
    	return add( (VectorFunctionNd) f1);	   	
    }
    
    public final AbstractVectorFunctionNd sub(final VectorFunctionNd f1){
        if(AbstractVectorFunctionNd.this.getDim()!=f1.getDim()) return null;
        return new AbstractVectorFunctionNd(){
            public VectorNd getValue(PointNd p){
                return AbstractVectorFunctionNd.this.getValue(p).sub(f1.getValue(p));
            }
            
            public int getDim()
            {
            	return AbstractVectorFunctionNd.this.getDim();
            }
        };
    }

    public final AbstractVectorFunctionNd sub( final AbstractVectorFunctionNd f1){
    	return sub( (VectorFunctionNd) f1);
    }
    
    public AbstractVectorFunctionNd mult(final double scalar){
        return new AbstractVectorFunctionNd(){
            public VectorNd getValue(PointNd p){
                return AbstractVectorFunctionNd.this.getValue(p).mult(scalar);
            }
            public int getDim()
            {
            	return AbstractVectorFunctionNd.this.getDim();
            }
        };
    }
    
}
