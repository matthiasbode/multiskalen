/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.poly;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.operations.SubOperations;
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
import applications.transshipment.model.structs.SpaceTimeElement;
import java.util.Collection;
import java.util.HashMap;
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
public class PolytopeLCSystemRule implements ConveyanceSystemRule<LCSystem> {

    private final LCSystem system;
    private final PolytopeMultipleTransportBundleHandler bundleHandler;
    private final StepFunctionBasedUtilizationManager manager;

    public PolytopeLCSystemRule(LCSystem system, InstanceHandler handler) {
        this.system = system;
        this.manager = new StepFunctionBasedUtilizationManager(system, system.getSharingResources().size(), system.getTemporalAvailability().getAllOverTimeSlot());

        HashMap<Agent, ConveyanceSystemRule> rules = new HashMap<>();
        for (Agent agent : system.getSharingResources()) {
            ConveyanceSystemRule agentRule = (ConveyanceSystemRule) handler.get(agent);
            rules.put(agent, agentRule);
        }
        this.bundleHandler = new PolytopeMultipleTransportBundleHandler(system, rules);
    }

    @Override
    public FieldElement getTransportationTime(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit lu) {
        Agent agent = system.getSharingResources().iterator().next();
        Point2d centerOrigin = origin.getCenterOfGeneralOperatingArea();
        Point2d centerDestination = destination.getCenterOfGeneralOperatingArea();

        double distance = centerOrigin.distance(centerDestination);

        /**
         * Heben uns senken!
         */
        long crabZTime = 0;
        if (lu != null) {
            crabZTime = 10 * 1000L;
        }
        long movementDuration = (long) (distance / agent.getVmax()) * 1000L + crabZTime;
        return new LongValue(movementDuration);
    }

    @Override
    public LCSystem getResource() {
        return system;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement startBundle) {
        MultiScaleTransportOperation top = (MultiScaleTransportOperation) o;
        if (startBundle == null) {
            throw new IllegalArgumentException("Keine Startzeit definiert!");
        }
        /**
         * Bestimme für die Operation das TransportBundle
         */
        return bundleHandler.isBundleScheduable(s, startBundle, top);
    }

    @Override
    public FieldElement getNextPossibleBundleStartTime(Schedule s, MultiScaleTransportOperation o, TimeSlot interval) {
        MultiScaleTransportOperation top = (MultiScaleTransportOperation) o;
        FieldElement startTime = bundleHandler.getStartTime(s, top, interval);
        if (startTime != null) {
            return startTime;
        }
        /**
         * Operation nicht einplanbar.
         */
        return null;
    }

//    @Override
//    public double getCapacity(long time) {
//        return this.system.getSharingResources().size();
//    }
//    @Override
//    public void determineDurationAndDemands(LoadUnitOperation op) {
//        if (!(op instanceof TransportOperation)) {
//            throw new IllegalArgumentException("Kann nur TransportOperationen verarbeiten");
//        }
//        TransportOperation operation = (TransportOperation) op;
//        FieldElement transportationTime = getTransportationTime(operation.getOrigin(), operation.getDestination(), operation.getLoadUnit());
//        operation.setDuration(transportationTime);
//    }
    /**
     * Wird derzeit nicht benutzt!
     *
     * @param predessor
     * @param transOp
     * @return
     */
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
        return this.bundleHandler.getBundle(top);
    }

    @Override
    public MultiScaleTransportOperation getDetailedOperation(RoutingTransportOperation o, LoadUnitStorage origin, LoadUnitStorage destination) {
        MultiScaleLCSTransportOperation operation = new MultiScaleLCSTransportOperation(o, origin, destination);
        FieldElement transportationTime = getTransportationTime(operation.getOrigin(), operation.getDestination(), operation.getLoadUnit());
        operation.setDuration(transportationTime);

        /**
         * Bestimmung des zusätzlichen Demands für die Kranbahn
         */
        SubOperations subDemand = new SubOperations();
        operation.setSubOperations(subDemand);
        return operation;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void initPositions(Schedule s, Collection<SpaceTimeElement> initialPositions) {
    }
}
