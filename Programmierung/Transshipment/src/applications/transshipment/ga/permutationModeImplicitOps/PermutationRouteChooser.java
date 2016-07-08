/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.JoNComponent;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.basics.util.TransshipmentEALOSAEBuilder;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import ga.individuals.subList.SubListIndividual;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Die Routenwahl wird implizit über eine Prioritätenlisten vorgenommen. Für
 * jede Zusammenhangskomoponente wird ein Graph nach und nach aufgebaut. Dabei
 * gibt eine Prioritätenliste für die Jobs an, welcher Job zuerst in den Graoh
 * eingeplant werden soll.
 *
 * @author bode
 */
public class PermutationRouteChooser {

    final MultiJobTerminalProblem problem;
    private ActivityOnNodeGraph<RoutingTransportOperation> completeAoN;
    private Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> operationEalosaes;
    private LinkedHashSet<LoadUnitJob> notRoutable;
    private HashMap<LoadUnitJob, JobOperationList<RoutingTransportOperation>> routingsPerJob = new HashMap<LoadUnitJob, JobOperationList<RoutingTransportOperation>>();

    public PermutationRouteChooser(MultiJobTerminalProblem problem, List<SubListIndividual<LoadUnitJob>> list) {
        notRoutable = new LinkedHashSet<>();

        this.problem = problem;
        this.completeAoN = new ActivityOnNodeGraph<>();
        this.operationEalosaes = new LinkedHashMap<>();

        /**
         * Gehe über alle Zusammenhangskomponenten, für jede
         * Zusammenhangskomponente wird eine Sortiermethode gewählt, dazu wird
         * die Rules-Liste verwendet.
         */
        for (int i = 0; i < list.size(); i++) {

            SubListIndividual<LoadUnitJob> subList = list.get(i);
            ArrayList<LoadUnitJob> jobs = new ArrayList<>(subList.getChromosome());

            ActivityOnNodeGraph<RoutingTransportOperation> componentAoN = new ActivityOnNodeGraph<>();
            /**
             * Gehe über alle Jobs und plane die beste Route ein, die möglich
             * ist.
             */
            for (LoadUnitJob loadUnitJob : jobs) {
                if(!problem.getJobs().contains(loadUnitJob)){
                    continue;
                }
                if (problem.getJobTimeWindows().get(loadUnitJob).equals(TimeSlot.nullTimeSlot)) {
                    continue;
                }
                List<JobOperationList<RoutingTransportOperation>> routings = loadUnitJob.getRoutings();
                boolean added = false;
                for (JobOperationList<RoutingTransportOperation> routing : routings) {
                    GraphBundle bundle = addRoute(routing, componentAoN);
                    if (bundle != null) {
                        routingsPerJob.put(loadUnitJob, routing);
                        operationEalosaes.putAll(bundle.ealosae);
                        componentAoN = bundle.aon;
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    /**
                     * Job muss DNF werden Überprüfung
                     */
                    notRoutable.add(loadUnitJob);
                     
                }
            }
            completeAoN.add(componentAoN);
        }
    }

    public HashMap<LoadUnitJob, JobOperationList<RoutingTransportOperation>> getRoutingsPerJob() {
        return routingsPerJob;
    }

    public ActivityOnNodeGraph<RoutingTransportOperation> getActivityOnNodeDiagramm() {
        if (completeAoN == null) {
            throw new NullPointerException("You have to calculate the AoN first");
        }
        return completeAoN;
    }

    public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> getEalosaes() {
        if (operationEalosaes == null) {
            throw new NullPointerException("You have to calculate the AoN first");
        }
        return operationEalosaes;
    }

    /**
     * Falls null, dann nicht möglich
     *
     * @param routing
     * @param aon
     * @return
     */
    private GraphBundle addRoute(JobOperationList<RoutingTransportOperation> routing, ActivityOnNodeGraph<RoutingTransportOperation> aon) {

        HashSet<RoutingTransportOperation> verticies = new HashSet<>(aon.vertexSet());
        verticies.addAll(routing);

        ActivityOnNodeGraph<RoutingTransportOperation> newComponentAON = problem.getActivityOnNodeDiagramm().getSubGraph(verticies);
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = TransshipmentEALOSAEBuilder.ealosaes(newComponentAON, problem.getJobTimeWindows());

        if (ealosaes == null) {
            return null;
        }
        return new GraphBundle(ealosaes, newComponentAON);

    }

    public LinkedHashSet<LoadUnitJob> getNotRouteable() {
        return notRoutable;
    }

    /**
     * Ein GraphBundle enthält einen ActivityOnNodeGraph für die
     * Zusammenhangskomponente und die entsprechenden EALOSAEs
     */
    static class GraphBundle {

        public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae;
        public ActivityOnNodeGraph<RoutingTransportOperation> aon;

        public GraphBundle(Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae, ActivityOnNodeGraph<RoutingTransportOperation> aon) {
            this.ealosae = ealosae;
            this.aon = aon;
        }

    }

    class JoNComponentComparator implements Comparator<JoNComponent<LoadUnitJob>> {

        HashMap<JoNComponent<LoadUnitJob>, Integer> map = new HashMap<>();

        public JoNComponentComparator(Collection<JoNComponent<LoadUnitJob>> components) {
            for (JoNComponent<LoadUnitJob> joNComponent : components) {
                int hashCode = 0;
                for (LoadUnitJob loadUnitJob : joNComponent.vertexSet()) {
                    hashCode += Objects.hashCode(loadUnitJob.getLoadUnit().getID());
                }
                map.put(joNComponent, hashCode);
            }
        }

        @Override
        public int compare(JoNComponent<LoadUnitJob> o1, JoNComponent<LoadUnitJob> o2) {
            return Integer.compare(map.get(o1), map.get(o2));
        }

    }
}
