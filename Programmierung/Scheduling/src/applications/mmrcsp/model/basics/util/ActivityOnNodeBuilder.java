/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics.util;

import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.ExtendedActivityOnNodeGraph;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.mmrcsp.model.modes.JobOperation;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.Restriction;
import applications.mmrcsp.model.restrictions.TimeRestrictions;
import applications.mmrcsp.model.restrictions.instances.MaximumTimeLag;
import applications.mmrcsp.model.restrictions.instances.MinimumTimeLag;
import applications.mmrcsp.model.schedule.Schedule;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class ActivityOnNodeBuilder {

    public static boolean DEBUG = false;

     

    public static <E extends Operation> ActivityOnNodeGraph<E> build(Collection<E> operations, TimeRestrictions<E> timeRestrictions) {

        ActivityOnNodeGraph<E> graph = new ActivityOnNodeGraph<>();

        for (E operation : operations) {
            graph.addVertex(operation);
        }

        /**
         * Über alle Operation o_i
         */
        for (E o_i : operations) {
            /**
             * Hole die Restriktionen zu Nachfolgern
             */
            Map<E, ArrayList<Restriction>> restrictionToSuccessors = timeRestrictions.getRestrictionToSuccessors(o_i);
            /**
             * Gehe über alle Nachfolger o_j der Operation o_i
             */
            for (E o_j : restrictionToSuccessors.keySet()) {
                /**
                 * Gehe über alle Restriktionen zwischen o_i und o_j
                 */
                for (Restriction restriction : restrictionToSuccessors.get(o_j)) {
                    if (restriction instanceof MinimumTimeLag) {
                        Pair<E, E> edge = new Pair<>(o_i, o_j);
                        graph.addEdge(edge, restriction.getB().negate());
                    }
                    if (restriction instanceof MaximumTimeLag) {
                        Pair<E, E> edge = new Pair<>(o_j, o_i);
                        graph.addEdge(edge, restriction.getB());
                    }
                }
            }
        }

        return graph;
    }

    public static <E extends JobOperation> ActivityOnNodeGraph<E> build(JobOnNodeDiagramm<? extends MultiModeJob<E>> joN) {
        ActivityOnNodeGraph<E> graph = new ActivityOnNodeGraph<>();
        for (MultiModeJob<E> multiModeJob : joN.vertexSet()) {
            for (JobOperationList<E> jobOperationList : multiModeJob.getRoutings()) {
                for (E e : jobOperationList) {
                    graph.addVertex(e);
                }
            }
        }

        for (Pair<? extends MultiModeJob<E>, ? extends MultiModeJob<E>> pair : joN.edgeSet()) {
            MultiModeJob<E> first = pair.getFirst();
            MultiModeJob<E> second = pair.getSecond();
            for (JobOperationList<E> jobOperationList1 : first.getRoutings()) {
                for (JobOperationList<E> jobOperationList2 : second.getRoutings()) {
                    graph.addEdge(jobOperationList1.getLast(), jobOperationList2.getFirst());
                }
            }
        }
        return graph;
    }

    /**
     * Erzeugt anhand eines übergebenen Schedules ein ActivityOnNodeDiagramm mit
     * disjuktiven Kanten hinzugefügt, die die Reihenfolge auf einer Ressource
     * angeben.
     *
     * @param <E>
     * @param css
     * @param schedule
     * @param aon
     * @return
     */
    public static <O extends Operation> ExtendedActivityOnNodeGraph<O> getExtendedActivityOnNodeGraph(Collection<Resource> css, Schedule schedule, ActivityOnNodeGraph<O> aon) {
        ExtendedActivityOnNodeGraph<O> eaon = new ExtendedActivityOnNodeGraph<>(aon);
        for (Resource resource : css) {
            final ArrayList<Operation> list = new ArrayList<>(schedule.getOperationsForResource(resource));

            TreeSet<Operation> operationsForResource = new TreeSet<>(new Comparator<Operation>() {
                @Override
                public int compare(Operation o1, Operation o2) {
                    return Integer.compare(list.indexOf(o1), list.indexOf(o2));
                }
            });
            operationsForResource.addAll(list);
            

            Operation current = operationsForResource.first();
            while (!current.equals(operationsForResource.last())) {
                Operation next = operationsForResource.higher(current);

                while (schedule.get(current).equals(schedule.get(next))) {
                    next = operationsForResource.higher(next);
                }

                if (next == null) {
                    break;
                }

                boolean addDisjunctiveEdge = eaon.addDisjunctiveEdge(resource, new Pair<O, O>((O) current, (O) next), null);
                if (!addDisjunctiveEdge) {
                    throw new NoSuchElementException("Konnte nicht hinzugefügt werden");
                }
                current = next;
            }
        }

        return eaon;
    }

    /**
     * Erzeugt anhand eines übergebenen Schedules ein ActivityOnNodeDiagramm mit
     * disjuktiven Kanten hinzugefügt, die die Reihenfolge auf einer Ressource
     * angeben. Gibt diese nach Zusammenhangskomponenten sortiert aus.
     *
     * @param <O>
     * @param resource
     * @param schedule
     * @return
     */
    public static <O extends Operation> Collection<ExtendedActivityOnNodeGraph<O>> getExtendedActivityOnNodeGraphPerComponent(Collection<Resource> resource, Schedule schedule, ActivityOnNodeGraph<O> graph) {
        LinkedHashSet<ExtendedActivityOnNodeGraph<O>> result = new LinkedHashSet<>();

        for (ActivityOnNodeGraph<O> activityOnNodeGraph : graph.getConnectionComponents().keySet()) {

            ExtendedActivityOnNodeGraph<O> eaon = new ExtendedActivityOnNodeGraph<>(activityOnNodeGraph);

            for (Resource conveyanceSystem : resource) {
                List<O> resourceList = new ArrayList<>();

                for (Operation operation : schedule.getOperationsForResource(conveyanceSystem)) {
                    if (operation.getClass().isInstance(activityOnNodeGraph.vertexSet().iterator().next())) {

                        if (activityOnNodeGraph.containsVertex((O) operation)) {
                            resourceList.add((O) operation);
                        }
                    }
                }

                if (resourceList.isEmpty()) {
                    continue;
                }
                O last = resourceList.get(0);

                for (int i = 1; i < resourceList.size(); i++) {
                    O next = resourceList.get(i);
                    boolean addDisjunctiveEdge = eaon.addDisjunctiveEdge(conveyanceSystem, new Pair<O, O>(last, next), null);
                    if (!addDisjunctiveEdge) {
                        throw new NoSuchElementException("Konnte nicht hinzugefügt werden");
                    }
                    last = next;
                }
            }
            result.add(eaon);
        }
        return result;
    }
}
