package bijava.math.function.interpolation;

import bijava.math.function.AbstractScalarFunction1d;

public class Horner extends AbstractScalarFunction1d {
    
    double[] a;
    double[] x;
    
    /**
     * implements Horners-s method to compute the polynomial
     * a0+(t-x0)(a1+(t-x1)(a2+(t-x2)(a3+(...(an-1+(t-an))...))))))
     * @param a the vector of coefficients
     * @param x the vector of x coordinates of nodes to be interpolated
     * @return the polynomial function
     * a0+(t-x0)(a1+(t-x1)(a2+(t-x2)(a3+(...(an-1+(t-an))...))))))
     */
    
    public Horner(double[] a, double[] x){
        this.a = new double[a.length];
        for(int i=0; i<a.length;i++) this.a[i]=a[i];
        this.x = new double[x.length];
        for(int i=0; i<x.length;i++) this.x[i]=x[i];
    }
    
    public double getValue(double t) {
        int n=a.length;
        double v=a[n-1];
        for (int i=1; i<n;i++) v=v*(t-x[n-1-i]) + a[n-1-i];
        return v;
    }
}

