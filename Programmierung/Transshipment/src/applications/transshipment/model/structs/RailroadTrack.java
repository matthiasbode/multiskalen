/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import applications.mmrcsp.model.resources.PositionedResource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import bijava.geometry.dim2.PolygonalCurve2d;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;
import javax.vecmath.Point2d;
import util.GeometryTools;

/**
 *
 * @author bode
 */
public class RailroadTrack implements PositionedResource {

    protected PolygonalCurve2d baseLine;
    private TimeSlotList tempAvail;
    private final double breite;
    private double zugStart = 0;
    private final String name;
    private Area area;

    public RailroadTrack(Rectangle2D bounds, String name) {
        this(bounds.getHeight(), name, new Point2d(bounds.getMinX(), bounds.getCenterY()), new Point2d(bounds.getMaxX(), bounds.getCenterY()));
    }

    public RailroadTrack(double breite, String name, Point2d... points) {
        this(breite, name, 0.0, points);
    }

    public RailroadTrack(double breite, String name, double zugStart, Point2d... points) {
        this.breite = breite;
        this.zugStart = zugStart;
        bijava.geometry.dim2.Point2d[] bijavaPoints = new bijava.geometry.dim2.Point2d[points.length];
        for (int i = 0; i < points.length; i++) {
            Point2d point2d = points[i];
            bijavaPoints[i] = new bijava.geometry.dim2.Point2d(point2d.x, point2d.y);
        }
        baseLine = new PolygonalCurve2d(bijavaPoints);
        this.name = name;
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        return tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.tempAvail = tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.tempAvail.clear();
        this.tempAvail.add(tempAvail);
    }

    @Override
    public String toString() {
        return "RailroadTrack{" + name + '}' + getGeneralOperatingArea().getBounds2D();
    }

    @Override
    public String getID() {
        return "RailroadTrack{" + name + '}';
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (this.area == null) {
            IdentityHashMap<Rectangle2D.Double, AffineTransform> recs = new IdentityHashMap<>();

            for (int i = 1; i < baseLine.getPoints().length; i++) {
                Point2d first = baseLine.getPoints()[i - 1];
                Point2d second = baseLine.getPoints()[i];

                Rectangle2D.Double rec = new Rectangle2D.Double(0, 0, first.distance(second), breite);
                AffineTransform transformation = GeometryTools.getRotationTransformation(first, second);
                transformation.concatenate(AffineTransform.getTranslateInstance(0, -breite / 2.));
                recs.put(rec, transformation);
            }
            this.area = new Area();
            for (Rectangle2D.Double rec : recs.keySet()) {
                Area ar = new Area(rec);
                ar.transform(recs.get(rec));
                this.area.add(ar);
            }
        }
        return this.area;

    }

    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
        double x = this.getGeneralOperatingArea().getBounds2D().getCenterX();
        double y = this.getGeneralOperatingArea().getBounds2D().getCenterY();
        return new Point2d(x, y);
    }

    public PolygonalCurve2d getBaseLine() {
        return baseLine;
    }

}
