package bijava.math.pde.fem.model.traffic;

/**
 *
 * @author milbradt
 */
// Fundamentaldiagramm
public class MacroFD {
    //Attribute
    //--------------------------------------------------------------------------
    public static final double vf       = 130.0 /    3.6;// Wunschgeschwindigkeit ( 130 km/h )
    public static final double rhoMax   = 175.0 / 1000.0;// maximale Dichte       ( 175 Fz/km)
    public static final double rhoH     =  30.0 / 1000.0;// 12.5 homogene Dichte  (12.5 Fz/km)
    public static final double tau      =            35.;// 6. Relaxationszeit    (   6 s    ) alt 20
    public static final double c0       =           13.0;//                       ( 13  m/s  )
    public static final double c02      =        c0 * c0;//                       (169 m2/s2 )
    public static final double mu       =           60.0;//                       ( 60  m/s  )

    
    //Konstruktoren
    //--------------------------------------------------------------------------
    private MacroFD() {
        
    }
    
    //Methoden
    //--------------------------------------------------------------------------
    
    //..Fundamentaldiagramm (Helbing).........................................//
    public static double getVelocity (double rho) {
        double V_ = vf / (tau * rho * A(rho) * P(rho));
        return (V_ / (2.0 * vf) * (-1.0 + Math.sqrt(1.0 + 4.0 * vf * vf / V_)));
    }
    
    private static double A (double rho) {
        double rhoKrit  = 0.270 * rhoMax;
        double A0       = 0.008;
        double deltaA   = 2.500 * A0;
        double deltaRho =   0.01;
        return (A0 + deltaA * (Math.tanh ((rho - rhoKrit) / deltaRho) + 1.0)); 
    }
    
    private static double P (double rho) {
        double T        = 1.8;
        double help = (1.0 - rho/rhoMax); 
        return (vf * rho * T * T / (tau * A(rhoMax) * help * help));
    }
    
    /*
    //..Fundamentaldiagramm von Cremer..........................................//
    public double getVelocity (double rho) {
        return vf * Math.pow(1.0 - Math.pow(rho/rhoMax, 1.4), 4.0);
     }

    //..Fundamentaldiagramm (linear)............................................//
    public double getVelocity (double rho) {
        return vf * (1.0 - (rho/rhoMax));
    }

    //..Fundamentaldiagramm (quadratisch).....................................//
    public double getVelocity (double rho) {
        return vf * (1.0 - 2.0 * (rho/rhoMax) + (rho/rhoMax)*(rho/rhoMax));
    }

    //..Fundamentaldiagramm (Kuehne)..........................................//
    public double getVelocity (double rho) {
        return vf * Math.pow(1.0 - Math.pow(rho/rhoMax,2.05),21.11);
    }

    //..Fundamentaldiagramm (Kerner und Konhaeuser)...........................//
      public double getVelocity (double rho) {
     return vf * ( 1.0 / ( 1.0 + Math.exp((rho/rhoMax - 0.20)/0.05))); }  
    */
}