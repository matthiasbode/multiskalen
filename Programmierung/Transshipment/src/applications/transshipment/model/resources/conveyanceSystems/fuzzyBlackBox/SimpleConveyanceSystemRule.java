/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.fuzzyBlackBox;

import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyUtilizationManager;
import applications.fuzzy.scheduling.rules.fuzzyCapacity.FuzzyCapacityUtilizationManager;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.utilization.IntervalUtilizationManager;
import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.transshipment.model.structs.SpaceTimeElement;
import bijava.math.function.ScalarFunction1d;
import java.util.Collection;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class SimpleConveyanceSystemRule<C extends ConveyanceSystem> implements ConveyanceSystemRule<C>, ScalarFunctionBasedRule<C> {

    private C c;
    private TransportationTimeCalculator<C> calculator;
    private UtilizationManager manager;
    public static boolean isInterval = false;

    public SimpleConveyanceSystemRule(C c, InstanceHandler handler, TransportationTimeCalculator<C> calculator, double capacity, TransshipmentParameter.FuzzyMode fuzzy) {
        this.c = c;
        this.calculator = calculator;
        if (fuzzy.equals(TransshipmentParameter.FuzzyMode.fuzzy)) {
            this.manager = new FuzzyUtilizationManager(c, capacity, handler.getStartTimeForResource(c));
        } else if (fuzzy.equals(TransshipmentParameter.FuzzyMode.crisp)) {
            if (isInterval) {
                this.manager = new IntervalUtilizationManager(c, capacity, handler.getStartTimeForResource(c), 10 * 60 * 1000);
            } else {
                this.manager = new StepFunctionBasedUtilizationManager(c, capacity, handler.getStartTimeForResource(c));
            }
        } else {
            this.manager = new FuzzyCapacityUtilizationManager(c, TransshipmentParameter.getCapacity(capacity), handler.getStartTimeForResource(c));
        }
    }

    @Override
    public FieldElement getTransportationTime(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit lu) {
        return calculator.getTransportationTime(c, origin, destination, lu);
    }

    @Override
    public IdleSettingUpOperation findIdleSettingUpOperation(Operation predecessor, MultiScaleTransportOperation transOp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TransportBundle getBundle(Schedule s, MultiScaleTransportOperation top, FieldElement startTimeTransport) {
        FieldElement start = startTimeTransport;
        return new TransportBundle(c, top, start);
    }

    @Override
    public MultiScaleTransportOperation getDetailedOperation(RoutingTransportOperation o, LoadUnitStorage origin, LoadUnitStorage destination) {
        MultiScaleTransportOperation<C> multiScaleTransportOperation = new MultiScaleTransportOperation<C>(o, origin, destination);
        multiScaleTransportOperation.setDuration(this.getTransportationTime(origin, destination, o.getLoadUnit()));
        return multiScaleTransportOperation;

    }

    @Override
    public FieldElement getNextPossibleBundleStartTime(Schedule s, MultiScaleTransportOperation o, TimeSlot interval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public C getResource() {
        return c;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement startBundle) {
        return manager.haveEnoughCapacity(s, o, startBundle);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public void initPositions(Schedule s, Collection<SpaceTimeElement> initialPositions) {
    }

}
