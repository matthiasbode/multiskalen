/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule;

import applications.fuzzy.operation.FuzzyWorkloadParameters;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.mmrcsp.model.operations.SingleResourceOperation;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import math.FieldElement;
import math.LongValue;

/**
 * Ein Schedule beinhaltet für alle Ressourcen die eingeplanten Operationen.
 * Auch das Einplanen der Operationen geschieht an dieser Stelle.
 *
 * @author bode
 */
public class Schedule {

    /**
     * Ein Ablaufplan wird beschrieben durch die Zuordnung einer Startzeit zu
     * jeder Operation. Für den einfacheren Zugriff werden auch noch weitere
     * Maps vorgehalten
     */
    protected LinkedHashMap<Operation, FieldElement> mapOperationToStartTime = new LinkedHashMap<>();
    protected LinkedHashMap<Resource, TreeMultimap<FieldElement, Operation>> mapTimeToOperations = new LinkedHashMap<>();

    protected LinkedHashSet<Operation> didNotFinish = new LinkedHashSet<>();
    private final InstanceHandler handler;

//    public static String KEY_SCHEDULE = "SCHEDULE";
//    public static String KEY_AON = "AON";
//    public static String KEY_EALOSAE = "EALOSAE";
    public LinkedHashMap<Operation, FuzzyWorkloadParameters> fuzzyWorkloadParameters = new LinkedHashMap<Operation, FuzzyWorkloadParameters>();

    public Schedule(InstanceHandler rules, Resource... resources) {
        this.handler = rules;
    }

    public Schedule(InstanceHandler handler) {
        this.handler = handler;
    }

