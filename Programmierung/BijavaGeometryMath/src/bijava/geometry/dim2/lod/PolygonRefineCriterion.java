package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * PolygonRefineCriterion.java ist eine Schnittstelle
 * fuer strukturelle Kriterien zur Verfeinerung eines polygonalen Objektes.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public interface PolygonRefineCriterion {
    
    /**
     * Funktion zur Verfeinerung einer Kante eines polygonalen
     * Objektes unter Zuhilfenahme der unmittelbaren Nachbarn (4-Knoten-Schema).
     * @param A Vorgaenger-Knoten.
     * @param B Anfangs-Knoten der Kante, die verfeinert wird.
     * @param C End-Knoten der Kante, die verfeinert wird.
     * @param D Nachfolger-Knoten.
     * @return Anfangs- und End-Knoten der verfeinerten Kante als Feld.
     */
    public Point2d[] getNextPoints(Point2d A, Point2d B, Point2d C, Point2d D);
}