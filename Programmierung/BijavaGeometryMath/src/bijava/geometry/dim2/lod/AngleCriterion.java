package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * AngleCriterion.java berechnet die Richtungsaenderung der Huelle 
 * eines geometrischen Objektes bei einem seiner Knoten.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class AngleCriterion implements PolygonReductCriterion {
    
    /**
     * Berechnet die Richtungsaenderung zwischen den Kanten AB und BC.
     */
    public double getValue(Point2d A, Point2d B, Point2d C) {
        double a = B.distance(C), b = C.distance(A), c = A.distance(B);
        return 180. - (180./(Math.PI)) * Math.acos((c*c + a*a - b*b) / (2.*c*a));
    }
}