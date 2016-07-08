package bijava.math.ode.ivp;

/** Die Klasse IVP stellt Algorithmen zur L&ouml;sung einer gew&ouml;hnlichen
 *  Differentialgleichung erster Ordnung <code>y' = F(t,x[])</code>
 *  zur Verf&uuml;gung.
 *
 *  Beginnend mit der Startl&ouml;sung <code>x0[]</code> zum Zeitpunkt
 *  <code>t0</code> l&ouml;st sie die gew&ouml;hnliche Differentialgleichung
 *  am Punkt <code>t1</code>.
 *
 *  <p><strong>Version:</strong>
 *  <br><dd>1.1, Juli 2000
 *  <p><strong>Author:</strong>
 *  <br><dd>Institut f&uuml;r Bauinformatik
 *  <br><dd>Universit&auml;t Hannover
 *  <br><dd>Dr.-Ing. Peter Milbradt, Dipl.-Ing. Martin Rose                 */
//==========================================================================//
public class IVP {
    static final double MACH_EPS = 2.220446049250313e-016;
    static final double MACH_2   = 100.*MACH_EPS;
    
    // verhindert, dass ein Objekt dieser Klasse erzeugt wird
    private IVP(){}
    
//--------------------------------------------------------------------------//
//  DGL MIT EINEM EINSCHRITT-ZEITSCHRITTVERFAHREN                           //
//--------------------------------------------------------------------------//
    /** Berechnet die L&ouml;sung eines gew&ouml;hnlichen
     *  Differentialgleichungssystems erster Ordnung mit Hilfe eines einfachen
     *  expliziten Einschrittverfahrens.
     *
     *  @param sys      gew&ouml;hnliches Differentialgleichungssystem
     *  @param t0       Startzeitpunkt
     *  @param x0       Startl&ouml;sung
     *  @param t1       Zeitpunkt, f&uuml;r dem die DGL gel&ouml;st wird
     *  @param method   einfaches Einschritt-Verfahren                          */
//--------------------------------------------------------------------------//
    public static double [] solve(ODESystem sys, double t0, double x0[], double t1,
            SimpleTStep method) {
        int    resultSize = sys.getResultSize();
        double t          = t0;
        double dt;
        double x[]        = new double[resultSize];
        
        for(int i=0; i<resultSize; i++) x[i] = x0[i];
        
        do
        { dt = sys.getMaxTimeStep();
          
          if((t+dt)>t1) dt = t1 - t;
          x = method.TimeStep(sys,t,dt,x);
          t += dt;
        }
        while (t<t1);
        
        return x;
    }
    
//--------------------------------------------------------------------------//
//  DGL MIT EINEM EINSCHRITT-ZEITSCHRITTVERFAHREN                           //
//--------------------------------------------------------------------------//
    /** Berechnet die L&ouml;sung eines gew&ouml;hnlichen
     *  Differentialgleichungssystems erster Ordnung mit Hilfe eines einfachen
     *  expliziten Einschrittverfahrens.
     *
     *  @param sys      gew&ouml;hnliches Differentialgleichungssystem
     *  @param t0       Startzeitpunkt
     *  @param x0       Startl&ouml;sung
     *  @param t1       Zeitpunkt, f&uuml;r dem die DGL gel&ouml;st wird
     *  @param method   einfaches Einschritt-Verfahren
     *  @param dtStart      Zeitintervall, mit dem eine L&ouml;sung begonnen wird
     *  @param errorAbsolute absoluter Fehler
     *  @param errorRelative relativer Fehler                                   */
//--------------------------------------------------------------------------//
    public static double[] solve(ODESystem sys, double t0, double x0[],
            double t1, SimpleTStep method,
            double    dtStart,
            double    errorAbsolute,
            double    errorRelative) {
        int    resultSize = sys.getResultSize();
        double t          = t0;
        double dt         = dtStart;
        double dt_max;
        double diff;
        double s;
        double x_good[]   = new double[resultSize];
        double x_bad[]    = new double[resultSize];
        double x_old[]    = new double[resultSize];
        double x_null[]   = new double[resultSize];
        
        for(int i = 0; i < resultSize; i++)
            x_old[i] = x0[i];
        
        do {
            if ((t + dt) > t1) dt = t1 - t;
            x_bad = method.TimeStep(sys, t, dt, x_old);
            x_good = method.TimeStep(sys, t, dt/2., x_old);
            x_good = method.TimeStep(sys, t + dt/2., dt/2., x_good);
            
            diff = norm(x_good, x_bad);
            
            if (diff < MACH_2) dt *= 2.;
            else {
                double xmax = norm(x_good,x_null);
                dt *= Math.min(2.,(errorAbsolute + errorRelative*xmax) /diff);
            }
            
            for(int i = 0; i < resultSize; i++)
                x_old[i] = x_good[i];
            
            t += dt;
            //System.out.println(dt);
        }
        while (t < t1);
        
        return x_good;
    }
    
//--------------------------------------------------------------------------//
//  DGL MIT EINGEBETTETEN RUNGE-KUTTA ZEITSCHRITTVERFAHREN                  //
//--------------------------------------------------------------------------//
    /** Berechnet die L&ouml;sung eines gew&ouml;hnlichen
     *  Differentialgleichungssystems erster Ordnung mit Hilfe eines expliziten
     *  eingebetten Runge-Kutta-Verfahrens.
     *
     *  Die Integration hat eine intere Zeitschrittkontrolle, die von einem
     *  absoluten und einem relativen Fehler abh&auml;ngt.
     *
     *  @param sys           gew&ouml;hnliches Differentialgleichungssystem
     *  @param t0            Startzeitpunkt
     *  @param x0            Startl&ouml;sung
     *  @param t1            Zeitpunkt, f&uuml;r dem die DGL gel&ouml;st wird
     *  @param method        eingebettetes Runge-Kutta-Verfahren
     *  @param dtStart      Zeitintervall, mit dem eine L&ouml;sung begonnen wird
     *  @param errorAbsolute absoluter Fehler
     *  @param errorRelative relativer Fehler                                   */
//--------------------------------------------------------------------------//
    public static double [] solve(ODESystem sys,
            double    t0,
            double    x0[],
            double    t1,
            RKETStep  method,
            double    dtStart,
            double    errorAbsolute,
            double    errorRelative) {
        int    resultSize = sys.getResultSize();
        double t          = t0;
        double dt         = dtStart;
        double dt_max;
        double diff;
        double s;
        double x_good[]   = new double[resultSize];
        double x_bad[]    = new double[resultSize];
        double x_old[]    = new double[resultSize];
        double x_null[]   = new double[resultSize];
        
        for (int i=0; i<resultSize; i++) { x_old[i] = x0[i]; x_null[i] = 0.0; }
        
        do
        { dt_max = sys.getMaxTimeStep();
          if ((t+dt)>t1) dt = t1 - t;
          x_good = method.TimeStep(sys, t, dt, x_old, x_bad);
          
          diff = norm(x_good, x_bad);
          
          if (diff < MACH_2) s=2.0;
          else {
              double xmax = norm(x_good,x_null);
              s = Math.sqrt(dt *(errorAbsolute + errorRelative*xmax) /diff);
          }
          
          if(s>1.0)// Step Akzept step dt
          { for (int i=0; i<resultSize; i++) x_old[i] = x_good[i];
            t  += dt;
            dt *= Math.min(2.0, 0.98*s);
            dt  = Math.min(dt, dt_max);
          } else { dt *= Math.max(0.5, 0.98 * s); }
        }
        while (t<t1);
        
        return x_good;
    }
    
//--------------------------------------------------------------------------//
//  DGL MIT EINEM EINSCHRITT-ZEITSCHRITTVERFAHREN                           //
//--------------------------------------------------------------------------//
    /** Berechnet die L&ouml;sung eines gew&ouml;hnlichen
     *  Differentialgleichungssystems erster Ordnung mit Hilfe eines einfachen
     *  expliziten Einschrittverfahrens.
     *
     *  Die Integration hat eine intere Zeitschrittkontrolle, die von einem
     *  absoluten und einem relativen Fehler abh&auml;ngt.
     *
     *  @param sys      gew&ouml;hnliches Differentialgleichungssystem
     *  @param t0       Startzeitpunkt
     *  @param x0       Startl&ouml;sung
     *  @param t1       Zeitpunkt, f&uuml;r dem die DGL gel&ouml;st wird
     *  @param method   einfaches Einschritt-Verfahren
     *  @param dtStart      Zeitintervall, mit dem eine L&ouml;sung begonnen wird
     *  @param errorAbsolute absoluter Fehler
     *  @param errorRelative relativer Fehler                                   */
//--------------------------------------------------------------------------//
    public static double [] solve(ODESystem sys, double t0, double x0[], double t1,
            ABMTStep methode, double dtStart,
            double errorAbsolute, double errorRelative) {
        int            resultSize = sys.getResultSize();
        DynNewtonPolynomOrder3[] f_help     = new DynNewtonPolynomOrder3[resultSize];
        DynNewtonPolynomOrder3[] f_tmp      = new DynNewtonPolynomOrder3[resultSize];
        EulerTStep     emethode   = new EulerTStep();
        double         t          = t0;
        double         dt         = dtStart;
        double         dt_max;
        double         s          = 2.0;
        double         diff;
        double[]       x_good     = new double[resultSize];
        double[]       x_bad      = new double[resultSize];
        double[]       x_old      = new double[resultSize];
        double[]       x_null     = new double[resultSize];
        
        for (int i=0; i<resultSize; i++) { x_old[i] = x0[i]; x_null[i] = 0.0; }
        
//	Startstep
        double[]      sysValue = new double[resultSize];
        double[][]    w        = new double[2][4];
        
        for(int i=0; i<resultSize; i++) {
            f_help[i] = new DynNewtonPolynomOrder3(w);
            f_tmp[i]  = new DynNewtonPolynomOrder3(w);
        }
        int start  = 0;
        dt_max = 10.0 * sys.getMaxTimeStep();
        
        do
        { if ((t+dt)>t1) { dt = t1 - t; start = 0; }
          if (start <= 4)  {
              sysValue = sys.getValue(t,x_old);
              for(int i=0;i<resultSize;i++)
                  f_help[i].addValue(t,sysValue[i]);
              x_good = emethode.TimeStep(sys,t,dt,x_old);
              start++;
              s=1.1;
              
          } else {
              x_good = methode.TimeStep(sys,t,dt,x_old,x_bad,f_help);
              diff = norm(x_good,x_bad);
              if ( diff < MACH_2 )
                  s=2.;
              else{
                  double xmax = norm(x_good,x_null);
                  s = Math.sqrt((errorAbsolute + errorRelative*xmax) /diff);
              }
          }
          
          if(s>1.){
              // Step Akzept step dt
              for(int i=0;i<resultSize;i++){
                  x_old[i] = x_good[i];
                  f_tmp[i] = (DynNewtonPolynomOrder3) f_help[i].clone();
              }
              t=t+dt;
              dt*=Math.min(2.,0.98*s);
              dt=Math.min(dt,dt_max);
          } else {
              // Step no Akzept
              for(int i=0;i<resultSize;i++){
                  f_help[i] = (DynNewtonPolynomOrder3) f_tmp[i].clone();
              }
              dt*=Math.max(0.5,0.98*s);
          }
          
          //System.out.println(dt+" t="+t);
          
          
          
        } while (t<t1);
        
        return x_good;
    }
    
//--------------------------------------------------------------------------//
// Private Klasse: BERECHNUNG EINER NORN                                    //
//--------------------------------------------------------------------------//
    private static double norm(double a[], double b[]) {
        double diff = 0.0, hilf;
        int    n    = a.length;
        
        for (int i=0;i<n;i++) {
            hilf = Math.abs(a[i] - b[i]);
            diff = Math.max(diff, hilf);
        }
        return diff;
    }
}
