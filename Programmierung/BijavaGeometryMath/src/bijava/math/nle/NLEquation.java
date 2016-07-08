
package bijava.math.nle;

import bijava.math.function.DifferentialScalarFunction1d;
import bijava.math.function.ScalarFunction1d;

/**
 *
 * @author milbradt
 */
public class NLEquation {
    
    /**
     * finds the solution of the equation f(x)=0 in the interval [a,b] if the
     * function values in a and b have opposite signs
     * @param f the function whose zero is to be found
     * @param a the left endpoint of the interval
     * @param b the right endpoint of the interval
     * @param eps the function value that makes the iteration stop
     * @return a 2-array that goves the root and the final interval length
     */
    public static double[] Bisector(ScalarFunction1d f, double a, double b, double eps) {
        double[] x = new double[2];
        double u=f.getValue(a), v=f.getValue(b);
        if (u==0.0) {x[0]=a; x[1]=0.0; return x;}
        if (v==0.0) {x[0]=b; x[1]=0.0; return x;}
        //if (u*v>0) System.out.println("Loesung existiert");
        int N = (int) Math.ceil(Math.log((b-a)/eps)/Math.log(2));
        double c=0,w=0;
        if (u*v<0) {
            for (int i=1; i<=N;i++) {
                c=(a+b)/2; w=f.getValue(c);
                if (w==0.0) {x[0]=c; x[1]=0.0; return x; }
                if (w*v<0) {a=c; u=w;} else {b=c;v=w;} }
        }
        if (f.getValue(c)==0.0) {x[0]=c; x[1]=0.0; return x;} else  {x[0]=c; x[1]=b-a; return x;}
    } // end method Bisector
    
    /**
     * gives the root of the equation f(x)=0 for a differentiable function
     * @param f the differential function whose zero is to be found
     * @param g the derivative of f
     * @param x0 the start (guess) value
     * @param eps the function value that will cause the iteration to stop
     * @param N the maximum number of iterations
     * @return a double[3] array giving the root, the final function value and the
     * number of iterations used
     */
    public static double[] Newton(DifferentialScalarFunction1d f, double x0, double eps, int N) {
        double f0=f.getValue(x0),g0;
        int i=0;
        while((i<N)&&(Math.abs(f0)>eps)){
            i++;
            g0=f.getGradient(x0);
            x0=x0-f0/g0;
            f0=f.getValue(x0); }
        double[] x = new double[3];
        x[0]=x0;x[1]=f0;x[2]=1.0*i;
        return x;
    }
    
    /**
     * implements the secant method for finding the root of the equation f(x)=0
     * @param f the function whose zero is to be found
     * @param x0 the first guess value
     * @param x1 the second guess value
     * @param eps the distance from an approximation to the next that will cause
     * the iteration to stop
     * @param N the maximum number of iterations
     * @return an array with 3 elements giving the approximation to the root, the
     * corresponding runction value and the number of iteration used
     */
    public static double[] Sekant(ScalarFunction1d f, double x0, double x1, double eps, double N)  {
        double f0=f.getValue(x0), f1=f.getValue(x1), dx=1;
        int i=1;
        while((i<N)&&(Math.abs(dx)>eps)) {
            i++; dx=f1*(x0-x1)/(f0-f1);
            double xm=x1-dx; x0=x1; f0=f.getValue(x0);
            x1=xm; f1=f.getValue(x1); 
        }
        double[] x=new double[3];
        x[0]=x1; x[1]=f1; x[2]=1.0*i;
        return x;
    }
    
    /**
     * uses the fixpoint method to find the root of the equation x=f(x)
     * @param f the function f in f(x)=x
     * @param p0 the guess value
     * @param eps the distance between two successive approximations that will
     * cause the iteration to stop
     * @param N the maximum allowed number of iterations
     * @return an array with 2 elements, the root and the number of iterations used.
     */
    public static double[] FixPoint(ScalarFunction1d f, double p0, double eps, int N) {
        int i=0;
        double p1=f.getValue(p0), d=p1-p0;
        while((i<N)&&(Math.abs(d)>eps)) {
            i++; 
            double p=f.getValue(p0);
            p1=p0; p0=p; d=p1-p0; 
        }
        double[] x=new double[2];
        x[0]=p0; x[1]=1.0*i;
        return x;
    }
    
}
