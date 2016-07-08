package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * PolygonCornerValue.java dient zur Speicherung des Funktionswertes
 * einer Ecke eines Polygons.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class PolygonCornerValue implements Comparable {
    public Point2d p; // Knoten der Ecke
    public double f; // struktureller Wert
    public PolygonCornerValue co0, co1; // Nachbar-Ecken
    
    /**
     * Erzeugt eine neue Ecke ohne strukturellen Wert.
     * @param p zweidimensionaler Punkt.
     */
    public PolygonCornerValue(Point2d p) {
        this.p = p;
    }
    
    /**
     * Aktualisiert den Funktionswert der Ecke durch Berechnung
     * mithilfe eines strukturellen Kriteriums zur Reduktion
     * eines polygonalen Objektes.
     * @param crit strukturelles Kriterium zur Reduktion eines
     *  polygonalen Objektes.
     */
    public void updateValue(PolygonReductCriterion crit) {
        if (co0 != null && co1 != null)
            f = crit.getValue(co0.p, p, co1.p);
    }
    
    /**
     * Redefiniert die Funktion <code>compareTo()</code> der
     * Schnittstelle <code>Comparable</code> und vergleicht den
     * Funktionswert dieser Ecke mit dem einer anderen Ecke.
     * Falls dieser Funktionswert kleiner ist, liefert die Methode
     * den <code>int</code>-Wert -1, bei Gleichheit 0 und sonst 1.
     * @param obj Ecke mit Funktionswert.
     * @return Zeiger fuer das Ergebnis des Vergleiches.
     */
    public int compareTo(Object obj) {
        if (!(obj instanceof PolygonCornerValue))
            throw new IllegalArgumentException("unsupported class");
        PolygonCornerValue co = (PolygonCornerValue) obj;
        if (f < co.f) return -1;
        if (f > co.f) return 1;
        return 0;
    }
}