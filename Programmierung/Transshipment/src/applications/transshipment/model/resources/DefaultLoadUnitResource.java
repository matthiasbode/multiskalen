/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */
public abstract class DefaultLoadUnitResource implements LoadUnitResource {

    private TimeSlotList timeAvailList;

    public DefaultLoadUnitResource() {

    }

    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
        double x = this.getGeneralOperatingArea().getBounds2D().getCenterX();
        double y = this.getGeneralOperatingArea().getBounds2D().getCenterY();
        return new Point2d(x, y);
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        if(timeAvailList == null){
          return null;
        }
        TimeSlotList clone = timeAvailList.clone();
        return clone;
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.timeAvailList = tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.timeAvailList = new TimeSlotList(tempAvail);
    }

}
