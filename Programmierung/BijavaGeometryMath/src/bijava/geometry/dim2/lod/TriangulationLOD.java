package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.Triangle2d;
import java.util.ArrayList;

/**
 * TriangulationLOD.java stellt LOD-Methoden fuer Triangulationen zur Verfuegung.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, November 2006
 */
public class TriangulationLOD {
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion einer Triangulation um die Knoten,
     * die ausserhalb einer Bounding Box liegen:
     * Alle Elemente, welche die Bounding Box schneiden,
     * gehoeren zum sichbaren Teil der Triangulation.
     * @param ele Triangulation.
     * @param xmin minimale x-Koordinate einer Bounding Box.
     * @param xmax maximale x-Koordinate einer Bounding Box.
     * @param ymin minimale y-Koordinate einer Bounding Box.
     * @param ymax maximale y-Koordinate einer Bounding Box.
     * @return Triangulation.
     */
    public Triangle2d[] reduceNonVisible(Triangle2d[] ele, double xmin, double xmax, double ymin, double ymax) {
        if (ele == null) return null;
        
        // Element sichtbar, falls dessen BoundingBox die uebergebene BoundingBox schneidet
        double width = xmax - xmin, height = ymax - ymin;
        double xs = xmin + width / 2., ys = ymin + height / 2.;
        ArrayList<Triangle2d> erg = new ArrayList<Triangle2d>();
        
        for (int i = 0; i < ele.length; i++) {
            Point2d[] pts = ele[i].getPoints();
            double ele_xmin = Math.min(pts[0].x, Math.min(pts[1].x, pts[2].x));
            double ele_xmax = Math.max(pts[0].x, Math.max(pts[1].x, pts[2].x));
            double ele_ymin = Math.min(pts[0].y, Math.min(pts[1].y, pts[2].y));
            double ele_ymax = Math.max(pts[0].y, Math.max(pts[1].y, pts[2].y));
            double ele_width = ele_xmax - ele_xmin, ele_height = ele_ymax - ele_ymin;
            double ele_xs = ele_xmin + ele_width / 2., ele_ys = ele_ymin + ele_height / 2.;
            double dx = Math.abs(ele_xs - xs), dy = Math.abs(ele_ys - ys);
            double width_mid = (width + ele_width) / 2., height_mid = (height + ele_height) / 2.;
            if (dx < width_mid && dy < height_mid)
                erg.add(ele[i]);
        }
        
        return erg.toArray(new Triangle2d[erg.size()]);
    }
}