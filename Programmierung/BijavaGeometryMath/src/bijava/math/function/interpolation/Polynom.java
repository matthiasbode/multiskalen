package bijava.math.function.interpolation;
import bijava.geometry.VectorPoint;
import bijava.math.function.*;
import java.util.Arrays;

/**
 * Klasse Polynom - beschreibt ein Polynom n-ter Ordnung. Die Faktoren der Koeffizienten werden im Feld c gespeichert.
 * Die Laenge des Feldes beschreibt also den hoechsten Grad des Polynoms. Der Wert in c[0] entpricht
 * dem Faktor fuer x^0, in c[1] fuer x^1, ... , in c[n] fuer x^n.
 */
public class Polynom implements DifferentialScalarFunction1d, VectorPoint<Polynom> {
    private double[] c;                             // Koeffizientenfeld
    
    /**..Standardkonstruktor: Nullpolynom vom Grad 0 .................. */
    
    public Polynom() {
        c = new double[1];                          // Erzeuge Feld
        c[0] = 0.0;                                 // Setze Koeffizient 0
    }
    
    /**..Konstruktor: Nullpolynom vom Grad n ........................... */
    public Polynom(int n) {
        c = new double[n+1];         // Erzeuge Feld
//        for (int j=0; j<=n; j++)
//            c[j] = 0.0;                  // Setze Koeffizienten 0
    }
    
    /**..Konstruktor: Polynom mit Koeffizienten........................ */
    public Polynom(double[] feld) {
        c = feld.clone();
//        c = new double[feld.length];            // Erzeuge Feld
//        for (int j=0; j<feld.length; j++)
//            c[j] = feld[j];                         // Kopiere Koeffizienten
    }
    
    /**..equals-Methode: Prueft zwei Polynome auf Gleichheit..*/
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Polynom))
            return false;
        Polynom p = (Polynom)o;
        if (c.length != p.c.length)
            return false;
        for (int i = 0; i < c.length; i++) {
            if (c[i] != p.c[i])
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.c);
        return hash;
    }
    
    @Override
    public int dim(){
        return c.length;
    }
    @Override
    public double getCoord(int i){
        return c[i];
    }
    
    @Override
    public void setCoord(int i,double d){
        c[i]=d;
    }
    
    @Override
    public double[] getCoords(){
        return c;
    }
    /**..Objektfunktion: Addition zweier Polynome this und p .......... */
    @Override
    public Polynom add(Polynom p) {
        int m = Math.max(p.c.length, this.c.length); //  Bestimme maximale Koeffizientenanzahl
        
        Polynom r = new Polynom(m-1);           // Erzeuge Polynom r
        for (int j=0; j < p.c.length; j++)
            r.c[j]+=p.c[j];                     // Addiere Koeffizien�en
        // von p zu r
        for (int j=0; j < this.c.length; j++)
            r.c[j]+=this.c[j];                     // Addiere Koeffizienten
        // von q zu r
        return r;                               // Rueckgabe Polynom r
    }
    
    /**..Objektfunktion: Subtraktion zweier Polynome this und p .......... */
    @Override
    public Polynom sub(Polynom p) {
        int m;
        if (p.c.length < this.c.length)            //  Bestimme maximale
            m = this.c.length;                     //  Koeffizientenanzahl m
        else m = p.c.length;                    //  von p oder q
        
        Polynom r = new Polynom(m-1);           // Erzeuge Polynom r
        for (int j=0; j < p.c.length; j++)
            r.c[j]-=p.c[j];                     // Subtrahiere Koeffizien�en
        // von p zu r
        for (int j=0; j < this.c.length; j++)
            r.c[j]-=this.c[j];                     // Subtrahiere Koeffizienten
        // von q zu r
        return r;                               // Rueckgabe Polynom r
    }
    
    /** Objectfunktion: Multipliziert jeden Koeffizienten mit uebergebenem double */
    @Override
    public Polynom mult(double x) {
        if (x == 0.0)
            return new Polynom();
        Polynom p = new Polynom(c.length-1);
        for (int i = 0; i < c.length; i++)
            p.c[i] = c[i]*x;
        return p;
    }
    
    /** Objeckt-Funktion: Multiplikation mit einem Polynom */
    public Polynom mult(Polynom p) {
        if ((c.length == 1 && c[0] == 0.0) || (p.c.length == 1 && p.c[0] == 0.0))
            return new Polynom();
        Polynom erg = new Polynom(p.c.length-1 + c.length-1);
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < p.c.length; j++)
                erg.c[i+j] += c[i]*p.c[j];
        }
        return erg;
    }
    
    /**..Objektfunktion: Funktionswert des Polynoms an Stelle x --- */
    @Override
    public double getValue(double x) {
        double sum = 0.0, xj = 1.0;   //   Setze Anfangswerte
        for (int j=0; j<c.length; j++)//   Berechne Funktionswert
        {
            sum += c[j] * xj;        //   ..Summenbildung
            xj *= x;                                       //   ..Potenzbildung
        }
        return sum;                                         // Rueckgabe Summenwert
    }
    
    /**..Objektfunktion: Skalieren eines Polynoms mit einem Faktor. */
    public Polynom scale(double factor) {
        for (int j=0; j<c.length; j++)
            c[j] *= factor;                        // Skaliere Koeffizienten
        return this;                                       // Rueckgabe Polynom
    }
    
    /**..*/
    
    
    //--------------------------------------------------------------------------//
    /** Gets the derivation of an argument x.                                   */
    //--------------------------------------------------------------------------//
    @Override
    public double getGradient(double x) {
        double sum = 0.0, xj = 1.0;    //   Setze Anfangswerte
        for (int j=1; j<c.length; j++) //   Berechne Funktionswert
        {
            sum += j*c[j] * xj;        //   ..Summenbildung
            xj *= x;                   //   ..Potenzbildung
        }
        return sum;
    }
    
    /** Gets the derivation of the Polynom.                                   */
    @Override
    public Polynom getDerivation() {
        // Wenn Polynom vom Grad 0 return neues Polynom p(x) = 0.0
        if (this.c.length == 1)
            return new Polynom();
        // Erstelle Polynom mit this.Grad-1
        Polynom p = new Polynom(c.length-2);
        for (int i = 1; i < c.length; i++)
            p.c[i-1] = c[i]*(i);
        return p;
    }
    
    /**..Objektfunktion: Gibt den Grad des Polynoms zurueck */
    public int getOrder() {
        return c.length-1;
    }
    
    
    /**..toString.............*/
    public String toString() {
        String poly = "p(x) = ";
        for (int i = getOrder(); i > 0; i--) {
            if (i==1)
                poly += c[i] + "x + ";
            else
                poly += c[i] + "x^" + i + " + ";
        }
        poly += c[0];
        return poly;
    }
}                                                                            // Klassenende

