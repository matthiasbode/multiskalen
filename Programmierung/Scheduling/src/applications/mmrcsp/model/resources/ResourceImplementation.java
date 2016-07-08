/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.resources;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;

/**
 *
 * @author bode
 */
public class ResourceImplementation implements Resource {

    private TimeSlotList tempAvail;
    public String name;

    public ResourceImplementation(String name) {
        this.name = name;
    }

    public ResourceImplementation() {

    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        return tempAvail.clone();
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.tempAvail = tempAvail.clone();
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.tempAvail = new TimeSlotList(tempAvail);
    }

    @Override
    public String getID() {
        return name;
    }

    @Override
    public String toString() {
        return "ResourceImplementation" + name + "_";
    }
    
    
}
