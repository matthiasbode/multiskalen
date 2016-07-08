
package bijava.math.function.interpolation;

import bijava.math.function.AbstractScalarFunction1d;

/**
 * cubic spline functions that interpolates the nodes (ti,xi)
 * @author milbradt
 */
public class CubicSplineOld extends AbstractScalarFunction1d {
    
    private double[] t, x, z;
    
    /** Creates a new instance of cubicSpline
     ** @param t the argument values of the nodes
     * @param x the ordinate values of the nodes
     * @param z the vector of second derivatives at the nodes */
    public CubicSplineOld(final double[] t,final double[] x, final double[] z) {
        this.t=t;
        this.x=x;
        this.z=z;
    }
    
    /** Creates a new instance of QuadSpline
     * @param t the argument values of the nodes
     * @param x the ordinate values of the nodes
     * the vector of derivatives at the nodes are cuputed*/
    public CubicSplineOld(double[] t, double[] x) {
        double z0=0.; // 2. Ableitung am 1. Knoten
        double zn=0.; // 2. Ableitung am letzten Knoten
        this.t=t;
        this.x=x;
        int n=t.length;
        double[] h=new double[n-1], b=new double[n-1],
                u=new double[n], v=new double[n];
        z=new double[n];
        u[0]=0; v[0]=0;
        for(int i=0;i<n-1;i++) {
            h[i]=t[i+1]-t[i]; b[i]=(x[i+1]-x[i])/h[i];}
        u[1]=2*(h[0]+h[1]); v[1]=6*(b[1]-b[0]);
        for(int i=2;i<n-1;i++) {
            u[i]=2*(h[i]+h[i-1])-h[i-1]*h[i-1]/u[i-1];
            v[i]=6*(b[i]-b[i-1])-h[i-1]*v[i-1]/u[i-1]; }
        z[n-1]=zn;
        for(int i=n-2;i>0;i--) z[i]=(v[i]-h[i]*z[i+1])/u[i];
        z[0]=z0;
    }
    
    public double getValue(double u) {
        int n=t.length,i=0;
        if(u<=t[0]) i=0;
        else {
            i=0; for(int k=1;k<n;k++) if(u-t[i]>0) i++; i--; }
        double h=t[i+1]-t[i];
        double tmp=0.5*z[i]+(u-t[i])*(z[i+1]-z[i])/6/h;
        tmp=-h*(z[i+1]+2*z[i])/6+(x[i+1]-x[i])/h+(u-t[i])*tmp;
        return x[i]+(u-t[i])*tmp; }
    
}
