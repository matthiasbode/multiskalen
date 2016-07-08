package bijava.math.function.interpolation.meshless;
import bijava.geometry.*;

public class ShepardVectorFunction {
    
    public static final int GLOBAL=0;
    public static final int LOCAL=1;
    public static final int FRANK_LITTLE=2;
    
    MetricPoint[] samplingPoints;
    double samplingValues[][];
    double 	mue = 2; //
    double R = 1.0;
    int	method = GLOBAL;
    
    private double dmax=Double.POSITIVE_INFINITY; // maximal distance between two
    // neighbouring sampling points
    
    private double  epsilon  = 0.00001;           //    epsilon range of a value
    
    public ShepardVectorFunction(
            MetricPoint[] points,	// points = Stuetzstellen ...............
            double[][] f      	// f[i] = Stuetzwerte ........................
            ) {
        this.samplingPoints = points;
        this.samplingValues = f;
        
//        System.out.println("compute maximal distance between nearbourhoud Points");
        dmax=0.;
        double dmin=Double.POSITIVE_INFINITY;
        for(int i=0; i<points.length-1;i++){
            dmin=Double.POSITIVE_INFINITY;
            for(int j=i+1; j<points.length;j++)
                dmin=Math.min(dmin,points[i].distance(points[j]));
            dmax=Math.max(dmax,dmin);
        }
//        System.out.println("maximal distance = "+dmax);
    }
    
    public void setMethod(int method){
        if (method < 0 || method >2)
            throw new IndexOutOfBoundsException("no such method [0-2] != "+method);
        this.method=method;
    }
    
    public void setScoupe(double radius){
        this.R=radius;
    }
    
    public double[] getValue(  		// Shepard-Interpolation (global,lokal,FL-Gewichte)
            MetricPoint p      	// p = Interpolationsstelle ............
            )
            /***********************************************************************
             * Dieses Unterprogramm berechnet einen Funktionswert zu vorgegebenen   *
             * Stuetzstellen nach dem Interpolationsverfahren von Shepard. Dabei    *
             * besteht die Auswahl zwischen der globalen Shepard-Methode, der       *
             * lokalen Shepard-Methode und der lokalen-Shepard-Methode mit          *
             * Franke-Little-Gewichten.                                             *
             *                                                                      *
             * Eingabeparameter:                                                    *
             * =================                                                    *
             * p        p ist der Punkt, an dem der Wert der                        *
             *          Shepard-Interpolationsfunktion gesucht wird.                *
             * s        Vektor mit den Stuetzstellen                                *
             * f        [0..n]-Vektor mit den Stuetzwerten                          *
             * mue      frei waehlbarer Parameter der Shepard-Methode, der als      *
             *          Exponent bei der Berechnung der Gewichte dient (mue > 0);   *
             *          gute Ergebnisse erzielt man mit 2 < mue < 6.                *
             *          Falls mue <= 0, wird mue automatisch auf den Wert 2         *
             *          gesetzt.                                                    *
             * methode  Nummer derjenigen Variante der Shepard-Methode, die zur     *
             *          Interpolation benutzt werden soll:                          *
             *          = 0: globale Methode                                        *
             *          = 1: lokale  Methode                                        *
             *          = 2: lokale  Methode mit Franke-Little-Gewichten            *
             * R        Radius fuer die lokale Methode; er bestimmt denjenigen      *
             *          Kreis um die Interpolationsstelle (x0,y0), in dem die bei   *
             *          der Interpolation zu beruecksichtigenden Stuetzstellen      *
             *          liegen; alle Stuetzstellen ausserhalb dieses Kreises werden *
             *          ignoriert.                                                  *
             *          Der Radius sollte so gewaehlt werden, dass noch genuegend   *
             *          viele Stuetzstellen in diesem Kreis liegen.                 *
             *                                                                      *
             * Ausgabeparameter:                                                    *
             * =================                                                    *
             * PHI      Interpolationswert bei (x0,y0)                              *
             *                                                                      *
             * Funktionswert:                                                       *
             * ==============                                                       *
             * Fehlercode. Folgende Werte koennen auftreten:                        *
             * = 0: alles in Ordnung                                                *
             * = 1: nicht erlaubte Eingabeparameter:                                *
             *      n < 0  oder  methode != 0,1,2  oder  R <= 0                     *
             * = 2: Alle Gewichte w[i] sind Null.                                   *
             * = 3: Speichermangel                                                  *
             *                                                                      *
             ***********************************************************************/
            
