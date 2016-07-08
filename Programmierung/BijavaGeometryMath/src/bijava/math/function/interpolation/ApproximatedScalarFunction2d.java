package bijava.math.function.interpolation;
import bijava.geometry.dim2.*;
import java.util.*;

public class ApproximatedScalarFunction2d extends DiscretizedScalarFunction2d{
    
    public static final int FRANK_LITTLE=0;
     
    double mue = 2; 
    double R = 1.0;
    int	method = FRANK_LITTLE;
    
    private double dmax=Double.POSITIVE_INFINITY; // maximal distance between two
    // neighbouring sampling points
    
    public double r[];          // !!!!! damit Werte im Kreis, Elipse in DarstellungTiefe gezeichnet werden koennen
    public double psi[];

   // points = Stuetzstellen ............... ,  // f[i] = Stuetzwerte ........................
    public ApproximatedScalarFunction2d(Point2d[] points,double[] f) 
    {
        this.samplingPoints = points;
        this.values = f;
        size=points.length;
       
        dmax=0.;
        double dmin=Double.POSITIVE_INFINITY;
        for(int i=0; i<points.length-1;i++){
            dmin=Double.POSITIVE_INFINITY;
            for(int j=i+1; j<points.length;j++)
                dmin=Math.min(dmin,points[i].distance(points[j]));
             dmax=Math.max(dmax,dmin);
        }
    }
        
    
    public void setMethod(int method){
        if (method < 0 || method >0)
            throw new IndexOutOfBoundsException("no such method= "+method);
        this.method=method;
    }
    
    public void setScoupe(double radius){
        this.R=radius;
    }
    
     public void setExponent(double mue){
        this.mue=mue;
    }
      
    //--------------------------------------------------------------------------//
/** Gets the number of sampling points in the discretized scalar function.  */
//--------------------------------------------------------------------------//
  public int getSizeOfValues() { return size; }
  
  //--------------------------------------------------------------------------//
/** Gets the value of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.
 *
 *  @return The method returns an array with the argument and the value.    */
//--------------------------------------------------------------------------//
  synchronized public double getSamplingValueAt(int i)
  { if (i < 0 || i >= size)
      throw new IndexOutOfBoundsException("You can't get get a value at "+ i);
    return   values[i];
  }
  //--------------------------------------------------------------------------//
/** Gets the value of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.
 *
 *  @return The method returns an array with the argument and the value.    */
//--------------------------------------------------------------------------//
  synchronized public Point2d getSamplingPointAt(int i)
  { if (i < 0 || i >= size)
      throw new IndexOutOfBoundsException("You can't get get a value at "+ i);
    return   samplingPoints[i];
  }
 
    public double getValue(  		// Shepard-Interpolation (global,lokal,FL-Gewichte)
            Point2d p     	// p = Interpolationsstelle ............
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
             * R        Radcius fuer die lokale Methode; er bestimmt denjenigen      *
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
        //double r[];         // [0..n]-Vektor mit den Euklidischen Abstaenden
        // der Stuetzstellen von der Interpolationsstelle
        double w[];         // [0..n]-Vektor mit den Gewichten fuer die
        // Stuetzwerte; haengt ab von r und mue.
        //double psi[] = null;// [0..n]-Hilfsvektor zur Berechnung der Gewichte
        // im Falle der lokalen Shepard-Methode
        double norm = 0;    // 1-Norm des Gewichtsvektors vor der Normierung
        int  j;             // Laufvariable
        
        int n = samplingPoints.length-1;
        
        double PHI=0;	// Ausgabeparameter fuer den
        double Rzwischen=R;  // Radius zwischengespeichert, falls R vergroessert werden muss, wenn nicht genug Messpunkte vorhanden
        double dmin=Double.POSITIVE_INFINITY, dmax=Double.NEGATIVE_INFINITY;
       
        if (n < 0) {                    	// unerlaubter Wert fuer n?
            return values[0];
        }
            
        r = new double[n+1];            	
        psi = new double[n+1];
        w   = new double[n+1];
        psi = new double[n+1];
                 	
        if (mue <= 0)                     	// unerlaubter Wert fuer mue?
            mue = 2;           
        
        for (j = 0; j <= n; j++) {	           // Abstaende r[j] berechnen
            r[j] = p.distance(samplingPoints[j]);  // den Standardwert 2 verwenden
            
            if( dmin> r[j]) dmin = r[j];  
            if( dmax< r[j]) dmax = r[j];
                    
        }
                
        switch (method) {           		
                  
       //-----------------------------------------------------------------------------------         
           case FRANK_LITTLE:
                
           int zaehler=0;
            
           while(true){  //mindestens drei Messpunkte im Radius
             for (j = 0; j <= n; j++) {                
               if (r[j] >= R)
                 psi[j] = 0;
               else{
                 psi[j] = 1 - r[j] / R;
                 zaehler++;
               }
             }
                   
             if(zaehler<3){
               R=R+dmin;
               zaehler=0;
             }
             else {
               break;
             } 
           }
             
           R=Rzwischen;
             
           for (j = 0, norm = 0; j <= n; j++) {      
             w[j] =  Math.pow(psi[j], mue);         
             norm  += w[j];                        
           }
                                 
                    
           PHI = 0; 
           if (norm == 0) {                         
             return Double.NaN;                   
           }
           
           for (j = 0; j <= n; j++){                  // die Gewichte
             if (w[j] != 0){                        // normieren
               PHI += w[j] / norm * values[j];  // Rueckgabewert
             }    
           }      
                               
            break;
            
        } //Ende switch
 
        //------------------------------------------------------------
 
        return PHI;
    }
       
    //--------------------------------------------------------------------------//
    /** Gets the confidence value of an argument x.
     *
     *  @param x If the argument isn't in the definition range the methode returns
     *           <code>0.</code>                                        */
    //--------------------------------------------------------------------------//
    synchronized public double getConfidenceValue(Point2d p) {
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
    
//--------------------------------------------------------------------------//
/** Gets the confidence region of the discretized scalar
 *  function.                                                               */
//--------------------------------------------------------------------------//
  public SimplePolygon2d getConfidenceRegion(){
        Point2d[] reg = {new Point2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), new Point2d(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), 
                new Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY) , new Point2d(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)};
      return new SimplePolygon2d(reg);
  
  }
    
 public static void main ( String[] args) 
  { 
    
    
  }  
  
}