/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.lattice;

import applications.mmrcsp.model.resources.PositionedResource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point2d;
import math.geometry.Polygon2D;

/**
 *
 * @author bode
 */
public class CellResource2D extends Polygon2D implements PositionedResource {

    private final int number;
    private static int count = 0;
    private TimeSlotList timeAvailList;

    public CellResource2D(Rectangle2D rec) {
        super(rec);
        this.number = count++;
    }

    public CellResource2D(Polygon pol) {
        super(pol);
        this.number = count++;
    }

    public CellResource2D(double[] xpoints, double[] ypoints, int npoints) {
        super(xpoints, ypoints, npoints);
        this.number = count++;
    }

    @Override
    public Area getGeneralOperatingArea() {
        return new Area(this);
    }

    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
        double x = this.getGeneralOperatingArea().getBounds2D().getCenterX();
        double y = this.getGeneralOperatingArea().getBounds2D().getCenterY();
        return new Point2d(x, y);
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        return timeAvailList.clone();
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.timeAvailList = tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.timeAvailList = new TimeSlotList(tempAvail);
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "CellResource2D{" + "number=" + number + '}';
    }

    @Override
    public String getID() {
        return toString();
    }

}
