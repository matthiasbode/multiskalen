package bijava.math.function.interpolation;

import bijava.geometry.dim2.BoundingBox2d;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.SimplePolygon2d;
import bijava.geometry.dim2.Vector2d;

import bijava.math.function.DifferentialScalarFunction2d;
import bijava.math.function.ScalarFunction2d;
import java.awt.BorderLayout;
import java.awt.Color;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/* ToDo im Feld f der Funktionswerte können NaN auftreten, da wo keine Werte vorliegen stehen dann Double.NaN
 * bei der Interpolation und der Berechnung der Gradienten müssen diese Sonderfälle beachtet werden
 */
import java.util.zip.ZipOutputStream;
import javax.swing.JFrame;
import javax.vecmath.GMatrix;

/**
 * RasterScalarFunction2d.java provides properties and methods for a twodimensional
 * raster scalar function.
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dr.-Ing. habil. Peter Milbradt<br>
 *  Dipl.-Ing. Mario Hoecker<br>
 *  Matthias Bode<br>
 * @version 1.2, Februar 2008
 */
public class RasterScalarFunction2d extends DiscretizedScalarFunction2d implements DifferentialScalarFunction2d {

    private String nodata_value = "-9999";
    protected double xmin, ymin, xmax, ymax; // bounding box
    protected double[][] f; // values of the sampling points
    protected double dx, dy; // dimension of a cell

    private RasterScalarFunction2d() {
    }

    public RasterScalarFunction2d(double xmin, double ymin, double xmax, double ymax, int nx, int ny, ScalarFunction2d fkt) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;

        this.f = new double[nx][ny];

        dx = (xmax - xmin) / (f.length - 1);
        dy = (ymax - ymin) / (f[0].length - 1);

