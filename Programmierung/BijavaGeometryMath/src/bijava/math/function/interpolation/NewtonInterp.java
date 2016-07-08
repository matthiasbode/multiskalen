package bijava.math.function.interpolation;

import bijava.math.function.AbstractScalarFunction1d;
import bijava.math.function.ScalarFunction1d;


public class NewtonInterp extends DiscretizedDifferentialScalarFunction1d {
    
    private double[] x;
    private ScalarFunction1d horner;
    private double dmax=0;
     
    public NewtonInterp(final double[] x, final double[] y) {
        super(x,y);
        if(x.length!=y.length) throw new IllegalArgumentException("Die Anzahl der Koordinaten entspricht nicht der Anzahl der Funktionswerte"); 
        
        double[] a=divDif(x,y);
        this.horner = new Horner(a,x);
       
        this.x=x;
        for(int i=0; i<this.x.length-1;i++){
            dmax=Math.max(dmax,(this.x[i+1]-this.x[i]));
           
          }     
    }
    
      public double getConfidenceValue(double t) {
         
      for (int i=0; i<x.length-1; i++){  
        
        if (t == x[i]) return 1.0;  
        if(x[i] < t && x[i+1] > t){
          double dmin=Math.min((t-x[i]),(x[i+1]-t));
          return 1-2*dmin/dmax; 
        }      
      }      
      
      if(t < x[0]) 
        if ((x[0]-t)<(dmax/2)) 
          return  1-2*(x[0]-t)/dmax; 
        else return 0;
      else       
        if ((t-x[x.length-1])<(dmax/2)) 
          return  1-2*(t-x[x.length-1])/dmax; 
        else return 0;
   
    }
       
    
    public double getValue(double t) {
        return horner.getValue(t);
    }
      
    /**
     * finds the divided differences, i.e. the coefficients in Newton's interpolation
     * polynomial interpolating the nodes (xi,yi)
     * @param x the x coordinates of the nodes to be interpolated
     * @param y the y coordinates of the nodes to be interpolated
     * @return the coefficients (divided differences) of the interpolating polynomial
     */
    
    private double[] divDif(double[] x, double[] y) {
        int N=x.length;
        double[][] M = new double[N][N];
        for (int i=0; i<N; i++) M[i][0]=y[i];
        for (int j=1; j<N; j++)
            for (int i=0;i<N-j;i++)
                M[i][j]=(M[i+1][j-1]-M[i][j-1])/(x[i+j]-x[i]);
        return M[0]; }
    
   
   public static void main(String[] args) {
 
   double[] x = {1,1.3,1.6,1.9,2.2};
   double[] y = {0.7651977,0.6200860,0.4554022,0.2818186,0.1103623};  
   double[] x1 = {1,2,4,6,10};
   double[] y1 = {1,4,16,36,100};  // f(x)=x^2;
   
   
   NewtonInterp lp = new NewtonInterp(x1,y1);
   System.out.println(lp.getValue(11));
   System.out.println(lp.getConfidenceValue(11));
   
   
   NewtonInterp lp1 = new NewtonInterp(x,y);
   //System.out.println(lp1.getValue(1.5));  // Loesung 0.5118277
     
  }		     
   
          /**Gibt NaN zurueck noch Implementieren
     *
     */
    public double getGradient(double x) {
        return Double.NaN;
    }
   
       /**Gibt die Ableitung als Funktion zurueck
     *
     * kopiert aus AbstractDifferentialFunction1d.
     * Bessere Loesung gesucht!
     */
    public AbstractScalarFunction1d getDerivation() {
        return new AbstractScalarFunction1d() {
            public double getValue(double p) {
                return getGradient(p);
            }
        };
    }
    
}