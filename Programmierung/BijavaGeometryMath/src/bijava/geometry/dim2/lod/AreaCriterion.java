package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * AreaCriterion.java berechnet die Flaeche eines geometrischen Objektes.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class AreaCriterion implements PolygonReductCriterion {
    
    /**
     * Berechnet die Flaeche von Dreieck ABC.
     */
    public double getValue(Point2d A, Point2d B, Point2d C) {        
        return Math.abs((- A.x*C.y - B.x*A.y - C.x*B.y + A.x*B.y + B.x*C.y + C.x*A.y) / 2.);
    }
}