package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dim3.*;
//==========================================================================//
/** The interface "AbstractScalarFunction3d" provides methods for addition and multiplication 3 dimensional
 *  scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public abstract class AbstractScalarFunction3d implements ScalarFunction3d, LinearPoint<AbstractScalarFunction3d> {
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract double getValue(Point3d p);
    
    public final AbstractScalarFunction3d add(final ScalarFunction3d f1){
            return new AbstractScalarFunction3d(){
                public double getValue(Point3d p){
                    return AbstractScalarFunction3d.this.getValue(p) + f1.getValue(p);
                }
            };
    }
    
    public final AbstractScalarFunction3d add( final AbstractScalarFunction3d g){
        return add((ScalarFunction3d)g);
    }
    
    public final AbstractScalarFunction3d sub(final ScalarFunction3d f1){
            return new AbstractScalarFunction3d(){
                public double getValue(Point3d p){
                    return AbstractScalarFunction3d.this.getValue(p) - f1.getValue(p);
                }
            };
    }
    
    public final AbstractScalarFunction3d sub( final AbstractScalarFunction3d g){
        return sub((ScalarFunction3d)g);
    }
    
    public final AbstractScalarFunction3d mult(final ScalarFunction3d f1){
            return new AbstractScalarFunction3d(){
                public double getValue(Point3d p){
                    return AbstractScalarFunction3d.this.getValue(p) * f1.getValue(p);
                }
            };
    }
    
    public AbstractScalarFunction3d mult(final double scalar){
        return new AbstractScalarFunction3d(){
            public double getValue(Point3d p){
                return scalar*AbstractScalarFunction3d.this.getValue(p);
            }
        };
    }
    
    /**
     * the sum of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
     */
    public static AbstractScalarFunction3d add(final ScalarFunction3d f, final ScalarFunction3d g) {
        return new AbstractScalarFunction3d() {
            public double getValue(Point3d x) {
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
    public static AbstractScalarFunction3d sub(final ScalarFunction3d f, final ScalarFunction3d g) {
        return new AbstractScalarFunction3d() {
            public double getValue(Point3d x) {
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
    public static AbstractScalarFunction3d mul(final ScalarFunction3d f, final ScalarFunction3d g) {
        return new AbstractScalarFunction3d() {
            public double getValue(Point3d x) {
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
    public static AbstractScalarFunction3d div(final ScalarFunction3d f, final ScalarFunction3d g) {
        return new AbstractScalarFunction3d() {
            public double getValue(Point3d x) {
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
    public AbstractScalarFunction3d mult(final ScalarFunction3d f,final double scalar){
        return new AbstractScalarFunction3d(){
            public double getValue(Point3d p){
                return scalar*f.getValue(p);
            }
        };
    }
}

