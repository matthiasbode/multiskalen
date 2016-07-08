/*
 * AbstractDifferentialVectorFunction1d.java
 *
 * Created on 17. Mai 2006, 12:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dimN.*;

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
public abstract class AbstractDifferentialVectorFunction1d extends AbstractVectorFunction1d implements DifferentialVectorFunction1d {
    
   /**Gradient der Funktion am Punkt x
     * @param x der Punkt, fuer den der Gradient zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract double[] getGradient(double x);
    
    public VectorFunction1d getDerivation() {
        return new VectorFunction1d() {
            public VectorNd getValue(double p) {
                return new VectorNd(AbstractDifferentialVectorFunction1d.this.getGradient(p));
            }
        };
    }
    
    public final AbstractDifferentialVectorFunction1d add( final DifferentialVectorFunction1d f1){
        return new AbstractDifferentialVectorFunction1d(){
            public VectorNd getValue(double p){
                return AbstractDifferentialVectorFunction1d.this.getValue(p).add(f1.getValue(p));
            }
            public double[] getGradient(double p) {
                double[] grad1 = AbstractDifferentialVectorFunction1d.this.getGradient(p);
                double[] grad2 = f1.getGradient(p);
                if (grad1.length==grad2.length) {
                    for (int i=0; i<grad1.length; i++) grad1[i]+=grad2[i];
                    return grad1;
                }
                else return null;
            }
        };
    }
    public final AbstractDifferentialVectorFunction1d add( final AbstractDifferentialVectorFunction1d g){
        return this.add((DifferentialVectorFunction1d)g);
    }
    
    public final AbstractDifferentialVectorFunction1d sub(final DifferentialVectorFunction1d f1){
        return new AbstractDifferentialVectorFunction1d(){
            public VectorNd getValue(double p){
                return AbstractDifferentialVectorFunction1d.this.getValue(p).sub(f1.getValue(p));
            }
            public double[] getGradient(double p) {
                double[] grad1 = AbstractDifferentialVectorFunction1d.this.getGradient(p);
                double[] grad2 = f1.getGradient(p);
                if (grad1.length==grad2.length) {
                    for (int i=0; i<grad1.length; i++) grad1[i]-=grad2[i];
                    return grad1;
                }
                else return null;
            }
            
        };
    }
    public final AbstractDifferentialVectorFunction1d sub( final AbstractDifferentialVectorFunction1d g){
        return this.sub((DifferentialVectorFunction1d)g);
    }
    
    public final AbstractDifferentialVectorFunction1d mult(final double scalar){
        return new AbstractDifferentialVectorFunction1d(){
            public VectorNd getValue(double p){
                return AbstractDifferentialVectorFunction1d.this.getValue(p).mult(scalar);
            }
            public double[] getGradient(double p) {
                double[] grad = AbstractDifferentialVectorFunction1d.this.getGradient(p);
                for (int i=0; i<grad.length; i++) grad[i]*=scalar;
                return grad;
            }
        };
    }
    
}
