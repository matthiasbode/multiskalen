/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.rules;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.mmrcsp.model.schedule.Schedule;
import com.google.common.collect.Iterators;
import java.util.Collection;

import java.util.LinkedHashMap;
import math.FieldElement;

/**
 * Interface, das vorschreibt, welche ScheduleRules bei welcher Ressource
 * benutzt werden. Aus diesem Builder wird ein Exemplar der Klasse ScheduleRules
 * erzeugt, welches im weiteren Verlauf dann genutzt wird.
 *
 * @author Matthias
 */
public abstract class ScheduleManagerBuilder {

    /**
     * Map, die bereits eingeplante Operationen berücksichtigt.
     */
    private final LinkedHashMap<Resource, FieldElement> startTimeResource = new LinkedHashMap<>();

    public ScheduleManagerBuilder() {
    }

    public static LinkedHashMap<Resource, FieldElement> getNewStartTimesForResource(FieldElement t, Schedule schedule, Collection<? extends Resource> resources) {
        final LinkedHashMap<Resource, FieldElement> startTimes = new LinkedHashMap<>();
        for (Resource resource : resources) {
            Collection<Operation> operationsForResource = schedule.getOperationsForResource(resource);
            FieldElement start;
            if (operationsForResource.isEmpty()) {
                start = t.clone();
            } else {
                Operation last = Iterators.getLast(operationsForResource.iterator());
                start = schedule.get(last).add(last.getDuration());
            }
            startTimes.put(resource, start);
        }
        return startTimes;
    }

    public void updateStartTimesForResources(FieldElement t, Schedule schedule, Collection<? extends Resource> resources) {
        for (Resource resource : resources) {
            Collection<Operation> operationsForResource = schedule.getOperationsForResource(resource);
            FieldElement start = null;
            if (operationsForResource.isEmpty()) {
                start = t.clone();
            } else {
                Operation last = Iterators.getLast(operationsForResource.iterator());
                start = schedule.get(last).add(last.getDuration());
            }
            startTimeResource.put(resource, start);
        }
    }

    public FieldElement getStartTimeForResource(Resource r) {
        return startTimeResource.get(r);
    }

    public abstract ScheduleRule build(Resource r, InstanceHandler handler);

    public abstract SharedResourceManager build(SharedResource r);
}
