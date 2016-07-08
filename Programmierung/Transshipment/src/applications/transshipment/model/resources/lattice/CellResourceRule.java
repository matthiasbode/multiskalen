/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.lattice;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import math.FieldElement;
import math.function.StepFunction;

/**
 *
 * @author bode
 */
public class CellResourceRule implements ScheduleRule<CellResource2D>, ScalarFunctionBasedRule<CellResource2D> {

    private CellResource2D cell;
    private StepFunctionBasedUtilizationManager manager;

    public CellResourceRule(CellResource2D cell, InstanceHandler handler) {
        this.cell = cell;
        this.manager = new StepFunctionBasedUtilizationManager(cell, 1., handler.getStartTimeForResource(cell));
    }

    @Override
    public CellResource2D getResource() {
        return cell;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s,o, start);
    }

//    @Override
//    public FieldElement getNextPossibleBundleStartTime(Schedule s, Operation o, TimeSlot interval) {
//        return manager.getStartTimeInternal(o, interval);
//    }

    @Override
    public void schedule(Operation o, Schedule s, FieldElement start) {
        manager.scheduleInternal(o, s, start);
    }

    @Override
    public void unSchedule(Operation o, Schedule s) {
        manager.unScheduleInternal(o, s);
    }

    @Override
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval) {
        return manager.getFreeSlotsInternal(s, o.getDemand(cell), o.getDuration(), interval);
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
