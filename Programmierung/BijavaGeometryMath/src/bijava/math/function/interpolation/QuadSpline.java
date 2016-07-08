
package bijava.math.function.interpolation;

import bijava.math.function.AbstractScalarFunction1d;

/**
 * quadratic spline functions that interpolates the nodes (ti,xi)
 * @author milbradt
 */
public class QuadSpline  extends AbstractScalarFunction1d {
    
    private double[] t, x, z;
    
    /** Creates a new instance of QuadSpline
     * @param t the argument values of the nodes
     * @param x the ordinate values of the nodes
     * @param z the vector of derivatives at the nodes */
    public QuadSpline(double[] t, double[] x,double[] z) {
        this.t=t;
        this.x=x;
        this.z=z;
    }
    /** Creates a new instance of QuadSpline
     * @param t the argument values of the nodes
     * @param x the ordinate values of the nodes
     * the vector of derivatives at the nodes are cuputed*/
    public QuadSpline(double[] t, double[] x) {
        double z0=(x[1]-x[0])/(t[1]-t[0]); // 1. Ableitung am 1.Knoten
        this.t=t;
        this.x=x;
        int n=t.length;
        z=new double[n];
        z[0]=z0;
        for(int i=1; i<n; i++) z[i]=-z[i-1]+2*(x[i]-x[i-1])/(t[i]-t[i-1]);
    }
    
    /**
     * finds the piecewise second degree function that interpolates given nodes (xi,yi)
     * @param t the argument values of the nodes
     * @param x the ordinate values of the nodes
     * @param z the vector of derivatives at the nodes
     * @return the piecewise second degree function that interpolates given nodes (xi,yi)
     */
    
    public double getValue(double u) {
        double w=0.0;
        if (u<=t[0]) w=(z[1]-z[0])*(u-t[0])*(u-t[0])*0.5/(t[1]-t[0])
        +z[0]*(u-t[0])+x[0];
        if (u>t[0]) {
            int i=0;
            for (int k=1;k<t.length;k++) {if (u-t[i]>0) i++;}
            i--;
            w=(z[i+1]-z[i])*(u-t[i])*(u-t[i])*0.5/(t[i+1]-t[i])+z[i]*(u-t[i])+x[i]; }
        return w;
    }
    
}
