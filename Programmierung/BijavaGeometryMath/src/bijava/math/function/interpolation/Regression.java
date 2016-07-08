package bijava.math.function.interpolation;
import  bijava.geometry.dim2.*;
import javax.vecmath.GMatrix;
public class Regression {
    
    public static double[] linregress(double[] x, double[] y) {
        int Nx=x.length, Ny=y.length, N=Nx<Ny?Nx:Ny;
        double sum1=0, sum2=0, sum3=0, sum4=0;
        for(int i=0; i<N; i++) {
            sum1+=x[i]; sum2+=x[i]*x[i];
            sum3+=y[i]; sum4+=x[i]*y[i]; }
        double D=N*sum2-sum1*sum1,
                koeff[] = new double[2];
        koeff[0]=(N*sum4-sum1*sum3)/D;
        koeff[1]=(sum3*sum2-sum1*sum4)/D;
        return koeff; 
    }
    
    public static double[] linregress(Point2d[] P) {
        int N=P.length;
        double[] x=new double[N], y=new double[N];
        for(int i=0; i<N; i++) {x[i]=P[i].x;y[i]=P[i].y;}
        return linregress(x,y); 
    }
    
    public static double[] linregress(double[]x, double[]y, double[]z)
    {
        //Design-Matrix 
        double [] designMatrix = new double [x.length*3];
    
        for(int i=0,j=0; i<x.length*3; i=i+3)
        {
            designMatrix[i] = 1; 
            designMatrix[i+1] = x[j];
            designMatrix[i+2] = y[j];
            j++;
        }
         
      
        GMatrix X = new GMatrix(x.length,3,designMatrix);      
        GMatrix Z = new GMatrix(z.length,1,z);
        
        
        
        GMatrix Xtrans = new GMatrix(X.getNumCol(),X.getNumRow());
        Xtrans.transpose(X);

        
        GMatrix Bzwischen1 = new GMatrix(X.getNumCol(),X.getNumCol());
        Bzwischen1.mul(Xtrans,X);
        Bzwischen1.invert();
        
        
        GMatrix Bzwischen2 = new GMatrix(Xtrans.getNumRow(),Z.getNumCol());
        Bzwischen2.mul(Xtrans,Z);        
        
        GMatrix B = new GMatrix(Bzwischen1.getNumRow(),Bzwischen2.getNumCol());
        B.mul(Bzwischen1, Bzwischen2);
        
        /* Ebenengleichung b0 + b1*x + b2*y -z */
        /* b = b0,b1,b2,sigma */
        double [] b = new double[4];
        B.getColumn(0,b);         
        
        
        /* Werte an den Stützstellen nach der Regression  ^Z = X*b */
        GMatrix Z_HAT = new GMatrix(X.getNumRow(),B.getNumCol());
        Z_HAT.mul(X,B);
        
        /*Berechnung der Standardabweichung*/
        /*   ^s^2 (Z_HAT_i-Z_i)^2 / (n-p)    */
        double sigma = 0;
        
        for(int i=0; i < Z_HAT.getNumRow(); i++)
             sigma += ( Z.getElement(i,0)-Z_HAT.getElement(i, 0) )*( Z.getElement(i,0)-Z_HAT.getElement(i, 0) );
        
        sigma  = Math.sqrt(sigma /(Z_HAT.getNumRow() - 2));
        
        b[3] = sigma; 
        return b;
    }
    
        
    
   
        
}
