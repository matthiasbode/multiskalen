/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.macro;

import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyUtilizationManager;
import applications.fuzzy.scheduling.rules.fuzzyCapacity.FuzzyCapacityUtilizationManager;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.setup.DefaultIdleSettingUpOperation;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.MultiScaleLCSTransportOperation;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.structs.SpaceTimeElement;
import bijava.math.function.ScalarFunction1d;
import com.google.common.collect.TreeMultimap;
import fuzzy.number.discrete.FuzzyFactory;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.vecmath.Point2d;
import math.FieldElement;
import math.LongValue;

/**
 * Implementierung aller wichtigen Funktionen in Hinblick darauf, dass ein Agent
 * benutzt wird. Der ScheduleSchemeGenerator stellt Anfragen an diese Rule über
 * die Methoden
 *
 * canScheduleInternal getNextPossibleStartTime findIdleSettingUpOperation
 *
 * Diese Methoden müssen so koordiniert werden, dass zunächst ein AGV ausgewählt
 * wird und für alle nachfolgenden Anfragen von diesem AGV ausgegangen wird.
 *
 * @author bode
 */
public class MacroscopicLCSystemRule implements ConveyanceSystemRule<LCSystem>, ScalarFunctionBasedRule<LCSystem> {

    private final LCSystem system;
    private final UtilizationManager manager;
    private TransshipmentParameter.FuzzyMode fuzzy;

    public MacroscopicLCSystemRule(LCSystem system, InstanceHandler handler, TransshipmentParameter.FuzzyMode fuzzy) {
        this.system = system;
        this.fuzzy = fuzzy;
        if (fuzzy.equals(TransshipmentParameter.FuzzyMode.fuzzy)) {
            this.manager = new FuzzyUtilizationManager(system, system.getSharingResources().size(), handler.getStartTimeForResource(system));
        } else if (fuzzy.equals(TransshipmentParameter.FuzzyMode.fuzzyCapacity)) {
            this.manager = new FuzzyCapacityUtilizationManager(system, TransshipmentParameter.getCapacity(system.getSharingResources().size()), handler.getStartTimeForResource(system));
        } else {
            this.manager = new StepFunctionBasedUtilizationManager(system, system.getSharingResources().size(), handler.getStartTimeForResource(system));
        }

    }

    @Override
    public FieldElement getTransportationTime(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit lu) {
        Agent agent = system.getSharingResources().iterator().next();
        Point2d fromEx = origin.getCenterOfGeneralOperatingArea();
        Point2d toEx = destination.getCenterOfGeneralOperatingArea();
        double laenge = Math.abs(fromEx.x - toEx.x) + Math.abs(fromEx.y - toEx.y);
        long dauer = (long) ((laenge / agent.getVmax()) * 1000);
        dauer += 2 * LCSystem.rendezvousTime;
        LongValue crispDuration = new LongValue((long) (dauer * 1.0));
        if (fuzzy.equals(TransshipmentParameter.FuzzyMode.crisp)) {
            return crispDuration;
        } else {
            return FuzzyFactory.createLinearInterval(dauer, 2 * 1000);
        }
    }

    @Override
    public LCSystem getResource() {
        return system;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement startTop) {
        if (startTop == null) {
            throw new IllegalArgumentException("Keine Startzeit definiert!");
        }
        /**
         * Bestimme für die Operation das TransportBundle
         */
        return manager.haveEnoughCapacity(s, o, startTop);
    }

    @Override
    public FieldElement getNextPossibleBundleStartTime(Schedule s, MultiScaleTransportOperation o, TimeSlot interval) {
        TimeSlotList freeSlotsInternal = manager.getFreeSlotsInternal(s, o.getDemand(system), o.getDuration(), interval);
        return freeSlotsInternal.getFromWhen();
    }

    @Override
    public IdleSettingUpOperation findIdleSettingUpOperation(Operation predessor, MultiScaleTransportOperation transOp) {
        /**
         * Bei Kranen Annahme: es kann nur einen Vorgänger geben, dieser muss
         * eine TransportOperation sein.
         */
        if (predessor == null) {
            DefaultIdleSettingUpOperation defaultIdleSettingUpOperation = new DefaultIdleSettingUpOperation(system, transOp.getOrigin(), transOp.getOrigin());
            defaultIdleSettingUpOperation.setDuration(new LongValue(1));
            return defaultIdleSettingUpOperation;
        }

        Operation preOp = predessor;
        if (preOp instanceof TransportOperation) {
            TransportOperation preTop = (TransportOperation) preOp;
            LoadUnitStorage startRuest = preTop.getDestination();
            LoadUnitStorage endRuest = transOp.getOrigin();

            DefaultIdleSettingUpOperation settingUp = new DefaultIdleSettingUpOperation(system, startRuest, endRuest);
            FieldElement transportationTime = getTransportationTime(startRuest, endRuest, null);
            settingUp.setDuration(transportationTime);
            /**
             * Hier muss noch die Berücksichtigung der Zelloperationen folgen.
             */
            return settingUp;
        } else {
            throw new UnknownError("Vorgängeroperation keine Transportopertion. Darf nicht vorkommen: " + preOp);
        }

    }

    @Override
    public TransportBundle getBundle(Schedule s, MultiScaleTransportOperation top, FieldElement startTimeTransport) {
        TransportOperation q = null;

        TreeMultimap<FieldElement, Operation> timeToOperationMap = s.getTimeToOperationMap(system);

        Map.Entry<FieldElement, Collection<Operation>> lowerEntry = timeToOperationMap.asMap().lowerEntry(startTimeTransport);

        Collection<Operation> operationsBefore = new LinkedHashSet<>();

        if (lowerEntry != null) {
            operationsBefore = lowerEntry.getValue();
            if (operationsBefore.size() != 1) {
                for (Operation operation : operationsBefore) {
                    if (operation instanceof TransportOperation) {
                        q = (TransportOperation) operation;
                        break;
                    }
                }
            } else {
                Operation qop = operationsBefore.iterator().next();

                /**
                 * Nur Transportoperationen können untersucht werden.
                 */
                if (qop instanceof IdleSettingUpOperation) {
                    return null;
                }

                q = (TransportOperation) qop;
            }
        }

        return new TransportBundle(system, top, startTimeTransport);
    }

    @Override
    public MultiScaleTransportOperation getDetailedOperation(RoutingTransportOperation o, LoadUnitStorage origin, LoadUnitStorage destination) {
        MultiScaleLCSTransportOperation multiScaleTransportOperation = new MultiScaleLCSTransportOperation(o, origin, destination);
        FieldElement transportationTime = getTransportationTime(multiScaleTransportOperation.getOrigin(), multiScaleTransportOperation.getDestination(), multiScaleTransportOperation.getLoadUnit());
        multiScaleTransportOperation.setDuration(transportationTime);
        return multiScaleTransportOperation;
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
        return manager.getFreeSlotsInternal(s, o.getDemand(system), o.getDuration(), interval);
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
