package bijava.graphics;

import bijava.geometry.dim3.Point3d;
import bijava.geometry.dim3.Triangle3d;
import java.util.ArrayList;

public abstract class IsoSurfaceGenerator {
    private static double EPSILON = 0.000001;
    
    public static ArrayList<IsoSurface> getIsoSurfaces(Triangle3d t, double[] isoVal) {
        return getIsoSurfaces(t.getPoint(0), t.getPoint(1), t.getPoint(2), isoVal);
    }
    
    public static ArrayList<IsoSurface> getIsoSurfaces(Point3d p0, Point3d p1, Point3d p2, double[] isoVal) {
        ArrayList<IsoSurface> isoSurfaces = new ArrayList<IsoSurface>();
        for (int i = 0; i < isoVal.length - 1; i++) {
            IsoSurface isoSurface = getIsoSurface(p0, p1, p2, isoVal[i], isoVal[i + 1]);
            if (isoSurface != null) isoSurfaces.add(isoSurface);
        }
        return isoSurfaces;
    }
    
    public static IsoSurface getIsoSurface(Point3d p0, Point3d p1, Point3d p2, double minVal, double maxVal) {
        // Dreieck hat keine mittlere Hoehe
        if (Double.isNaN(p0.z) || Double.isNaN(p1.z) || Double.isNaN(p2.z)) return null;
        // Dreieck liegt nicht zwischen den Isowerten
        if (p0.z < minVal && p1.z < minVal && p2.z < minVal) return null;
        if (p0.z > maxVal && p1.z > maxVal && p2.z > maxVal) return null;
        
        // Isowerte minimal aendern falls im Bereich der Dreieckshoehen
        double rEPSILON = EPSILON * (maxVal - minVal);
        double minValDYN = minVal, maxValDYN = maxVal, dVal = 2 * rEPSILON;
        boolean b0 = true, b1 = true, b2 = true, b3 = true, b4 = true, b5 = true;
        while (true) {
            if (b0 && Math.abs(p0.z - minValDYN) < rEPSILON) { minValDYN -= dVal; b0 = false;
            } else if (b1 && Math.abs(p1.z - minValDYN) < rEPSILON) { minValDYN -= dVal; b1 = false;
            } else if (b2 && Math.abs(p2.z - minValDYN) < rEPSILON) { minValDYN -= dVal; b2 = false;
            } else if (b3 && Math.abs(p0.z - maxValDYN) < rEPSILON) { maxValDYN += dVal; b3 = false;
            } else if (b4 && Math.abs(p1.z - maxValDYN) < rEPSILON) { maxValDYN += dVal; b4 = false;
            } else if (b5 && Math.abs(p2.z - maxValDYN) < rEPSILON) { maxValDYN += dVal; b5 = false;
            } else break;
        }
        // Dreieck liegt nicht zwischen den geanderten Isowerten
        if (!b0 || !b1 || !b2 || !b3 || !b4 || !b5) {
            if (p0.z < minValDYN && p1.z < minValDYN && p2.z < minValDYN) return null;
            if (p0.z > maxValDYN && p1.z > maxValDYN && p2.z > maxValDYN) return null;
        }
        
        // Schleife ueber alle Kanten
        ArrayList<Point3d> isoPoints = new ArrayList<Point3d>(4);
        Point3d[] points = {p0, p1, p2};
        for (int i = 0; i < 3; i++) {
            Point3d pa = points[i];
            Point3d pe = points[(i + 1) % 3];
            double dz = pe.z - pa.z;
            double minR = (minValDYN - pa.z) / dz, maxR = (maxValDYN - pa.z) / dz;
            if (minR > maxR) { double tmp = minR; minR = maxR; maxR = tmp; }
            if (minR >= 1. || maxR <= 0.) continue;
            if (minR <= 0.) isoPoints.add(pa);
            else isoPoints.add(pa.add(pe.sub(pa).mult(minR)));
            if (maxR < 1.) isoPoints.add(pa.add(pe.sub(pa).mult(maxR)));
        }
        
        return new IsoSurface(isoPoints.toArray(new Point3d[isoPoints.size()]), minVal, maxVal);
    }
}