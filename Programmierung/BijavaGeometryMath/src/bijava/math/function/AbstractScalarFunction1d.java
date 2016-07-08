package bijava.math.function;

import bijava.geometry.LinearPoint;

//==========================================================================//
/** The interface "AbstractScalarFunction1d" provides methods for 1 dimensional
 *  scalar functions, that can be added, subtracted and multiplied.
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
 *  @autor University of Hannover
 *  @autor Institute of Computer Science in Civil Engineering
 *  @autor Dr.-Ing. habil. Peter Milbradt                                      */
//==========================================================================//
public abstract class AbstractScalarFunction1d implements ScalarFunction1d, LinearPoint<AbstractScalarFunction1d> {
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract double getValue(double x);
    
    /**
     * the composite this function f with a other function g
     * @param g the second function
     * @return f o g - first g then f
     */
    public final AbstractScalarFunction1d compose(final ScalarFunction1d  g) {
        return new AbstractScalarFunction1d() {
            public double getValue(double x) {
                return AbstractScalarFunction1d.this.getValue(g.getValue(x));
            }
        };
    }
    
    /**
     * the sum of this function f with a other function g
     * @param g the second function
     * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
     */
    public final AbstractScalarFunction1d add( final ScalarFunction1d g){
        return new AbstractScalarFunction1d(){
            public double getValue(double p){
                return AbstractScalarFunction1d.this.getValue(p)+ g.getValue(p);
            }
        };
    }
    
    public final AbstractScalarFunction1d add( final AbstractScalarFunction1d g){
        return this.add((ScalarFunction1d)g);
    }
    
    /**
     * the difference of this function f with a other function g
     * @param g the second function
     * @return the function f-g defined by (f-g)(x)=f(x)-g(x)
     */
    public final AbstractScalarFunction1d sub(final ScalarFunction1d g){
        return new AbstractScalarFunction1d(){
            public double getValue(double p){
                return AbstractScalarFunction1d.this.getValue(p)- g.getValue(p);
            }
        };
    }
    
    @Override
    public final AbstractScalarFunction1d sub( final AbstractScalarFunction1d g){
        return this.sub((ScalarFunction1d)g);
    }

    
    /**
     * the product of this function f with a other function g
     * @param g the second function
     * @return the function f*g defined by (f*g)(x)=f(x)*g(x)
     */
    public final AbstractScalarFunction1d mult(final ScalarFunction1d g){
        return new AbstractScalarFunction1d(){
            public double getValue(double p){
                return AbstractScalarFunction1d.this.getValue(p)* g.getValue(p);
            }
        };
    }
    
    /**
     * the quaotient of this function f with a other function g
     * @param g the second function
     * @return the function f/g defined by (f/g)(x)=f(x)/g(x)
     */
    public final AbstractScalarFunction1d div(final ScalarFunction1d g){
        return new AbstractScalarFunction1d(){
            public double getValue(double p){
                return AbstractScalarFunction1d.this.getValue(p) / g.getValue(p);
            }
        };
    }
    
    
    /**
     * the product of this function f with a scalar value
     * @param scalar
     * @return the function scalar*f defined by scalar*f(x)=scalar*g(x)
     */
    public AbstractScalarFunction1d mult(final double scalar){
        return new AbstractScalarFunction1d(){
            public double getValue(double p){
                return scalar*AbstractScalarFunction1d.this.getValue(p);
            }
        };
    }
    
    /**
     * the composite of to functions
     * @param f the first function
     * @param g the second function
     * @return f o g - first g then f
     */
    public final static AbstractScalarFunction1d compose(final ScalarFunction1d  f, final ScalarFunction1d  g) {
        return new AbstractScalarFunction1d() {
            public double getValue(double x) {
                return f.getValue(g.getValue(x));
            }
        };
    }
    
    /**
     * the sum of two functions
     * @param f the first function
     * @param g the second function
     * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
     */
    public static AbstractScalarFunction1d add(final ScalarFunction1d f, final ScalarFunction1d g) {
        return new AbstractScalarFunction1d() {
            public double getValue(double x) {
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
    public static AbstractScalarFunction1d sub(final ScalarFunction1d f, final ScalarFunction1d g) {
        return new AbstractScalarFunction1d() {
            public double getValue(double x) {
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
    public static AbstractScalarFunction1d mult(final ScalarFunction1d f, final ScalarFunction1d g) {
        return new AbstractScalarFunction1d() {
            public double getValue(double x) {
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
    public static AbstractScalarFunction1d div(final ScalarFunction1d f, final ScalarFunction1d g) {
        return new AbstractScalarFunction1d() {
            public double getValue(double x) {
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
    public static AbstractScalarFunction1d mult(final ScalarFunction1d f,final double scalar){
        return new AbstractScalarFunction1d(){
            public double getValue(double p){
                return scalar*f.getValue(p);
            }
        };
    }
    
}

