package bijava.graphics;

import bijava.geometry.dim2.Point2d;
import javax.vecmath.Point3d;
import bijava.geometry.dim3.Triangle3d;
import java.util.ArrayList;

public class IsoLineGenerator {

    private static double EPSILON = 0.000001;

    public static ArrayList<IsoEdge2d> getIsoLines(Triangle3d t, double[] isoVal) {
        return getIsoLines(t.getPoint(0), t.getPoint(1), t.getPoint(2), isoVal);
    }

    public static ArrayList<IsoEdge2d> getIsoLines(Point3d p0, Point3d p1, Point3d p2, double[] isoVal) {
        ArrayList<IsoEdge2d> isoLines = new ArrayList<IsoEdge2d>();
        for (int i = 0; i < isoVal.length; i++) {
            IsoEdge2d isoLine = getIsoLine(p0, p1, p2, isoVal[i]);
            if (isoLine != null) {
                isoLines.add(isoLine);
            }
        }
        return isoLines;
    }

    public static IsoEdge2d getIsoLine(Point3d p0, Point3d p1, Point3d p2, double isoValue) {
        Point2d[] p = new Point2d[3];
        int pi = 0;
        double z0 = p0.z, z1 = p1.z, z2 = p2.z;

        // Sonderfaelle abfangen
        if ((z0 - isoValue) == 0.) {
            p[pi++] = new Point2d(p0.x, p0.y);
        }
        if ((z1 - isoValue) == 0.) {
            p[pi++] = new Point2d(p1.x, p1.y);
        }
        if ((z2 - isoValue) == 0.) {
            p[pi++] = new Point2d(p2.x, p2.y);
        }

        // teste, ob Schnitt auf Kante p0-p1
        if ((z0 - isoValue) * (z1 - isoValue) < 0) {
            // Sonderfall
            if ((z1 - z0) == 0.) {
                z1 += EPSILON;
            }
            double lambda = (isoValue - z0) / (z1 - z0);
            p[pi++] = new Point2d(p0.x + lambda * (p1.x - p0.x), p0.y + lambda * (p1.y - p0.y));
        }
        // teste, ob Schnitt auf Kante p1-p2
        if ((z1 - isoValue) * (z2 - isoValue) < 0) {
            // Sonderfall
            if ((z2 - z1) == 0.) {
                z2 += EPSILON;
            }
            double lambda = (isoValue - z1) / (z2 - z1);
            p[pi++] = new Point2d(p1.x + lambda * (p2.x - p1.x), p1.y + lambda * (p2.y - p1.y));
        }
        if (pi == 0) {
            return null;
        }
        // teste, ob Schnitt auf Kante p2-p0
        if ((z2 - isoValue) * (z0 - isoValue) < 0) {
            //Sonderfall
            if ((z0 - z2) == 0.) {
                z0 += EPSILON;
            }
            double lambda = (isoValue - z2) / (z0 - z2);
            p[pi++] = new Point2d(p2.x + lambda * (p0.x - p2.x), p2.y + lambda * (p0.y - p2.y));
        }
        if (pi == 2) {
            return new IsoEdge2d(p[0], p[1], isoValue);
        }
        return null;
    }

    /** betimmt die Isolinien in einem Viereck - gegen den Uhrzeiger nummeriert */
    public static ArrayList<IsoEdge2d> getIsoLines(Point3d p0, Point3d p1, Point3d p2, Point3d p3, double isoVal) {
        Point3d pm = new Point3d(0.25 * (p0.x + p1.x + p2.x + p3.x), 0.25 * (p0.y + p1.y + p2.y + p3.y), 0.25 * (p0.z + p1.z + p2.z + p3.z));
        ArrayList<IsoEdge2d> isoLines = new ArrayList<IsoEdge2d>();

        IsoEdge2d isoLine = getIsoLine(p0, p1, pm, isoVal);
        if (isoLine != null) {
            isoLines.add(isoLine);
        }

        isoLine = getIsoLine(p1, p2, pm, isoVal);
        if (isoLine != null) {
            isoLines.add(isoLine);
        }

        isoLine = getIsoLine(p2, p3, pm, isoVal);
        if (isoLine != null) {
            isoLines.add(isoLine);
        }

        isoLine = getIsoLine(p3, p0, pm, isoVal);
        if (isoLine != null) {
            isoLines.add(isoLine);
        }

        return isoLines;
    }
}
