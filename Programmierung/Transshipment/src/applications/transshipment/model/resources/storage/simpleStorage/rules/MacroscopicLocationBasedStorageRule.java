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
import applications.transshipment.model.schedule.rules.StorageRule;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
import applications.transshipment.TransshipmentParameter;
import bijava.math.function.ScalarFunction1d;
import java.util.ArrayList;
import java.util.List;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class MacroscopicLocationBasedStorageRule implements StorageRule<LocationBasedStorage>, ScalarFunctionBasedRule<LocationBasedStorage> {

    private final LocationBasedStorage resource;
    private final UtilizationManager manager;
    private TransshipmentParameter.FuzzyMode fuzzy;

    public MacroscopicLocationBasedStorageRule(LocationBasedStorage s, InstanceHandler handler, TransshipmentParameter.FuzzyMode fuzzy) {
        this.resource = s;
        this.fuzzy = fuzzy;
        if (fuzzy.equals(TransshipmentParameter.FuzzyMode.fuzzy)) {
            this.manager = new FuzzyUtilizationManager(s, resource.getStorageLocations().size(), handler.getStartTimeForResource(s), true);
        } else if (fuzzy.equals(TransshipmentParameter.FuzzyMode.fuzzyCapacity)) {
            this.manager = new FuzzyCapacityUtilizationManager(s, TransshipmentParameter.getCapacity(resource.getStorageLocations().size()), handler.getStartTimeForResource(s), true);
        } else {
            this.manager = new StepFunctionBasedUtilizationManager(s, resource.getStorageLocations().size(), handler.getStartTimeForResource(s));
        }
    }

    @Override
    public LocationBasedStorage getResource() {
        return resource;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s, o, start);
    }

    /**
     * Das Intervall schreibt hier vor, ab wann möglich und bis wann
     * verbindindlich die Lagerung stattfindet.
     *
     * @param s
     * @param o
     * @param interval
     * @return
     */
//    @Override
//    public FieldElement getNextPossibleBundleStartTime(Schedule s, Operation o, TimeSlot interval) {
//        /**
//         * t_k_mue bestimmen, ab dem die Operation einplanbar ist.
//         */
//        return manager.getStartTimeInternal(o, interval);
//    }
//    @Override
//    public double getCapacity(long time) {
//        return resource.getStorageLocations().size();
//    }
    @Override
    public List<StoreOperation> getPossibleStoreOperations(LoadUnit lu, Schedule s, FieldElement start, FieldElement duration) {
        ArrayList<StoreOperation> result = new ArrayList<>();
        DefaultStoreOperation storeOp = new DefaultStoreOperation(lu, resource, duration);

        if (canSchedule(s, storeOp, start)) {
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
        return manager.getFreeSlotsInternal(s, o.getDemand(resource), o.getDuration(), interval);
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
