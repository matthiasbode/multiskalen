package bijava.geometry.dim2;

import bijava.geometry.FalseSpaceDimensionException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;

import javax.swing.JPanel;

/**
 *
 * @author Peter
 */
public class AlgGeometry2d {

    /** Creates a new instance of AlgGeometry2d */
    private AlgGeometry2d() {
    }

    public static Point2d getSchnittpunkt(Point2d p0, Point2d p1, Point2d p2, Point2d p3) {	// allgemeine Schnittpunktsberechnung
        if ((p1.x == p0.x) && (p3.x == p2.x)) // beide Geraden senkrecht
        {
            return null;
        }
        double m1 = (p1.y - p0.y) / (p1.x - p0.x);
        double m2 = (p3.y - p2.y) / (p3.x - p2.x);
        if (m1 == m2) // Geraden parallel
        {
            return null;
        }
        Point2d p = new Point2d();
        if (p1.x == p0.x) //nur erste Gerade senkrecht
        {
            p.x = p0.x;
            p.y = m2 * (p.x - p2.x) + p2.y;
            return p;
        }
        if (p3.x == p2.x) //nur zweite Gerade senkrecht
        {
            p.x = p2.x;
            p.y = m1 * (p.x - p0.x) + p0.y;
            return p;
        }
        p.x = (m1 * p0.x - m2 * p2.x - p0.y + p2.y) / (m1 - m2);
        p.y = m1 * (p.x - p0.x) + p0.y;
        return p;
    }

    /**
     * Computes the incentre of a triangle.
     * The inCentre of a triangle is the point which is equidistant
     * from the sides of the triangle.
     * It is also the point at which the bisectors
     * of the triangle's angles meet.
     * It is the centre of the incircle, which
     * is the unique circle that is tangent to each of the triangle's three sides.
     *
     * @param a a vertx of the triangle
     * @param b a vertx of the triangle
     * @param c a vertx of the triangle
     * @return the point which is the incentre of the triangle
     */
    public static Point2d inCentre(Point2d a, Point2d b, Point2d c) {
        // the lengths of the sides, labelled by their opposite vertex
        double len0 = b.distance(c);
        double len1 = a.distance(c);
        double len2 = a.distance(b);
        double circum = len0 + len1 + len2;

        double inCentreX = (len0 * a.x + len1 * b.x + len2 * c.x) / circum;
        double inCentreY = (len0 * a.y + len1 * b.y + len2 * c.y) / circum;
        return new Point2d(inCentreX, inCentreY);
    }

