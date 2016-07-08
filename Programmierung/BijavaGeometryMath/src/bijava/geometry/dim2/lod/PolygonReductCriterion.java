package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * PolygonReductCriterion.java ist eine Schnittstelle fuer
 * strukturelle Kriterien zur Reduktion eines polygonalen Objektes.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public interface PolygonReductCriterion {
    
    /**
     * Funktion zur Berechnung eines strukturellen Wertes
     * eines polygonalen Objektes bei einem seiner Knoten unter
     * Zuhilfenahme der unmittelbaren Nachbarn (3-Knoten-Schema).
     * @param A Vorgaenger-Knoten.
     * @param B Knoten, bei dem ein struktureller Wert berechnet wird.
     * @param C Nachfolger-Knoten.
     * @return struktureller Wert bei Knoten B.
     */
    public double getValue(Point2d A, Point2d B, Point2d C);
}