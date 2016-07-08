/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.AoNComponent;
import applications.mmrcsp.model.basics.ExtendedActivityOnNodeGraph;
import applications.mmrcsp.model.basics.JoNComponent;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.transshipment.model.structs.TrainType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import math.FieldElement;
import math.LongValue;
import org.graph.directed.DefaultDirectedGraph;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class LoadUnitJobActivityOnNodeBuilder {

    public static boolean DEBUG = false;

    /**
     * Erzeugt ein ActitvityOnNodeDiagramm für ein TerminalProblem, dass alle
     * möglichen Operationen aller Routen enthält. Dieses muss später dann
     * ausgedünnt werden, in dem nur die Knoten behalten werden, die zu den
     * gewählten Routen zählen.
     *
     * @param problem
     * @return
     */
    public static ActivityOnNodeGraph<RoutingTransportOperation> buildAlloverGraph(MultiJobTerminalProblem problem) {
        List<JobOperationList<RoutingTransportOperation>> allRoutings = new ArrayList<>();
        InstanceHandler rules = new InstanceHandler(problem.getScheduleManagerBuilder());
        /**
         * Generierung der Zusammenhangskomponenten.
         */
        Collection<JoNComponent<LoadUnitJob>> connectionComponents = problem.getJobOnNodeDiagramm().getConnectionComponents();
        /**
         * Schleife über die Zusammenhangskomponenten.
         */
        for (DefaultDirectedGraph<LoadUnitJob> componentGraph : connectionComponents) {

            /**
             * Hinzufügen der Routen zu den möglichen Routen, berücksichtigen,
             * dass bei firstJob die erste (die direkte) Route entfernt wird.
             */
            for (LoadUnitJob loadUnitJob : componentGraph.vertexSet()) {
                List<JobOperationList<RoutingTransportOperation>> routings = loadUnitJob.getRoutings();
                if (problem.notDirectlyTransportable != null && problem.notDirectlyTransportable.contains(loadUnitJob)) {
                    routings.remove(0);
                }
                allRoutings.addAll(routings);
            }
        }

        /**
         * Alle Operationen.
         */
        ArrayList<RoutingTransportOperation> allOperations = new ArrayList<>();

        /**
         * Alle Operationen aus den Routings ziehen.
         */
        for (JobOperationList<RoutingTransportOperation> routing : allRoutings) {
            allOperations.addAll(routing);
        }

        /**
         * Bestimme die Dauern und den Bedarf für die RoutingTransportOperation.
         */
        for (RoutingTransportOperation operation : allOperations) {
            ConveyanceSystemRule rule = (ConveyanceSystemRule) rules.get(operation.getResource());
            if (rule == null) {
                System.out.println(operation);
                System.out.println(operation.getResource());
                System.out.println(rules.getResources().size());
                throw new NoSuchElementException("Keine Regel zur Transportzeitberechnung hinterlegt");
            }
            FieldElement transportationTime = rule.getTransportationTime(operation.getOrigin(), operation.getDestination(), operation.getLoadUnit());
            operation.setDuration(transportationTime);
        }

        /**
         * AON über alle Operationen und alle Routings!
         */
        ActivityOnNodeGraph<RoutingTransportOperation> aon = build(problem);

        return aon;
    }

    private static ActivityOnNodeGraph<RoutingTransportOperation> build(MultiJobTerminalProblem problem) {

        ActivityOnNodeGraph<RoutingTransportOperation> allOverGraph = new ActivityOnNodeGraph<>();
//        System.out.println("Anzahl ZKs: " + problem.getJobOnNodeDiagramm().getConnectionComponents().size());
        for (JoNComponent<LoadUnitJob> joNComponent : problem.getJobOnNodeDiagramm().getConnectionComponents()) {
            AoNComponent<RoutingTransportOperation> aoN = new AoNComponent<>(allOverGraph);
            List<JobOperationList<RoutingTransportOperation>> routingsOfComponent = new ArrayList<>();

            /**
             * Hinzufügen der Routen zu den möglichen Routen, berücksichtigen,
             * dass bei firstJob die erste (die direkte) Route entfernt wird.
             */
            for (LoadUnitJob loadUnitJob : joNComponent.vertexSet()) {
                List<JobOperationList<RoutingTransportOperation>> rs = loadUnitJob.getRoutings();
                if (problem.notDirectlyTransportable != null && problem.notDirectlyTransportable.contains(loadUnitJob)) {
                    rs.remove(0);
                }
                routingsOfComponent.addAll(rs);
            }
            for (JobOperationList<RoutingTransportOperation> jobOperationList : routingsOfComponent) {
                for (RoutingTransportOperation routingTransportOperation : jobOperationList) {
                    aoN.addVertex(routingTransportOperation);
                }
            }

            for (JobOperationList<RoutingTransportOperation> currentRouting : routingsOfComponent) {
                /**
                 * Vorgängerbeziehungen innerhalb der Route abgearbeitet.
                 */
                for (int i = 0; i < currentRouting.size(); i++) {
                    RoutingTransportOperation first = currentRouting.get(i);
                    if (i < currentRouting.size() - 1) {
                        RoutingTransportOperation second = currentRouting.get(i + 1);
                        aoN.addEdge(first, second, new LongValue(first.getDuration().longValue()));
                    }
                }
            }

            /*
             * ################ ORIGIN/DESTINATION CONSTRAINTS
             */
            /**
             * Gehe über alle Routings
             */
            for (JobOperationList<RoutingTransportOperation> routing1 : routingsOfComponent) {

                for (JobOperationList<RoutingTransportOperation> routing2 : routingsOfComponent) {
                    if (routing1.get(0).getLoadUnit().equals(routing2.get(0).getLoadUnit())) {
                        continue;
                    }

                    /**
                     * Wenn das Ziel von Routing1 gleich dem Origin von Routing2
                     * ist, muss Routing2 zuerst abgearbeitet werden.
                     */
                    if (routing1.getLast().getDestination().equals(routing2.getFirst().getOrigin())) {
                        if (routing1.getLast().getDestination() instanceof TrainType) {
                            /**
                             * Bearbeitete Routen werden ausgenommen.
                             */
                            if (problem.notDirectlyTransportable == null || !problem.notDirectlyTransportable.contains(routing2.getJob())) {
                                aoN.addEdge(routing2.getFirst(), routing1.getLast(), new LongValue(routing2.getFirst().getDuration().longValue()));
                            }
                        }
                    }

                }
            }
            allOverGraph.add(aoN);
            
        }
        return allOverGraph;
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
    public static ExtendedActivityOnNodeGraph<RoutingTransportOperation> getExtendedActivityOnNodeGraph(Collection<ConveyanceSystem> css, LoadUnitJobSchedule schedule, ActivityOnNodeGraph<RoutingTransportOperation> aon) {
        ExtendedActivityOnNodeGraph<RoutingTransportOperation> eaon = new ExtendedActivityOnNodeGraph<>(aon);
        for (Resource conveyanceSystem : css) {
            List<MultiScaleTransportOperation> resourceList = new ArrayList<>();
            for (Operation operation : schedule.getOperationsForResource(conveyanceSystem)) {
                if (operation instanceof MultiScaleTransportOperation) {
                    resourceList.add((MultiScaleTransportOperation) operation);
                }
            }
            RoutingTransportOperation last = resourceList.get(0).getRoutingTransportOperation();

            for (int i = 1; i < resourceList.size(); i++) {
                RoutingTransportOperation next = resourceList.get(i).getRoutingTransportOperation();
                boolean addDisjunctiveEdge = eaon.addDisjunctiveEdge(conveyanceSystem, new Pair<RoutingTransportOperation, RoutingTransportOperation>(last, next), null);
                if (!addDisjunctiveEdge) {
                 throw new NoSuchElementException("Konnte nicht hinzugefügt werden");
                }
                last = next;
            }
        }

        return eaon;
    }

    /**
     * Erzeugt anhand eines übergebenen Schedules ein ActivityOnNodeDiagramm mit
     * disjuktiven Kanten hinzugefügt, die die Reihenfolge auf einer Ressource
     * angeben. Gibt diese nach Zusammenhangskomponenten sortiert aus.
     *
     * @param css
     * @param schedule
     * @param graph
     * @return
     */
    public static Collection<ExtendedActivityOnNodeGraph<RoutingTransportOperation>> getExtendedActivityOnNodeGraphPerComponent(Collection<ConveyanceSystem> css, LoadUnitJobSchedule schedule, ActivityOnNodeGraph<RoutingTransportOperation> graph) {
        LinkedHashSet<ExtendedActivityOnNodeGraph<RoutingTransportOperation>> result = new LinkedHashSet<>();
       
        
        for (ActivityOnNodeGraph<RoutingTransportOperation> activityOnNodeGraph : graph.getConnectionComponents().keySet()) {

            ExtendedActivityOnNodeGraph<RoutingTransportOperation> eaon = new ExtendedActivityOnNodeGraph<>(activityOnNodeGraph);
            for (ConveyanceSystem conveyanceSystem : css) {
                List<MultiScaleTransportOperation> resourceList = new ArrayList<>();
                for (Operation operation : schedule.getOperationsForResource(conveyanceSystem)) {
                    if (operation instanceof MultiScaleTransportOperation) {
                        MultiScaleTransportOperation mtop = (MultiScaleTransportOperation) operation;
                        if (activityOnNodeGraph.containsVertex(mtop.getRoutingTransportOperation())) {
                            resourceList.add((MultiScaleTransportOperation) operation);
                        }
                    }
                }
                if (resourceList.isEmpty()) {
                    continue;
                }
                RoutingTransportOperation last = resourceList.get(0).getRoutingTransportOperation();

                for (int i = 1; i < resourceList.size(); i++) {
                    RoutingTransportOperation next = resourceList.get(i).getRoutingTransportOperation();
                    boolean addDisjunctiveEdge = eaon.addDisjunctiveEdge(conveyanceSystem, new Pair<RoutingTransportOperation, RoutingTransportOperation>(last, next), null);
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
