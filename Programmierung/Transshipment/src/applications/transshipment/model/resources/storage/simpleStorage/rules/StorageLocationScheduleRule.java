/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage.simpleStorage.rules;

import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyUtilizationManager;
import applications.fuzzy.scheduling.rules.fuzzyCapacity.FuzzyCapacityUtilizationManager;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.storage.DefaultStoreOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.schedule.rules.StorageRule;
import bijava.math.function.ScalarFunction1d;
import java.util.ArrayList;
import java.util.List;
import math.FieldElement;

/**
 * Da es sich bei einer StorageLocation um einen exakten Platz handelt, ist auch
 * nur eine DetailedScheduleRule nötig.
 *
 * @author bode
 */
public class StorageLocationScheduleRule implements StorageRule<StorageLocation>, ScalarFunctionBasedRule<StorageLocation> {

    private final StorageLocation loc;
    private final UtilizationManager manager;
    private TransshipmentParameter.FuzzyMode fuzzy;

    public StorageLocationScheduleRule(StorageLocation loc, InstanceHandler handler, TransshipmentParameter.FuzzyMode fuzzy) {
        this.loc = loc;
        this.fuzzy = fuzzy;
        if (fuzzy.equals(TransshipmentParameter.FuzzyMode.fuzzy)) {
            manager = new FuzzyUtilizationManager(loc, 1.0, handler.getStartTimeForResource(loc), true);
        } 
        else if(fuzzy.equals(TransshipmentParameter.FuzzyMode.fuzzyCapacity)){
            manager = new FuzzyCapacityUtilizationManager(loc, TransshipmentParameter.getCapacity(1.0), handler.getStartTimeForResource(loc), true);
        }
        else {
            manager = new StepFunctionBasedUtilizationManager(loc, 1., handler.getStartTimeForResource(loc));
        }
    }

    @Override
    public StorageLocation getResource() {
        return loc;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s, o, start);
    }

    @Override
    public List<StoreOperation> getPossibleStoreOperations(LoadUnit lu, Schedule s, FieldElement start, FieldElement duration) {
        DefaultStoreOperation storeOp = new DefaultStoreOperation(lu, loc, duration);
        ArrayList<StoreOperation> result = new ArrayList<>();
        if (manager.haveEnoughCapacity(s, storeOp, start)) {
            result.add(storeOp);
        }
        return result;
    }

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
        return manager.getFreeSlotsInternal(s, o.getDemand(loc), o.getDuration(), interval);
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s, o, start);
    }

    @Override
    public ScalarFunction1d getWorkloadFunction() {
        return manager.getWorkloadFuction();
    }

    @Override
    public double getMax() {
        return manager.getCapacity().doubleValue();
    }
}
