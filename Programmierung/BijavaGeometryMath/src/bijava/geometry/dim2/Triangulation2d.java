package bijava.geometry.dim2;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Triangulation2d.java stellt Eigenschaften und Methoden fuer Triangulationen
 * im 2D zur Verfuegung.
 *
 * @author Leibniz Universtaet Hannover<br>
 * Institut fuer Bauinformatik<br>
 * Dr.-Ing. habil. Peter Milbradt<br>
 * Dipl.-Ing. Mario Hoecker
 * @version 1.2, Februar 2007
 */
public class Triangulation2d {

    ArrayList<Triangle2d> triangles; // Dreiecke
    Point2d[] pts; // Knoten

    Triangulation2d() {
    }

    /**
     * Erzeugt eine Triangulation im 2D. Die Knoten muessen zu den Dreiecken
     * gehoeren. Wird von TicadIO.java verwendet.
     *
     * @param tr Feld mit Dreiecken.
     * @param points Feld mit Knoten.
     */
    public Triangulation2d(Triangle2d[] tr, Point2d[] points) {
        triangles = new ArrayList<Triangle2d>(tr.length);
        for (int i = 0; i < tr.length; i++) {
            triangles.add(tr[i]);
        }
        pts = points;
    }

    /**
     * Erzeugt eine Triangulation im 2D. Trianguliert mit dem Bowyer Watson
     * Algorithmus.
     *
     * @param points Feld mit Knoten.
     */
    public Triangulation2d(Point2d[] points) {
        triangles = new ArrayList<Triangle2d>();
        pts = points;
        BowyerWatsonTriangulation();
    }

    /**
     * Liefert eine Triangulation im 2D. Trianguliert mit dem einfachen Delaunay
     * Algorithmus.
     *
     * @param points Feld mit Knoten.
     * @return Triangulation.
     */
    public static Triangulation2d simpleDelaunayTriangulation(Point2d[] points) {
        Triangulation2d t = new Triangulation2d();
        t.triangles = new ArrayList<Triangle2d>();
        t.pts = points;
        t.simpleDelaunayTriangulation();
        return t;
    }

    /**
     * Liefert eine Triangulation im 2D. Trianguliert mit dem Bowyer Watson
     * Algorithmus.
     *
     * @param points Feld mit Knoten.
     * @return Triangulation.
     */
    public static Triangulation2d BowyerWatsonTriangulation(Point2d[] points) {
        Triangulation2d t = new Triangulation2d();
        t.triangles = new ArrayList<Triangle2d>();
        t.pts = points;
        t.BowyerWatsonTriangulation();
        return t;
    }

