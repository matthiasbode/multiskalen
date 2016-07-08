/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.micro;

import applications.transshipment.model.resources.conveyanceSystems.SingleTransportBundleHandler;
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
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.transshipment.model.structs.SpaceTimeElement;
import java.util.Collection;
import javax.vecmath.Point2d;
import math.FieldElement;
import math.LongValue;
import math.function.StepFunction;

/**
 * Dies Klasse beschreibt die Einplanvorschriften für einen Kran auf einer
 * Makroskopischen Ebene. Unschärfe wird hier aber zunächst nicht
 * berücksichtigt.
 *
 * @author bode
 */
public class MicroscopicAgentRule implements ConveyanceSystemRule<Agent>, ScalarFunctionBasedRule<Agent> {

    private final Agent agent;
    private final SingleTransportBundleHandler bundleHandler;
    private final StepFunctionBasedUtilizationManager manager;

    public MicroscopicAgentRule(Agent agent, InstanceHandler handler) {
        this.agent = agent;
        this.bundleHandler = new SingleTransportBundleHandler(agent, this);
        this.manager = new StepFunctionBasedUtilizationManager(agent, 1., handler.getStartTimeForResource(agent));
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
        /**
         * sollte Zeitfenster null sein, betrachte alle Zeitfenster, bis eines
         * gefunden wurde, in das eingeplant werden kann. Möglichst früh
         * einplanen!
         */
        FieldElement demand = o.getDemand(agent.getLcSystem());
        FieldElement duration = o.getDuration();
        if (demand == null) {
            throw new IllegalArgumentException("Kein Bedarf gesetzt");
        }
        TimeSlotList freeSlots = manager.getFreeSlotsInternal(s, demand, duration, interval);
        for (TimeSlot freeSlot : freeSlots) {
            FieldElement startTime = bundleHandler.getBundleStartTime(s, top, freeSlot);
            if (startTime != null) {
                return startTime;
            }
        }

        /**
         * Operation nicht einplanbar, keine Startzeit gefunden.
         */
        return null;
    }

    @Override
    public Agent getResource() {
        return agent;
    }

    @Override
    public FieldElement getTransportationTime(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit lu) {
        Point2d centerOrigin = origin.getCenterOfGeneralOperatingArea();
        Point2d centerDestination = destination.getCenterOfGeneralOperatingArea();

        double longitudinalMovement = Math.abs(centerOrigin.x - centerDestination.x);
        double verticalMovement = Math.abs(centerOrigin.y - centerDestination.y);
        /**
         * Heben uns senken!
         */
        long crabZTime = 0;
        if (lu != null) {
            crabZTime = 10 * 1000L;
        }
        long movementDuration = (long) (longitudinalMovement / agent.getVmax() + verticalMovement / agent.getVmax()) * 1000L + crabZTime;
        return new LongValue(movementDuration);
    }

    @Override
    public IdleSettingUpOperation findIdleSettingUpOperation(Operation predecessor, MultiScaleTransportOperation transOp) {

        /**
         * Bei Kranen Annahme: es kann nur einen Vorgänger geben, dieser muss
         * eine TransportOperation sein.
         */
        if (predecessor == null) {
            DefaultIdleSettingUpOperation defaultIdleSettingUpOperation = new DefaultIdleSettingUpOperation(agent, transOp.getOrigin(), transOp.getOrigin());
            defaultIdleSettingUpOperation.setDuration(new LongValue(1));
            return defaultIdleSettingUpOperation;
        }

        Operation preOp = predecessor;
        if (preOp instanceof TransportOperation) {
            TransportOperation preTop = (TransportOperation) preOp;
            LoadUnitStorage startRuest = preTop.getDestination();
            LoadUnitStorage endRuest = transOp.getOrigin();

            DefaultIdleSettingUpOperation settingUp = new DefaultIdleSettingUpOperation(agent, startRuest, endRuest);
            FieldElement transportationTime = getTransportationTime(startRuest, endRuest, null);
            settingUp.setDuration(transportationTime);

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
        throw new UnsupportedOperationException("Kann nur am LCSystem aufgerufen werden");
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
        return manager.getFreeSlotsInternal(s, o.getDemand(agent.getLcSystem()), o.getDuration(), interval);
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
    @Override
    public void initPositions(Schedule s, Collection<SpaceTimeElement> initialPositions) {
    }
}
