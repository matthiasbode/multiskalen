/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane;

import applications.transshipment.model.resources.DefaultSharedResource;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.PolygonalCurve2d;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.vecmath.Point3d;
import math.geometry.Polygon2D;
import util.GeometryTools;

/**
 *
 * @author bode
 */
public class CraneRunway extends DefaultSharedResource<Crane> {

    public static final String PREFIX = "CraneRunway";
    /**
     * Die ID wird bei Erstellung eines Objektes dieser Klasse aus dem
     * {@link #PREFIX} und der {@link #number} gebildet.
     */
    protected final String ID;
    public PolygonalCurve2d baseLine;
    public double breite;
    double factorOverlap = 1.5;
    double craneWidth = 30.;

    public CraneRunway(Point2d startOfBaseLine, Point2d endOfBaseLine, double breite) {
        this.ID = PREFIX + "-" + getNumber();
        baseLine = new PolygonalCurve2d(startOfBaseLine, endOfBaseLine);
        this.breite = breite;
        Point2d[] boxPoints = GeometryTools.getBoxPoints(startOfBaseLine, endOfBaseLine, this.breite);
        double[] xpoints = new double[]{boxPoints[0].x, boxPoints[1].x, boxPoints[2].x, boxPoints[3].x};
        double[] ypoints = new double[]{boxPoints[0].y, boxPoints[1].y, boxPoints[2].y, boxPoints[3].y};
        this.setArea(new Area(new Polygon2D(xpoints, ypoints, boxPoints.length)));

    }

    public CraneRunway(Point2d startOfBaseLine, Point2d endOfBaseLine, double breite, int numberOfCranes) {
        this(startOfBaseLine, endOfBaseLine, breite);

        double overlap = 0.;
        if (numberOfCranes > 1) {
            overlap = factorOverlap * craneWidth;
        }

        double maxWorkingLength = baseLine.getLength() / numberOfCranes;
        // Passen die Krane in WorkingArea der Kranbahn?
        if (maxWorkingLength < craneWidth) {
            throw new IllegalArgumentException("Length of OperatingArea (" + baseLine.getLength() + ") to small for " + numberOfCranes + " Cranes with width of " + craneWidth);
        }

        double xmin = startOfBaseLine.x;

        // Erzeugen der Krane und setzen der smin, smax
        for (int i = 0; i < numberOfCranes; i++) {
            if (i == 0) {
                this.addSharingResource(new Crane(this, xmin + 0., xmin + maxWorkingLength + overlap, craneWidth, breite));
            } else if (i == numberOfCranes - 1) {
                this.addSharingResource(new Crane(this, xmin + baseLine.getLength() - overlap - maxWorkingLength, xmin + baseLine.getLength(), craneWidth, breite));
            } else {
                this.addSharingResource(new Crane(this, xmin + i * maxWorkingLength - overlap, xmin + (i + 1) * maxWorkingLength + overlap, craneWidth, breite));
            }
        }
    }

    public CraneRunway(Rectangle2D rec, int numberOfCranes) {
        this(new Point2d(rec.getMinX(), rec.getCenterY()), new Point2d(rec.getMaxX(), rec.getCenterY()), rec.getHeight(), numberOfCranes);
    }

    /**
     * Transformiert den Punkt p von lokalen Koordinaten in Weltkoordinaten.
     * <code>p</code> wird nicht veraendert, es wird ein neues Objekt
     * zurueckgegeben. Die z-Koordinate bleibt unveraendert.
     *
     * @param p zu transformierender Punkt in lokalen Koordinaten
     * @return neuer Punkt, der p in Weltkoordinaten repraesentiert
     */
    public Point3d localToWorld(Point3d p) {
        Point2d p2d = new Point2d(p.x, p.y);
        p2d = localToWorld(p2d);
        return new Point3d(p2d.x, p2d.y, p.z);
    }

    public Point2d localToWorld(Point2d p) {

        if (this.baseLine.size() > 2) {
            throw new UnsupportedOperationException("method not defined for baseLine.size() > 2 (here: " + baseLine.size() + ")");
        }
        Point2d p0 = baseLine.getPointAt(0);
        Point2d p1 = baseLine.getPointAt(1);

        double l = p1.distance(p0);
        double cos_a = (p1.x - p0.x) / l;
        double sin_a = (p1.y - p0.y) / l;

        double u = p0.x + cos_a * p.x - sin_a * p.y;
        // orthogonal dazu
        double v = p0.y + cos_a * p.y + sin_a * p.x;

        return new Point2d(u, v);
    }

    public Point3d worldToLocal(Point3d p) {
        Point2d p2d = new Point2d(p.x, p.y);
        p2d = worldToLocal(p2d);
        return new Point3d(p2d.x, p2d.y, p.z);
    }

    public Point2d worldToLocal(Point2d p) {

        if (this.baseLine.size() > 2) {
            throw new UnsupportedOperationException("method not defined for baseLine.size() > 2 (here: " + baseLine.size() + ")");
        }
        Point2d p0 = baseLine.getPointAt(0);
        Point2d p1 = baseLine.getPointAt(1);

        double l = p1.distance(p0);
        double cos_a = (p1.x - p0.x) / l;
        double sin_a = (p1.y - p0.y) / l;

        // lokale Koordinaten mit p0 als Ursprung:
        // in Richtung p1-p0
        double u = (p.x - p0.x) * cos_a + (p.y - p0.y) * sin_a;
        // orthogonal dazu
        double v = -(p.x - p0.x) * sin_a + (p.y - p0.y) * cos_a;

        return new Point2d(u, v);
    }

    @Override
    public ArrayList<Crane> getSharingResources() {
        return sharingResources;
    }
}