    public static double getArea(Point2d p0, Point2d p1, Point2d p2) {
        return Math.abs(0.5 * ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y))); // Kai 23.01.08 durch umsortieren
    }

    public static ConvexPolygon2d getConvexHull2d_QuickHull(Point2d[] points) {
        ConvexHull2d ch = new ConvexHull2d();
        ch.points = points;
        ch.quickHull();
        return ch.getHullPolygon();
    }

    public static Point2d[] getConvexHull2d_QuickHull_points(Point2d[] points) {
        ConvexHull2d ch = new ConvexHull2d();
        ch.points = points;
        ch.quickHull();
        return ch.getHullPoints();
    }

    public static ConvexPolygon2d getConvexHull2d_JarvisMarch(Point2d[] points) {
        ConvexHull2d ch = new ConvexHull2d();
        ch.points = points;
        ch.jarvisMarch();
        return ch.getHullPolygon();
    }

    public static ConvexPolygon2d getConvexHull2d_GrahamScan(Collection<? extends Point2d> points) {
        return ConvexHullGrahamScan.getConvexHull(points);
    }

    public static ConvexPolygon2d getConvexHull2d_GrahamScan(Point2d[] points) {
        return ConvexHullGrahamScan.getConvexHull(points);
    }

    public static Point2d[] getHullPoints2d_GrahamScan(Point2d[] points) {
        return ConvexHullGrahamScan.grahamScan(points);
    }
    /** 
     * Berechnet eine Kontur der uebergebenen Punktmenge.
     * Bei Bedarf: Es existiert eine multi-threaded Variante dieser Methode.
     * @author kaapke
     * Sweep von links und rechts, Ausduennung, Zusammensetzen, Aufblasen
     */
    private static Vector2d v_old;

    public static SimplePolygon2d getContour(Collection<? extends Point2d> col) throws FalseSpaceDimensionException {

        List<Point2d> list = new ArrayList<Point2d>(col);

        // Sortiere alle Punkte nach der x-Koordinate
        Collections.sort(list, new Comparator<Point2d>() {

            public int compare(Point2d p1, Point2d p2) {
                if (p1.x < p2.x) {
                    return -1;
                } else if (p1.x > p2.x) {
                    return 1;
                } else {
                    if (p1.y < p2.y) {
                        return -1;
                    } else if (p1.y > p2.y) {
                        return 1;
                    }
                }
                return 0;
            }
        });

        // Laufe von Links gegen die Punkte
        Point2d minY = list.get(0), pE = list.get(list.size() - 1), pW = minY, maxY = minY;

        List<Point2d> northWest = new ArrayList<Point2d>();
        List<Point2d> southWest = new ArrayList<Point2d>();

        for (Point2d p : list) {
            if (p.y > maxY.y) {
                northWest.add(p);
                maxY = p;
            }
            if (p.y < minY.y) {
                southWest.add(p);
                minY = p;
            }
        }

        DouglasPeuckerSimplification dps = new DouglasPeuckerSimplification(northWest);
        double tol = dps.guessTolerance();
        northWest = dps.computeSimplifiedPolygon(tol);

        dps = new DouglasPeuckerSimplification(southWest);
        tol = dps.guessTolerance();
        southWest = dps.computeSimplifiedPolygon(tol);

        //  Laufe von Rechts gegen die Punkte
        List<Point2d> northEast = new ArrayList<Point2d>();
        List<Point2d> southEast = new ArrayList<Point2d>();
        minY = maxY = list.get(list.size() - 1);
        for (int j = list.size() - 2; j >= 0; j--) {
            Point2d p = list.get(j);
            if (p.y > maxY.y) {
                northEast.add(p);
                maxY = p;
            }
            if (p.y < minY.y) {
                southEast.add(p);
                minY = p;
            }
        }

        // Falls keine Ausdï¿½nnung gewï¿½nscht wird,
        // folgende acht Codezeilen sowie den u.a. blowUp-Aufruf auskommentieren.
        double maxTol;
        dps = new DouglasPeuckerSimplification(northEast);
        maxTol = tol = dps.guessTolerance();
        northEast = dps.computeSimplifiedPolygon(tol);

        dps = new DouglasPeuckerSimplification(southEast);
        tol = dps.guessTolerance();
        if (tol > maxTol) {
            maxTol = tol;
        }
        southEast = dps.computeSimplifiedPolygon(tol);

        // Zusammensetzen
        List<Point2d> result = new ArrayList<Point2d>();
        result.add(pW);
        result.addAll(southWest);
        for (int i = southEast.size() - 1; i >= 0; i--) {  // Der letzte Punkt in South East ist pSouth. Der wurde schon mit South West ins Ergebnis genommen.
            result.add(southEast.get(i));
        }
        result.add(pE);
        result.addAll(northEast);
        for (int i = northWest.size() - 1; i >= 0; i--) {  // bzgl. i > 0 (anstatt > =0) gilt o.g. analog.
            result.add(northWest.get(i));
        }
        result.add(pW);
        SimplePolygon2d resPoly = new SimplePolygon2d(result.toArray(new Point2d[result.size()]));
        return blowUpPolygon2d(resPoly, maxTol);
    }

    /** 
     * ToDo: Richtungsï¿½nderung der Verschiebung bei Nicht-Konvexitï¿½t
     * Winkel-treu bei konvexen Polygonen, sonst nicht winkel-treu, niemals lï¿½ngen-treu. 
     * @param poly
     * @param e
     * @return
     */
    public static SimplePolygon2d blowUpPolygon2d(SimplePolygon2d poly, double e) {

        Point2d[] points = poly.getPoints();
        int n = points.length;
        Point2d[] tmp = new Point2d[n];

        Point2d p0 = getShiftedPoint(points[n - 2], points[0], points[1], e);
        tmp[0] = p0;
        for (int i = 1; i < n - 1; i++) {
            tmp[i] = getShiftedPoint(points[((i - 1) % n)], points[(i % n)], points[((i + 1) % n)], e);
        }
        tmp[n - 1] = p0;
        return new SimplePolygon2d(tmp);
    }

    /**
     * Corner p2 and two edges p1p2 und p3p2
     * @param e
     * @return
     */
    private static Point2d getShiftedPoint(Point2d p1, Point2d p2, Point2d p3, double e) {

        Vector2d v1 = new Vector2d(p1.x - p2.x, p1.y - p2.y);
        v1.normalize();
        Vector2d v2 = new Vector2d(p3.x - p2.x, p3.y - p2.y);
        v2.normalize();
        Vector2d res = v1.add(v2);
        res.normalize();

        // ï¿½berprï¿½fe die Orientierung, um bei nicht-konvexen Segmenten, 
        // in Richtung der Winkelhalbierenden aufzublï¿½hen, sonst engegebengesetzt. 
        double norm = ((p2.x - p1.x) * (p3.y - p2.y) - (p2.y - p1.y) * (p3.x - p2.x));
        if (norm > 0.) {
            res.scale(-1. * e);
            v_old = res;
        } else if (norm < 0.) {
            res.scale(e);
            v_old = res;
        } else {
            res = v_old;
        }
        Point2d p = new Point2d(p2.x + v1.x, p2.y + v1.y);
        return p;
    }

    /**
     * @see http://geometryalgorithms.com/Archive/algorithm_0205/algorithm_0205.htm
     * @author kaapke - used for getContour
     */
    static class DouglasPeuckerSimplification {

        private List<Point2d> poly;

        public DouglasPeuckerSimplification(List<Point2d> poly) {
            this.poly = poly;
        }

        public List<Point2d> computeSimplifiedPolygon(double tolerance) throws FalseSpaceDimensionException {

            if (poly.size() == 0) {
                return poly;
            }

            boolean[] marker = new boolean[poly.size()];

            // Initially mark the end points
            marker[0] = true;
            marker[poly.size() - 1] = true;

            // Recursively simplify the polygon
            simplifyDP(tolerance, poly.toArray(new Point2d[poly.size()]), 0, poly.size() - 1, marker);

            // Copy Marked vertices to the output simplified polyline
            List<Point2d> result = new ArrayList<Point2d>();
            for (int i = 0; i < marker.length; i++) {
                if (marker[i]) {
                    result.add(poly.get(i));
                }
            }
            return result;
        }

        /**
         * This is the Douglas-Peucker recursive simplification routine
         * It just marks vertices that are part of the simplified polyline
         * for approximating the polyline subchain v[j] to v[k].
         * Input:  tol = approximation tolerance
         *         v[] = polyline array of vertex points
         *         j,k = indices for the subchain v[j] to v[k]
         * Output: mk[] = array of markers matching vertex array v[]
         * @param tol
         */
        private double simplifyDP(double tol, Point2d[] v, int j, int k, boolean[] marker) throws FalseSpaceDimensionException {

            if (k <= j + 1) {
                return 0.0;
            }                        // there is nothing to simplify                        

            // check for adequate approximation by segment S from v[j] to v[k]
            int maxi = j;                // index of vertex farthest from S
            double maxd2 = 0;                // distance squared of farthest vertex
            double tol2 = tol * tol;        // tolerance squared

            Point2d p0 = v[j];                      // segment from v[j] to v[k]
            Point2d p1 = v[k];
            Point2d u = p1.sub(p0);                 // segment direction vector
            double cu = u.scalarProduct(u);        // segment length squared

            // test each vertex v[i] for max distance from S
            Point2d w;                              // vector from segment point to v[i]          
            double cw, dv2;                       // dv2 = distance v[i] to S squared

            for (int i = j + 1; i < k; i++) {
                // compute distance squared
                w = v[i].sub(p0);
                cw = w.scalarProduct(u);
                if (cw <= 0) {                                  // P lies to the left of the segment            
                    Point2d d = v[i].sub(p0);
                    dv2 = d.scalarProduct(d);
                } else if (cu <= cw) {                  // P lies to the right of the segment                           
                    Point2d d = v[i].sub(p1);
                    dv2 = d.scalarProduct(d);
                } else {                                                // P lies inside the segment
                    Point2d d = v[i].sub(p0);
                    dv2 = d.scalarProduct(d);                         // squared distance of v[i] and p0
                    cw /= u.norm();                         // length of the projection of d onto w
                    dv2 = dv2 - cw * cw;                  // Pythagoras                                                       
                }
                // test with current max distance squared
                if (dv2 > maxd2) {
                    // v[i] is a new max vertex
                    maxi = i;
                    maxd2 = dv2;
                }
            }
            if (maxd2 > tol2) {    // error is worse than the tolerance     

                // split the polyline at the farthest vertex from S
                marker[maxi] = true;                    // mark v[maxi] for the simplified polyline

                // recursively simplify the two subpolylines at v[maxi]
                simplifyDP(tol, v, j, maxi, marker);  // polyline v[j] to v[maxi]
                simplifyDP(tol, v, maxi, k, marker);  // polyline v[maxi] to v[k]
            }
            // else the approximation is OK, so ignore intermediate vertices
            return maxd2;
        }

        public double guessTolerance() throws FalseSpaceDimensionException {
            return 0.20 * Math.sqrt(simplifyDP(Double.POSITIVE_INFINITY, poly.toArray(new Point2d[poly.size()]), 0, poly.size() - 1, null));
        }
    }

    /**
     * Berechnet die konvexe Hï¿½lle (Polygon2d) aus einer ï¿½bergebenen Punktwolke mittels Graham-Algorithmus.
     * 
     * @author kaapke, ver. 0.1, Jun 06
     */
    static class ConvexHullGrahamScan {

        /** 
         * Computes the convex hull of the given points.
         * @param pointCloud - array of points, remains unmodified
         * @return Polygon2d - convex hull
         */
        public static ConvexPolygon2d getConvexHull(Point2d[] pointCloud) {
            if (pointCloud.length < 3) {
                throw new IllegalArgumentException("A polygon needs at least three non-collinear points.");
            }
            Point2d[] pp = grahamScan(pointCloud);

            if (pp.length == 2) {
                System.out.println("ConvexHullGrahamScan.getConvexHull: Die uebergebene Punktmenge ist colinear.");
                Point2d[] pp2 = new Point2d[3];
                pp2[0] = pp[0];
                pp2[1] = pp[1];
                pp2[2] = new Point2d(pp[0].x + 1.0, pp[0].y + 1.0);
                pp = pp2;
            }

            return new ConvexPolygon2d(pp);
        }

        /** 
         * Computes the convex hull of the given points.
         * @param pointCloud - Collection of points, remains unmodified
         * @return Polygon2d - convex hull
         */
        public static ConvexPolygon2d getConvexHull(Collection<? extends Point2d> coll) {

            Point2d[] array = coll.toArray(new Point2d[coll.size()]);
            System.out.println(array[0]);
            return getConvexHull(coll.toArray(new Point2d[coll.size()]));
        }

        /**
         * Computes the convex hull of a point set according to the GRAHAM SCAN algorithm.
         * Laufzeit: P4m 2GHz: 0.2 sec. fuer 100.000 Punkte, TODO Parallelisierung moeglich?
         * 
         * @param Point2d[]
         * @return Polygon2d
         */
        public static Point2d[] grahamScan(Point2d[] points) {

            int p0_index = rightmost_lowest(points);
            Point2d p0 = points[p0_index];

            // Copy all points into a new array without p0.
            Point2d[] radialOrderedPoints = new Point2d[points.length - 1];
            System.arraycopy(points, 0, radialOrderedPoints, 0, p0_index);
            System.arraycopy(points, p0_index + 1, radialOrderedPoints, p0_index, points.length - p0_index - 1);

            // Radial sort those points
            Arrays.sort(radialOrderedPoints, new RadialComparator(p0));

            // Identify hull points
            Stack<Point2d> stack = getHullPoints(p0, radialOrderedPoints);

            return stack.toArray(new Point2d[stack.size()]);
        }

        private static Stack<Point2d> getHullPoints(Point2d p0, Point2d[] radialOrderedPoints) {

            // Identify the hull points
            Stack<Point2d> stack = new Stack<Point2d>();
            stack.push(p0);
            stack.push(radialOrderedPoints[0]);

            int i = 1;
            while (i < radialOrderedPoints.length) {

                double isLeftResult = isLeft(stack.get(stack.size() - 2), stack.peek(), radialOrderedPoints[i]);

                if (isLeftResult > 0.0) {
                    stack.push(radialOrderedPoints[i]);
                    i++;
                } else if (isLeftResult == 0.0) {
                    i++;
                } else {
                    stack.pop();
                }
            }
            return stack;
        }

        /**
         * Vergleicht zwei Punkte auf ihren Winkel zwischen einer
         * Verbindungslinie zu einem Referenzpunkt und der x-Achse.
         */
        static class RadialComparator implements Comparator<Point2d> {

            protected Point2d p0; // reference point
            private double result; // helper

            public RadialComparator(Point2d ref) {
                this.p0 = ref;
            }

            public int compare(Point2d p1, Point2d p2) {

                result = (p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y);

                if (result > 0.0) {
                    return -1;
                } else if (result == 0.0) {
                    return (p1.distance(p0) < p2.distance(p0) ? 1 : -1);
                } else {
                    return 1;
                }
            }
        }

        /**
         * Tests if a point is Left|On|Right of an infinite line
         * Input: three points P0, P1, and P2
         * @return: 
         *   >0 for P2 left of the line through P0 and P1
         *   =0 for P2 on the line
         *   <0 for P2 right of the line
         */
        private static double isLeft(Point2d p0, Point2d p1, Point2d p2) {

            return (p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y);
        }

        /**
         * Ermittelt in dem uebergebenen Punktefeld den Index des unter 
         * allen untersten Punkten am weitesten rechts liegenden.
         * 
         * @param points
         * @return int
         */
        private static int rightmost_lowest(Point2d[] points) {

            // look for the rightmost lowest point
            int index = 0;

            for (int i = 1; i < points.length; i++) {

                if (points[i].y < points[index].y) {
                    index = i;

                } else if (points[i].y == points[index].y && points[i].x > points[index].x) {
                    index = i;
                }
            }
            return index;
        }

        static class TestPanel extends JPanel {

            private static final long serialVersionUID = 1L;
            private TreeSet<Point2d> points;
            private Point2d[] test;

            public TestPanel() {

                points = new TreeSet<Point2d>(new Point2dComparator());

                this.addMouseListener(new MouseAdapter() {

                    public void mouseClicked(MouseEvent me) {
                        if (me.getButton() == MouseEvent.BUTTON1) {
                            points.add(new Point2d(me.getX(), me.getY()));
                            repaint();

                        } else if (me.getButton() == MouseEvent.BUTTON3) {
                            test = grahamScan(points.toArray(new Point2d[points.size()]));

                            //System.out.println(Arrays.toString(test));
                            repaint();
                        }
                    }
                });

                for (int i = 0; i < 100; i++) {
                    double x = Math.random() * 700.;
                    double y = Math.random() * 500.;
                    Point2d p = new Point2d(x, y);
                    points.add(p);

                }
                repaint();
            }

            public void paintComponent(Graphics g) {

                Graphics2D g2d = (Graphics2D) g;
                Rectangle2D.Double recPoint = new Rectangle2D.Double();
                for (Point2d p : points) {
                    recPoint.setRect(p.x, p.y, 2., 2.);
                    g2d.draw(recPoint);
                }

                if (test != null) {
                    g2d.setColor(Color.RED);
                    GeneralPath path = new GeneralPath();

                    path.moveTo((float) test[0].x, (float) test[0].y);
                    for (int i = 1; i < test.length; i++) {
                        path.lineTo((float) test[i].x, (float) test[i].y);
                    }
                    path.lineTo((float) test[0].x, (float) test[0].y);

                    g2d.draw(path);
                }
            }
        }

        static class Point2dComparator implements Comparator<Point2d>, Serializable {

            public Point2dComparator() {
            }
            ;

            public int compare(Point2d p1, Point2d p2) {

                if (p1.x < p2.x) {
                    return -1;
                } else if (p1.x > p2.x) {
                    return 1;
                } else {
                    if (p1.y < p2.y) {
                        return -1;
                    } else if (p1.y > p2.y) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }  // END ConvexHullGrahamScan
}

class ConvexHull2d {

    Point2d[] points; // zweidimensionale Punktemenge
    int h; // Anzahl Punkte der konvexen Huelle, Speicher-Indizies 0 - h

    public ConvexHull2d() {
    }

    /** Gibt die konvexe Huelle als Punktemenge zurueck
     */
    public Point2d[] getHullPoints() {
        Point2d[] hull = new Point2d[h];
        for (int i = 0; i < h; i++) {
            hull[i] = points[i];
        }
        return hull;
    }

    /** Gibt die konvexe Huelle als geschlossenes Polygon zurueck
     */
    public ConvexPolygon2d getHullPolygon() {
        return new ConvexPolygon2d(getHullPoints());
    }

    /** Implementiert den QuickHull-Algorithmus
     */
    public void quickHull() {
        double eps = 1e-3;
        exchange(0, indexOfLowestPoint());
        h++;
        Edge2d g = new Edge2d(points[0], new Point2d((points[0].x - eps), points[0].y));
        computeHullPoints(g, 1, points.length - 1);
    }

    // Tauscht zwei Punkte der Punktemenge
    private void exchange(int i, int j) {
        Point2d temp = points[i];
        points[i] = points[j];
        points[j] = temp;
    }

    // Sucht den Punkt mit der minimalen y-Koordinate
    private int indexOfLowestPoint() {
        int i, min = 0;
        for (i = 1; i < points.length; i++) {
            if (points[i].y < points[min].y || points[i].y == points[min].y && points[i].x < points[min].x) {
                min = i;
            }
        }
        return min;
    }

    private void computeHullPoints(Edge2d g, int lo, int hi) {
        if (lo > hi) {
            return;
        }
        int k = indexOfFurthestPoint(g, lo, hi);
        Edge2d g0 = new Edge2d(g.p0, points[k]);
        Edge2d g1 = new Edge2d(points[k], g.p1);
        exchange(k, hi);

        int i = partition(g0, lo, hi - 1);
        // alle Punkte von lo bis i - 1 liegen rechts von g0
        // alle Punkte von i bis hi - 1 liegen links von g0
        computeHullPoints(g0, lo, i - 1);

        // alle eben rekursiv erzeugten Punkte liegen
        // auf dem Huellpolygonzug vor p[hi]
        exchange(hi, i);
        exchange(i, h);
        h++;

        int j = partition(g1, i + 1, hi);
        // alle Punkte von i + 1 bis j - 1 liegen rechts von g1,
        // alle Punkte von j bis hi liegen im Inneren
        computeHullPoints(g1, i + 1, j - 1);
    }

    private int indexOfFurthestPoint(Edge2d g, int lo, int hi) {
        int i, h = lo;
        double d, mx = 0;
        for (i = lo; i <= hi; i++) {
            d = -cross(g.p0.sub(points[i]), g.p1.sub(points[i]));
            if (d > mx || d == mx && points[i].x > points[h].x) {
                mx = d;
                h = i;
            }
        }
        return h;
    }

    // Transformiert alle Punkte der Punktemenge relativ zum uebergebenen Punkt
    private void sub(Point2d p0) {
        Point2d p1 = new Point2d(p0); // notwendig, weil p0 in p[] sein kann
        for (int i = 0; i < points.length; i++) {
            points[i].sub(p1);
        }
    }

    private int partition(Edge2d g, int lo, int hi) {
        int i = lo, j = hi;
        while (i <= j) {
            while (i <= j && cross(g.p0.sub(points[i]), g.p1.sub(points[i])) < 0) {
                i++;
            }
            while (i <= j && cross(g.p0.sub(points[j]), g.p1.sub(points[j])) > 0) {
                j--;
            }
            if (i <= j) {
                exchange(i++, j--);
            }
        }
        return i;
    }

    /** Implementiert den JarvisMarch-Algorithmus
     */
    public void jarvisMarch() {
        int i = indexOfLowestPoint();
        do {
            exchange(h, i);
            i = indexOfRightmostPointFrom(points[h]);
            h++;
        } while (i > 0);
    }

    private int indexOfRightmostPointFrom(Point2d q) {
        int i = 0, j;
        Point2d u = new Point2d();
        for (j = 1; j < points.length; j++) {
            if (cross(points[j].sub(q), points[i].sub(q)) > 0 || cross(points[j].sub(q), points[i].sub(q)) == 0 && points[j].sub(q).distance(u) > points[i].sub(q).distance(u)) {
                i = j;
            }
        }
        return i;
    }

    // Kreuzprodukt zweier Punkte
    private double cross(Point2d p0, Point2d p1) {
        return p0.x * p1.y - p0.y * p1.x;
    }

    //==============================================================================//
    /** 
     *  Eine BoxTopologie dient der verwaltung groszer Mengen von <code>Point2d</code>-Objekten.
     *  Es werden Methoden zum effizienten Abfragen von Punktmengen die innerhalb einer
     *  gew&uuml;nscheten x,y-Region liegen zur VerfÂgung gestellt.
     *  @author Jan Stilhammer
     *  @version 1.2.0                                                               */
//==============================================================================//
    public class BoxTopologie {

        private static final int NEIGHB_EXCLUDE = 1;
        private static final int NEIGHB_INCLUDE = 2;
        private final boolean includeNeighbours = false;
        private ArrayList<Point2d> dataPoints;
        private ArrayList<Point2d>[][] boxes;
        private double dMinX = Double.POSITIVE_INFINITY,  dMaxX = Double.NEGATIVE_INFINITY,  dMinY = Double.POSITIVE_INFINITY,  dMaxY = Double.NEGATIVE_INFINITY;
        private double deltaX,  deltaY;
        private double anzDeltaX,  anzDeltaY;
        private int nodesPerBox;
        private int anzX,  anzY;

        //------------------------------------------------------------------------------//
        /**
         * Erzeugt eine neue Instanz von BoxTopologie.
         * @param data Die zu verwaltenden Punkte
         * @param nodesPerBox Anzahl der Punkte pro Zelle
         */
        //------------------------------------------------------------------------------//
        public BoxTopologie(Collection<Point2d> data, int nodesPerBox) {
            if (data instanceof ArrayList) {
                dataPoints = (ArrayList<Point2d>) data;
            } else {
                dataPoints = new ArrayList<Point2d>(data);
            }
            System.out.print("Initialisiere BoxTopologie...");
            this.nodesPerBox = nodesPerBox;
            for (Point2d p : data) {
                dMinX = Math.min(dMinX, p.x);
                dMaxX = Math.max(dMaxX, p.x);
                dMinY = Math.min(dMinY, p.y);
                dMaxY = Math.max(dMaxY, p.y);
            }
            System.out.println("Extrema bestimmt (" + data.size() + " Punkte)");
            initBoxes();
            System.out.println(" done");
        }

//	------------------------------------------------------------------------------//
        /**
         * Erzeugt eine neue Instanz von BoxTopologie. Sind die Extremalwerte der Punkte
         * bereits bekannt, ist dieser Konstruktor effizienter.
         * @param data Die zu verwaltenden Punkte
         * @param nodesPerBox Anzahl der Punkte pro Zelle
         * @param maxX Maximale x-Koordinate
         * @param minX Minimale x-Koordinate
         * @param maxY Maximale y-Koordinate
         * @param minY Minimale y-Koordinate
         * @param maxZ Maximale z-Koordinate
         * @param minZ Minimale z-Koordinate
         */
        //------------------------------------------------------------------------------//
        public BoxTopologie(Collection<Point2d> data, int nodesPerBox, double maxX, double minX, double maxY, double minY, double maxZ, double minZ) {
            if (data instanceof ArrayList) {
                dataPoints = (ArrayList<Point2d>) data;
            } else {
                dataPoints = new ArrayList<Point2d>(data);
            }
            dMaxX = maxX;
            dMinX = minX;
            dMaxY = maxY;
            dMinY = minY;
            this.nodesPerBox = nodesPerBox;

            initBoxes();
        }

        //------------------------------------------------------------------------------//
        /**
         * Initialisiert das Boxenraster und f&uuml;gt die Messpunkte hinzu.
         */
        //------------------------------------------------------------------------------//
        private void initBoxes() {
            anzX = anzY = (int) Math.sqrt(size() / nodesPerBox) + 1;
            boxes = new ArrayList[anzX + 1][anzY + 1];
//		boxes = (ArrayList<Integer>[][])(new Object[anzX+1][anzY+1]);
            deltaX = dMaxX - dMinX;
            deltaY = dMaxY - dMinY;
            anzDeltaX = deltaX / (double) anzX;
            anzDeltaY = deltaY / (double) anzY;
            System.out.println("anzX=" + anzX);
            System.out.println("deltaX=" + deltaX + ", deltaY=" + deltaY);
            System.out.println("anzDeltaX=" + anzDeltaX + ", anzDeltaY=" + anzDeltaY);


            int i, j;
            int index = -1;
            for (Point2d p : dataPoints) {
                index++;
//        	if((index)%100==0)System.out.println(index);
                double di = ((p.x - dMinX) / anzDeltaX);
                i = (int) di;
                di = di - i; // Wenn di<=0.5 dann linke, sonst rechte Haelfte
                double dj = ((p.y - dMinY) / anzDeltaY);
                j = (int) dj;
                dj = dj - j; // Wenn dj<=0.5 dann untere, sonst obere Haelfte
//            if((index++)%100==0)System.out.println("i="+1+"; j="+j);
                //einfuegen in die Box in die der Punkt direkt faellt
                if (boxes[i][j] == null) {
                    boxes[i][j] = new ArrayList<Point2d>();
                }
                if (!boxes[i][j].contains(p)) {
                    boxes[i][j].add(p);
                }
                if (includeNeighbours) {
                    //Einfuegen in angrenzende boxen
                    if (di < 0.5) {// In linke Box einsetzen
                        if (i - 1 >= 0) { // RandÂberschreitung pruefen
                            if (boxes[i - 1][j] == null) {
                                boxes[i - 1][j] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i - 1][j].contains(p)) {
                                boxes[i - 1][j].add(p);
                            }
                        }
                    }
                    if (di > 0.5) {// In rechte Box einsetzen
                        if (i + 1 <= anzX) { // RandÂberschreitung pruefen
                            if (boxes[i + 1][j] == null) {
                                boxes[i + 1][j] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i + 1][j].contains(p)) {
                                boxes[i + 1][j].add(p);
                            }
                        }
                    }
                    if (dj < 0.5) {// In untere Box einsetzen
                        if (j - 1 >= 0) { // RandÂberschreitung pruefen
                            if (boxes[i][j - 1] == null) {
                                boxes[i][j - 1] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i][j - 1].contains(p)) {
                                boxes[i][j - 1].add(p);
                            }
                        }
                    }
                    if (dj > 0.5) {// In obere Box einsetzen
                        if (j + 1 <= anzY) { // RandÂberschreitung pruefen
                            if (boxes[i][j + 1] == null) {
                                boxes[i][j + 1] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i][j + 1].contains(p)) {
                                boxes[i][j + 1].add(p);
                            }
                        }
                    }

                    if (di < 0.5 && dj > 0.5) {// In links-obere Box einsetzen
                        if (i - 1 >= 0 && j + 1 <= anzY) { // RandÂberschreitung pruefen
                            if (boxes[i - 1][j + 1] == null) {
                                boxes[i - 1][j + 1] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i - 1][j + 1].contains(p)) {
                                boxes[i - 1][j + 1].add(p);
                            }
                        }
                    }
                    if (di > 0.5 && dj > 0.5) {// In rechts-obere Box einsetzen
                        if (i + 1 <= anzX && j + 1 <= anzY) { // RandÂberschreitung pruefen
                            if (boxes[i + 1][j + 1] == null) {
                                boxes[i + 1][j + 1] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i + 1][j + 1].contains(p)) {
                                boxes[i + 1][j + 1].add(p);
                            }
                        }
                    }
                    if (dj < 0.5 && di < 0.5) {// In unten-linke Box einsetzen
                        if (j - 1 >= 0 && i - 1 >= 0) { // RandÂberschreitung pruefen
                            if (boxes[i - 1][j - 1] == null) {
                                boxes[i - 1][j - 1] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i - 1][j - 1].contains(p)) {
                                boxes[i - 1][j - 1].add(p);
                            }
                        }
                    }
                    if (dj < 0.5 && di > 0.5) {// In unten-rechte Box einsetzen
                        if (j - 1 >= 0 && i + 1 <= anzX) { // RandÂberschreitung pruefen
                            if (boxes[i + 1][j - 1] == null) {
                                boxes[i + 1][j - 1] = new ArrayList<Point2d>();
                            }
                            if (!boxes[i + 1][j - 1].contains(p)) {
                                boxes[i + 1][j - 1].add(p);
                            }
                        }
                    }
                }
                dMinX = Math.min(dMinX, p.x);
                dMaxX = Math.max(dMaxX, p.x);
                dMinY = Math.min(dMinY, p.y);
                dMaxY = Math.max(dMaxY, p.y);
            }

            System.out.println("BoxTopologie erzeugt:");
            System.out.println("\tanzahl:        " + dataPoints.size());
            System.out.println("\tminima(x,y): " + dMinX + ",  " + dMinY);
            System.out.println("\tmaxima(x,y): " + dMaxX + ",  " + dMaxY);

        }

        //------------------------------------------------------------------------------//
        /**
         * Liefert alle Punkte als <code>ArrayList<Point2d></code> zur&uuml;ck.
         * Die Reihenfolge der Punkte entspricht der des Iterators der im Konstruktor
         * &uuml;bergebenen
         * Collection.
         * @return Alle Datenpunkte
         */
        //------------------------------------------------------------------------------//
        public ArrayList<Point2d> getDataPoints() {
            return dataPoints;
        }

