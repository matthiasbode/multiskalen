/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import java.util.HashMap;
import java.util.LinkedHashMap;
import math.FieldElement;

/**
 * Klasse, die für ConveyanceSysteme, die aus mehreren Agenten bestehen, die
 * Anfragen unter Berücksichtigung von Rüstfahrten kapselt und an
 * SingleTransportBundleHandler weiterreicht.
 *
 * @author bode
 */
public class MultipleTransportBundleHandler<E extends ConveyanceSystem> {

    private MultipleAgentConveyanceSystem<E> system;
    private HashMap<E, ConveyanceSystemRule> rules;
    private LinkedHashMap<Operation, TransportBundle> bundles = new LinkedHashMap<>();

    public MultipleTransportBundleHandler(MultipleAgentConveyanceSystem cs, HashMap<E, ConveyanceSystemRule> rules) {
        this.system = cs;
        this.rules = rules;
    }

    /**
     * Methode, die von ConveyanceSystemen mit mehreren Agenten aufgerufen wird,
     * um an das Bundle aus Transport- und Rüstfahrten zu kommen, damit diese
     * eingeplant werden.
     *
     * @param top
     * @return
     */
    public TransportBundle getBundle(TransportOperation top) {
        TransportBundle bundle = this.bundles.get(top);
        /**
         * Hinzufügen des Bedarfs bei der Operation an die wirklich ausführende
         * Ressource (das AGV)
         */
//        top.getDemands().put(bundle.getResource(), top.getDemand(system));
        return bundle;
    }

    public boolean isScheduable(Schedule s, FieldElement startTop, MultiScaleTransportOperation top) {
        for (E e : system.getSharingResources()) {
            ConveyanceSystemRule rule = rules.get(e);
            boolean scheduable = rule.canSchedule(s, top, startTop);
            if (scheduable) {
                setAgent(s, top, e, startTop);
                return true;
            }
        }
        return false;
    }

    public boolean isBundleScheduable(Schedule s, FieldElement bundleStart, MultiScaleTransportOperation top) {
        for (E e : system.getSharingResources()) {
            ConveyanceSystemRule rule = rules.get(e);
            boolean scheduable = rule.canSchedule(s, top, bundleStart);
            if (scheduable) {
                setAgent(s, top, e, null);
                return true;
            }
        }
        return false;
    }

    public FieldElement getStartTime(Schedule schedule, MultiScaleTransportOperation o, TimeSlot interval) {
        LinkedHashMap<E, FieldElement> startTimes = new LinkedHashMap<>();
        for (E e : system.getSharingResources()) {

            TimeSlotList freeSlots = rules.get(e).getFreeSlots(schedule, o, interval);
            for (TimeSlot freeSlot : freeSlots) {
                FieldElement startTime = rules.get(e).getNextPossibleBundleStartTime(schedule, o, freeSlot);
                /**
                 * Nur hinzufügen, wenn einplanbar.
                 */
                if (startTime != null) {
                    startTimes.put(e, startTime);
                }
            }
        }
        /**
         * Wähle den AGV, bei dem der Transport als erstes einplanbar ist.
         */
        E executingAgent = null;
        FieldElement min = interval.getUntilWhen();
        for (E e : startTimes.keySet()) {
            if (startTimes.get(e).isLowerThan(min)) {
                min = startTimes.get(e);
                executingAgent = e;
            }
        }
        if (executingAgent == null) {
            return null;
        }
        FieldElement startTime = startTimes.get(executingAgent);
        setAgent(schedule, o, executingAgent, startTime);
        return startTime;
    }

    private void setAgent(Schedule s, MultiScaleTransportOperation o, E executingAgent, FieldElement startTime) {
        ConveyanceSystemRule rule = rules.get(executingAgent);
        bundles.put(o, rule.getBundle(s, o, startTime));

    }
}
