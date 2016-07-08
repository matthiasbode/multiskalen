package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.Polygon2d;
import bijava.geometry.dim2.PolygonalCurve2d;
import java.util.ArrayList;
import java.util.Collections;

/**
 * PolygonLOD.java stellt LOD-Methoden fuer polygonale Geometrien zur Verfuegung.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class PolygonLOD {
//-------------------------------------------------------------------------------------------------------
    private static LengthCriterion LCRIT = new LengthCriterion();
    private static MaxLengthErrorFunction1d MLEFctn = new MaxLengthErrorFunction1d();
    private int size;
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Topologische Reduktion einer polygonalen Kurve:
     * Alle n-ten Knoten gehoeren zum naechsten LOD.
     * Ausnahme: Ein Knoten bleibt erhalten, falls durch dessen
     * Reduktion die Simplizitaet verloren geht.
     * @param cu polygonale Kurve.
     * @param n Schrittweite.
     * @return polygonale Kurve.
     */
    public PolygonalCurve2d reduceTopolog(PolygonalCurve2d cu, int n) {
        if (cu == null || n < 1) return null;
        if (n == 1) return cu;
        
        Point2d[] pts = cu.getPoints();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        for (int i = 0; i < pts.length; i++) plist.add(pts[i]);
        size = pts.length;
        
        for (int i = 1; i < pts.length; i++)
            if (i % n != 0)
                if (i == pts.length - 1 || this.curveIsSimple(plist, pts[i - 1], pts[i + 1])) {
            plist.remove(pts[i]);
            size--;
                }
        
        if (size < 2) return null;
        return new PolygonalCurve2d(plist.toArray(new Point2d[size]));
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Topologische Reduktion eines Polygons:
     * Alle n-ten Knoten gehoeren zum naechsten LOD.
     * Ausnahme: Ein Knoten bleibt erhalten, falls durch dessen
     * Reduktion die Simplizitaet verloren geht.
     * @param pg Polygon.
     * @param n Schrittweite.
     * @return Polygon.
     */
    public Polygon2d reduceTopolog(Polygon2d pg, int n) {
        if (pg == null || n < 1) return null;
        if (n == 1) return pg;
        
        Point2d[] pts = pg.getPoints();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        for (int i = 0; i < pts.length; i++) plist.add(pts[i]);
        size = pts.length;
        
        for (int i = 0; i < pts.length; i++)
            if (i % n != 0)
                if (this.polygonIsSimple(plist, pts[(i + pts.length - 1) % pts.length], pts[(i + 1) % pts.length])) {
            plist.remove(pts[i]);
            size--;
                }
        
        if (size < 3) return null;
        return new Polygon2d(plist.toArray(new Point2d[size]));
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Graduelle Reduktion einer polygonalen Kurve:
     * Alle Knoten mit einem strukturellen Wert groesser als ein
     * Minimum gehoeren zum naechsten LOD.
     * Ausnahme: Ein Knoten bleibt erhalten, falls durch dessen
     * Reduktion die Simplizitaet verloren geht.
     * @param cu polygonale Kurve.
     * @param crit strukturelles Kriterium zur Reduktion eines polygonalen Objektes.
     * @param eps minimaler struktureller Wert bezueglich des Kriteriums.
     * @return polygonale Kurve.
     */
    public PolygonalCurve2d reduceGradual(PolygonalCurve2d cu, PolygonReductCriterion crit, double eps) {
        if (cu == null || crit == null || eps < 0.) return null;
        if (eps == 0.) return cu;
        
        Point2d[] pts = cu.getPoints();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        for (int i = 0; i < pts.length; i++) plist.add(pts[i]);
        size = pts.length;
        
        for (int i = 1; i < pts.length - 1; i++) {
            Point2d A = pts[i - 1], B = pts[i], C = pts[i + 1];
            if (crit.getValue(A, B, C) <= eps)
                if (this.curveIsSimple(plist, A, C)) {
                plist.remove(B);
                size--;
                }
        }
        
        return new PolygonalCurve2d(plist.toArray(new Point2d[size]));
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Graduelle Reduktion eines Polygons:
     * Alle Knoten mit einem strukturellen Wert groesser als ein
     * Minimum gehoeren zum naechsten LOD.
     * Ausnahme: Ein Knoten bleibt erhalten, falls durch dessen
     * Reduktion die Simplizitaet verloren geht.
     * @param pg Polygon.
     * @param crit strukturelles Kriterium zur Reduktion eines polygonalen Objektes.
     * @param eps minimaler struktureller Wert bezueglich des Kriteriums.
     * @return Polygon.
     */
    public Polygon2d reduceGradual(Polygon2d pg, PolygonReductCriterion crit, double eps) {
        if (pg == null || crit == null || eps < 0.) return null;
        if (eps == 0.) return pg;
        
        Point2d[] pts = pg.getPoints();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        for (int i = 0; i < pts.length; i++) plist.add(pts[i]);
        size = pts.length;
        
        for (int i = 0; i < pts.length; i++) {
            Point2d A = pts[(i + pts.length - 1) % pts.length], B = pts[i], C = pts[(i + 1) % pts.length];
            if (crit.getValue(A, B, C) <= eps)
                if (this.polygonIsSimple(plist, A, C)) {
                plist.remove(B);
                size--;
                }
        }
        
        if (size < 3) return null;
        return new Polygon2d(plist.toArray(new Point2d[size]));
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion einer polygonalen Kurve unter Beachtung
     * einer Bildschirmaufloesung:
     * Es wird automatisch die gaengige Methode verwendet.
     * @param cu polygonale Kurve.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return polygonale Kurve.
     */
    public PolygonalCurve2d reduce(PolygonalCurve2d cu, double k) {
        return this.reduceNonVisible(this.reduceByMinK(cu, LCRIT, k), k);
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion eines Polygons unter Beachtung
     * einer Bildschirmaufloesung:
     * Es wird automatisch die gaengige Methode verwendet.
     * @param pg Polygon.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return Polygon.
     */
    public Polygon2d reduce(Polygon2d pg, double k) {
        return this.reduceNonVisible(this.reduceByMinK(pg, LCRIT, k), k);
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion einer polygonalen Kurve nach Minimum
     * unter Beachtung einer Bildschirmaufloesung:
     * Entfernung des Knotens mit dem minimalen strukturellen Wert
     * bis ein Laengenfehler ueberschritten wird.
     * Die Simplizitaet der polygonalen Kurve wird beruecksichtigt.
     * @param cu polygonale Kurve.
     * @param crit strukturelles Kriterium zur Reduktion eines polygonalen Objektes.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return polygonale Kurve.
     */
    public PolygonalCurve2d reduceByMinK(PolygonalCurve2d cu, PolygonReductCriterion crit, double k) {
        return this.reduceByMinE(cu, crit, MLEFctn.getValue_PolygonReductByMin(k));
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion eines Polygons nach Minimum
     * unter Beachtung einer Bildschirmaufloesung:
     * Entfernung des Knotens mit dem minimalen strukturellen Wert
     * bis ein Laengenfehler ueberschritten wird.
     * Die Simplizitaet des Polygons wird beruecksichtigt.
     * @param pg Polygon.
     * @param crit strukturelles Kriterium zur Reduktion eines polygonalen Objektes.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return Polygon.
     */
    public Polygon2d reduceByMinK(Polygon2d pg, PolygonReductCriterion crit, double k) {
        return this.reduceByMinE(pg, crit, MLEFctn.getValue_PolygonReductByMin(k));
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion einer polygonalen Kurve nach Minimum
     * unter Beachtung eines maximalen Laengenfehlers:
     * Entfernung des Knotens mit dem minimalen strukturellen Wert
     * bis ein Laengenfehler ueberschritten wird.
     * Die Simplizitaet der polygonalen Kurve wird beruecksichtigt.
     * @param cu polygonale Kurve.
     * @param crit strukturelles Kriterium zur Reduktion eines polygonalen Objektes.
     * @param e maximaler Laengenfehler in Prozent.
     * @return polygonale Kurve.
     */
    public PolygonalCurve2d reduceByMinE(PolygonalCurve2d cu, PolygonReductCriterion crit, double e) {
        if (cu == null || crit == null) return null;
        // Nachbarschaften und strukturelle Werte setzen
        Point2d[] pts = cu.getPoints();
        ArrayList<PolygonCornerValue> colist = new ArrayList<PolygonCornerValue>();
        for (int i = 0; i < pts.length; i++)
            colist.add(new PolygonCornerValue(pts[i]));
        for (int i = 0; i < pts.length; i++) {
            PolygonCornerValue co = colist.get(i);
            if (i == 0) {
                co.co1 = colist.get(i + 1);
                co.f = Double.POSITIVE_INFINITY;
            } else if (i == pts.length - 1) {
                co.co0 = colist.get(i - 1);
                co.f = Double.POSITIVE_INFINITY;
            } else {
                co.co0 = colist.get(i - 1);
                co.co1 = colist.get(i + 1);
                co.updateValue(crit);
            }
        }
        // Knoten nach minimalem Wert sortieren
        Collections.sort(colist);
        // Reduktion bis maximaler Fehler erreicht ist
        double length0 = cu.getLength(), dLengthTot = 0.;
        size = pts.length;
        while (true) {
            // Knoten mit minimalem Wert und Erhaltung der Simplizitaet bestimmen
            PolygonCornerValue co = null;
            for (int i = 0; i < size - 2 && co == null; i++) {
                PolygonCornerValue cotmp = colist.get(i);
                if (this.curveIsSimple(colist, cotmp))
                    co = cotmp;
            }
            // Falls es keinen solchen Knoten gibt ist die Reduktion beendet
            if (co == null) break;
            // Aenderung des Laengenfehlers bestimmen
            double dLength = LCRIT.getValue(co.co0.p, co.p, co.co1.p);
            // Falls der Laengenfehler ueberschritten wird ist die Reduktion beendet
            if (((dLengthTot + dLength) / length0) * 100. > e) break;
            // Aenderung des Laengenfehlers speichern
            dLengthTot += dLength;
            // Kurve um Knoten reduzieren
            colist.remove(co);
            size--;
            // Nachbarn aktualisieren
            co.co0.co1 = co.co1;
            co.co0.updateValue(crit);
            co.co1.co0 = co.co0;
            co.co1.updateValue(crit);
            // Falls nur noch 2 Knoten vorhanden sind ist die Reduktion beendet
            if (size == 2) break;
            // Nachbarn neu einsortieren
            Collections.sort(colist);
        }
        // Knoten in Reihenfolge bringen
        Point2d[] pts_red = new Point2d[size];
        PolygonCornerValue co = colist.get(size - 2);
        for (int i = 0; i < size; i++) {
            pts_red[i] = co.p;
            co = co.co1;
        }
        
        return new PolygonalCurve2d(pts_red);
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion eines Polygons nach Minimum
     * unter Beachtung eines maximalen Laengenfehlers:
     * Entfernung des Knotens mit dem minimalen strukturellen Wert
     * bis ein Laengenfehler ueberschritten wird.
     * Die Simplizitaet des Polygons wird beruecksichtigt.
     * @param pg Polygon.
     * @param crit strukturelles Kriterium zur Reduktion eines polygonalen Objektes.
     * @param e maximaler Laengenfehler in Prozent.
     * @return Polygon.
     */
    public Polygon2d reduceByMinE(Polygon2d pg, PolygonReductCriterion crit, double e) {
        if (pg == null || crit == null) return null;
        // Nachbarschaften und strukturelle Werte setzen
        Point2d[] pts = pg.getPoints();
        ArrayList<PolygonCornerValue> colist = new ArrayList<PolygonCornerValue>();
        for (int i = 0; i < pts.length; i++)
            colist.add(new PolygonCornerValue(pts[i]));
        for (int i = 0; i < pts.length; i++) {
            PolygonCornerValue co = colist.get(i);
            co.co0 = colist.get((i + pts.length - 1) % pts.length);
            co.co1 = colist.get((i + 1) % pts.length);
            co.updateValue(crit);
        }
        // Knoten nach minimalem Wert sortieren
        Collections.sort(colist);
        // Reduktion bis maximaler Fehler erreicht ist
        double length0 = pg.getLength(), dLengthTot = 0.;
        size = pts.length;
        while (true) {
            // Knoten mit minimalem Wert und Erhaltung der Simplizitaet bestimmen
            PolygonCornerValue co = null;
            for (int i = 0; i < size && co == null; i++) {
                PolygonCornerValue cotmp = colist.get(i);
                if (this.polygonIsSimple(colist, cotmp))
                    co = cotmp;
            }
            // Falls es keinen solchen Knoten gibt ist die Reduktion beendet
            if (co == null) break;
            // Aenderung des Laengenfehlers bestimmen
            double dLength = LCRIT.getValue(co.co0.p, co.p, co.co1.p);
            // Falls der Laengenfehler ueberschritten wird ist die Reduktion beendet
            if (((dLengthTot + dLength) / length0) * 100. > e) break;
            // Aenderung des Laengenfehlers speichern
            dLengthTot += dLength;
            // Polygon um Knoten reduzieren
            colist.remove(co);
            size--;
            // Nachbarn aktualisieren
            co.co0.co1 = co.co1;
            co.co0.updateValue(crit);
            co.co1.co0 = co.co0;
            co.co1.updateValue(crit);
            // Falls nur noch 2 Knoten vorhanden sind ist die Reduktion beendet
            if (size == 3) break;
            // Nachbarn neu einsortieren
            Collections.sort(colist);
        }
        // Knoten in Reihenfolge bringen
        Point2d[] pts_red = new Point2d[size];
        PolygonCornerValue co = colist.get(0);
        for (int i = 0; i < size; i++) {
            pts_red[i] = co.p;
            co = co.co1;
        }
        
        return new Polygon2d(pts_red);
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion einer polygonalen Kurve um die Knoten,
     * die nach einer Koordinatentransformation verdeckt sind.
     * @param cu polygonale Kurve.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return polygonale Kurve.
     */
    public PolygonalCurve2d reduceNonVisible(PolygonalCurve2d cu, double k) {
        if (cu == null) return null;
        Point2d[] pts_red = this.reduceNonVisible(cu.getPoints(), k);
        
        if (pts_red.length < 2) return null;
        return new PolygonalCurve2d(pts_red);
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion eines Polygons um die Knoten,
     * die nach einer Koordinatentransformation verdeckt sind.
     * @param pg Polygon.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return Polygon.
     */
    public Polygon2d reduceNonVisible(Polygon2d pg, double k) {
        if (pg == null) return null;
        Point2d[] pts_red = this.reduceNonVisible(pg.getPoints(), k);
        
        if (pts_red.length < 3) return null;
        return new Polygon2d(pts_red);
    }
//-------------------------------------------------------------------------------------------------------
    private Point2d[] reduceNonVisible(Point2d[] pts, double k) {
        // Mindestkantenlaenge minL in Abhaengigkeit des Skalierungsfaktors bestimmen
        double minL = 1. / k; //1. / (Math.sqrt(2.) * k);
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        Point2d LastPoint = pts[0];
        plist.add(LastPoint);
        // Nur Knoten zulassen, die groesser gleich minL zum letzen Knoten entfernt sind
        for (int i = 1; i < pts.length; i++)
            if (pts[i].distance(LastPoint) > minL) {
            LastPoint = pts[i];
            plist.add(LastPoint);
            }
        
        return plist.toArray(new Point2d[plist.size()]);
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion einer polygonalen Kurve um die Knoten,
     * die ausserhalb einer Bounding Box liegen:
     * Alle Kanten, welche die Bounding Box schneiden,
     * gehoeren zum sichbaren Teil der Kurve.
     * @param cu polygonale Kurve.
     * @param xmin minimale x-Koordinate einer Bounding Box.
     * @param xmax maximale x-Koordinate einer Bounding Box.
     * @param ymin minimale y-Koordinate einer Bounding Box.
     * @param ymax maximale y-Koordinate einer Bounding Box.
     * @return Liste mit polygonalen Kurven.
     */
    public ArrayList<PolygonalCurve2d> reduceNonVisible(PolygonalCurve2d cu, double xmin, double xmax, double ymin, double ymax) {
        if (cu == null) return null;
        
        Point2d[] pts = cu.getPoints();
        ArrayList<PolygonalCurve2d> culist = new ArrayList<PolygonalCurve2d>();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        boolean inside = false;
        // Erste Kante auf Ueberschneidung mit der Bounding Box pruefen
        if (this.intersects(pts[0], pts[1], xmin, xmax, ymin, ymax)) {
            plist.add(pts[0]);
            plist.add(pts[1]);
            inside = true;
        }
        // Uebrige Kanten auf Ueberschneidung mit der Bounding Box pruefen
        for (int i = 1; i < pts.length - 1; i++) {
            if (this.intersects(pts[i], pts[i + 1], xmin, xmax, ymin, ymax)) {
                if (!inside) { plist.add(pts[i]); inside = true; }
                plist.add(pts[i + 1]);
            } else if (inside) {
                // Falls die Kurve nicht sichbar wird: Letzten sichtbaren Abschnitt speichern
                culist.add(new PolygonalCurve2d(plist.toArray(new Point2d[plist.size()])));
                plist.clear();
                inside = false;
            }
        }
        // Falls die Kurve noch sichtbar ist: Letzten sichtbaren Abschnitt speichern
        if (inside) culist.add(new PolygonalCurve2d(plist.toArray(new Point2d[plist.size()])));
        
        return culist;
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion eines Polygons um die Knoten,
     * die ausserhalb einer Bounding Box liegen:
     * Alle Kanten, welche die Bounding Box schneiden,
     * gehoeren zum sichbaren Teil des Polygons.
     * @param pg Polygon.
     * @param xmin minimale x-Koordinate einer Bounding Box.
     * @param xmax maximale x-Koordinate einer Bounding Box.
     * @param ymin minimale y-Koordinate einer Bounding Box.
     * @param ymax maximale y-Koordinate einer Bounding Box.
     * @return Liste mit polygonalen Kurven und/oder Polygonen.
     */
    public ArrayList reduceNonVisible(Polygon2d pg, double xmin, double xmax, double ymin, double ymax) {
        if (pg == null) return null;
        
        Point2d[] pts = pg.getPoints();
        ArrayList pglist = new ArrayList();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        boolean inside = false;
        // Erste Kante auf Ueberschneidung mit der Bounding Box pruefen
        if (this.intersects(pts[0], pts[1], xmin, xmax, ymin, ymax)) {
            plist.add(pts[0]);
            plist.add(pts[1]);
            inside = true;
        }
        // Uebrige Kanten auf Ueberschneidung mit der Bounding Box pruefen
        for (int i = 1; i < pts.length; i++) {
            if (this.intersects(pts[i], pts[(i + 1) % pts.length], xmin, xmax, ymin, ymax)) {
                if (!inside) { plist.add(pts[i]); inside = true; }
                plist.add(pts[(i + 1) % pts.length]);
            } else if (inside) {
                // Falls die Kurve nicht sichbar wird: Letzten sichtbaren Abschnitt speichern
                pglist.add(new PolygonalCurve2d(plist.toArray(new Point2d[plist.size()])));
                plist.clear();
                inside = false;
            }
        }
        // Falls die Kurve noch sichtbar ist: Letzten sichtbaren Abschnitt speichern
        if (inside) {
            if (pglist.size() == 0) pglist.add(pg);
            else pglist.add(new PolygonalCurve2d(plist.toArray(new Point2d[plist.size()])));
            // Ggf. ersten mit dem letzten Abschnitt kombinieren
        }
        
        return pglist;
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    private boolean curveIsSimple(ArrayList<Point2d> plist, Point2d A, Point2d C) {
        for (int i = 0; i < size - 1; i++) {
            Point2d P0 = plist.get(i), P1 = plist.get(i + 1);
            if (P1 != A && P0 != A && P1 != C && P0 != C)
                if (this.intersects(A, C, P0, P1))
                    return false;
        } return true;
    }
//-------------------------------------------------------------------------------------------------------
    private boolean polygonIsSimple(ArrayList<Point2d> plist, Point2d A, Point2d C) {
        for (int i = 0; i < size; i++) {
            Point2d P0 = plist.get(i), P1 = plist.get((i + 1) % size);
            if (P1 != A && P0 != A && P1 != C && P0 != C)
                if (this.intersects(A, C, P0, P1))
                    return false;
        } return true;
    }
    //-------------------------------------------------------------------------------------------------------
    private boolean curveIsSimple(ArrayList<PolygonCornerValue> colist, PolygonCornerValue co) {
        PolygonCornerValue co0 = colist.get(size - 2);
        for (int i = 0; i < size - 1; i++) {
            if (co0.co1.p != co.co0.p && co0.p != co.co0.p && co0.co1.p != co.co1.p && co0.p != co.co1.p)
                if (this.intersects(co.co0.p, co.co1.p, co0.p, co0.co1.p))
                    return false;
            co0 = co0.co1;
        } return true;
    }
//-------------------------------------------------------------------------------------------------------
    private boolean polygonIsSimple(ArrayList<PolygonCornerValue> colist, PolygonCornerValue co) {
        PolygonCornerValue co0 = colist.get(0);
        for (int i = 0; i < size; i++) {
            if (co0.co1.p != co.co0.p && co0.p != co.co0.p && co0.co1.p != co.co1.p && co0.p != co.co1.p)
                if (this.intersects(co.co0.p, co.co1.p, co0.p, co0.co1.p))
                    return false;
            co0 = co0.co1;
        } return true;
    }
//-------------------------------------------------------------------------------------------------------
    private boolean intersects(Point2d P00, Point2d P01, Point2d P10, Point2d P11) {
        if (P00 == null || P01 == null || P10 == null || P11 == null) return false;
        double[] param = this.getDirectionParamofCutPoint(P00, P01, P10, P11);
        double s = param[0], t = param[1];
        if (!(s >= 0. && s <= 1.)) return false;
        if (!(t >= 0. && t <= 1.)) return false;
        return true;
    }
//-------------------------------------------------------------------------------------------------------
    private double[] getDirectionParamofCutPoint(Point2d P00, Point2d P01, Point2d P10, Point2d P11) {
        double s = (P00.x * (P10.y - P11.y) + P10.x * (P11.y - P00.y)
        + P11.x * (P00.y - P10.y)) / (P00.x * (P10.y - P11.y)
        + P01.x * (P11.y - P10.y) + P10.x * (P01.y - P00.y)
        + P11.x * (P00.y - P01.y));
        double t = (P00.x * (P01.y - P10.y) + P01.x * (P10.y - P00.y)
        + P10.x * (P00.y - P01.y)) / (P00.x * (P11.y - P10.y)
        + P01.x * (P10.y - P11.y) + P10.x * (P00.y - P01.y)
        + P11.x * (P01.y - P00.y));
        return new double[] {s, t};
    }
//-------------------------------------------------------------------------------------------------------
    private boolean intersects(Point2d P0, Point2d P1, double xmin, double xmax, double ymin, double ymax) {
        double xminEdge, xmaxEdge, yminEdge, ymaxEdge;
        if (P1.x > P0.x) { xminEdge = P0.x; xmaxEdge = P1.x; } else { xminEdge = P1.x; xmaxEdge = P0.x; }
        if (P1.y > P0.y) { yminEdge = P0.y; ymaxEdge = P1.y; } else { yminEdge = P1.y; ymaxEdge = P0.y; }
        if (xmaxEdge <= xmin || xminEdge >= xmax || ymaxEdge <= ymin || yminEdge >= ymax) return false;
        return true;
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Erzeugung einer feineren Approximation einer
     * polygonalen Kurve nach Kriterium.
     * @param cu polygonale Kurve.
     * @param crit Kriterium zum Verfeinern eines polygonalen Objektes.
     * @return polygonale Kurve.
     */
    public PolygonalCurve2d refine(PolygonalCurve2d cu, PolygonRefineCriterion crit) {
        if (cu == null || crit == null) return null;
        
        Point2d[] pts = cu.getPoints();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        
        for (int i = 0; i < pts.length; i++) {
            Point2d[] pts_tmp;
            // Fallunterscheidung
            if (i == 0 || i == pts.length - 2) pts_tmp = crit.getNextPoints(null, pts[i], pts[i + 1], null);
            else if (i == pts.length - 1) pts_tmp = crit.getNextPoints(pts[i - 1], pts[i], null, null);
            else pts_tmp = crit.getNextPoints(pts[i - 1], pts[i], pts[i + 1], pts[i + 2]);
            for (int j = 0; j < pts_tmp.length; j++)
                plist.add(pts_tmp[j]);
        }
        
        return new PolygonalCurve2d(plist.toArray(new Point2d[plist.size()]));
    }
//-------------------------------------------------------------------------------------------------------
    /**
     * Erzeugung einer feineren Approximation eines
     * Polygons nach Kriterium.
     * @param pg Polygon.
     * @param crit Kriterium zum Verfeinern eines polygonalen Objektes.
     * @return Polygon.
     */
    public Polygon2d refine(Polygon2d pg, PolygonRefineCriterion crit) {
        if (pg == null || crit == null) return null;
        
        Point2d[] pts = pg.getPoints();
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        
        for (int i = 0; i < pts.length; i++) {
            Point2d[] pts_tmp;
            // Fallunterscheidung
            if (i == 0) pts_tmp = crit.getNextPoints(pts[pts.length - 1], pts[i], pts[i + 1], pts[i + 2]);
            else if (i == pts.length - 2) pts_tmp = crit.getNextPoints(pts[i - 1], pts[i], pts[i + 1], pts[0]);
            else if (i == pts.length - 1) pts_tmp = crit.getNextPoints(pts[i - 1], pts[i], pts[0], pts[1]);
            else pts_tmp = crit.getNextPoints(pts[i - 1], pts[i], pts[i + 1], pts[i + 2]);
            for (int j = 0; j < pts_tmp.length; j++)
                plist.add(pts_tmp[j]);
        }
        
        return new Polygon2d(plist.toArray(new Point2d[plist.size()]));
    }
}