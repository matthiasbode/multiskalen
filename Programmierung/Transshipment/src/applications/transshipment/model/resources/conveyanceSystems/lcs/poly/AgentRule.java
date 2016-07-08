/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.poly;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.setup.DefaultIdleSettingUpOperation;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.SingleTransportBundleHandler;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.transshipment.model.structs.SpaceTimeElement;
import java.util.ArrayList;
import java.util.Collection;
import javax.vecmath.Point2d;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class AgentRule implements ConveyanceSystemRule<Agent> {

    /**
     * Dieser Manager verwaltete für den speziellen Agenten die eingeplanten
     * Operationen und überwacht, dass er nicht 2 Aufträge gleichzeitig machen
     * kann.
     */
    private final AreaManager manager;
    private final LCSystem system;
    private final Agent agent;
    private final SingleTransportBundleHandler bundleHandler;
    private final StepFunctionBasedUtilizationManager utilizationManager;

    /**
     * 
     *
     * @param agent
     * @param handler
     */
    public AgentRule(Agent agent,  InstanceHandler handler) {
        this.agent = agent;
        this.system = agent.getLcSystem();
        this.manager = (AreaManager) handler.getSharedManager(agent.getLcSystem());
        this.utilizationManager = new StepFunctionBasedUtilizationManager(agent, 1., handler.getStartTimeForResource(agent));
        this.bundleHandler = new SingleTransportBundleHandler(agent, this);
    }

    @Override
    public FieldElement getTransportationTime(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit lu) {
        Point2d centerOrigin = origin.getCenterOfGeneralOperatingArea();
        Point2d centerDestination = destination.getCenterOfGeneralOperatingArea();

        double distance = centerOrigin.distance(centerDestination);

        /**
         * Heben uns senken!
         */
        long crabZTime = 0;
        if (lu != null) {
            crabZTime = 10l * 1000l;
        }

        long movementDuration = (long) (distance / agent.getVmax()) * 1000l + crabZTime;
        return new LongValue(movementDuration);
    }

    @Override
    public IdleSettingUpOperation findIdleSettingUpOperation(Operation predecessor, MultiScaleTransportOperation transOp) {
        /**
         * Bei Kranen Annahme: es kann nur einen Vorgänger geben, dieser muss
         * eine TransportOperation sein.
         */
        if (predecessor == null) {
            DefaultIdleSettingUpOperation defaultIdleSettingUpOperation = new DefaultIdleSettingUpOperation(agent, system.getStartingAgentPositions().get(agent), transOp.getOrigin());
            defaultIdleSettingUpOperation.setDuration(getTransportationTime(system.getStartingAgentPositions().get(agent), transOp.getOrigin(), null));
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Agent getResource() {
        return agent;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement start) {
        MultiScaleTransportOperation top = (MultiScaleTransportOperation) o;
        if (start == null) {
            throw new IllegalArgumentException("Keine Startzeit definiert!");
        }
        /**
         * Bestimme für die Operation das TransportBundle
         */
        if (!bundleHandler.isBundleScheduable(s, start, top)) {
            return false;
        }
        return this.manager.isSchedulable(bundleHandler.getBundle(top), start);
    }

    /**
     * Könnte man erstmal rauslassen. Wird nur bei seriellen Schedule Scheme
     * benutzt.
     *
     * @param s
     * @param o
     * @param interval
     * @return
     */
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
        TimeSlotList freeSlots = utilizationManager.getFreeSlotsInternal(s, demand, duration, interval);
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
    public void schedule(Operation o, Schedule s, FieldElement start) {
        utilizationManager.scheduleInternal(o, s, start);
        manager.schedule(o, agent, start);
    }

    @Override
    public void unSchedule(Operation o, Schedule s) {
        utilizationManager.unScheduleInternal(o, s);
        manager.unSchedule(o, agent);
    }

    @Override
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval) {
        return utilizationManager.getFreeSlotsInternal(s, o.getDemand(agent.getLcSystem()), o.getDuration(), interval);
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        return utilizationManager.haveEnoughCapacity(s,o, start);
    }   

    public AreaManager getManager() {
        return manager;
    }

    @Override
    public void initPositions(Schedule s, Collection<SpaceTimeElement> initialPositions) {
    }
    
    

}
