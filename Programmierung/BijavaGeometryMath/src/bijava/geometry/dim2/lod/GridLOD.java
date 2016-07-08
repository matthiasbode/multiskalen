package bijava.geometry.dim2.lod;

import bijava.geometry.dim2.Point2d;
import bijava.math.function.interpolation.RasterScalarFunction2d;
import bijava.math.function.interpolation.ShepardScalarFunction2d;
import java.util.ArrayList;

/**
 * GridLOD.java stellt LOD-Methoden fuer Raster zur Verfuegung.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class GridLOD {
//-------------------------------------------------------------------------------------------------------
    /**
     * Topologische Reduktion eines Rasters:
     * Alle n-ten Knoten gehoeren zum naechsten LOD.
     * @param rf regelmaessiges Raster als <code>RasterScalarFunction2d</code>.
     * @param nx Schrittweite in x-Richtung.
     * @param ny Schrittweite in y-Richtung.
     * @return regelmaessiges Raster als <code>RasterScalarFunction2d</code>.
     */
    public RasterScalarFunction2d reduceTopolog(RasterScalarFunction2d rf, int nx, int ny) {
        if (rf == null || nx < 1 || ny < 1) return null;
        if (nx == 1 && ny == 1) return rf;
        // Rasterabmessungen abfragen
        int lx = rf.getRowSize(), ly = rf.getColumnSize(); // Anzahl Knoten je Richtung
        int lx_red = lx / nx, ly_red = ly / ny;
        if (lx % nx != 0) lx_red++;
        if (ly % ny != 0) ly_red++;
        Point2d pmin = rf.getSamplingPointAt(0, 0), pmax = rf.getSamplingPointAt((lx_red - 1) * nx, (ly_red - 1) * ny);
        // Funktionswerte extrahieren
        double[][] f = new double[lx_red][ly_red];
        for (int i = 0; i < lx_red; i++)
            for (int j = 0; j < ly_red; j++)
                f[i][j] = rf.getSamplingValueAt(i * nx, j * ny);
        
        return new RasterScalarFunction2d(pmin.x, pmin.y, pmax.x, pmax.y, f);
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Graduelle Reduktion eines Rasters:
     * Alle Knoten mit einem strukturellen Wert groeszer als
     * ein Minimum gehoeren zum naechsten LOD.
     * @param rf regelmaessiges Raster als <code>RasterScalarFunction2d</code>.
     * @param crit strukturelles Kriterium zur Reduktion eines polygonalen Objektes.
     * @param eps minimaler struktureller Wert bezueglich des Kriteriums.
     * @return unregelmaessige Punktemenge als <code>ShepardScalarFunction2d</code>.
     */
    public ShepardScalarFunction2d reduceGradual(RasterScalarFunction2d rf, PolygonReductCriterion crit, double eps) {
        if (rf == null || crit == null) return null;
        // Rasterabmessungen abfragen
        int lx = rf.getRowSize(), ly = rf.getColumnSize(); // Anzahl Knoten je Richtung
        
        ArrayList<Point2d> plist = new ArrayList<Point2d>();
        ArrayList<Double> flist = new ArrayList<Double>();
        Point2d A = new Point2d(), B = new Point2d(), C = new Point2d();
        
        for (int i = 0; i < lx; i++)
            for (int j = 0; j < ly; j++) {
            // Ecken bleiben erhalten
            if ((i == 0 && j == 0) || (i == lx - 1 && j == 0) || (i == lx - 1 && j == ly - 1) || (i == 0 && j == ly - 1)) {
                plist.add(rf.getSamplingPointAt(i, j));
                flist.add(rf.getSamplingValueAt(i, j));
            }
            // Punkt liegt auf Rand im WESTEN oder OSTEN: (y, z) ueberpruefen
            else if (i == 0 || i == lx - 1) {
                Point2d P0 = rf.getSamplingPointAt(i, j - 1), P1 = rf.getSamplingPointAt(i, j), P2 = rf.getSamplingPointAt(i, j + 1);
                double z0 = rf.getSamplingValueAt(i, j - 1), z1 = rf.getSamplingValueAt(i, j), z2 = rf.getSamplingValueAt(i, j + 1);
                A.x = P0.y; A.y = z0; B.x = P1.y; B.y = z1; C.x = P2.y; C.y = z2;
                if (crit.getValue(A, B, C) > eps) { plist.add(P1); flist.add(z1); }
            }
            // Punkt liegt auf Rand im SUEDEN oder NORDEN: (x, z) ueberpruefen
            else if (j == 0 || j == ly - 1) {
                Point2d P0 = rf.getSamplingPointAt(i - 1, j), P1 = rf.getSamplingPointAt(i, j), P2 = rf.getSamplingPointAt(i + 1, j);
                double z0 = rf.getSamplingValueAt(i - 1, j), z1 = rf.getSamplingValueAt(i, j), z2 = rf.getSamplingValueAt(i + 1, j);
                A.x = P0.x; A.y = z0; B.x = P1.x; B.y = z1; C.x = P2.x; C.y = z2;
                if (crit.getValue(A, B, C) > eps) { plist.add(P1); flist.add(z1); }
            }
            // Punkt liegt innerhalb des Rasters: (x, z) und (y, z) ueberpruefen
            else {
                Point2d P0 = rf.getSamplingPointAt(i, j - 1), P1 = rf.getSamplingPointAt(i, j), P2 = rf.getSamplingPointAt(i, j + 1);
                double z0 = rf.getSamplingValueAt(i, j - 1), z1 = rf.getSamplingValueAt(i, j), z2 = rf.getSamplingValueAt(i, j + 1);
                A.x = P0.y; A.y = z0; B.x = P1.y; B.y = z1; C.x = P2.y; C.y = z2;
                if (crit.getValue(A, B, C) > eps) { plist.add(P1); flist.add(z1); } else {
                    P0 = rf.getSamplingPointAt(i - 1, j); P2 = rf.getSamplingPointAt(i + 1, j);
                    z0 = rf.getSamplingValueAt(i - 1, j); z2 = rf.getSamplingValueAt(i + 1, j);
                    A.x = P0.x; A.y = z0; B.x = P1.x; C.x = P2.x; C.y = z2;
                    if (crit.getValue(A, B, C) > eps) { plist.add(P1); flist.add(z1); }
                }
            }}
        
        double[] f = new double[flist.size()];
        for (int i = 0; i < f.length; i++)
            f[i] = flist.get(i).doubleValue();
        
        return new ShepardScalarFunction2d(plist.toArray(new Point2d[f.length]), f);
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion eines Rasters um die Knoten,
     * die nach einer Koordinatentransformation verdeckt sind.
     * @param rf regelmaessiges Raster als <code>RasterScalarFunction2d</code>.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return regelmaessiges Raster als <code>RasterScalarFunction2d</code>.
     */
    public RasterScalarFunction2d reduceNonVisible(RasterScalarFunction2d rf, double k) {
        if (rf == null) return null;
        // Mindestkantenlaenge in Abhaengigkeit der Bildschirmaufloesung bestimmen
        double minL = 1. / k; //1. / (Math.sqrt(2.) * k);
        // Rasterabmessungen abfragen
        int lx = rf.getRowSize(), ly = rf.getColumnSize(); // Anzahl Knoten je Richtung
        Point2d pmin = rf.getSamplingPointAt(0, 0), pmax = rf.getSamplingPointAt(lx - 1, ly - 1);
        double dx = (pmax.x - pmin.x) / (double) (lx - 1), dy = (pmax.y - pmin.y) / (double) (ly - 1);
        // Schrittweiten in nx und ny fuer topologische Reduktion ableiten
        int nx = 1, ny = 1;
        if (minL > dx) nx = (int) (minL / dx) + 1;
        if (minL > dy) ny = (int) (minL / dy) + 1;
        
        return this.reduceTopolog(rf, nx, ny);
    }
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
    /**
     * Reduktion eines Rasters um die Knoten,
     * die ausserhalb einer Bounding Box liegen.
     * @param rf regelmaessiges Raster als <code>RasterScalarFunction2d</code>.
     * @param xmin minimale x-Koordinate einer Bounding Box.
     * @param xmax maximale x-Koordinate einer Bounding Box.
     * @param ymin minimale y-Koordinate einer Bounding Box.
     * @param ymax maximale y-Koordinate einer Bounding Box.
     * @return regelmaessiges Raster als <code>RasterScalarFunction2d</code>.
     */
    public RasterScalarFunction2d reduceNonVisible(RasterScalarFunction2d rf, double xmin, double xmax, double ymin, double ymax) {
        if (rf == null) return null;
        // Rasterabmessungen abfragen
        int lx = rf.getRowSize(), ly = rf.getColumnSize(); // Anzahl Knoten je Richtung
        Point2d pmin = rf.getSamplingPointAt(0, 0), pmax = rf.getSamplingPointAt(lx - 1, ly - 1);
        double dx = (pmax.x - pmin.x) / (double) (lx - 1), dy = (pmax.y - pmin.y) / (double) (ly - 1);
        // Indizies der sichtbaren Rasterelemente bestimmen
        int i0 = (int) ((xmin - pmin.x) / dx);
        if (i0 > lx - 2) return null;
        int i1 = (int) ((xmax - pmin.x) / dx);
        if (i1 < 0) return null;
        int j0 = (int) ((ymin - pmin.y) / dy);
        if (j0 > ly - 2) return null;
        int j1 = (int) ((ymax - pmin.y) / dy);
        if (j1 < 0) return null;
        if (i0 < 0) i0 = 0;
        if (i1 > lx - 2) i1 = lx - 2;
        if (j0 < 0) j0 = 0;
        if (j1 > ly - 2) j1 = ly - 2;
        // Bounding Box bestimmen
        pmin = rf.getSamplingPointAt(i0, j0);
        pmax = rf.getSamplingPointAt(i1, j1);
        // Funktionswerte extrahieren
        int lx_red = i1 - i0 + 1, ly_red = j1 - j0 + 1;
        double[][] f = new double[lx_red][ly_red];
        for (int i = 0; i < lx_red; i++)
            for (int j = 0; j < ly_red; j++)
                f[i][j] = rf.getSamplingValueAt(i0 + i, j0 + j);
        
        return new RasterScalarFunction2d(pmin.x, pmin.y, pmax.x, pmax.y, f);
    }
}