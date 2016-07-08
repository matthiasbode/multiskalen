package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * DistanceCriterion.java berechnet den Abstand zwischen
 * zwei geometrischen Objekten.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class DistanceCriterion implements PolygonReductCriterion {
    
    /**
     * Berechnet den Abstand zwischen Knoten B und Kante AC.
     */
    public double getValue(Point2d A, Point2d B, Point2d C) {
        double dx = C.x-A.x, dy = C.y-A.y;
        return Math.abs((dx*(B.y-A.y) - dy*(B.x-A.x)) / Math.sqrt(dx*dx + dy*dy));
    }
}