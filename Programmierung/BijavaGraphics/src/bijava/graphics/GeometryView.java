package bijava.graphics;

import bijava.geometry.dim2.BoundingBox2d;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.Polygon2d;
import bijava.geometry.dim2.PolygonalCurve2d;
import bijava.geometry.dim2.PolygonalRegion2d;
import bijava.geometry.dim2.SimplePolygon2d;
import bijava.geometry.dim3.Point3d;
import bijava.geometry.dim3.Triangle3d;
import bijava.graphics.canvas2D.RulerComponent;
import bijava.math.function.ScalarFunction2d;
import bijava.math.function.interpolation.RasterScalarFunction2d;
import bijava.math.function.interpolation.ShepardScalarFunction2d;
import bijava.math.function.interpolation.TriangulatedScalarFunction2d;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;

/**
 * GeometryView.java ist eine grafische Komponente zur Darstellung von Geometrien.
 * Zur Verwendung: Siehe Funktion userDescription().
 * 
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 1.2, Februar 2009
 */
public class GeometryView extends JComponent implements KeyListener, MouseListener, MouseMotionListener {

    Object[] geometries;
    int index = 0, index0, index1;

    double xmin             = Double.POSITIVE_INFINITY;
    double xmax             = Double.NEGATIVE_INFINITY;
    double ymin             = Double.POSITIVE_INFINITY;
    double ymax             = Double.NEGATIVE_INFINITY;
    double xminSelect,  xmaxSelect,  yminSelect,  ymaxSelect,  xminFrame,  xmaxFrame,  yminFrame,  ymaxFrame;
    double minValue         = Double.POSITIVE_INFINITY;
    double maxValue         = Double.NEGATIVE_INFINITY;
    Transform trafo         = new Transform();

    RulerComponent axesX,  axesY;
//    Spacer2D axes;

    int w = -1, h = -1;
    int X0, Y0, X1, Y1;
    double x0, y0, x1, y1;

    Point2d clickPoint      = new Point2d(Double.NaN, Double.NaN);
    double clickValue       = Double.NaN;

    Color bgCol             = new Color(255, 255, 220);
    Color bgColPgPos        = new Color(238, 238, 238);
    Color bgColPgNeg        = Color.WHITE;
    Color clickPointCol     = Color.MAGENTA;
    Color latticeCol        = Color.GRAY;

    public IsoPalette isoPalette;
    double[] isoValues;
    int isoLevels           = 8;
    Image buffer;
    Graphics bg, dragGr;

    boolean paintAxes       = true;     // Zeiger zum Zeichnen der Koordinatenachsen
    boolean paintAllGeom    = true;     // Zeiger zum Zeichnen saemtlicher Geometrien
    boolean paintPoints     = false;    // Zeiger zum Zeichnen von Punkten
    boolean paintLattice    = true;
    boolean paintIsoArea    = false;    // Zeiger zum Zeichnen der Isoflaeche
    boolean paintClickPoint = true;     // Zeiger zum Zeichnen des angeklickten Punktes
    public boolean modusChanged    = false;
    boolean sizeChanged;

    /**
     * Erzeugt eine grafische Komponente zur Darstellung von Geometrien.
     */
    public GeometryView() {
        this(new Object[]{});
    }

    /**
     * Erzeugt eine grafische Komponente zur Darstellung von Geometrien.
     * @param obj Geometrie.
     */
    public GeometryView(Object obj) {
        this(new Object[]{obj});
    }

    /**
     * Erzeugt eine grafische Komponente zur Darstellung von Geometrien.
     * @param col Menge mit Geometrien.
     */
    public GeometryView(Collection col) {
        this(col.toArray(new Object[col.size()]));
    }

    /**
     * Erzeugt eine grafische Komponente zur Darstellung von Geometrien.
     * @param obj Feld mit Geometrien.
     */
    public GeometryView(Object[] obj) {
        this.geometries = obj;
        axesX = new RulerComponent(RulerComponent.HORIZONTAL);
        axesY = new RulerComponent(RulerComponent.VERTICAL);
        super.add(axesX);
        super.add(axesY);
//        axes = new Spacer2D();
//        super.add(axes);

        this.computeBoundingBox();
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
//        axes.addMouseListener(this);
//        axes.addMouseMotionListener(this);
    }

    /**
     * Fuegt eine Geometrie hinzu.
     * @param obj Geometrie.
     */
    public void addGeometry(Object obj) {
        Object[] geometriesNew = new Object[geometries.length + 1];
        for (int i = 0; i < geometries.length; i++) {
            geometriesNew[i] = geometries[i];
        }
        geometriesNew[geometries.length] = obj;
        geometries = geometriesNew;
        this.updateBoundingBox(obj);
    }

    /**
     * Entfernt eine Geometrie.
     * @param obj Geometrie.
     */
    public void removeGeometry(Object obj) {
        ArrayList geometriesNew = new ArrayList();
        for (int i = 0; i < geometries.length; i++) {
            if (geometries[i] != obj) {
                geometriesNew.add(geometries[i]);
            }
        }
        int sizeNew = geometriesNew.size();
        if (sizeNew != geometries.length) {
            geometries = geometriesNew.toArray(new Object[sizeNew]);
            this.computeBoundingBox();
        }
    }

