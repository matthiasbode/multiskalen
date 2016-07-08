package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * CornerCutting.java verwirklicht die gleichnamige Methode zur Verfeinerung von
 * geometrischen Objekten als Kriterium.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class CornerCutting implements PolygonRefineCriterion {
    
    /**
     * Staucht die Kante BC auf die Haelfte ihrer Laenge.
     */
    public Point2d[] getNextPoints(Point2d A, Point2d B, Point2d C, Point2d D) {
        // Sonderfall pruefen
        if (C == null) return new Point2d[0];
        // 2-Knoten-Schema
        return new Point2d[] {B.add((C.sub(B)).mult(0.25)), B.add((C.sub(B)).mult(0.75))};
    }
}