//	------------------------------------------------------------------------------//
        /** Liefert die Anzahl der Punkte.
         *  
         *  @return Anzahl aller Punkte                                             */
//	------------------------------------------------------------------------------//
        public int size() {
            return dataPoints.size();
        }

//	------------------------------------------------------------------------------//
        /** Liefert den Punkt an dem Index <code>i</code>.
         *  
         *  @param  i   Index des gew&uuml;nschten Messpunktes                          */
//	------------------------------------------------------------------------------//
        public Point2d getPoint(int i) {
            return dataPoints.get(i);
        }

        //------------------------------------------------------------------------------//
        /**
         * Liefert alle Punkte die innerhalb der BoundingBox <code>bounds</code> liegen.
         * @param bounds Begrenzung der geforderten Punkte
         * @return Liste aller Punkte innerhalb der BoundingBox
         */
        //------------------------------------------------------------------------------//
        public ArrayList<Point2d> getPoints(BoundingBox2d bounds) {
            ArrayList<Point2d> points = new ArrayList<Point2d>();
            int p0_i = (int) ((bounds.getP0().x - dMinX) / anzDeltaX);
            int p0_j = (int) ((bounds.getP0().y - dMinY) / anzDeltaY);
            int p2_i = (int) ((bounds.getP2().x - dMinX) / anzDeltaX);
            int p2_j = (int) ((bounds.getP2().y - dMinY) / anzDeltaY);
            for (int i = p0_i; i <= p2_i; i++) {
                for (int j = p0_j; j <= p2_j; j++) {
                    if (boxExists(i, j)) {
                        for (Point2d p : boxes[i][j]) {
                            if (!points.contains(p)) {
                                if (bounds.contains(p.x, p.y)) {
                                    points.add(p);
                                }
                            }
                        }
                    }
                }
            }
            return points;
        }

        //	------------------------------------------------------------------------------//
        /**
         * Liefert alle Punkte die innerhalb des Kreises <code>c</code> liegen.
         * @param c Begrenzung der geforderten Punkte
         * @return Liste aller Punkte innerhalb des Kreises
         */
        //------------------------------------------------------------------------------//
        public ArrayList<Point2d> getPoints(Circle2d c) {
            double lx = c.m.x - c.r, ux = c.m.x + c.r;
            double ly = c.m.y - c.r, uy = c.m.y + c.r;
            ArrayList<Point2d> points = new ArrayList<Point2d>();
            int p0_i = (int) ((lx - dMinX) / anzDeltaX);
            int p0_j = (int) ((ly - dMinY) / anzDeltaY);
            int p2_i = (int) ((ux - dMinX) / anzDeltaX);
            int p2_j = (int) ((uy - dMinY) / anzDeltaY);
            for (int i = p0_i; i <= p2_i; i++) {
                for (int j = p0_j; j <= p2_j; j++) {
                    if (boxExists(i, j)) {
                        for (Point2d pc : boxes[i][j]) {
                            if (!points.contains(pc)) {
                                if (c.contains(new Point2d(pc.x, pc.y))) {
                                    points.add(pc);
                                }
                            }
                        }
                    }
                }
            }
            return points;
        }

//	------------------------------------------------------------------------------//
        /**
         * Liefert alle Punkte die innerhalb des Kreises mit dem Mittelpunkt <code>p</code>
         * und dem Radius <code>r</code> liegen.
         * @param p Mittelpunkt des Begrenzungskreises
         * @param r Radius des Begrenzungskreises
         * @return Liste aller Punkte innerhalb des Kreises
         */
        //------------------------------------------------------------------------------//
        public ArrayList<Point2d> getPoints(Point2d p, double r) {
            return getPoints(new Circle2d(p, r));

        }

        //------------------------------------------------------------------------------//
        /**
         * &Uuml;perpr&uuml;ft ob <code>i</code> und <code>j</code> zul&auml;ssige
         * Parameter sind.
         * @param i Index i
         * @param j Index j
         * @return
         */
        //------------------------------------------------------------------------------//
        private boolean boxExists(int i, int j) {
            if (i < 0 || i >= boxes.length || j < 0 || j >= boxes[0].length) {
                return false;
            } else {
                return true;
            }

        }

        public double getMaxX() {
            return dMaxX;
        }

        public double getMaxY() {
            return dMaxY;
        }

        public double getMinX() {
            return dMinX;
        }

        public double getMinY() {
            return dMinY;
        }
    }
}
