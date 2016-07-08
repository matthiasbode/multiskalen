package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dim2.*;
//==========================================================================//
/** The interface "AbstractScalarFunction2d" provides methods for addition and multiplication N dimensional
 *  scalar functions.
 *
 *  @autor University of Hannover
 *  @autor Institute of Computer Science in Civil Engineering
 *  @autor Dr.-Ing. habil. Peter Milbradt                                   */
//==========================================================================//
public abstract class AbstractScalarFunction2d implements ScalarFunction2d, LinearPoint<AbstractScalarFunction2d> {
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    @Override
    public abstract double getValue(Point2d p);
    
    public final AbstractScalarFunction2d add(final ScalarFunction2d f1){
        return new AbstractScalarFunction2d(){
            @Override
            public double getValue(Point2d p){
                return AbstractScalarFunction2d.this.getValue(p) + f1.getValue(p);
            }
        };
    }
    
    public final AbstractScalarFunction2d add( final AbstractScalarFunction2d g){
        return add((ScalarFunction2d)g);
    }
    
    public final AbstractScalarFunction2d sub(final ScalarFunction2d f1){
        return new AbstractScalarFunction2d(){
            public double getValue(Point2d p){
                return AbstractScalarFunction2d.this.getValue(p) - f1.getValue(p);
            }
        };
    }
    
    public final AbstractScalarFunction2d sub( final AbstractScalarFunction2d g){
        return sub((ScalarFunction2d)g);
    }
        
    public final AbstractScalarFunction2d mult(final ScalarFunction2d f1){
        return new AbstractScalarFunction2d(){
            public double getValue(Point2d p){
                return AbstractScalarFunction2d.this.getValue(p) * f1.getValue(p);
            }
        };
    }
    
    public AbstractScalarFunction2d mult(final double scalar){
        return new AbstractScalarFunction2d(){
            public double getValue(Point2d p){
                return scalar*AbstractScalarFunction2d.this.getValue(p);
            }
        };
    }
    
    /**
     * the sum of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
     */
    public static AbstractScalarFunction2d add(final ScalarFunction2d f, final ScalarFunction2d g) {
        return new AbstractScalarFunction2d() {
            public double getValue(Point2d x) {
                return f.getValue(x)+g.getValue(x);
            }
        };
    }
    
    /**
     * the difference of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f-g defined by (f-g)(x)=f(x)-g(x)
     */
    public static AbstractScalarFunction2d sub(final ScalarFunction2d f, final ScalarFunction2d g) {
        return new AbstractScalarFunction2d() {
            public double getValue(Point2d x) {
                return f.getValue(x)-g.getValue(x);
            }
        };
    }
    
    /**
     * the product of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f*g defined by (f*g)(x)=f(x)*g(x)
     */
    public static AbstractScalarFunction2d mul(final ScalarFunction2d f, final ScalarFunction2d g) {
        return new AbstractScalarFunction2d() {
            public double getValue(Point2d x) {
                return f.getValue(x)*g.getValue(x);
            }
        };
    }
    
    /**
     * the quaotient of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f/g defined by (f/g)(x)=f(x)/g(x)
     */
    public static AbstractScalarFunction2d div(final ScalarFunction2d f, final ScalarFunction2d g) {
        return new AbstractScalarFunction2d() {
            public double getValue(Point2d x) {
                return f.getValue(x)/g.getValue(x);
            }
        };
    }
    
    /**
     * the product of the function f with a scalar value
     * @param f the function
     * @param scalar
     * @return the function scalar*f defined by scalar*f(x)=scalar*g(x)
     */
    public AbstractScalarFunction2d mult(final ScalarFunction2d f,final double scalar){
        return new AbstractScalarFunction2d(){
            public double getValue(Point2d p){
                return scalar*f.getValue(p);
            }
        };
    }
}

