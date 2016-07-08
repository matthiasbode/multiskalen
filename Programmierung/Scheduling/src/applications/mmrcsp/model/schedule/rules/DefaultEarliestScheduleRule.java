/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.rules;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import math.FieldElement;
import math.LongValue;
import math.function.StepFunction;

/**
 *
 * @author bode
 */
public class DefaultEarliestScheduleRule<E extends Resource> implements ScheduleRule<E>, ScalarFunctionBasedRule<E> {

    private E resource;
    public double capacity;
    private StepFunctionBasedUtilizationManager manager;

    public DefaultEarliestScheduleRule(E conveyanceSystem, double capacity) {
        this.resource = conveyanceSystem;
        this.capacity = capacity;
        this.manager = new StepFunctionBasedUtilizationManager(resource, capacity, new LongValue(0));
    }

    @Override
    public E getResource() {
        return resource;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s,o, start);
    }

    @Override
    public void schedule(Operation o, Schedule s, FieldElement start) {
        manager.scheduleInternal(o, s, start);
    }

    @Override
    public void unSchedule(Operation o, Schedule s) {
        manager.unScheduleInternal(o, s);
    }

//    @Override
//    public TimeSlotList getFreeSlotsInternal(FieldElement demand, TimeSlot interval) {
//        return manager.getFreeSlotsInternal(demand, interval);
//    }
    @Override
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval) {
        return manager.getFreeSlotsInternal(s, o.getDemand(resource), o.getDuration(), interval);
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s,o, start);
    }

    @Override
    public StepFunction getWorkloadFunction() {
        return manager.getWorkloadFuction();
    }

    @Override
    public double getMax() {
        return manager.getCapacity().doubleValue();
    }
}
