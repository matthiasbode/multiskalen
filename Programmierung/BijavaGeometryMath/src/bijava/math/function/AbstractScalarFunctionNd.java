package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dimN.*;
//==========================================================================//
/** The interface "AbstractScalarFunctionNd" provides methods for addition and multiplication N dimensional
 *  scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public abstract class AbstractScalarFunctionNd implements ScalarFunctionNd, LinearPoint<AbstractScalarFunctionNd> {
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract double getValue(PointNd p);
    public abstract int getDim();
    
    public final AbstractScalarFunctionNd add(final ScalarFunctionNd f1){
        if(AbstractScalarFunctionNd.this.getDim()!=f1.getDim()) return null;
        return new AbstractScalarFunctionNd(){
            public double getValue(PointNd p){
                return AbstractScalarFunctionNd.this.getValue(p) + f1.getValue(p);
            }
            
            public int getDim()
            {
            	return AbstractScalarFunctionNd.this.getDim();
            }
        };
    }

    public final AbstractScalarFunctionNd add( final AbstractScalarFunctionNd f1){
    	return add( (ScalarFunctionNd) f1);	   	
    }
    
    public final AbstractScalarFunctionNd sub(final ScalarFunctionNd f1){
        if(AbstractScalarFunctionNd.this.getDim()!=f1.getDim()) return null;
        return new AbstractScalarFunctionNd(){
            public double getValue(PointNd p){
                return AbstractScalarFunctionNd.this.getValue(p) - f1.getValue(p);
            }
            
            public int getDim()
            {
            	return AbstractScalarFunctionNd.this.getDim();
            }
        };
    }
    
    public final AbstractScalarFunctionNd sub( final AbstractScalarFunctionNd f1){
    	return sub( (ScalarFunctionNd) f1);
    }
    
    
    public final AbstractScalarFunctionNd mult(final ScalarFunctionNd f1){
        if(AbstractScalarFunctionNd.this.getDim()!=f1.getDim()) return null;
        return new AbstractScalarFunctionNd(){
            public double getValue(PointNd p){
                return AbstractScalarFunctionNd.this.getValue(p) * f1.getValue(p);
            }
            public int getDim()
            {
            	return AbstractScalarFunctionNd.this.getDim();
            }
        };
    }
    
    public AbstractScalarFunctionNd mult(final double scalar){
        return new AbstractScalarFunctionNd(){
            public double getValue(PointNd p){
                return scalar*AbstractScalarFunctionNd.this.getValue(p);
            }
            public int getDim()
            {
            	return AbstractScalarFunctionNd.this.getDim();
            }
        };
    }
    
    /**
     * the sum of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
     */
    public static AbstractScalarFunctionNd add(final ScalarFunctionNd f, final ScalarFunctionNd g) {
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractScalarFunctionNd() {
            public double getValue(PointNd x) {
                return f.getValue(x)+g.getValue(x);
            }
            public int getDim()
            {
            	return f.getDim();
            }
        };
    }
    
    /**
     * the difference of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f-g defined by (f-g)(x)=f(x)-g(x)
     */
    public static AbstractScalarFunctionNd sub(final ScalarFunctionNd f, final ScalarFunctionNd g) {
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractScalarFunctionNd() {
            public double getValue(PointNd x) {
                return f.getValue(x)-g.getValue(x);
            }
            public int getDim()
            {
            	return f.getDim();
            }
        };
    }
    
    /**
     * the product of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f*g defined by (f*g)(x)=f(x)*g(x)
     */
    public static AbstractScalarFunctionNd mult(final ScalarFunctionNd f, final ScalarFunctionNd g) {
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractScalarFunctionNd() {
            public double getValue(PointNd x) {
                return f.getValue(x)*g.getValue(x);
            }
            public int getDim()
            {
            	return f.getDim();
            }
        };
    }
    
    /**
     * the quaotient of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f/g defined by (f/g)(x)=f(x)/g(x)
     */
    public static AbstractScalarFunctionNd div(final ScalarFunctionNd f, final ScalarFunctionNd g) {
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractScalarFunctionNd() {
            public double getValue(PointNd x) {
                return f.getValue(x)/g.getValue(x);
            }
            public int getDim()
            {
            	return f.getDim();
            }
        };
    }
    
    /**
     * the product of the function f with a scalar value
     * @param f the function
     * @param scalar
     * @return the function scalar*f defined by scalar*f(x)=scalar*g(x)
     */
    public AbstractScalarFunctionNd mult(final ScalarFunctionNd f,final double scalar){
        return new AbstractScalarFunctionNd(){
            public double getValue(PointNd p){
                return scalar*f.getValue(p);
            }
            public int getDim()
            {
            	return f.getDim();
            }
        };
    }
}

