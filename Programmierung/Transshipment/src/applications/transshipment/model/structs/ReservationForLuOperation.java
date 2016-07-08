/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.LoadUnitResource;
import math.FieldElement;

/**
 * Diese Klasse stellt eine Reservierung einer LoadUnitResource fuer eine
 * LoadUnit in einem TimeSlot dar.
 *
 * @author hofmann
 */
public class ReservationForLuOperation {

    private final LoadUnit lu;
    private final TimeSlot ts;
    private final LoadUnitResource resource;

    public ReservationForLuOperation(LoadUnitResource resource, LoadUnit lu, TimeSlot ts) {
        this.lu = lu;
        this.ts = ts;
        this.resource = resource;
    }

    public LoadUnit getLoadUnit() {
        return lu;
    }

    public LoadUnitResource getResource() {
        return resource;
    }

    public FieldElement getDauer() {
        return ts.getDuration();
    }

    public FieldElement getCurrentExecutionStart() {
        return ts.getFromWhen();
    }

    public FieldElement getCurrentExecutionEnd() {
        return ts.getUntilWhen();
    }

    public TimeSlot getCurrentTimeSlot() {
        return ts;
    }

}
