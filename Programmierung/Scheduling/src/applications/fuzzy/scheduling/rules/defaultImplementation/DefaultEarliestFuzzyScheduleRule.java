/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.scheduling.rules.defaultImplementation;

import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.operation.BetaOperation;
import applications.fuzzy.scheduling.rules.FuzzyFunctionBasedRule;
import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyUtilizationManager;
import applications.fuzzy.scheduling.rules.fuzzyCapacity.FuzzyCapacityUtilizationManager;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
import fuzzy.number.discrete.interval.FuzzyInterval;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode, brandt
 *
 * Diese Klasse verwendet die an die LinearizedMembershipFunction1d angepasste
 * Rule: LinearizedMembershipFunction1dBasedRule. Ebenso wird ein angepasster
 * UtilizationManager verwendet: LinearizedMembershipBasedUtilizationManager.
 *
 */
public class DefaultEarliestFuzzyScheduleRule<E extends Resource> implements FuzzyFunctionBasedRule<E> {

    private E resource;
    public FieldElement capacity;
    private FuzzyUtilizationManager manager;

    public DefaultEarliestFuzzyScheduleRule(E conveyanceSystem, double capacity) {
        this.resource = conveyanceSystem;
        this.capacity = new DoubleValue(capacity);
        this.manager = new FuzzyUtilizationManager(resource, capacity, new LongValue(0));
    }

    @Override
    public E getResource() {
        return resource;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s, (BetaOperation) o, start);
    }

    public FieldElement getNextPossibleBundleStartTime(Schedule s, BetaOperation o, TimeSlot interval) {
        TimeSlotList freeSlotsInternal = manager.getFreeSlotsInternal(s, o, interval);
        return freeSlotsInternal.getFromWhen();
    }

    @Override
    public void schedule(Operation o, Schedule s, FieldElement start) {
        manager.scheduleInternal((BetaOperation) o, s, start);
    }

    @Override
    public void unSchedule(Operation o, Schedule s) {
        manager.unScheduleInternal((BetaOperation) o, s);
    }

    @Override
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval) {
        return manager.getFreeSlotsInternal(s, (BetaOperation) o, interval);
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s, (BetaOperation) o, start);
    }

    @Override
    public FieldElement getMax() {
        return manager.getCapacity();
    }

    /**
     *
     * @return
     */
    @Override
    public LinearizedFunction1d getWorkloadFunction() {
        return manager.getWorkloadFuction();
    }

}
