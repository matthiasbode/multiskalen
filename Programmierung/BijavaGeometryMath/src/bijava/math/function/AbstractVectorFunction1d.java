package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dimN.VectorNd;

/**
 *
 *  ------------------------------------------------
 *  Funktionen
 *  - setPeriodic(boolean): void
 *  - isPeriodic(): boolean
 *  entfernt. Stattdessen PeriodicalVectorFunction1d
 *  von ScalarFunction1d ableiten.
 *  Berthold, 31.05.06
 *  ------------------------------------------------
 *
 * @author berthold
 */
public abstract class AbstractVectorFunction1d implements VectorFunction1d, LinearPoint<AbstractVectorFunction1d>{
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract VectorNd getValue(double x);
    

    /**
     * the sum of this function f with a other function g
     * @param g the second function
     * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
     */
    public final AbstractVectorFunction1d add( final VectorFunction1d g){
        return new AbstractVectorFunction1d(){
            public VectorNd getValue(double p){
                return AbstractVectorFunction1d.this.getValue(p).add(g.getValue(p));
            }
        };
    }
    public final AbstractVectorFunction1d add( final AbstractVectorFunction1d g){
        return add((VectorFunction1d)g);
    }
    
    /**
     * the difference of this function f with a other function g
     * @param g the second function
     * @return the function f-g defined by (f-g)(x)=f(x)-g(x)
     */
    public final AbstractVectorFunction1d sub(final VectorFunction1d g){
        return new AbstractVectorFunction1d(){
            public VectorNd getValue(double p){
                return AbstractVectorFunction1d.this.getValue(p).sub(g.getValue(p));
            }
        };
    }
    public final AbstractVectorFunction1d sub( final AbstractVectorFunction1d g){
        return sub((VectorFunction1d)g);
    }
    
    /**
     * the product of this function f with a scalar value
     * @param scalar
     * @return the function scalar*f defined by scalar*f(x)=scalar*g(x)
     */
    public AbstractVectorFunction1d mult(final double scalar){
        return new AbstractVectorFunction1d(){
            public VectorNd getValue(double p){
                return AbstractVectorFunction1d.this.getValue(p).mult(scalar);
            }
        };
    }
}
