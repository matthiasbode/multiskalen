/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.utilization;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import bijava.math.function.ScalarFunction1d;
import math.DoubleValue;
import math.FieldElement;

/**
 *
 * @author Matthias
 */
public interface UtilizationManager {

    public FieldElement getCapacity();

    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start);

    public void scheduleInternal(Operation o, Schedule s, FieldElement start);

    public void unScheduleInternal(Operation o, Schedule s);

    public void setTimeSlot(TimeSlot t);

    public ScalarFunction1d getWorkloadFuction();

    public TimeSlotList getFreeSlotsInternal(Schedule s, FieldElement demand, FieldElement duration, TimeSlot interval);
}
