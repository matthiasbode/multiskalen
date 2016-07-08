package bijava.math.function.interpolation;

import bijava.math.function.AbstractScalarFunction1d;
import bijava.math.function.interpolation.DiscretizedDifferentialScalarFunction1d;
import bijava.vecmath.DMatrix;

/**
 * cubic spline functions that interpolates the nodes (xi,yi)
 * @author dorow
 */
// n Teilstuecke mit Si(x) = ai(x-xi) + bi(x-xi) + ci(x-xi) + di
// es gilt : Si-1(xi) = Si(xi) = yi , S'i-1(xi) = S'i(xi) , S"i-1(xi) = S"i(xi)
// daraus folgt: 
// I :  di = yi
// II:  ai-1 =  (bi - bi-1) /  (3(xi-xi-1))
// III: ci-1 = (di-di-1)/(xi-xi-1) - (bi-bi-1)(xi-xi-1)/3 - bi-1(xi-xi-1)
// IV: (xi-xi-1)bi-1 + 2(xi+1-xi-1)bi + (xi+1-xi)bi+1 = 3((di+1-di)/(xi+1-xi) - (di-di-1)/(xi-xi-1))
//
// Aus IV ergibt sich ein LGS mit welchem die einzelen b-Koeffizienten berechnet werden koennen
//	     	b1          b2          b3          b4      ...     bn-3        bn-2            bn-1
//  i=1		2(x2-x0)    x2-x1 	0           0       ...     0           0               0
//  i=2		x2-x1       2(x3-x1)	x3-x2       0       ...     0           0               0
//  i=3		0           x3-x2	2(x4-x2)    x4-x3   ...     0           0               0
//  ...		...         ...         ...         ...     ...     ...         ...             ...
//  i=n-2	0           0           0           0       0       xn-2-xn-3	2(xn-1-xn-3)	xn-1-xn-2
//  i=n-1	0           0           0           0       0       0           xn-1-xn-2	2(xn-xn-2)

public class CubicSpline extends DiscretizedDifferentialScalarFunction1d {
    
    private double[] r; // rechte Seite des Gleichungssystem 
    private double[] a; // Koeffizient a 
    private double[] b; // Koeffizient b
    private double[] c; // Koeffizient c
    private double[] x;
    private double[] y; // Koeffizient di = yi
    
    private boolean negativeInfinite=true; // true : Auszerhalb der Stuetzstellen -> y-Wert der naechst gelegenen Stuetzstellen
    private boolean positiveInfinite=true; // false : Funktion wird weiter gefuehrt (Extrapolation)
    

    public CubicSpline(double[] x, double[] y) {
        super(x,y);
        this.x=x;
        this.y=y;
        if(x.length<2)
            throw new IllegalArgumentException("Anzahl der Stuetzstellen < 2");

        calcCoef();                
    }
    
     public CubicSpline(double[][] values) {
        super(values);
        x=values[0];
        y=values[1];
        if(x.length<2)
            throw new IllegalArgumentException("Anzahl der Stuetzstellen < 2 ");

        calcCoef();    
     }
    
