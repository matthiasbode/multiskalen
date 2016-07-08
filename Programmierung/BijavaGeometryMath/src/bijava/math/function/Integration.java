package bijava.math.function;

/** Klasse zur Integration von Funktionen
 *
 * @author Peter
 */
public class Integration {
    
    /** Creates a new instance of Integration */
    private Integration() {
    }
    
    public static double lowerRiemann(ScalarFunction1d f, double a, double b, int N) {
        double x=a, h=(b-a)/N, sum=0,fx1,fx2,fx;
        for(int i=0; i<N; i++) {
            fx1=f.getValue(x); fx2=f.getValue(x+h); fx=fx1<fx2?fx1:fx2;
            sum+=fx; x+=h;
        }
        sum*=h;
        return sum;
    }
    
    public static double upperRiemann(ScalarFunction1d f, double a, double b, int N) {
        double x=a, h=(b-a)/N, sum=0,fx1,fx2,fx;
        for(int i=0; i<N; i++) {
            fx1=f.getValue(x); fx2=f.getValue(x+h); fx=fx1<fx2?fx2:fx1;
            sum+=fx; x+=h;
        }
        sum*=h;
        return sum;
    }
    
    public static double trapez(ScalarFunction1d f, double a, double b, int N) {
        double x=a, h=(b-a)/N, sum=f.getValue(x);
        for(int i=0; i<N-1; i++) {
            x+=h; sum+=2*f.getValue(x);
        }
        sum+=f.getValue(b);
        sum*=(h/2); return sum;
    }
    
    public static double midpoint(ScalarFunction1d f, double a, double b, int N) {
        double h=(b-a)/N, x=a+0.5*h, sum=0;
        for(int i=0; i<N; i++) {
            sum+=f.getValue(x);
            x+=h;
        }
        sum*=h;
        return sum;
    }
    
    public static double simpson(ScalarFunction1d f, double a, double b, int N) {
        double h=0.5*(b-a)/N, sum=f.getValue(a), x=a;
        for(int i=1; i<N; i++) {
            x+=h;
            sum+=4*f.getValue(x);
            x+=h;
            sum+=2*f.getValue(x);
        }
        x+=h;
        sum+=4*f.getValue(x);
        sum+=f.getValue(b);
        sum*=(h/3);
        return sum;
    }
}
