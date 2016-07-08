package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;

/**
 * VertexInsertion.java verwirklicht die gleichnamige Methode
 * zur Verfeinerung von geometrischen Objekten als Kriterium.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class VertexInsertion implements PolygonRefineCriterion {
    
    /**
     * Teilt die Kante BC in zwei Haelften durch mittiges Einfuegen eines Knotens.
     */
    public Point2d[] getNextPoints(Point2d A, Point2d B, Point2d C, Point2d D) {
        // Sonderfaelle pruefen
        if (A == null) return new Point2d[] {B, B.add((C.sub(B)).mult(0.5))};
        if (C == null) return new Point2d[] {B};
        // 4-Knoten-Schema
        return new Point2d[] {B, (((B.add(C)).mult(9.)).sub(A.add(D))).mult(1./16.)};
    }
}