    private void calcCoef(){
        
        int n=x.length;
        
        if(n==2){
            a=new double[1]; a[0]=0;
            b=new double[1]; b[0]=0;
            c=new double[1]; c[0]=((y[1]-y[0])/(x[1]-x[0]));
            
//            System.out.println("Koeffizienten");
//            System.out.println("0 a="+a[0]+ " b="+b[0]+" c="+c[0]+ " d=" +y[0]);
            
            
        } else {
            
            r=new double[n-2];
            // Rechte Seite des Gleichungssystems
            for(int i=1; i<n-1 ;i++){
                r[i-1]=3*((y[i+1]-y[i])/(x[i+1]-x[i]) - (y[i]-y[i-1])/(x[i]-x[i-1]));
            }
            
            //linke Seite Koeffizientenmatrix -> Kruemmung im ersten und letzten Punkt ist 0
            double[][] matrix = new double[n-2][n-2];
            
            for(int i=0; i<=n-3; i++){
                for(int j=0; j<=n-3; j++){
                    
                    if(i==0){
                        if(j==i)
                            matrix[i][j]=2*(x[j+2]-x[j]);
                        else if(j==i+1)
                            matrix[i][j]=(x[j+1]-x[j]);
                        else
                            matrix[i][j]=0;
                    } else if(i==n-3){
                        
                        if(j==i-1)
                            matrix[i][j]=x[j+1]-x[j];
                        else if(j==i)
                            matrix[i][j]=2*(x[j+2]-x[j]);
                        else
                            matrix[i][j]=0;
                    } else {
                        if(j==i-1)
                            matrix[i][j]=x[j+1]-x[j];
                        else if(j==i)
                            matrix[i][j]=2*(x[j+2]-x[j]);
                        else if(j==i+1)
                            matrix[i][j]=(x[j+2]-x[j+1]);
                        else
                            matrix[i][j]=0;
                    }
                }
            }
            
            // Loesen des Gleichungsystems und Koeffizienten bi in b kopieren
            DMatrix A = new DMatrix(matrix);
            double[] zw = A.solve(r);
            
            b=new double[n];
            for (int i=0; i<n; i++){
                if(i==0)
                    b[i]=0;
                else if(i==n-1)
                    b[i]=0;
                else
                    b[i]=zw[i-1];
            }
            
            // Berechnen Koeffizienten a und c
            a=new double[n-1];
            c=new double[n-1];
                        
//            System.out.println("Koeffizienten der einzelnen Polynome");
            for (int i=1; i<n; i++){
                a[i-1]=(b[i]-b[i-1])/(3*(x[i]-x[i-1]));
                c[i-1]=(y[i]-y[i-1])/(x[i]-x[i-1])-(b[i]-b[i-1])*(x[i]-x[i-1])/3. - b[i-1]*(x[i]-x[i-1]);
//                System.out.println((i-1)+" a="+a[i-1]+ " b="+b[i-1]+" c="+c[i-1]);
            }
            
//            System.out.println("Stuetzstellen");
//            StringBuffer buffer1 = new StringBuffer();
//            StringBuffer buffer2 = new StringBuffer();
//            for (int i=0; i<x.length; i++){
//              buffer1.append(" "+x[i]);
//              buffer2.append(" "+y[i]);
//            }
//            System.out.println(buffer1);
//            System.out.println(buffer2);

        }
    }
    
    
    public double getValue(double u) {
        
        int size=x.length;
        double result=Double.NaN;
        
        if (u < x[0]){
            return negativeInfinite ? y[0] : (a[0]*(u-x[0])*(u-x[0])*(u-x[0])+ b[0]*(u-x[0])*(u-x[0]) + c[0]*(u-x[0]) + y[0]);
        }
        else if (u > x[size-1]){
            return positiveInfinite ? y[size-1] : (a[size-2]*(u-x[size-2])*(u-x[size-2])*(u-x[size-2])+ b[size-2]*(u-x[size-2])*(u-x[size-2]) + c[size-2]*(u-x[size-2]) + y[size-2]);
        }       
        else {
          for (int i=0; i<size-1; i++){
            if(u>=x[i] && u<=x[i+1]){
              result= a[i]*(u-x[i])*(u-x[i])*(u-x[i])+ b[i]*(u-x[i])*(u-x[i]) + c[i]*(u-x[i]) + y[i];
            } 
          }
        }
        return result;
    }
    
    public double getGradient(double u) {
        
        int size=x.length;
        double result=Double.NaN;
        
        if (u < x[0]){
            return negativeInfinite ? 0 : 3*a[0]*(u-x[0])*(u-x[0])+ 2*b[0]*(u-x[0]) + c[0];
        }
        else if (u > x[size-1]){
            return positiveInfinite ? 0 : 3*a[size-2]*(u-x[size-2])*(u-x[size-2])+ 2*b[size-2]*(u-x[size-2]) + c[size-2];
        }       
        else {
          for (int i=0; i<size-1; i++){
            if(u>=x[i] && u<=x[i+1]){
              result= 3*a[i]*(u-x[i])*(u-x[i])+ 2*b[i]*(u-x[i]) + c[i];
            } 

          }
        }
        return result;
    }
    
    public AbstractScalarFunction1d getDerivation() {
        return new AbstractScalarFunction1d() {
            public double getValue(double p) {
                return getGradient(p);
            }
        };
    }
          
          
     public void setNegativeInifinity(boolean infinite) {
              negativeInfinite = infinite;
     }
                    
     public boolean isNegativeInifinite() { 
         return negativeInfinite; 
     }
          
     public void setPositiveInifinity(boolean infinite) {
              positiveInfinite = infinite;
     }
     
     public boolean isPositiveInifinite() { 
         return positiveInfinite; 
     }
          
     
     public static void main(String[] args) {
//        double[] x={0, 1 , 2 , 2.5 };
//        double[] y={0, 1, 0.5, 0};
         
         double[] x={1, 2 , 3 , 4., 5. ,6. };
         double[] y={2, 4, 4.5, 4., 2., 1.};
         
         CubicSpline spline = new CubicSpline(x, y);
        
         double[][] array = {{4.,1.},{1.,3.}};
         double[][] arrayx1 = {{4.,1.,-4.5},{1.,3.,-1.5}};
         
         DMatrix AX = new DMatrix(arrayx1);
         System.out.println(AX);
         DMatrix x1 = bijava.vecmath.LGS.gauss(AX);
         System.out.println("\n Loesung LGS \n"+x1);
         
         DMatrix BX = new DMatrix(array);
         double[] b1 = new double[2];
         b1[0]=-4.5;
         b1[1]=-1.5;
         double[] x2 = BX.solve(b1);
         System.out.println("Loesung DMatrix");
         for (int i=0; i<x2.length;i++)
             System.out.println(x2[i]);
         
     }
}