    {
        double r[];         // [0..n]-Vektor mit den Euklidischen Abstaenden
        // der Stuetzstellen von der Interpolationsstelle
        double w[];         // [0..n]-Vektor mit den Gewichten fuer die
        // Stuetzwerte; haengt ab von r und mue.
        double psi[] = null;// [0..n]-Hilfsvektor zur Berechnung der Gewichte
        // im Falle der lokalen Shepard-Methode
        double xi[] = null; // [0..n]-Hilfsvektor zur Berechnung der Gewichte
        // im Falle der lokalen Shepard-Methode mit
        // Franke-Little-Gewichten
        double norm = 0;    // 1-Norm des Gewichtsvektors vor der Normierung
        int  j;             // Laufvariable
        
        int n = samplingPoints.length-1;
        
        double PHI[] = new double[samplingValues[0].length];  	// Ausgabeparameter fuer den
        
        if (n < 0) {                    	// unerlaubter Wert fuer n?
            for (int z_vec = 0;z_vec<samplingValues[0].length;z_vec++)
                PHI[z_vec] = samplingValues[0][z_vec];
            return PHI;
        }
        
        if (method != GLOBAL)                 	// nicht globale Methode?
            if (R <= 0)                     // unerlaubter Wert fuer R?
                if (method == FRANK_LITTLE)       // lokal mit Franke-Little-Gew.?
                    R = (double)0.1;// R korrigieren
                else {                  // andere Methode?
            for (int z_vec = 0;z_vec<samplingValues[0].length;z_vec++)
                PHI[z_vec] = Double.NaN;
            return PHI;
                }
        
        r = new double[n+1];            	// Speicher fuer drei Vektoren
        // anfordern
        w   = new double[n+1];
        if (method != GLOBAL)
            psi = new double[n+1];
        
        xi  = psi;                      	// nur ein anderer Name fuer
        // schon vorhandenen Speicher
        
        if (mue <= 0)                     	// unerlaubter Wert fuer mue?
            mue = 2;                        // den Standardwert 2 verwenden
        
        for (j = 0; j <= n; j++) {	         // Abstaende r[j] berechnen
            r[j] = p.distance(samplingPoints[j]);
            
            if (r[j] == 0) {                // Abstand Null, d. h. (x0,y0)
                // ist eine Stuetzstelle?
                for (int z_vec = 0;z_vec<samplingValues[0].length;z_vec++)
                    PHI[z_vec] = samplingValues[j][z_vec];// passenden Stuetzwert
                // zurueckgeben
                return PHI;
            }
        }
        
        
        switch (method) {           		// Gewichtsvektor
            // vorbesetzen, seine
            // 1-Norm berechnen
            case GLOBAL:                  	// globale Methode?
                for (j = 0, norm = 0; j <= n; j++) {
                    w[j] = 1 / Math.pow(r[j], mue);
                    norm += w[j];
                }
                break;
                
            case LOCAL:                   	// lokale Methode?
                for (j = 0; j <= n; j++)// psi[j] berechnen
                    if (r[j] >= R)
                        psi[j] = 0;
                    else
                        psi[j] = (R / r[j]) - 1;
                
                for (j = 0, norm = 0; j <= n; j++)  			// aus psi die
                    if (psi[j] != 0) {          			// Gewichte (noch
                    w[j] = 1 / Math.pow(psi[j], mue);   	// nicht normiert) und
                    norm += w[j];                       	// ihre Summe
                    } else {                              	    	// berechnen
                    w[j] = 0;
                    }
                break;
                
            case FRANK_LITTLE:                                    	 // lokale Methode mit
                // Franke-Little-Gew.?
                for (j = 0; j <= n; j++)                 // xi[j] berechnen
                    if (r[j] >= R)
                        xi[j] = 0;
                    else
                        xi[j] = 1 - r[j] / R;
                
                for (j = 0, norm = 0; j <= n; j++) {       // aus xi die Gewichte
                    w[j] =  Math.pow(xi[j], mue);      // (noch nicht
                    norm  += w[j];                     // normiert) und ihre
                }			                   // Summe berechnen
        }
        
        for (int z_vec = 0;z_vec<samplingValues[0].length;z_vec++) {
            
            if (norm == 0) {                                   // Alle Gewichte w[j]
                // sind Null?
                PHI[z_vec] = Double.NaN;                   // Fehler melden
                break;
            }
            for (j = 0; j <= n; j++)                     	   // die Gewichte
                if (w[j] != 0)                             // normieren
                    w[j] /= norm;
            
            PHI[z_vec] = 0;                         	   // Wert der Interpolationsfunktion
            for (j = 0; j <= n; j++)         		   // an der Stelle (x0,y0) berechnen
                PHI[z_vec] += w[j] * samplingValues[j][z_vec];
            
        }
        return PHI;
    }
    
        //--------------------------------------------------------------------------//
    /** Gets the confidence value of an argument x.
     *
     *  @param x If the argument isn't in the definition range the methode returns
     *           <code>0.</code>                                        */
    //--------------------------------------------------------------------------//
    synchronized public double getConfidenceValue(MetricPoint p) {
        int      size   = samplingPoints.length;
        double   value  = 0.0;
        double   dist   = 0.0;
        double mindistance = Double.POSITIVE_INFINITY;
        
        for (int j = 0; j < size; j++) {
            dist = p.distance(samplingPoints[j]);
            if (Math.abs(dist) < epsilon) return 1.;
            else mindistance = Math.min(mindistance,dist);
        }
        return 1.- Math.min(1.,2.* mindistance/dmax);
    }
}