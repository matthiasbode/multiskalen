package bijava.math.function;

//==========================================================================//

import bijava.geometry.LinearPoint;

/** The abstract class "AbstractDifferentialFunction1d" provides methods for 1 dimensional
 *  scalar functions.
 *
 *  ------------------------------------------------
 *  Funktionen
 *  - setPeriodic(boolean): void
 *  - isPeriodic(): boolean
 *  entfernt. Stattdessen PeriodicalScalarFunction1d
 *  von ScalarFunction1d ableiten.
 *  Berthold, 31.05.06
 *  ------------------------------------------------
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, February 2005</dd></p>
 *  @author University of Hannover</dd>
 *  @author Institute of Computer Science in Civil Engineering</dd>
 *  @author Dipl.-Ing. Tobias Pick</dd>
 *  @author Dr.-Ing. habil. Peter Milbradt</dd>
 *  @author Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public abstract class AbstractDifferentialFunction1d extends AbstractScalarFunction1d implements DifferentialScalarFunction1d{
    
    /**Gradient der Funktion am Punkt x
     * @param x der Punkt, fuer den der Gradient zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract double getGradient(double x);
    
    public AbstractScalarFunction1d getDerivation() {
        return new AbstractScalarFunction1d() {
            public double getValue(double p) {
                return getGradient(p);
            }
        };
    }
    
    /**
     * the composite this function f with a other function g
     * @param g the second function
     * @return f o g - first g then f
     */
    public final AbstractDifferentialFunction1d compose(final DifferentialScalarFunction1d  g) {
        return new AbstractDifferentialFunction1d() {
            public double getValue(double x) {
                return AbstractDifferentialFunction1d.this.getValue(g.getValue(x));
            }
            public double getGradient(double p) {
                return AbstractDifferentialFunction1d.this.getGradient(g.getValue(p))*g.getGradient(p);
            }
        };
    }
    
    public final AbstractDifferentialFunction1d add( final DifferentialScalarFunction1d f1){
        return new AbstractDifferentialFunction1d(){
            public double getValue(double p){
                return AbstractDifferentialFunction1d.this.getValue(p)+ f1.getValue(p);
            }
            public double getGradient(double p) {
                return AbstractDifferentialFunction1d.this.getGradient(p)+f1.getGradient(p);
            }
        };
    }
    
    public final AbstractDifferentialFunction1d add( final AbstractDifferentialFunction1d g){
        return this.add((DifferentialScalarFunction1d)g);
    }
    
    public final AbstractDifferentialFunction1d sub(final DifferentialScalarFunction1d f1){
        return new AbstractDifferentialFunction1d(){
            public double getValue(double p){
                return AbstractDifferentialFunction1d.this.getValue(p)- f1.getValue(p);
            }
            public double getGradient(double p) {
                return AbstractDifferentialFunction1d.this.getGradient(p)-f1.getGradient(p);
            }
            
        };
    }
    
    public final AbstractDifferentialFunction1d sub( final AbstractDifferentialFunction1d g){
        return this.sub((DifferentialScalarFunction1d)g);
    }
    
    public final AbstractDifferentialFunction1d mult(final DifferentialScalarFunction1d f1){
        return new AbstractDifferentialFunction1d(){
            public double getValue(double p){
                return AbstractDifferentialFunction1d.this.getValue(p)* f1.getValue(p);
            }
            public double getGradient(double p) {
                return AbstractDifferentialFunction1d.this.getGradient(p)*f1.getValue(p)+AbstractDifferentialFunction1d.this.getValue(p)*f1.getGradient(p);
            }
        };
    }
    
    @Override
    public AbstractDifferentialFunction1d mult(final double scalar){
        return new AbstractDifferentialFunction1d(){
            public double getValue(double p){
                return scalar*AbstractDifferentialFunction1d.this.getValue(p);
            }
            public double getGradient(double p) {
                return AbstractDifferentialFunction1d.this.getGradient(p)*scalar;
            }
        };
    }
    
    /**
     * the quaotient of this function f with a other function g
     * @param g the second function
     * @return the function f/g defined by (f/g)(x)=f(x)/g(x)
     */
    public final AbstractDifferentialFunction1d div(final DifferentialScalarFunction1d g){
        return new AbstractDifferentialFunction1d(){
            public double getValue(double p){
                return AbstractDifferentialFunction1d.this.getValue(p) / g.getValue(p);
            }
            public double getGradient(double p) {
                return (AbstractDifferentialFunction1d.this.getGradient(p)*g.getValue(p)-AbstractDifferentialFunction1d.this.getValue(p)*g.getGradient(p))/Math.pow(g.getValue(p),2);
            }
        };
    }
    
    /**
     * the composite of to functions
     * @param f the first function
     * @param g the second function
     * @return f o g - first g then f
     */
    public final static AbstractDifferentialFunction1d compose(final DifferentialScalarFunction1d  f, final DifferentialScalarFunction1d  g) {
        return new AbstractDifferentialFunction1d() {
            public double getValue(double x) {
                return f.getValue(g.getValue(x));
            }
            public double getGradient(double p) {
                return f.getGradient(g.getValue(p))*g.getGradient(p);
            }
        };
    }
    
    /**
     * the sum of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
     */
    public static AbstractDifferentialFunction1d add(final DifferentialScalarFunction1d f, final DifferentialScalarFunction1d g) {
        return new AbstractDifferentialFunction1d() {
            public double getValue(double x) {
                return f.getValue(x)+g.getValue(x);
            }
            public double getGradient(double p) {
                return f.getGradient(p)+g.getGradient(p);
            }
        };
    }
    
    /**
     * the difference of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f-g defined by (f-g)(x)=f(x)-g(x)
     */
    public static AbstractDifferentialFunction1d sub(final DifferentialScalarFunction1d f, final DifferentialScalarFunction1d g) {
        return new AbstractDifferentialFunction1d() {
            public double getValue(double x) {
                return f.getValue(x)-g.getValue(x);
            }
            public double getGradient(double p) {
                return f.getGradient(p)-g.getGradient(p);
            }
        };
    }
    
    /**
     * the product of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f*g defined by (f*g)(x)=f(x)*g(x)
     */
    public static AbstractDifferentialFunction1d mul(final DifferentialScalarFunction1d f, final DifferentialScalarFunction1d g) {
        return new AbstractDifferentialFunction1d() {
            public double getValue(double x) {
                return f.getValue(x)*g.getValue(x);
            }
            public double getGradient(double p) {
                return f.getGradient(p)*g.getValue(p)+f.getValue(p)*g.getGradient(p);
            }
        };
    }
    
    /**
     * the quaotient of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f/g defined by (f/g)(x)=f(x)/g(x)
     */
    public static AbstractDifferentialFunction1d div(final DifferentialScalarFunction1d f, final DifferentialScalarFunction1d g) {
        return new AbstractDifferentialFunction1d() {
            public double getValue(double x) {
                return f.getValue(x)/g.getValue(x);
            }
            public double getGradient(double p) {
                return (f.getGradient(p)*g.getValue(p)-f.getValue(p)*g.getGradient(p))/Math.pow(g.getValue(p),2);
            }
        };
    }
    
    /**
     * the product of the function f with a scalar value
     * @param f the function
     * @param scalar
     * @return the function scalar*f defined by scalar*f(x)=scalar*g(x)
     */
    public static AbstractDifferentialFunction1d mult(final DifferentialScalarFunction1d f,final double scalar){
        return new AbstractDifferentialFunction1d(){
            public double getValue(double p){
                return scalar*f.getValue(p);
            }
            public double getGradient(double p) {
                return f.getGradient(p)*scalar;
            }
        };
    }
    
}

