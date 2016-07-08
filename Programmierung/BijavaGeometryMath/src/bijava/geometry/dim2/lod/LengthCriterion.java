package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * LengthCriterion.java berechnet die Laengenaenderung
 * eines geometrischen Objektes durch Reduktion einer Komponente.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class LengthCriterion implements PolygonReductCriterion {
    
    /**
     * Berechnet die Laengendifferenz zwischen dem
     * Kantenzug AB - BC und der Kante AC.
     */
    public double getValue(Point2d A, Point2d B, Point2d C) {
        return A.distance(B) + B.distance(C) - A.distance(C);
    }
}