/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.poly;

import applications.transshipment.model.resources.conveyanceSystems.*;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import math.FieldElement;

/**
 * Klasse, die für ConveyanceSysteme, die aus mehreren Agenten bestehen, die
 * Anfragen unter Berücksichtigung von Rüstfahrten kapselt und an
 * SingleTransportBundleHandler weiterreicht.
 *
 * @author bode
 * @param <E>
 */
public class PolytopeMultipleTransportBundleHandler<E extends ConveyanceSystem> {

    private final MultipleAgentConveyanceSystem<E> system;
    private final HashMap<E, ConveyanceSystemRule> rules;
    private final LinkedHashMap<Operation, TransportBundle> bundles = new LinkedHashMap<>();

    public PolytopeMultipleTransportBundleHandler(MultipleAgentConveyanceSystem cs, HashMap<E, ConveyanceSystemRule> rules) {
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
        ArrayList<E> sortedAgents = sortAgentsDistance(s, system.getSharingResources(), top, bundleStart);
        for (E e : sortedAgents) {
            ConveyanceSystemRule rule = rules.get(e);
            if (rule.canSchedule(s, top, bundleStart)) {
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

    /**
     * Sortiert die Agenten nach aufsteigender Rüstfahrtlänge zur neuen
     * Operation über einen BubbleSort
     *
     * @param s
     * @param agents
     * @param op
     * @param startBundle
     */
    private ArrayList<E> sortAgentsDistance(Schedule s, Collection<E> agents, MultiScaleTransportOperation op, FieldElement startBundle) {
        ArrayList<E> sorted = new ArrayList<>();
        sorted.addAll(agents);
        int n = sorted.size();
        boolean swapped;
        do {
            swapped = false;
            for (int i = 1; i < n; i++) {
                if (getSetupDistance(s, sorted.get(i - 1), op, startBundle) > getSetupDistance(s, sorted.get(i), op, startBundle)) {
                    Collections.swap(sorted, i - 1, i);
                    swapped = true;
                }
            }
            n--;
        } while (swapped);
        return sorted;
    }

    private double getSetupDistance(Schedule s, E agent, MultiScaleTransportOperation top, FieldElement startBundle) {
        TreeMultimap<FieldElement, Operation> timeToOperationMap = s.getTimeToOperationMap(agent);
        /**
         * Vorangegangene TransportOperation.
         */
        MultiScaleTransportOperation q = null;
        /**
         * Bestimme vorangegangen TransportOperation
         */
        Map.Entry<FieldElement, Collection<Operation>> lowerEntry = timeToOperationMap.asMap().lowerEntry(startBundle);

        Collection<Operation> operationsBefore;

        if (lowerEntry != null) {
            operationsBefore = lowerEntry.getValue();
            if (operationsBefore.size() != 1) {
                for (Operation operation : operationsBefore) {
                    if (operation instanceof MultiScaleTransportOperation) {
                        q = (MultiScaleTransportOperation) operation;
                        break;
                    }
                }
            } else {
                Operation qop = operationsBefore.iterator().next();
                /**
                 * Nur Transportoperationen können untersucht werden.
                 */
                if (qop instanceof IdleSettingUpOperation) {
                    return Double.POSITIVE_INFINITY;
                }

                q = (MultiScaleTransportOperation) qop;
            }
        }

        /**
         * Suche SetupOperation von vorangegangener zur neu einzuplanender
         * Operation.
         */
        IdleSettingUpOperation sqj = rules.get(agent).findIdleSettingUpOperation(q, top);

        return sqj.getStart().getCenterOfGeneralOperatingArea().distance(sqj.getEnd().getCenterOfGeneralOperatingArea());
    }
}