    /**
     * Einfache Delaunay Triangulation n^4. Beruecksichtigt nicht den Fall '4
     * Knoten auf einem Umkreis'.
     */
    private void simpleDelaunayTriangulation() {
        for (int i = 0; i < pts.length - 2; i++) {
            for (int j = i + 1; j < pts.length - 1; j++) {
                for (int k = j + 1; k < pts.length; k++) {
                    try {
                        Triangle2d t = new Triangle2d(pts[i], pts[j], pts[k]);
                        boolean empty = true;
                        for (int m = 0; (m < pts.length) && empty; m++) {
                            empty = !t.inDelaunayCircle(pts[m]);
                        }
                        if (empty) {
                            triangles.add(t);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }
        System.out.println("Simple Delaunay Triangulation: " + triangles.size() + " Dreiecke");
    }

    /**
     * Bowyer Watson Triangulation.
     */
    private void BowyerWatsonTriangulation() {
        // Bounding Box berechnen
        double xmin = pts[0].x, ymin = pts[0].y;
        double xmax = xmin, ymax = ymin;
        for (int i = 1; i < pts.length; i++) {
            double x = pts[i].x, y = pts[i].y;
            if (x < xmin) {
                xmin = x;
            }
            if (x > xmax) {
                xmax = x;
            }
            if (y < ymin) {
                ymin = y;
            }
            if (y > ymax) {
                ymax = y;
            }
        }
        // Super Rectangle erzeugen
        double width = xmax - xmin, height = ymax - ymin;
        double ds;
        if (width > height) {
            ds = width;
        } else {
            ds = height;
        }
        ds *= 100.;
        double x0 = xmin - ds, y0 = ymin - ds, x1 = xmax + ds, y1 = ymax + ds;
        Point2d p0 = new Point2d(x0, y0);
        Point2d p1 = new Point2d(x1, y0);
        Point2d p2 = new Point2d(x1, y1);
        Point2d p3 = new Point2d(x0, y1);
        triangles.add(new Triangle2d(p0, p1, p3));
        triangles.add(new Triangle2d(p1, p2, p3));
        // Knoten zur Triangulation hinzufuegen
        for (int i = 0; i < pts.length; i++) {
            addPointInConfidenceRegion(pts[i]);
        }
        // Super Rectangle loeschen
        ArrayList<Triangle2d> triangles_next = new ArrayList<Triangle2d>();
        for (Triangle2d t : triangles) {
            boolean add = true;
            for (int i = 0; i < 3 && add; i++) {
                Point2d tp = t.getPointAt(i);
                if (tp == p0 || tp == p1 || tp == p2 || tp == p3) {
                    add = false;
                }
            }
            if (add) {
                triangles_next.add(t);
            }
        }
        triangles = triangles_next;
        System.out.println("Bowyer Watson Triangulation: " + triangles.size() + " Dreiecke");
    }

    /**
     * Liefert die Dreiecke dieser Triangulation als Feld.
     *
     * @return Dreiecke dieser Triangulation als Feld.
     */
    public Triangle2d[] getTriangles() {
        return triangles.toArray(new Triangle2d[triangles.size()]);
    }

    /**
     * Liefert die Knoten dieser Triangulation als Feld.
     *
     * @return Knoten dieser Triangulation als Feld.
     */
    public Point2d[] getNodes() {
        return pts;
    }

    /**
     * Fuegt einen Knoten hinzu. Knoten ausserhalb des Konfidenzbereiches werden
     * bislang nicht hinzugefuegt.
     *
     * @param p Knoten.
     * @return <code>true</code> falls der Knoten hinzugefuegt wurde.
     */
    public boolean addPoint(Point2d p) {
        if (p == null) {
            return false;
        }
        // Lage des Knoten ueberpruefen
        boolean p_inConfidenceRegion = false;
        for (Triangle2d t : triangles) {
            if (t.contains(p)) {
                p_inConfidenceRegion = true;
                break;
            }
        }
        if (!p_inConfidenceRegion) {
            return false;
        }
        // Knoten hinzufuegen
        Point2d[] ptsNext = new Point2d[pts.length + 1];
        for (int i = 0; i < pts.length; i++) {
            ptsNext[i] = pts[i];
        }
        ptsNext[pts.length] = p;
        pts = ptsNext;
        addPointInConfidenceRegion(p);
        return true;
    }

    private void addPointInConfidenceRegion(Point2d p) {
        // bestehen bleibende Dreiecke und Kanten des Insertion Polygons bestimmen
        ArrayList<Triangle2d> triangles_next = new ArrayList<Triangle2d>();
        ArrayList<Edge2d> edges = new ArrayList<Edge2d>();
        for (Triangle2d t : triangles) {
            if (t.inDelaunayCircle(p)) {
                for (int i = 0; i < 3; i++) {
                    Point2d tp0 = t.points[i];
                    Point2d tp1 = t.points[(i + 1) % 3];
                    boolean add = true;
                    for (Edge2d edge : edges) {
                        if (tp0 == edge.p1 && tp1 == edge.p0) {
                            edges.remove(edge);
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        edges.add(new Edge2d(tp0, tp1));
                    }
                }
            } else {
                triangles_next.add(t);
            }
        }
        // Insertion Polygon triangulieren
        for (Edge2d edge : edges) {

            try {
                triangles_next.add(new Triangle2d(p, edge.p0, edge.p1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        triangles = triangles_next;
    }

    /**
     * Liefert die Konfidenzregion dieser Triangulation.
     *
     * @return Konfidenzregion dieser Triangulation.
     */
    public PolygonalRegion2d getConfidenceRegion() {
        ArrayList<SimplePolygon2d> polygons = getConfidencePolygons();
        PolygonalRegion2d region = new PolygonalRegion2d(polygons.remove(0));
        for (SimplePolygon2d polygon : polygons) {
            region.add(polygon);
        }
        return region;
    }

    private ArrayList<SimplePolygon2d> getConfidencePolygons() {
        // Kanten der Konfidenzregion bestimmen
        ArrayList<Edge2d> edges = new ArrayList<Edge2d>();
        for (Triangle2d t : triangles) {
            for (int i = 0; i < 3; i++) {
                Point2d tp0 = t.points[i];
                Point2d tp1 = t.points[(i + 1) % 3];
                boolean add = true;
                for (Edge2d edge : edges) {
                    if (tp0 == edge.p1 && tp1 == edge.p0) {
                        edges.remove(edge);
                        add = false;
                        break;
                    }
                }
                if (add) {
                    edges.add(new Edge2d(tp0, tp1));
                }
            }
        }
        // Polygone erzeugen
        ArrayList<SimplePolygon2d> polygons = new ArrayList<SimplePolygon2d>();
        while (!edges.isEmpty()) {
            ArrayList<Point2d> points = new ArrayList<Point2d>();
            Edge2d edge0 = edges.remove(0);
            points.add(edge0.p0);
            Point2d lastPoint = edge0.p1;
            while (lastPoint != edge0.p0) {
                points.add(lastPoint);
                for (Edge2d edge : edges) {
                    if (edge.p0 == lastPoint) {
                        edges.remove(edge);
                        lastPoint = edge.p1;
                        break;
                    }
                }
            }
            SimplePolygon2d polygon = new SimplePolygon2d(points.toArray(new Point2d[points.size()]));
            if (polygon.isPositiveOriented()) {
                polygons.add(0, polygon);
            } else {
                polygons.add(polygon);
            }
        }
        return polygons;
    }

    /**
     * Reduziert diese Triangulation unter Verwendung einer Knotendichte.
     *
     * @return Anzahl Dreiecke die reduziert wurden.
     */
    public int reduceByPointDense() {
        return 0;
    }

    /**
     * Liefert die Schwerpunkt-Regionen der Knoten. Die Ecken der Regionen sind
     * die Schwerpunkte der Dreiecke.
     *
     * @return Feld mit einfachen Polygonen als Schwerpunkt-Regionen in
     * Reihenfolge der Knoten.
     */
    public SimplePolygon2d[] getCentroidRegions() {
        // einfache Polygone als Schwerpunkts-Regionen
        SimplePolygon2d[] creg = new SimplePolygon2d[pts.length];
        // Schleife ueber alle Knoten
        for (int i = 0; i < pts.length; i++) {
            // angrenzende Kanten berechnen
            ArrayList<Edge2d> nedges = getSortedNEdges(pts[i]);
            int size = nedges.size();
            // zugehoerige Schwerpunkte fuer die Region berechnen
            ArrayList<Point2d> regpts = new ArrayList<Point2d>(size);
            for (Edge2d e : nedges) {
                regpts.add(pts[i].add(e.p0).add(e.p1).mult(1. / 3.));
            }
            // ggf. Kantenmittelpunkte und p[i] einbeziehen
            Point2d p0 = nedges.get(0).p0;
            Point2d p1 = nedges.get(size - 1).p1;
            if (!(p0 == p1)) {
                regpts.add(p1.add(pts[i]).mult(0.5));
                regpts.add(pts[i]);
                regpts.add(pts[i].add(p0).mult(0.5));
            }
            creg[i] = new SimplePolygon2d(regpts.toArray(new Point2d[regpts.size()]));
        }
        return creg;
    }

    /**
     * Liefert die Voronoi-Regionen der Knoten. Die Ecken der Regionen sind die
     * Umkreismittelpunkte der Dreiecke.
     *
     * @return Feld mit einfachen Polygonen als Voronoi-Regionen in Reihenfolge
     * der Knoten.
     */
    public SimplePolygon2d[] getVoronoiRegions() {
        // einfache Polygone als Voronoi-Regionen
        SimplePolygon2d[] vreg = new SimplePolygon2d[pts.length];
        // Randpolygone der Triangulation als Begrenzung
        ArrayList<SimplePolygon2d> conpgs = getConfidencePolygons();
        // Schleife ueber alle Knoten
        for (int i = 0; i < pts.length; i++) {
            // angrenzende Kanten berechnen
            ArrayList<Edge2d> nedges = getSortedNEdges(pts[i]);
            int size = nedges.size();
            // zugehoerige Umkreismittelpunkte fuer die Region berechnen
            ArrayList<Point2d> regpts = new ArrayList<Point2d>(size);
            for (Edge2d e : nedges) {
                double n = 4. * (((pts[i].x - e.p0.x) * (e.p0.y - e.p1.y))
                        - ((e.p0.x - e.p1.x) * (pts[i].y - e.p0.y)));
                double z1 = 2. * (((pts[i].x * pts[i].x + pts[i].y * pts[i].y - e.p0.x * e.p0.x - e.p0.y * e.p0.y)
                        * (e.p0.y - e.p1.y))
                        - ((e.p0.x * e.p0.x + e.p0.y * e.p0.y - e.p1.x * e.p1.x - e.p1.y * e.p1.y)
                        * (pts[i].y - e.p0.y)));
                double z2 = 2. * (((e.p0.x * e.p0.x + e.p0.y * e.p0.y - e.p1.x * e.p1.x - e.p1.y * e.p1.y)
                        * (pts[i].x - e.p0.x))
                        - ((pts[i].x * pts[i].x + pts[i].y * pts[i].y - e.p0.x * e.p0.x - e.p0.y * e.p0.y)
                        * (e.p0.x - e.p1.x)));
                regpts.add(new Point2d(z1 / n, z2 / n));
            }
            // Fallunterscheidung: Rand- und Gebietsknoten
            Point2d p0 = nedges.get(0).p0;
            Point2d p1 = nedges.get(size - 1).p1;
            // Randknoten
            if (!(p0 == p1)) {
                // 1ter Schnittpunkt mit Rand
                Point2d cp1 = null;
                for (int j = 0; j < size - 1 && cp1 == null; j++) {
                    Point2d pj0 = regpts.get(j);
                    Point2d pj1 = regpts.get(j + 1);
                    if (Edge2d.intersects(p1, pts[i], pj0, pj1)) {
                        double[] s = Edge2d.getDirectionParamofCutPoint(p1, pts[i], pj0, pj1);
                        cp1 = p1.add(pts[i].sub(p1).mult(s[0]));
                        for (int k = j + 1; k < size; k++) {
                            regpts.remove(j + 1);
                        }
                        regpts.add(cp1);
                    }
                }
                if (cp1 == null) {
                    regpts.add(p1.add(pts[i]).mult(0.5));
                }
                // p[i]
                regpts.add(pts[i]);
                // 2ter Schnittpunkt mit Rand
                Point2d cp0 = null;
                size = regpts.size();
                for (int j = 0; j < size - 2 && cp0 == null; j++) {
                    Point2d pj0 = regpts.get(j);
                    Point2d pj1 = regpts.get(j + 1);
                    if (Edge2d.intersects(pts[i], p0, pj0, pj1)) {
                        double[] s = Edge2d.getDirectionParamofCutPoint(pts[i], p0, pj0, pj1);
                        cp0 = pts[i].add(p0.sub(pts[i]).mult(s[0]));
                        for (int k = 0; k < j + 1; k++) {
                            regpts.remove(0);
                        }
                        regpts.add(cp0);
                    }
                }
                if (cp0 == null) {
                    regpts.add(pts[i].add(p0).mult(0.5));
                }
                // Korrektur
                correct(regpts);
                // geschlossene Region
                vreg[i] = new SimplePolygon2d(regpts.toArray(new Point2d[regpts.size()]));
            } else {
                // Korrektur
                correct(regpts);
                // Gebietspunkte haben immer eine geschlossene Region
                vreg[i] = new SimplePolygon2d(regpts.toArray(new Point2d[regpts.size()]));
                // ggf. mit dem Rand schneiden
                for (SimplePolygon2d conpg : conpgs) {
                    if (vreg[i].intersects(conpg)) {
                        SimplePolygon2d[] section = conpg.section(vreg[i]);
                        boolean contains = false;
                        for (int j = 0; j < section.length && !contains; j++) {
                            if (section[j].contains(pts[i])) {
                                vreg[i] = section[j];
                                contains = true;
                            }
                        }
                        if (contains) {
                            break;
                        }
                    }
                }
            }
            if (!vreg[i].isPositiveOriented()) {
                vreg[i].changeOrientation();
            }
        }
        return vreg;
    }

    private ArrayList<Edge2d> getSortedNEdges(Point2d p) {
        ArrayList<Edge2d> nedges = new ArrayList<Edge2d>();
        // Kanten suchen
        for (Triangle2d t : triangles) {
            if (t.points[0] == p) {
                nedges.add(new Edge2d(t.points[1], t.points[2]));
            } else if (t.points[1] == p) {
                nedges.add(new Edge2d(t.points[2], t.points[0]));
            } else if (t.points[2] == p) {
                nedges.add(new Edge2d(t.points[0], t.points[1]));
            }
        }
        // ... sortieren
        int size = nedges.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (nedges.get(i).p1 == nedges.get(j).p0) {
                    nedges.add(i + 1, nedges.remove(j));
                    break;
                } else if (nedges.get(0).p0 == nedges.get(j).p1) {
                    nedges.add(0, nedges.remove(j));
                    break;
                }
            }
        }
        return nedges;
    }

    private void correct(ArrayList<Point2d> regpts) {
        int size = regpts.size();
        for (int i = 0; i < size; i++) {
            Point2d P0 = regpts.get(i);
            Point2d P1 = regpts.get((i + 1) % size);
            Point2d P2 = regpts.get((i + 2) % size);
            Point2d P3 = regpts.get((i + 3) % size);
            if (Edge2d.intersects(P0, P1, P2, P3)) {
                regpts.set((i + 1) % size, P2);
                regpts.set((i + 2) % size, P1);
            }
        }
    }

//------------------------------------------------------------------------------
    public static void main(String[] args) {
        Point2d[] points = {
            new Point2d(150, 300),
            new Point2d(400, 280),
            new Point2d(280, 212),
            new Point2d(440, 88),
            new Point2d(234, 64),
            new Point2d(160, 160),
            new Point2d(350, 160),
            new Point2d(40, 160)
        };
//        Point2d[] points = {
//            new Point2d(100, 200),
//            new Point2d(100, 400),
//            new Point2d(300, 400),
//            new Point2d(300, 200)
//        };

        Triangulation2d triangulation = new Triangulation2d(points);
//        Triangulation2d triangulation = simpleDelaunayTriangulation(points);
//        VoronoiTriangulation triangulation = new VoronoiTriangulation(points);
//        IncrementTriangulation triangulation = new IncrementTriangulation(points);

        javax.swing.JFrame fr = new javax.swing.JFrame("Triangulation");
        fr.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        fr.setSize(640, 480);
        fr.getContentPane().add(new Triangulation2dView(triangulation));
        fr.setVisible(true);
    }
}

class Triangulation2dView extends javax.swing.JComponent implements java.awt.event.MouseListener {

    Triangulation2d tr;
    SimplePolygon2d[] reg;
    boolean paintReg = false;

    public Triangulation2dView(Triangulation2d tr) {
        this.tr = tr;
        reg = tr.getVoronoiRegions();
        addMouseListener(this);
    }

    public void paintComponent(java.awt.Graphics g) {
        g.setColor(java.awt.Color.BLACK);
        for (Triangle2d t : tr.triangles) {
            int[] x = {(int) t.points[0].x, (int) t.points[1].x, (int) t.points[2].x};
            int[] y = {(int) t.points[0].y, (int) t.points[1].y, (int) t.points[2].y};
            g.drawPolygon(x, y, 3);
        }
        if (paintReg) {
            g.setColor(java.awt.Color.RED);
            for (int i = 0; i < reg.length; i++) {
                Point2d[] pts = reg[i].getPoints();
                int[] x = new int[pts.length];
                int[] y = new int[pts.length];
                for (int j = 0; j < pts.length; j++) {
                    x[j] = (int) pts[j].x;
                    y[j] = (int) pts[j].y;
                }
                g.drawPolygon(x, y, pts.length);
            }
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent me) {
        int mod = me.getModifiers();
        if (mod == 8) {
            paintReg = !paintReg;
            repaint();
        } else if (mod == 16) {
            tr.addPoint(new Point2d(me.getX(), me.getY()));
            reg = tr.getVoronoiRegions();
            repaint();
        }
    }

    public void mousePressed(java.awt.event.MouseEvent me) {
    }

    public void mouseReleased(java.awt.event.MouseEvent me) {
    }

    public void mouseEntered(java.awt.event.MouseEvent me) {
    }

    public void mouseExited(java.awt.event.MouseEvent me) {
    }
}