    /**
     * Gibt die Geometrien in einem Feld zurueck.
     * @return Feld mit Geometrien.
     */
    public Object[] getGeometries() {
        return geometries;
    }

    /**
     * Gibt die Anzahl der Geometrien zurueck.
     * @return Anzahl der Geometrien.
     */
    public int numberOfGeometries() {
        return geometries.length;
    }

    /**
     * Berechnet das umgebende Rechteck auf Basis der Geometrien.
     */
    public void computeBoundingBox() {
        xmin = ymin = minValue = Double.POSITIVE_INFINITY;
        xmax = ymax = maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < geometries.length; i++) {
            this.updateBoundingBox(geometries[i]);
        }
    }

    private void updateBoundingBox(Object obj) {
        Point2d[] pts2d = null;
        Point3d[] pts3d = null;

        if (obj instanceof Point2d) {
            pts2d = new Point2d[]{(Point2d) obj};
        } else if (obj instanceof Polygon2d) {
            pts2d = ((Polygon2d) obj).getPoints();
        } else if (obj instanceof PolygonalCurve2d) {
            pts2d = ((PolygonalCurve2d) obj).getPoints();
        } else if (obj instanceof PolygonalRegion2d) {
            PolygonalRegion2d region = (PolygonalRegion2d) obj;
            SimplePolygon2d[] polys = region.getPolygons();
            int n = 0;
            for (int i = 0; i < polys.length; i++) {
                n += polys[i].getPoints().length;
            }  // Zaehlen
            pts2d = new Point2d[n];
            Point2d[] tmp = polys[0].getPoints();
            for (int i = 0, m = 0; i < polys.length; i++, m += tmp.length) {
                tmp = polys[i].getPoints();
                System.arraycopy(tmp, 0, pts2d, m, tmp.length);
            }
        } else if (obj instanceof ShepardScalarFunction2d) {
            ShepardScalarFunction2d sf = (ShepardScalarFunction2d) obj;
            int size = sf.getSizeOfValues();
            pts2d = new Point2d[size];
            for (int i = 0; i < size; i++) {
                pts2d[i] = sf.getSamplingPointAt(i);
                double val = sf.getSamplingValueAt(i);
                if (!Double.isNaN(val)) {
                    minValue = (val < minValue) ? val : minValue;
                    maxValue = (val > maxValue) ? val : maxValue;
                }
            }
        } else if (obj instanceof RasterScalarFunction2d) {
            RasterScalarFunction2d rf = (RasterScalarFunction2d) obj;
            pts2d = new Point2d[]{rf.getSamplingPointAt(0, 0), rf.getSamplingPointAt(rf.getRowSize() - 1, rf.getColumnSize() - 1)};
            double rfMin = rf.getMin();
            double rfMax = rf.getMax();
            minValue = (rfMin < minValue) ? rfMin : minValue;
            maxValue = (rfMax > maxValue) ? rfMax : maxValue;
        } else if (obj instanceof TriangulatedScalarFunction2d) {
            TriangulatedScalarFunction2d tf = (TriangulatedScalarFunction2d) obj;
            int size = tf.getSizeOfValues();
            pts2d = new Point2d[size];
            for (int i = 0; i < size; i++) {
                pts2d[i] = tf.getSamplingPointAt(i);
                double val = tf.getSamplingValueAt(i);
                if (!Double.isNaN(val)) {
                    minValue = (val < minValue) ? val : minValue;
                    maxValue = (val > maxValue) ? val : maxValue;
                }
            }
        } else if (obj instanceof ScalarFunction2d) {
            // Bei ScalarFunction2d kann kein umgebendes Rechteck erfragt werden, daher ignorieren.
            // Das umgebende Rechteck kann manuell ueber die Methode setBoundingBox(...) gesetzt werden.
        } else if (obj instanceof Point3d) {
            pts3d = new Point3d[]{(Point3d) obj};
            minValue = (pts3d[0].z < minValue) ? pts3d[0].z : minValue;
            maxValue = (pts3d[0].z > maxValue) ? pts3d[0].z : maxValue;
        } else if (obj instanceof Triangle3d) {
            pts3d = ((Triangle3d) obj).getPoints();
            minValue = Math.min(minValue, Math.min(pts3d[0].z, Math.min(pts3d[1].z, pts3d[2].z)));
            maxValue = Math.max(maxValue, Math.max(pts3d[0].z, Math.max(pts3d[1].z, pts3d[2].z)));
        } else {
            throw new IllegalArgumentException("Geometrie " + obj + " kann nicht verarbeitet werden");
        }
        if (pts2d != null) {
            for (int i = 0; i < pts2d.length; i++) {
                xmin = (pts2d[i].x < xmin) ? pts2d[i].x : xmin;
                ymin = (pts2d[i].y < ymin) ? pts2d[i].y : ymin;
                xmax = (pts2d[i].x > xmax) ? pts2d[i].x : xmax;
                ymax = (pts2d[i].y > ymax) ? pts2d[i].y : ymax;
            }
        } else if (pts3d != null) {
            for (int i = 0; i < pts3d.length; i++) {
                xmin = (pts3d[i].x < xmin) ? pts3d[i].x : xmin;
                ymin = (pts3d[i].y < ymin) ? pts3d[i].y : ymin;
                xmax = (pts3d[i].x > xmax) ? pts3d[i].x : xmax;
                ymax = (pts3d[i].y > ymax) ? pts3d[i].y : ymax;
            }
        }
        xminSelect = xmin;
        yminSelect = ymin;
        xmaxSelect = xmax;
        ymaxSelect = ymax;

        if (isoPalette == null || minValue != Double.POSITIVE_INFINITY) {
            this.setIsoPalette();
        }
    }

    /**
     * Aktualisiert das umgebende Rechteck, indem dieses mit dem uebergebenen vereint wird.
     * @param bb umgebendes Rechteck.
     */
    public void updateBoundingBox(BoundingBox2d bb) {
        xminSelect = xmin = Math.min(bb.getXmin(), xmin);
        xmaxSelect = xmax = Math.max(bb.getXmax(), xmax);
        yminSelect = ymin = Math.min(bb.getYmin(), ymin);
        ymaxSelect = ymax = Math.max(bb.getYmax(), ymax);
    }

    /**
     * Setzt ein umgebendes Rechteck.
     * @param bb umgebendes Rechteck.
     */
    public void setBoundingBox(BoundingBox2d bb) {
        xminSelect = xmin = bb.getXmin();
        xmaxSelect = xmax = bb.getXmax();
        yminSelect = ymin = bb.getYmin();
        ymaxSelect = ymax = bb.getYmax();
    }

    /**
     * Gibt das umgebende Rechteck zurueck.
     * @return Feld mit minimalen und maximalen x- und y-Koordinaten (xmin, ymin, xmax, ymax).
     */
    public double[] getBoundingBox() {
        return new double[]{xmin, ymin, xmax, ymax};
    }

    /**
     * Gibt das ausgewaehlte umgebende Rechteck zurueck.
     * @return Feld mit minimalen und maximalen x- und y-Koordinaten (xmin, ymin, xmax, ymax).
     */
    public double[] getBoundingBoxSelected() {
        return new double[]{xminSelect, yminSelect, xmaxSelect, ymaxSelect};
    }

    @Override
    public void setBackground(Color bg) {
        bgCol = bg;
    }

    /**
     * Setzt die Hintergrundfarbe eines positiv orientierten Polygons.
     * @param bgpgpos Farbe.
     */
    public void setBackgroundPolyPos(Color bgpgpos) {
        bgColPgPos = bgpgpos;
    }

    /**
     * Setzt die Hintergrundfarbe eines negativ orientierten Polygons.
     * @param bgpgneg Farbe.
     */
    public void setBackgroundPolyNeg(Color bgpgneg) {
        bgColPgNeg = bgpgneg;
    }

    /**
     * Setzt die Farbe einer Zerlegung (Gitter).
     * @param c Farbe.
     */
    public void setLattice(Color c) {
        latticeCol = c;
    }

    /**
     * Setzt eine Isopalette mit einer Farbskala von Weiss nach Schwarz und Isowerten entsprechend den Werten der Geometrien.
     */
    public void setIsoPalette() {
        // Farbpalette festlegen
        double eps = (maxValue - minValue) * 1.E-02;
        double zminPal = minValue - eps;
        double zmaxPal = maxValue + eps;
        isoPalette = new IsoPalette(new double[]{zminPal, zmaxPal}, new Color[]{Color.WHITE, Color.BLACK});
        // Hoehenlinien festlegen
        double dz = (zmaxPal - zminPal) / (double) isoLevels;
        isoValues = new double[isoLevels + 1];
        for (int i = 0; i < isoLevels + 1; i++) {
            isoValues[i] = zminPal + dz * (double) i;
        }
    }

    /**
     * Setzt die Isopalette und die Isowerte.
     * @param isoPalette Isopalette: Farben, die Hoehenwerten zugeordnet sind.
     * @param isoValues Isowerte: Hoehenwerte, an denen ggf. Isolinien gezeichnet werden.
     */
    public void setIsoPalette(IsoPalette isoPalette, double[] isoValues) {
        this.isoPalette = isoPalette;
        this.isoValues = isoValues;
    }

    /**
     * Setzt eine neue IsoPalette und die Anzahl der darzustellenden Farben der
     * Farbskala.
     * @param isoPalette neue IsoPalette
     * @param n Anzahl der dazustellenden Farben.
     */
    public void setIsoPalette(IsoPalette isoPalette, int n) {
        this.isoPalette = isoPalette;
        this.isoLevels = n;

        double eps = (maxValue - minValue) * 1.E-02;
        double zminPal = minValue - eps;
        double zmaxPal = maxValue + eps;

        // Hoehenlinien festlegen
        double dz = (zmaxPal - zminPal) / (double) isoLevels;
        isoValues = new double[isoLevels + 1];
        for (int i = 0; i < isoLevels + 1; i++) {
            isoValues[i] = zminPal + dz * (double) i;
        }
    }

    /**
     * Setzt die Anzahl der Farben in der Farbskala.
     * @param n Anzahl Farben in der Farbskala.
     */
    public void setNumberofIsoLevels(int n) {
        this.isoLevels = n;
        this.setIsoPalette();
    }

    /**
     * Setzt den Zeiger zum Zeichnen der Koordinatenachsen.
     * @param b boolescher Zeiger.
     */
    public void paintAxesOfCoordinates(boolean b) {
        if (paintAxes != b) {
            paintAxes = b;

            if (paintAxes) {
                super.add(axesX);
                super.add(axesY);
//                super.add(axes);
//                axes.addMouseListener(this);
//                axes.addMouseMotionListener(this);
            } else {
                super.remove(axesX);
                super.remove(axesY);
//                super.remove(axes);
//                addMouseListener(this);
//                addMouseMotionListener(this);
            }
        }
    }

    /**
     * Setzt den Zeiger zum Zeichnen der Punkte.
     * @param b boolescher Zeiger.
     */
    public void paintPoints(boolean b){
       paintPoints = b;
    }

    /**
     * Setzt den Zeiger zum Zeichnen des Gitters.
     * @param b boolescher Zeiger.
     */
    public void paintLattice(boolean b){
       paintLattice = b;
    }

    /**
     * Setzt den Zeiger zum Zeichnen der Isoflaeche.
     * @param b boolescher Zeiger.
     */
    public void paintIsoArea(boolean b){
       paintIsoArea = b;
    }

    /**
     * Setzt den Zeiger zum Zeichnen des angeklickten Punktes.
     * @param b boolescher Zeiger.
     */
    public void paintClickPoint(boolean b) {
        this.paintClickPoint = b;
    }

    /**
     * Liefert den angeklickten Punkt in Weltkoordinaten.
     * @return angeklickter Punkt.
     */
    public Point2d getClickPoint() {
        return new Point2d(clickPoint);
    }

    /**
     * Liefert den Funktionswert des angeklickten Punktes, sofern es einen gibt.
     * Bei mehreren Schichten wird der Funktionswert aus der obersten Schicht berechnet.
     * @return Funktionswert des angeklickten Punktes.
     */
    public double getClickValue() {
        return clickValue;
    }

    /**
     * Zeichnet diese grafische Komponente.
     * @param g grafischer Kontext.
     */
    @Override
    public void paintComponent(Graphics g) {
        Dimension d = this.getSize();
        sizeChanged = (w != d.width || h != d.height);
        if (modusChanged || sizeChanged) {  // TODO: es sollte noch eine Moeglichkeit geben, neu zu zeichnen, auch wenn sich intern nichts geaendert hat?!
            if (sizeChanged) {
                w = d.width;
                h = d.height;
                axesX.setSize(w, h);
                axesY.setSize(w, h);
//                axes.setSize(d);
                buffer = this.createImage(w, h);
                bg = buffer.getGraphics();
                trafo.computeScale();
            }
            // Hintergrund
            bg.setColor(bgCol);
            bg.fillRect(0, 0, w, h);
            // Objekte
            if (paintPoints || paintLattice || paintIsoArea) {
                if (paintAllGeom) {
                    index0 = 0;
                    index1 = geometries.length;
                } else {
                    index0 = index;
                    index1 = index0 + 1;
                }
                for (int i = index0; i < index1; i++) {
                    if (geometries[i] instanceof Point2d) {
                        this.paintPoint2d((Point2d) geometries[i], bg);
                    } else if (geometries[i] instanceof Polygon2d) {
                        this.paintPolygon2d((Polygon2d) geometries[i], bg);
                    } else if (geometries[i] instanceof PolygonalCurve2d) {
                        this.paintPolygonalCurve2d((PolygonalCurve2d) geometries[i], bg);
                    } else if (geometries[i] instanceof PolygonalRegion2d) {
                        this.paintPolygonalRegion2d((PolygonalRegion2d) geometries[i], bg);
                    } else if (geometries[i] instanceof ShepardScalarFunction2d) {
                        this.paintShepardScalarFunction2d((ShepardScalarFunction2d) geometries[i], bg);
                    } else if (geometries[i] instanceof RasterScalarFunction2d) {
                        this.paintRasterScalarFunction2d((RasterScalarFunction2d) geometries[i], bg);
                    } else if (geometries[i] instanceof TriangulatedScalarFunction2d) {
                        this.paintTriangulatedScalarFunction2d((TriangulatedScalarFunction2d) geometries[i], bg);
                    } else if (geometries[i] instanceof ScalarFunction2d) {
                        this.paintScalarFunction2d((ScalarFunction2d) geometries[i], bg);
                    } else if (geometries[i] instanceof Point3d) {
                        this.paintPoint3d((Point3d) geometries[i], bg);
                    } else if (geometries[i] instanceof Triangle3d) {
                        this.paintTriangle3d((Triangle3d) geometries[i], bg);
                    } 
                }
            }
            if (paintClickPoint) {
                this.paintClickPoint(bg);
            }
            modusChanged = false;
        }
        g.drawImage(buffer, 0, 0, this);
    }

    private void paintPoint2d(Point2d p, Graphics g) {
        if (paintPoints) {
            g.setColor(latticeCol);
            g.fillRect(trafo.transformX(p.x), trafo.transformY(p.y), 3, 3);
        }
    }

    private void paintPolygon2d(Polygon2d pg, Graphics g) {
        Point2d[] pts = pg.getPoints();
        int[] x = new int[pts.length];
        int[] y = new int[pts.length];
        for (int i = 0; i < pts.length; i++) {
            x[i] = trafo.transformX(pts[i].x);
            y[i] = trafo.transformY(pts[i].y);
        }
        if (paintIsoArea) {
            Color c = ((pg instanceof SimplePolygon2d) && (((SimplePolygon2d) pg).getOrientation() == -1)) ? bgColPgNeg : bgColPgPos;
            g.setColor(c);
            g.fillPolygon(x, y, pts.length);
        }
        if (paintLattice) {
            g.setColor(latticeCol);
            g.drawPolygon(x, y, pts.length);
        }
        if (paintPoints) {
            g.setColor(latticeCol);
            for (int i = 0; i < pts.length; i++) {
                g.fillRect(x[i], y[i], 3, 3);
            }
        }
    }

    private void paintPolygonalCurve2d(PolygonalCurve2d pgc, Graphics g) {
        if (paintPoints || paintLattice) {
            g.setColor(latticeCol);
            Point2d[] pts = pgc.getPoints();
            int[] x = new int[pts.length];
            int[] y = new int[pts.length];
            for (int i = 0; i < pts.length; i++) {
                x[i] = trafo.transformX(pts[i].x);
                y[i] = trafo.transformY(pts[i].y);
            }
            if (paintLattice) {
                g.drawPolyline(x, y, pts.length);
            }
            if (paintPoints) {
                for (int i = 0; i < pts.length; i++) {
                    g.fillRect(x[i], y[i], 3, 3);
                }
            }
        }
    }

    private void paintPolygonalRegion2d(PolygonalRegion2d re, Graphics g) {
        SimplePolygon2d[] polys = re.getPolygons();
        for (int i = 0; i < polys.length; i++) {
            this.paintPolygon2d(polys[i], g);
        }
    }

    private void paintShepardScalarFunction2d(ShepardScalarFunction2d sf, Graphics g) {
        if (paintPoints || paintIsoArea) {
            int size = sf.getSizeOfValues();
            for (int i = 0; i < size; i++) {
                Point2d p = sf.getSamplingPointAt(i);
                int x = trafo.transformX(p.x);
                int y = trafo.transformY(p.y);

                if (paintIsoArea) {
                    double z = sf.getSamplingValueAt(i);
                    if (!Double.isNaN(z)) {
                        g.setColor(isoPalette.getColor(z));
                        g.fillRect(x, y, 3, 3);
                    } else {
                        g.setColor(latticeCol);
                        g.fillRect(x, y, 3, 3);
                    }
                } else {
                    g.setColor(latticeCol);
                    g.fillRect(x, y, 3, 3);
                }
            }
        }
    }

    private void paintRasterScalarFunction2d(RasterScalarFunction2d rf, Graphics g) {
        int lx = rf.getRowSize();
        int ly = rf.getColumnSize();
        int[][][] screenCoords = new int[lx][ly][2];
        for (int i = 0; i < lx; i++) {
            for (int j = 0; j < ly; j++) {
                Point2d p = rf.getSamplingPointAt(i, j);
                screenCoords[i][j][0] = trafo.transformX(p.x);
                screenCoords[i][j][1] = trafo.transformY(p.y);
            }
        }
        if (paintIsoArea || paintLattice) {
            for (int i = 0; i < lx - 1; i++) {
                for (int j = 0; j < ly - 1; j++) {
                    int x00 = screenCoords[i][j][0];
                    int y00 = screenCoords[i][j][1];
                    int x01 = screenCoords[i + 1][j][0];
                    int y10 = screenCoords[i][j + 1][1];
                    if (paintIsoArea) {
                        double z00 = rf.getSamplingValueAt(i, j);
                        double z01 = rf.getSamplingValueAt(i + 1, j);
                        double z10 = rf.getSamplingValueAt(i, j + 1);
                        double z11 = rf.getSamplingValueAt(i + 1, j + 1);
                        if (!(Double.isNaN(z00) || Double.isNaN(z01) || Double.isNaN(z10) || Double.isNaN(z11))) {
                            // Schwerpunktsmethode
                            double zsp = (z00 + z01 + z10 + z11) / 4.;
                            g.setColor(isoPalette.getColor(zsp));
                            g.fillRect(x00, y10, x01 - x00, y00 - y10);
                        }
                    }
                    if (paintLattice) {
                        g.setColor(latticeCol);
                        g.drawRect(x00, y10, x01 - x00, y00 - y10);
                    }
                }
            }
        }
        if (paintPoints) {
            g.setColor(latticeCol);
            for (int i = 0; i < lx; i++) {
                for (int j = 0; j < ly; j++) {
                    g.fillRect(screenCoords[i][j][0], screenCoords[i][j][1], 3, 3);
                }
            }
        }
    }

    private void paintTriangulatedScalarFunction2d(TriangulatedScalarFunction2d tf, Graphics g) {
        int tsize = tf.triangles.length;
        if (paintIsoArea) {
            Point3d p0_3d = new Point3d();
            Point3d p1_3d = new Point3d();
            Point3d p2_3d = new Point3d();

            for (int i = 0; i < tsize; i++) {
                int[] num = tf.getTrianglePointNumbers(i);
                Point2d p0_2d = tf.getSamplingPointAt(num[0]);
                Point2d p1_2d = tf.getSamplingPointAt(num[1]);
                Point2d p2_2d = tf.getSamplingPointAt(num[2]);
                p0_3d.x = p0_2d.x;
                p0_3d.y = p0_2d.y;
                p0_3d.z = tf.getSamplingValueAt(num[0]);
                p1_3d.x = p1_2d.x;
                p1_3d.y = p1_2d.y;
                p1_3d.z = tf.getSamplingValueAt(num[1]);
                p2_3d.x = p2_2d.x;
                p2_3d.y = p2_2d.y;
                p2_3d.z = tf.getSamplingValueAt(num[2]);

                ArrayList<IsoSurface> isoSurfaces = IsoSurfaceGenerator.getIsoSurfaces(p0_3d, p1_3d, p2_3d, isoValues);
                for (IsoSurface isoSurface : isoSurfaces) {
                    Point3d[] isoPoints = isoSurface.getPoints();
                    int[] x = new int[isoPoints.length];
                    int[] y = new int[isoPoints.length];
                    for (int j = 0; j < isoPoints.length; j++) {
                        x[j] = trafo.transformX(isoPoints[j].x);
                        y[j] = trafo.transformY(isoPoints[j].y);
                    }
                    g.setColor(isoPalette.getColor(isoSurface.getValue()));
                    g.fillPolygon(x, y, isoPoints.length);
                }
            }
        }
        if (paintPoints || paintLattice) {
            g.setColor(latticeCol);
            int psize = tf.getSizeOfValues();
            int[][] screenCoords = new int[psize][2];

            for (int i = 0; i < psize; i++) {
                Point2d p = tf.getSamplingPointAt(i);
                screenCoords[i][0] = trafo.transformX(p.x);
                screenCoords[i][1] = trafo.transformY(p.y);
            }
            if (paintLattice) {
                int[] x = new int[3];
                int[] y = new int[3];
                for (int i = 0; i < tsize; i++) {
                    int[] num = tf.getTrianglePointNumbers(i);
                    x[0]= screenCoords[num[0]][0];
                    x[1]= screenCoords[num[1]][0];
                    x[2]= screenCoords[num[2]][0];
                    y[0]= screenCoords[num[0]][1];
                    y[1]= screenCoords[num[1]][1];
                    y[2]= screenCoords[num[2]][1];
                    g.drawPolygon(x, y, 3);
                }
            }
            if (paintPoints) {
                for (int i = 0; i < psize; i++) {
                    g.fillRect(screenCoords[i][0], screenCoords[i][1], 3, 3);
                }
            }
        }
    }

    private void paintScalarFunction2d(ScalarFunction2d sf, Graphics g) {
        // Soll mit aktuellen Einstellungen ueberhaupt gezeichnet werden?
        if (paintIsoArea) {
            // einfachste Methode: hole fuer jeden Pixel den Wert und zeichne ihn
            // ist allerdings sehr aufwaendig

            // Pixel in Weltkoordinaten
            Point2d pixWorld = new Point2d();

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    // Aktuelle Position setzen
                    pixWorld.x = trafo.transformX(i);
                    pixWorld.y = trafo.transformY(j);
                    
                    double value = sf.getValue(pixWorld);
                    if (!Double.isNaN(value)) {
                        // Farbe holen
                        g.setColor(isoPalette.getColor(value));
                        // ... und zeichnen
                        g.fillRect(i, j, 1, 1);
                    }
//                    else
//                        g.setColor(isoPalette.getColor(value));
                    
                }
            }
        }
    }

    private void paintPoint3d(Point3d p, Graphics g) {
        if (paintPoints || paintIsoArea) {
            int x = trafo.transformX(p.x);
            int y = trafo.transformY(p.y);

            if (paintIsoArea && !Double.isNaN(p.z)) {
                g.setColor(isoPalette.getColor(p.z));
                g.fillRect(x, y, 3, 3);
            } else {
                g.setColor(latticeCol);
                g.fillRect(x, y, 3, 3);
            }
        }
    }

    private void paintTriangle3d(Triangle3d tr, Graphics g) {
        if (paintIsoArea) {
            ArrayList<IsoSurface> isoSurfaces = IsoSurfaceGenerator.getIsoSurfaces(tr, isoValues);
            for (IsoSurface isoSurface : isoSurfaces) {
                Point3d[] isoPoints = isoSurface.getPoints();
                int[] x = new int[isoPoints.length];
                int[] y = new int[isoPoints.length];
                for (int j = 0; j < isoPoints.length; j++) {
                    x[j] = trafo.transformX(isoPoints[j].x);
                    y[j] = trafo.transformY(isoPoints[j].y);
                }
                g.setColor(isoPalette.getColor(isoSurface.getValue()));
                g.fillPolygon(x, y, isoPoints.length);
            }
        }
        if (paintPoints || paintLattice) {
            g.setColor(latticeCol);
            Point3d[] pts = tr.getPoints();
            int p0x = trafo.transformX(pts[0].x);
            int p0y = trafo.transformY(pts[0].y);
            int p1x = trafo.transformX(pts[1].x);
            int p1y = trafo.transformY(pts[1].y);
            int p2x = trafo.transformX(pts[2].x);
            int p2y = trafo.transformY(pts[2].y);

            if (paintLattice) {
                g.drawPolygon(new int[]{p0x, p1x, p2x}, new int[]{p0y, p1y, p2y}, 3);
            }
            if (paintPoints) {
                g.fillRect(p0x, p0y, 3, 3);
                g.fillRect(p1x, p1y, 3, 3);
                g.fillRect(p2x, p2y, 3, 3);
            }
        }
    }

    private void paintClickPoint(Graphics g) {
        if (Double.isNaN(clickPoint.x) || Double.isNaN(clickPoint.y) || clickPoint.x < xminFrame || clickPoint.x > xmaxFrame || clickPoint.y < yminFrame || clickPoint.y > ymaxFrame) {
            return;
        }
        String s = "(" + (float) clickPoint.x + ", " + (float) clickPoint.y;
        s += Double.isNaN(clickValue) ? ")" : ", " + (float) clickValue + ")";
        int fieldLength = (int) (6.35 * (double) s.length());
        g.setColor(Color.WHITE);
        g.fillRect(w - fieldLength - 5, h - 25, fieldLength, 20);
//        g.fillRect(w - fieldLength - 35, h - 55, fieldLength, 20);
        g.setColor(Color.BLACK);
        g.drawString(s, w - fieldLength, h - 10);
//        g.drawString(s, w - fieldLength - 30, h - 40);
        int xClickScreen = trafo.transformX(clickPoint.x);
        int yClickScreen = trafo.transformY(clickPoint.y);
        g.setColor(clickPointCol);
        g.drawLine(xClickScreen - 10, yClickScreen, xClickScreen + 10, yClickScreen);
        g.drawLine(xClickScreen, yClickScreen - 10, xClickScreen, yClickScreen + 10);
    }

    public void keyTyped(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
        int code = ke.getKeyCode();
        if (code == KeyEvent.VK_A) {
            paintIsoArea = !paintIsoArea;
            modusChanged = true;
        } else if (code == KeyEvent.VK_C) {
            paintClickPoint = !paintClickPoint;
            modusChanged = true;
        } else if (code == KeyEvent.VK_I) {
            paintAllGeom = !paintAllGeom;
            this.setZClick();
            modusChanged = true;
        } else if (code == KeyEvent.VK_L) {
            paintLattice  = !paintLattice;
            modusChanged = true;
        } else if (code == KeyEvent.VK_P) {
            paintPoints  = !paintPoints;
            modusChanged = true;
        } else if (code == KeyEvent.VK_X) {
            this.paintAxesOfCoordinates(!paintAxes);
            modusChanged = true;
        } else if (!paintAllGeom) {
            if (code == KeyEvent.VK_UP) {
                if (index == geometries.length - 1) {
                    index = 0;
                } else {
                    index++;
                }
                this.setZClick();
                modusChanged = true;
            } else if (code == KeyEvent.VK_DOWN) {
                if (index == 0) {
                    index = geometries.length - 1;
                } else {
                    index--;
                }
                this.setZClick();
                modusChanged = true;
            }
        }
        if (modusChanged) {
            repaint();
        }
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void mouseClicked(MouseEvent me) {
        int mod = me.getModifiers();
        if (mod == 4) {
            xminSelect = xmin;
            yminSelect = ymin;
            xmaxSelect = xmax;
            ymaxSelect = ymax;
            trafo.computeScale();
            modusChanged = true;
        } else if (mod == 16) {
            clickPoint.x = trafo.transformX(me.getX());
            clickPoint.y = trafo.transformY(me.getY());
            this.setZClick();
            modusChanged = true;
        }
        if (modusChanged) {
            repaint();
        }
    }

    private void setZClick() {
        clickValue = Double.NaN;
        if (paintAllGeom) {
            index0 = 0;
            index1 = geometries.length;
        } else {
            index0 = index;
            index1 = index0 + 1;
        }
        for (int i = index0; i < index1; i++) {
            if (geometries[i] instanceof ScalarFunction2d) {
                double z = ((ScalarFunction2d) geometries[i]).getValue(clickPoint);
                clickValue = Double.isNaN(z) ? clickValue : z;
            } else if (geometries[i] instanceof Point3d) {
                Point3d p = (Point3d) geometries[i];
                clickValue = (clickPoint.x != p.x || clickPoint.y != p.y) ? clickValue : p.z;
            } else if (geometries[i] instanceof Triangle3d) {
                Triangle3d tr = (Triangle3d) geometries[i];
                Point3d clickPoint_3d = new Point3d(clickPoint.x, clickPoint.y, 0.);
                double[] natCoord_2d = tr.getNaturefromCart(clickPoint_3d);
                if (natCoord_2d != null) {
                    Point3d[] pts = tr.getPoints();
                    clickValue = natCoord_2d[0] * pts[0].z + natCoord_2d[1] * pts[1].z + natCoord_2d[2] * pts[2].z;
                }
            }
        }
    }

    public void mousePressed(MouseEvent me) {
        int mod = me.getModifiers();
        if (mod == 16) {
            dragGr = getGraphics();
            dragGr.setXORMode(getBackground());
            X0 = me.getX();
            Y0 = me.getY();
            X1 = X0;
            Y1 = Y0;
        }
    }

    public void mouseReleased(MouseEvent me) {
        int mod = me.getModifiers();
        if (mod == 16) {
            dragGr.setPaintMode();
            dragGr.dispose();
            if (X0 != X1 && Y0 != Y1) {
                x0 = trafo.transformX(X0);
                y0 = trafo.transformY(Y0);
                x1 = trafo.transformX(X1);
                y1 = trafo.transformY(Y1);
                xminSelect = Math.min(x0, x1);
                yminSelect = Math.min(y0, y1);
                xmaxSelect = Math.max(x0, x1);
                ymaxSelect = Math.max(y0, y1);
                trafo.computeScale();
                modusChanged = true;
            }
        }
        if (modusChanged) {
            repaint();
        }
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mouseDragged(MouseEvent me) {
        int mod = me.getModifiers();
        if (mod == 16) {
            // Loesche das vorherige Rechteck durch Zeichnen im XOR mode
            dragGr.drawRect(Math.min(X0, X1), Math.min(Y0, Y1), Math.abs(X1 - X0), Math.abs(Y1 - Y0));
            X1 = Math.max(0, Math.min(w - 2, me.getX()));
            Y1 = Math.max(0, Math.min(h - 2, me.getY()));
            // Zeichne das Rechteck in seiner neuen Positon
            dragGr.drawRect(Math.min(X0, X1), Math.min(Y0, Y1), Math.abs(X1 - X0), Math.abs(Y1 - Y0));
        }
    }

    public void mouseMoved(MouseEvent me) {
    }

    class Transform {
        double scaleX,  scaleY,  scale;

        public void computeScale() {
            scaleX = (double) w / (xmaxSelect - xminSelect);
            scaleY = (double) h / (ymaxSelect - yminSelect);
            scale  = (scaleX < scaleY) ? scaleX : scaleY;
            scaleX = scaleY = scale;

            xminFrame = xminSelect;
            xmaxFrame = xminFrame + (this.transformX(w) - this.transformX(0));
            ymaxFrame = ymaxSelect;
            yminFrame = ymaxFrame + (this.transformY(h) - this.transformY(0));
            axesX.setMinMax(xminFrame, xmaxFrame);
            axesY.setMinMax(yminFrame, ymaxFrame);
//            axes.setBoundingBox(xminFrame, xmaxFrame, yminFrame, ymaxFrame);
        }

        public double transformX(int    x) {
            return (double) x / scale + xminSelect;
        }

        public double transformY(int    y) {
            return ymaxSelect - (double) y / scale;
        }

        public int    transformX(double x) {
            return (int) (scale * (x - xminSelect));
        }

        public int    transformY(double y) {
            return (int) (scale * (ymaxSelect - y));
        }
    }

    public String userDescription() {
        return "Bedienung des GeometryView:\n" +
                "- linke Maustaste fuer Zoom, rechte Maustaste fuer Gesamtansicht\n" +
                "- Lage (x, y) und Funktionswert f per Klick mit linker Maustaste\n" +
                "- A-Taste zur Anzeige der Isoflaeche\n" +
                "- C-Taste zur Anzeige einer angeklickten Lage (x, y)\n" +
                "- I-Taste zum Geom.-Durchlauf mit Auf- und -Ab-Taste\n" +
                "- L-Taste zur Anzeige der Kanten / des Gitters\n" +
                "- P-Taste zur Anzeige der Knoten\n" +
                "- X-Taste zur Anzeige der Koordinatenachsen";
    }
}