        size = f.length * f[0].length;
        minValue = Double.POSITIVE_INFINITY;
        maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                f[i][j] = fkt.getValue(new Point2d(xmin + i * dx, ymin + j * dy));
                if (!Double.isNaN(f[i][j])) {
                    minValue = Math.min(minValue, f[i][j]);
                    maxValue = Math.max(maxValue, f[i][j]);
                }
            }
        }
    }

    /**
     * Creates a twodimensional raster scalar function.
     * @param xmin minimal x-coordinate.
     * @param ymin minimal y-coordinate.
     * @param xmax maximal x-coordinate.
     * @param ymax maximal y-coordinate.
     * @param f array containing the values of the sampling points.
     */
    public RasterScalarFunction2d(double xmin, double ymin, double xmax, double ymax, double[][] f) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.f = f;
        dx = (xmax - xmin) / (f.length - 1);
        dy = (ymax - ymin) / (f[0].length - 1);

        size = f.length * f[0].length;
        minValue = Double.POSITIVE_INFINITY;
        maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                if (!Double.isNaN(f[i][j])) {
                    minValue = Math.min(minValue, f[i][j]);
                    maxValue = Math.max(maxValue, f[i][j]);
                }
            }
        }
    }

    /**
     * Creates a twodimensional raster scalar function.
     * @param xmin minimal x-coordinate.
     * @param ymin minimal y-coordinate.
     * @param xmax maximal x-coordinate.
     * @param ymax maximal y-coordinate.
     * @param Java3D general matrix containing the values of the sampling points.
     */
    public RasterScalarFunction2d(double xmin, double ymin, double xmax, double ymax, GMatrix matrix) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;

        final int cols = matrix.getNumCol();
        final int rows = matrix.getNumRow();
        this.f = new double[cols][rows];
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                f[col][row] = matrix.getElement(row, col);
            }
        }

        dx = (xmax - xmin) / (f.length - 1);
        dy = (ymax - ymin) / (f[0].length - 1);

        size = f.length * f[0].length;
        minValue = Double.POSITIVE_INFINITY;
        maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                if (!Double.isNaN(f[i][j])) {
                    minValue = Math.min(minValue, f[i][j]);
                    maxValue = Math.max(maxValue, f[i][j]);
                }
            }
        }
    }

    public double getMinX() {
        return xmin;
    }

    public double getMinY() {
        return ymin;
    }

    public double getMaxX() {
        return xmax;
    }

    public double getMaxY() {
        return ymax;
    }

    /**
     * Creates a twodimensional raster scalar function by reading a xyz-file.
     * @param nam save location of a xyz-file.
     * @return twodimensional raster scalar function.
     */
    public static RasterScalarFunction2d readfromXYZDat(String nam) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(nam));
            String line = input.readLine();
            StringTokenizer st = new StringTokenizer(line);
            int lx = Integer.parseInt(st.nextToken()), ly = Integer.parseInt(st.nextToken());
            double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY;
            double ymin = xmin, ymax = xmax;
            double[][] f = new double[lx][ly];
            for (int j = ly - 1; j > -1; j--) {
                for (int i = 0; i < lx; i++) {
                    line = input.readLine();
                    st = new StringTokenizer(line);
                    double x = Double.parseDouble(st.nextToken());
                    if (x < xmin) {
                        xmin = x;
                    }
                    if (x > xmax) {
                        xmax = x;
                    }
                    double y = Double.parseDouble(st.nextToken());
                    if (y < ymin) {
                        ymin = y;
                    }
                    if (y > ymax) {
                        ymax = y;
                    }
                    f[i][j] = Double.parseDouble(st.nextToken());
                }
            }
            input.close();
            System.out.println(nam + " gelesen");
            return new RasterScalarFunction2d(xmin, ymin, xmax, ymax, f);
        } catch (IOException e) {
            System.out.println(nam + " konnte nicht gelesen werden");
            return null;
        }
    }

    /**
     * Saves a twodimensional raster scalar function as xyz-file.
     * @param rf twodimensional raster scalar function.
     * @param nam save location of a xyz-file.
     */
    public static void writetoXYZDat(RasterScalarFunction2d rf, String nam) {
        int lx = rf.getRowSize(), ly = rf.getColumnSize();
        try {
            PrintWriter output = new PrintWriter(new FileWriter(nam));
            output.println(lx + " " + ly);
            for (int j = ly - 1; j > -1; j--) {
                for (int i = 0; i < lx; i++) {
                    Point2d p = rf.getSamplingPointAt(i, j);
                    double value = rf.getSamplingValueAt(i, j);
                    output.println(p.x + " " + p.y + " " + value);
                }
            }
            output.close();
            System.out.println(nam + " geschrieben");
        } catch (IOException e) {
            System.out.println("Raster " + rf + " konnte nicht nach " + nam + " geschrieben werden");
        }
    }

    /**
     * Reads a twodimensional raster scalar function from binaery file.
     * @param nam save location.
     * @return twodimensional raster scalar function.
     */
    public static RasterScalarFunction2d readfromRasterBin(String nam) {
        try {
            DataInputStream is = new DataInputStream(new FileInputStream(nam));
            double xmin = is.readDouble(), xmax = is.readDouble();
            int lx = is.readInt();
            double ymin = is.readDouble(), ymax = is.readDouble();
            int ly = is.readInt();
            double[][] f = new double[lx][ly];
            for (int i = 0; i < lx; i++) {
                for (int j = 0; j < ly; j++) {
                    f[i][j] = is.readDouble();
                }
            }
            is.close();
            System.out.println(nam + " gelesen");
            return new RasterScalarFunction2d(xmin, ymin, xmax, ymax, f);
        } catch (IOException e) {
            System.out.println(nam + " konnte nicht gelesen werden");
            return null;
        }
    }

    /**
     * Writes a twodimensional raster scalar function to binaery file.
     * @param rf twodimensional raster scalar function.
     * @param nam save location.
     */
    public static void writetoRasterBin(RasterScalarFunction2d rf, String nam) {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(nam));
            int lx = rf.f.length, ly = rf.f[0].length;
            os.writeDouble(rf.xmin);
            os.writeDouble(rf.xmax);
            os.writeInt(lx);
            os.writeDouble(rf.ymin);
            os.writeDouble(rf.ymax);
            os.writeInt(ly);
            for (int i = 0; i < lx; i++) {
                for (int j = 0; j < ly; j++) {
                    os.writeDouble(rf.f[i][j]);
                }
            }
            os.close();
            System.out.println(nam + " geschrieben");
        } catch (IOException e) {
            System.out.println("Raster " + rf + " konnte nicht nach " + nam + " geschrieben werden");
        }
    }

    /**
     * Gets the value of a point p by bilinear interpolation.
     * @param p twodimensional point.
     */
    @Override
    public double getValue(Point2d p) {
        // linear interpolation on east boundary
        if (p.x == xmax && (p.y >= ymin && p.y < ymax)) {
            int j = (int) ((p.y - ymin) / dy);
            double y1 = ymin + j * dy, y2 = ymin + (j + 1) * dy, z1 = f[f.length - 1][j], z2 = f[f.length - 1][j + 1];
            return (z2 - z1) * (p.y - y1) / (y2 - y1) + z1;
        }
        // linear interpolation on north boundary
        if (p.y == ymax && (p.x >= xmin && p.x < xmax)) {
            int i = (int) ((p.x - xmin) / dx);
            double x1 = xmin + i * dx, x2 = xmin + (i + 1) * dx, z1 = f[i][f[0].length - 1], z2 = f[i + 1][f[0].length - 1];
            return (z2 - z1) * (p.x - x1) / (x2 - x1) + z1;
        }
        // identification of the grid cell by indicies
        int i = (int) ((p.x - xmin) / dx), j = (int) ((p.y - ymin) / dy);

        if (i < 0 || i >= f.length - 1 || j < 0 || j >= f[0].length - 1) {
            return Double.NaN;
        }

        // bilinear interpolation
        double xa = xmin + i * dx, ya = ymin + j * dy, xb = xmin + (i + 1) * dx, yb = ymin + (j + 1) * dy;
        double z1 = f[i][j], z2 = f[i + 1][j], z3 = f[i + 1][j + 1], z4 = f[i][j + 1];

        return ((yb - p.y) * (xb - p.x) * z1 + (p.y - ya) * (p.x - xa) * z3
                + (yb - p.y) * (p.x - xa) * z2 + (p.y - ya) * (xb - p.x) * z4) / ((yb - ya) * (xb - xa));
    }

    /**
     * Get all data values by reference.
     * @return      Array of double
     */
    public final double[][] getValuesRef() {
        return f;
    }

    /**
     * Gets the value of a sampling point.
     * @param i position of the sampling point in this function.
     * @return value of a sampling point.
     */
    @Override
    public double getSamplingValueAt(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("You can't get a value at " + i);
        }
        //return f[i / f.length][i % f.length];
        return f[i % f.length][i / f.length];
    }

    /**
     * Gets the value of a sampling point.
     * @param i x-position of the sampling point in this function.
     * @param j y-position of the sampling point in this function.
     * @return value of a sampling point.
     */
    public double getSamplingValueAt(int i, int j) {
        if (i < 0 || i >= f.length || j < 0 || j >= f[0].length) {
            throw new IndexOutOfBoundsException("You can't get a value at " + i + " " + j);
        }
        return f[i][j];
    }

    /**
     * Gets a sampling point.
     * @param i position of the sampling point in this function.
     * @return a sampling point.
     */
    @Override
    public Point2d getSamplingPointAt(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("You can't get a point at " + i);
        }
        //return new Point2d(xmin + (i / f.length) * dx, ymin + (i % f.length) * dy);
        return new Point2d(xmin + (i % f.length) * dx, ymin + (i / f.length) * dy);
    }

    /**
     * Gets a sampling point.
     * @param i x-position of the sampling point in this function.
     * @param j y-position of the sampling point in this function.
     * @return a sampling point.
     */
    public Point2d getSamplingPointAt(int i, int j) {
        if (i < 0 || i >= f.length || j < 0 || j >= f[0].length) {
            throw new IndexOutOfBoundsException("You can't get a point at " + i + " " + j);
        }
        return new Point2d(xmin + i * dx, ymin + j * dy);
    }

    /**
     * Sets the value of a sampling point.
     * @param i position of a sampling point in this function.
     * @param value settig value.
     */
    @Override
    public void setValueAt(int i, double value) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("You can't set a value at " + i);
        }
        //f[i / f.length][i % f.length] = value;
        f[i % f.length][i / f.length] = value;
    }

    /**
     * Sets the value of a sampling point.
     * @param i x-position of the sampling point in this function.
     * @param j y-position of the sampling point in this function.
     * @param value settig value.
     */
    public void setValueAt(int i, int j, double value) {
        if (i < 0 || i >= f.length || j < 0 || j >= f[0].length) {
            throw new IndexOutOfBoundsException("You can't set a value at " + i + " " + j);
        }
        f[i][j] = value;
    }

    /**
     * Sets the values of all sampling points with a values of a specified scalar function.
     * @param function twodimensional scalar function.
     */
    public void setValues(ScalarFunction2d function) {
        for (int i = 0; i < size; i++) {
            this.setValueAt(i, function.getValue(samplingPoints[i]));
        }
    }

    /**
     * Gets the number of sampling points in row of this function.
     * @return number of sampling points in row of this function.
     */
    public int getRowSize() {
        return f.length;
    }

    /**
     * Gets the number of sampling points in column of this function.
     * @return number of sampling points in column of this function.
     */
    public int getColumnSize() {
        return f[0].length;
    }

    /** 
     * @return Rasterweite in x-Richtung
     */
    public double getDx() {
        return dx;
    }

    /** 
     * @return Rasterweite in y-Richtung
     */
    public double getDy() {
        return dy;
    }

    public double getDxy(int r, int c) {
        if ((r > 0) && (r < f.length - 1) && (c > 0) && (c < f[0].length - 1)) {
            double dxy = Math.sqrt(dx * dx + dy * dy);
            double ldx = (f[r][c] - f[r - 1][c - 1]) / dxy;
            double rdx = (f[r + 1][c + 1] - f[r][c]) / dxy;
            return (rdx - ldx) / dxy;
        } else {
            return 0.;
        }
    }

    public double getDyx(int r, int c) {
        if ((r > 0) && (r < f.length - 1) && (c > 0) && (c < f[0].length - 1)) {
            double dxy = Math.sqrt(dx * dx + dy * dy);
            double ldx = (f[r][c] - f[r - 1][c + 1]) / dxy;
            double rdx = (f[r + 1][c - 1] - f[r][c]) / dxy;
            return (rdx - ldx) / dxy;
        } else {
            return 0.;
        }
    }

    /**
     * Gets the confidence region of this function.
     * @return confidence region of this function as <code>Polygon2d</code>.
     */
    @Override
    public SimplePolygon2d getConfidenceRegion() {
        Point2d[] reg = {new Point2d(xmin, ymin), new Point2d(xmax, ymin), new Point2d(xmax, ymax), new Point2d(xmin, ymax)};
        return new SimplePolygon2d(reg);
    }

    /**
     * Gets a barycentric triangulated scalar function of this function.
     * @return barycentric triangulated scalar function of this function.
     */
    public TriangulatedScalarFunction2d getBarycentricTriangulatedScalarFunction2d() {
        return TriangulatedScalarFunction2d.getBarycentricTriangulatedScalarFunction2d(this);
    }

    /**
     * Converts the proverties of this function to a string.
     * @return proverties of this function as a string.
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < size; i++) {
            s += " " + this.getSamplingPointAt(i) + " " + this.getSamplingValueAt(i);
        }
        return s;
    }

    public Vector2d getGradient(int r, int c) {
        Vector2d rvalue = null;
        double dfdx = 0.;
        double dfdy = 0.;
        if ((r > 0) && (r < f.length - 1) && (c > 0) && (c < f[0].length - 1)) {
            dfdx = ((f[r + 1][c + 1] + Math.sqrt(2.) * f[r + 1][c] + f[r + 1][c - 1]) - (f[r - 1][c + 1] + Math.sqrt(2.) * f[r - 1][c] + f[r - 1][c - 1])) / (4. + 2 * Math.sqrt(2.)) / dx;
            dfdy = ((f[r - 1][c + 1] + Math.sqrt(2.) * f[r][c + 1] + f[r + 1][c + 1]) - (f[r - 1][c - 1] + Math.sqrt(2.) * f[r][c - 1] + f[r + 1][c - 1])) / (4. + 2 * Math.sqrt(2.)) / dy;
        } else {
            if ((r == 0) && (c > 0) && (c < f[0].length - 1)) {
                dfdx = (f[r + 1][c] - f[r][c]) / dx;
                dfdy = (f[r][c + 1] - f[r][c - 1]) / 2. / dy;

            }
            if ((r == f.length - 1) && (c > 0) && (c < f[0].length - 1)) {
                dfdx = (f[r][c] - f[r - 1][c]) / dx;
                dfdy = (f[r][c + 1] - f[r][c - 1]) / 2. / dy;

            }
            if ((r > 0) && (r < f.length - 1) && (c == 0)) {
                dfdx = (f[r + 1][c] - f[r - 1][c]) / 2. / dx;
                dfdy = (f[r][c + 1] - f[r][c]) / dy;

            }
            if ((r > 0) && (r < f.length - 1) && (c == f[0].length - 1)) {
                dfdx = (f[r + 1][c] - f[r - 1][c]) / 2. / dx;
                dfdy = (f[r][c] - f[r][c - 1]) / dy;

            }
        }

        rvalue = new Vector2d(dfdx, dfdy);
        return rvalue;
    }

    @Override
    public RasterScalarFunction2d[] getDerivation() {
        RasterScalarFunction2d[] rvalue = new RasterScalarFunction2d[2];

        double[][] dfdx = new double[f.length][f[0].length];
        double[][] dfdy = new double[f.length][f[0].length];

        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                Vector2d v = getGradient(i, j);
                dfdx[i][j] = v.x;
                dfdy[i][j] = v.y;
            }
        }

        rvalue[0] = new RasterScalarFunction2d(xmin, ymin, xmax, ymax, dfdx);
        rvalue[1] = new RasterScalarFunction2d(xmin, ymin, xmax, ymax, dfdy);

        return rvalue;
    }

       public RasterScalarFunction2d getTotalDerivation() {

        RasterScalarFunction2d rvalue;

        double[][] dfdx = new double[f.length][f[0].length];
        double[][] dfdy = new double[f.length][f[0].length];
        double[][] dfdxdy = new double[f.length][f[0].length];

        for(int i=0;i<f.length;i++)
            for(int j=0;j<f[0].length;j++){
                Vector2d v=getGradient(i,j);
                dfdx[i][j]=v.x;
                dfdy[i][j]=v.y;
                dfdxdy[i][j] = v.x+v.y;

            }

        rvalue = new RasterScalarFunction2d(xmin, ymin, xmax, ymax, dfdxdy);

        return rvalue;

    }

    public double getDxx(int r, int c) {
        if ((r > 0) && (r < f.length - 1) && (c > 0) && (c < f[0].length - 1)) {
            double ldx = (f[r][c] - f[r - 1][c]) / dx;
            double rdx = (f[r + 1][c] - f[r][c]) / dx;
            return (rdx - ldx) / dx;
        } else {
            return 0.;
        }
    }

    public double getDyy(int r, int c) {
        if ((r > 0) && (r < f.length - 1) && (c > 0) && (c < f[0].length - 1)) {
            double ldy = (f[r][c] - f[r][c - 1]) / dy;
            double rdy = (f[r][c + 1] - f[r][c]) / dy;
            return (rdy - ldy) / dy;
        } else {
            return 0.;
        }
    }

    public RasterScalarFunction2d[] getSecondDerivation() {
        RasterScalarFunction2d[] rvalue = new RasterScalarFunction2d[2];

        double[][] dfdx = new double[f.length][f[0].length];
        double[][] dfdy = new double[f.length][f[0].length];

        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                dfdx[i][j] = getDxx(i, j);
                dfdy[i][j] = getDyy(i, j);
            }
        }

        rvalue[0] = new RasterScalarFunction2d(xmin, ymin, xmax, ymax, dfdx);
        rvalue[1] = new RasterScalarFunction2d(xmin, ymin, xmax, ymax, dfdy);

        return rvalue;
    }

    public Vector2d getInterpolatedGradient(Point2d p) {
        ScalarFunction2d[] d = getDerivation();
        return new Vector2d(d[0].getValue(p), d[1].getValue(p));
    }

    public Vector2d getBilinearGradient(Point2d p) {

        if (p.x == xmax && (p.y >= ymin && p.y <= ymax)) {
            int j = (int) ((p.y - ymin) / dy);

            double xa = xmax - dx, ya = ymin + j * dy, xb = xmax, yb = ymin + (j + 1) * dy;
            double z1 = f[f.length - 2][j], z2 = f[f.length - 1][j], z3 = f[f.length - 1][j + 1], z4 = f[f.length - 2][j + 1];
            double d_y = ((z3 - z2) / (yb - ya));

            double wert_am_Punkt = ((z3 - z2) / (yb - ya)) * (p.y - ya) + z2;
            double Q1_zwischen = ((z4 - z1) / (yb - ya)) * (p.y - ya) + z1;
            double d_x = (wert_am_Punkt - Q1_zwischen) / (xb - xa);

            return new Vector2d(d_x, d_y);
        }
        // linear interpolation on north boundary
        if (p.y == ymax && (p.x >= xmin && p.x <= xmax)) {
            int i = (int) ((p.x - xmin) / dx);

            double xa = xmin + i * dx, ya = ymax - dy, xb = xmin + (i + 1) * dx, yb = ymax;
            double z1 = f[i][f[0].length - 2], z2 = f[i + 1][f[0].length - 2], z3 = f[i + 1][f[0].length - 1], z4 = f[i][f[0].length - 1];
            double d_x = ((z3 - z4) / (xb - xa));


            double wert_am_Punkt = ((z3 - z4) / (xb - xa)) * (p.x - xa) + z4;
            double Q1_zwischen = ((z2 - z1) / (xb - xa)) * (p.x - ya) + z1;
            double d_y = (wert_am_Punkt - Q1_zwischen) / (yb - ya);

            return new Vector2d(d_x, d_y);
        }

        // identification of the grid cell by indicies
        int i = (int) ((p.x - xmin) / dx), j = (int) ((p.y - ymin) / dy);

        if (i < 0 || i >= f.length - 1 || j < 0 || j >= f[0].length - 1) {
            return null;
        }

        //Festlegen der Bezeichnungen fuer das aktuelle Element
        double xa = xmin + i * dx, ya = ymin + j * dy, xb = xmin + (i + 1) * dx, yb = ymin + (j + 1) * dy;
        double z1 = f[i][j], z2 = f[i + 1][j], z3 = f[i + 1][j + 1], z4 = f[i][j + 1];

        double u_Q1_zwischen = ((z4 - z1) / (yb - ya)) * (p.y - ya) + z1;
        double u_Q2_zwischen = ((z3 - z2) / (yb - ya)) * (p.y - ya) + z2;
        double u_R1_zwischen = ((z2 - z1) / (xb - xa)) * (p.x - xa) + z1;
        double u_R2_zwischen = ((z3 - z4) / (xb - xa)) * (p.x - xa) + z4;
        double u_d_x = (u_Q2_zwischen - u_Q1_zwischen) / (xb - xa);
        double u_d_y = (u_R2_zwischen - u_R1_zwischen) / (yb - ya);

        return new Vector2d(u_d_x, u_d_y);
    }

    public Vector2d getGradient(Point2d p) {

        // identification of the grid cell by indicies
        int i = (int) ((p.x - xmin) / dx), j = (int) ((p.y - ymin) / dy);

        if (i < 0 || i >= f.length - 1 || j < 0 || j >= f[0].length - 1) {
            return null;
        }

        Vector2d d = null;

        // bilinear Interpolation of the Gradient
        // linear interpolation on east boundary
        if (p.x == xmax && (p.y >= ymin && p.y <= ymax)) {
            double y1 = ymin + j * dy, y2 = ymin + (j + 1) * dy;
            Vector2d v1 = getGradient(f.length - 1, j);
            Vector2d v2 = getGradient(f.length - 1, j + 1);
            d = v1.add((v2.sub(v1)).mult((p.y - y1) / (y2 - y1)));
        }
        // linear interpolation on north boundary
        if (p.y == ymax && (p.x >= xmin && p.x <= xmax)) {
            double x1 = xmin + i * dx, x2 = xmin + (i + 1) * dx;
            Vector2d v1 = getGradient(i, f[0].length - 1);
            Vector2d v2 = getGradient(i + 1, f[0].length - 1);
            d = v1.add((v2.sub(v1)).mult((p.x - x1) / (x2 - x1)));
        }

        // bilinear interpolation
        double xa = xmin + i * dx, ya = ymin + j * dy, xb = xmin + (i + 1) * dx, yb = ymin + (j + 1) * dy;

        Vector2d v1 = getGradient(i, j);
        Vector2d v2 = getGradient(i + 1, j);
        Vector2d v3 = getGradient(i + 1, j + 1);
        Vector2d v4 = getGradient(i, j + 1);

        d = (v1.mult((yb - p.y) * (xb - p.x)));
        d = d.add(v2.mult((p.y - ya) * (p.x - xa)));
        d = d.add(v3.mult((yb - p.y) * (p.x - xa)));
        d = d.add(v4.mult((p.y - ya) * (xb - p.x)));
        d = d.mult(1. / ((yb - ya) * (xb - xa)));

        double xm = xb - dx / 2., ym = yb - dy / 2.;
        //Berechnung der Wichtungsfaktoren f�r beide Algorithmen  
        double wichtung_y = Math.abs(2. * (p.y - ym) / dy);
        double wichtung_x = Math.abs(2. * (p.x - xm) / dx);

        double lambda = Math.sqrt(wichtung_y * wichtung_y + wichtung_x * wichtung_x) / Math.sqrt(2.);

        Vector2d bd = getBilinearGradient(p);

        return new Vector2d((1. - lambda) * bd.x + lambda * d.x, (1. - lambda) * bd.y + lambda * d.y);
    }

    private Vector2d getGradientSlow(Point2d p) {

        // identification of the grid cell by indicies
        int i = (int) ((p.x - xmin) / dx), j = (int) ((p.y - ymin) / dy);

        if (i < 0 || i >= f.length - 1 || j < 0 || j >= f[0].length - 1) {
            return null;
        }

        ScalarFunction2d[] d = getDerivation();

        //Festlegen der Bezeichnungen fuer das aktuelle Element
        double xb = xmin + (i + 1) * dx, yb = ymin + (j + 1) * dy;
        double xm = xb - dx / 2., ym = yb - dy / 2.;
        //Berechnung der Wichtungsfaktoren fuer beide Algorithmen  
        double wichtung_y = Math.abs(2. * (p.y - ym) / dy);
        double wichtung_x = Math.abs(2. * (p.x - xm) / dx);
        System.out.println("wichtung_y " + wichtung_y);
        System.out.println("wichtung_x " + wichtung_x);

        double lambda = Math.sqrt(wichtung_y * wichtung_y + wichtung_x * wichtung_x) / Math.sqrt(2.);
        System.out.println("lambda " + lambda);

        Vector2d bd = getBilinearGradient(p);

        return new Vector2d((1. - lambda) * bd.x + lambda * d[0].getValue(p), (1. - lambda) * bd.y + lambda * d[1].getValue(p));
    }

    /**
     * Liest die angegebene ZIP-Datei im ESRI-ASCII-Grid-Format ein.
     * HINWEIS: TopoPoint[i][j] kann null sein, wenn kein gültiger Tiefenwert vorliegt.
     * @param fn Dateiname
     */
    public static RasterScalarFunction2d importZippedAscii(String fn) throws IOException, ParseException {
        FileInputStream fis = new FileInputStream(fn);
        ZipInputStream zis = new ZipInputStream(fis);
        zis.getNextEntry();
        InputStreamReader isr = new InputStreamReader(zis);
        BufferedReader br = new BufferedReader(isr);
        return readGridFromReader(br);
    }

    /**
     * Liest die angegebene Datei im ESRI-ASCII-Grid-Format ein.
     * @param fn Dateiname
     */
    public static RasterScalarFunction2d importAscii(String fn) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(fn));
        return readGridFromReader(br);
    }

    private static RasterScalarFunction2d readGridFromReader(BufferedReader br) throws IOException, ParseException {

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(8);

        int ncols = 0, nrows = 0;

        RasterScalarFunction2d grid = new RasterScalarFunction2d();
        String line = null;
        StringTokenizer tokenizer = null;

        // Header parsen
        while ((line = br.readLine()) != null) {

            line = line.replace('.', ',');
            StringTokenizer strto = new StringTokenizer(line, " \t\n\r\f");
            String key = strto.nextToken();

            if (key.equals("ncols")) {
                ncols = Integer.parseInt(strto.nextToken());
//                 System.out.println(ncols);

            } else if (key.equals("nrows")) {
                nrows = Integer.parseInt(strto.nextToken());

            } else if (key.equals("xllcorner")) {
                grid.xmin = numberFormat.parse(strto.nextToken()).doubleValue();

            } else if (key.equals("yllcorner")) {
                grid.ymin = numberFormat.parse(strto.nextToken()).doubleValue();

            } else if (key.equals("cellsize")) {
                grid.dy = grid.dx = numberFormat.parse(strto.nextToken()).doubleValue();

            } else if (key.equals("NODATA_value")) {
                grid.nodata_value = strto.nextToken();

            } else {
                break;
            }
        }
        grid.xmax = grid.xmin + grid.dx * ncols;
        grid.ymax = grid.ymin + grid.dy * nrows;
        // END - Header parsen

        int i = 0, j = nrows - 1;
        grid.f = new double[ncols][nrows];

        grid.size = ncols * nrows;

        do {
            line = line.replace('.', ',');
            tokenizer = new StringTokenizer(line, " \t\n\r\f");
            while (tokenizer.hasMoreTokens()) {

                String token = tokenizer.nextToken();
                if (!token.equals(grid.nodata_value)) {
                    grid.f[i][j] = numberFormat.parse(token).doubleValue();
                } else {
                    grid.f[i][j] = Double.NaN;
                }

                i++;
                if (i == ncols) {
                    i = 0;
                    j--;
                }
            }
        } while ((line = br.readLine()) != null);

        br.close();

        return grid;
    }

    /**
     * Schreibt die Punktdatenim ESRI-ASCII-Grid-Format raus.
     * @param fn Dateiname
     */
    public void exportAscii(String fn) throws IOException {

        if (f == null) {
            throw new RuntimeException("No grid set yet. Call 'setGrid' or 'read' first");
        }

        FileWriter fw = new FileWriter(fn);
        fw.write(writeGridToStringBuffer().toString());
        fw.flush();
        fw.close();
    }

    /**
     * Schreibt die Punktdaten im ESRI-ASCII-Grid-Format in ein gezipptes File raus.
     * @param fn Dateiname
     */
	public void exportZippedAscii(String fn) throws IOException {
	
		if (f == null) {
			throw new RuntimeException("No grid set yet.");
		}
		
		// erstmal der triviale Weg
		StringBuffer buffer = writeGridToStringBuffer();
		byte[] byteArray = buffer.toString().getBytes();
		
		FileOutputStream fos = new FileOutputStream(fn);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry zipEntry = new ZipEntry("ESRIGrid.grd");
		zos.putNextEntry(zipEntry);
		zos.write(byteArray);
		zos.close();
		fos.close();
	}

    private StringBuffer writeGridToStringBuffer() {

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(8);
        final String newline = System.getProperty("line.separator");

        if (f == null) {
            throw new RuntimeException("No grid set yet. Call 'setGrid' or 'read' first");
        }


        StringBuffer buffer = new StringBuffer();

        buffer.append("ncols ");
        buffer.append(numberFormat.format(f.length));
        buffer.append(newline);
        buffer.append("nrows ");
        buffer.append(numberFormat.format(f[0].length));
        buffer.append(newline);
        buffer.append("xllcorner ");
        buffer.append(numberFormat.format(xmin));
        buffer.append(newline);
        buffer.append("yllcorner ");
        buffer.append(numberFormat.format(ymin));
        buffer.append(newline);
        buffer.append("cellsize ");
        buffer.append(numberFormat.format(dx));
        buffer.append(newline);
        buffer.append("NODATA_value ");
        buffer.append(nodata_value);
        buffer.append(newline);

        for (int j = f[0].length - 1; j > -1; j--) {
            for (int i = 0; i < f.length; i++) {
                if (!Double.isNaN(f[i][j])) {
                    buffer.append(numberFormat.format(f[i][j]));
                } else {
                    buffer.append(nodata_value);
                }
                buffer.append(" ");
            }
            buffer.append(newline);
        }

        return buffer;
    }

    /**
     * Exportiert eine Rasterfunktion in ein Graustufen-PNG-Bild gleicher Groesse. Die Farbwerte werden auf min/max im Raster skaliert.
     * @param filename
     */
    public void writeToPngImage(String filename) {

        if (filename.endsWith(".png") == false) {
            filename = filename.concat(".png");
        }

        final int M = this.getRowSize();
        final int N = this.getColumnSize();

        final double max = this.getMax();
        final double min = this.getMin();

        BufferedImage img = new BufferedImage(M, N, BufferedImage.TYPE_INT_RGB);

        System.out.println("Speichere " + filename + "...");

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                double v = this.getValuesRef()[i][j];
                final int colB = (int) (255. * (v - min) / (max - min));
                final int colI = (colB << 16) + (colB << 8) + colB;
                img.setRGB(i, N - 1 - j, colI);
            }
        }
        try {
            if (filename.isEmpty() == false) {
                ImageIO.write(img, "png", new File(filename));
            }
        } catch (IOException ex) {
            System.out.println("Fehler beim Speichern! " + ex);
        } finally {
            System.out.println("...fertig.");
        }

    }

    /**
     * skaliert alle Funktionswert
     * @param s
     */
    public void scale(double s) {
        minValue = Double.POSITIVE_INFINITY;
        maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                f[i][j] *= s;
                if (!Double.isNaN(f[i][j])) {
                    minValue = Math.min(minValue, f[i][j]);
                    maxValue = Math.max(maxValue, f[i][j]);
                }
            }
        }
    }
 
}