    /**
     * Ein Schedule wird bereits eingeplant in diesem Schedule. Die
     * Auslastungskurven werden dementsprechend angepasst.
     *
     * @param s
     * @param handler
     */
    public Schedule(Schedule s, InstanceHandler handler) {
        this.handler = handler;
        this.didNotFinish = new LinkedHashSet<>(s.didNotFinish);
        for (Operation o : s.mapOperationToStartTime.keySet()) {
            this.schedule(o.clone(), s.mapOperationToStartTime.get(o));
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.mapOperationToStartTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Schedule other = (Schedule) obj;
        if (!Objects.equals(this.mapOperationToStartTime, other.mapOperationToStartTime)) {
            return false;
        }
        return true;
    }
    
    
    

    public Collection<Operation> getOperationsForResource(Resource r) {
        TreeMultimap<FieldElement, Operation> operationsToTime = getTimeToOperationMap(r);
        return operationsToTime.values();
    }

    @Deprecated
    public NavigableSet<Operation> getOperationsForResourceAsNavigableSet(Resource r) {
        TreeMultimap<FieldElement, Operation> operationsToTime = getTimeToOperationMap(r);
        ArrayList<Operation> values = new ArrayList<>(operationsToTime.values());
        TreeSet<Operation> result = new TreeSet<>(new OperationComparator<>(this));
        result.addAll(values);
        return result;
    }

    public TreeMultimap<FieldElement, Operation> getTimeToOperationMap(Resource r) {
        TreeMultimap<FieldElement, Operation> result = mapTimeToOperations.get(r);

        if (result == null) {
            result = TreeMultimap.create(Ordering.natural(), Ordering.usingToString());
            mapTimeToOperations.put(r, result);
        }
        return result;
    }

    public Collection<Resource> getResources() {
        return mapTimeToOperations.keySet();
    }

    public List<FieldElement> getScheduleEventTimes() {
        ArrayList<FieldElement> res = new ArrayList<>();
        for (Operation operation : mapOperationToStartTime.keySet()) {
            if (!res.contains(mapOperationToStartTime.get(operation))) {
                res.add(mapOperationToStartTime.get(operation));
            }
        }
        Collections.sort(res);
        return res;
    }

    public FieldElement getLastScheduleEventTime() {
        ArrayList<FieldElement> res = new ArrayList<>();
        for (Resource r : mapTimeToOperations.keySet()) {
            TreeMultimap<FieldElement, Operation> times = mapTimeToOperations.get(r);
            res.add(times.asMap().lastKey());
        }
        Collections.sort(res);
        return res.get(res.size() - 1);
    }

    public Set<Operation> getActiveSet(long time) {
        Set<Operation> ops = new LinkedHashSet<>();
        for (Resource resource : mapTimeToOperations.keySet()) {
            for (Collection<Operation> set : mapTimeToOperations.get(resource).asMap().subMap(new LongValue(time), true, new LongValue(time + 1), true).values()) {
                ops.addAll(set);
            }
        }
        return ops;
    }

    public Collection<Operation> getLastOperationsBeforeTimeStamp(Resource r, long time) {
        Map.Entry<FieldElement, Collection<Operation>> lowerEntry = getTimeToOperationMap(r).asMap().lowerEntry(new LongValue(time));
        if (lowerEntry != null) {
            return lowerEntry.getValue();
        } else {
            return new HashSet<>();
        }
    }

    public Collection<Operation> getFirstOperationsAfterTimeStamp(Resource r, long time) {
        Map.Entry<FieldElement, Collection<Operation>> higherEntry = getTimeToOperationMap(r).asMap().higherEntry(new LongValue(time));
        if (higherEntry != null) {
            return higherEntry.getValue();
        } else {
            return new HashSet<>();
        }
    }

    public FieldElement get(Operation o) {
        return mapOperationToStartTime.get(o);
    }

    public boolean isScheduled(Operation o) {
        if (get(o) != null) {
            return true;
        }
        return false;
    }

    public void schedule(Operation o, long executionStart) {
        this.schedule(o, new LongValue(executionStart));
    }

    public void schedule(Operation o, FieldElement executionStart) throws NoSuchElementException {
        if (executionStart == null) {
            throw new IllegalArgumentException("Keine Startzeit festgelegt");
        }
        /**
         * Map zwischen Operation und Startzeit
         */
        mapOperationToStartTime.put(o, (executionStart));
        /**
         * Suche nach Startzeit und füge hinzu
         */
        for (Resource resource : o.getRequieredResources()) {
            TreeMultimap<FieldElement, Operation> timeToOperationMap = getTimeToOperationMap(resource);
            timeToOperationMap.put(executionStart, o);
        }
        /**
         * Aktualisierung Auslastungskurve
         */
        for (Resource r : o.getRequieredResources()) {
            this.getHandler().get(r).schedule(o, this, executionStart);
        }

        /**
         * Behandlung für SubResourcenBedarf
         */
        if (o instanceof SingleResourceOperation) {
            SingleResourceOperation so = (SingleResourceOperation) o;
            SubOperations subResourceDemand = so.getSubOperations();
            if (subResourceDemand != null) {
                for (Operation subOp : subResourceDemand.getSubOperations()) {
                    /**
                     * Berechne Startzeit der Operation
                     */
                    FieldElement subStart = executionStart;
                    FieldElement offset = so.getSubOperations().getTimeOffset().get(subOp);
                    if (offset instanceof LongValue) {
                        LongValue lv = (LongValue) offset;
                        if (lv.longValue() != 0) {
                            subStart = executionStart.add(offset);
                        }
                    }

                    this.schedule(subOp, subStart);
                }
            }
        }

    }

    /**
     * Gibt die alte Startzeit zurück und plant die Operation aus.
     *
     * @param o
     * @return
     */
    public FieldElement unschedule(Operation o) {
        FieldElement startTime = mapOperationToStartTime.get(o);

        /**
         * Aktualisierung Auslastungskurve
         */
        for (Resource r : o.getRequieredResources()) {
            getHandler().get(r).unSchedule(o, this);
        }

        if (o instanceof SingleResourceOperation) {
            SingleResourceOperation so = (SingleResourceOperation) o;
            SubOperations subResourceDemand = so.getSubOperations();
            if (subResourceDemand != null) {
                for (Operation subOp : subResourceDemand.getSubOperations()) {
                    unschedule(subOp);
                }
            }
        }

        /**
         * Suche nach Startzeit und entferne diese.
         */
        for (Resource resource : o.getRequieredResources()) {
            getTimeToOperationMap(resource).remove(startTime, o);
        }

        mapOperationToStartTime.remove(o);
        return startTime;
    }

    public Set<Operation> getActiveSet(long time, Resource r) {
        Set<Operation> activeSet = this.getActiveSet(time);
        Set<Operation> result = new LinkedHashSet<>();
        for (Operation operation : activeSet) {
            if (operation.getRequieredResources().contains(r)) {
                result.add(operation);
            }
        }
        return result;
    }

    public NavigableSet<FieldElement> getScheduleEventTimes(Resource r) {
        TreeMultimap<FieldElement, Operation> res = mapTimeToOperations.get(r);
        return res.keySet();
    }

    public Map<Operation, FieldElement> getStartTimes() {
        return mapOperationToStartTime;
    }

    public Set<Operation> getDidNotFinish() {
        return didNotFinish;
    }

    public void addNotFinish(Operation o) {
        this.didNotFinish.add(o);
    }

    public Map<Operation, FieldElement> getMapOperationToStartTime() {
        return mapOperationToStartTime;
    }

    public Set<Operation> getScheduledOperations() {
        return mapOperationToStartTime.keySet();
    }

    public InstanceHandler getHandler() {
        return handler;
    }

}
