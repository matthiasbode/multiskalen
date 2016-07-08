/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.scheduling.rules.fuzzyCapacity;

import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.operation.BetaOperation;
import applications.fuzzy.scheduling.rules.FuzzyFunctionBasedRule;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import fuzzy.number.discrete.interval.FuzzyInterval;
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
public class FuzzyCapacityEarliestFuzzyScheduleRule<E extends Resource> implements FuzzyFunctionBasedRule<E> {

    private E resource;
    public FieldElement capacity;
    private FuzzyCapacityUtilizationManager manager;

   
    public FuzzyCapacityEarliestFuzzyScheduleRule(E conveyanceSystem, FuzzyInterval capacity) {
        this.resource = conveyanceSystem;
        this.capacity = capacity;
        this.manager = new FuzzyCapacityUtilizationManager(resource, capacity, new LongValue(0));
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
