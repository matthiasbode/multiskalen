/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.routeDetermination;

import applications.mmrcsp.model.Job;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.JoNComponent;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.basics.util.TransshipmentEALOSAEBuilder;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import math.FieldElement;
import math.LongValue;

/**
 * Die Routenwahl wird implizit über eine Prioritätenlisten vorgenommen. Für
 * jede Zusammenhangskomoponente wird ein Graph nach und nach aufgebaut. Dabei
 * gibt eine Prioritätenliste für die Jobs an, welcher Job zuerst in den Graoh
 * eingeplant werden soll.
 *
 * @author bode
 */
public class ImplicitRouteChooser {

    final MultiJobTerminalProblem problem;
    private ActivityOnNodeGraph<RoutingTransportOperation> completeAoN;
    private Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> operationEalosaes;
    private LinkedHashSet<LoadUnitJob> notRoutable;
    private HashMap<LoadUnitJob, Integer> routes = new HashMap<>();

    public ImplicitRouteChooser(MultiJobTerminalProblem problem, List<LoadUnitJobPriorityRules.Identifier> modeStrategies) {
        notRoutable = new LinkedHashSet<>();
        JobOnNodeDiagramm<LoadUnitJob> jobOnNodeDiagramm = problem.getJobOnNodeDiagramm();
        LoadUnitJobPriorityRules compartators = new LoadUnitJobPriorityRules(jobOnNodeDiagramm, problem.getJobTimeWindows());
        this.problem = problem;
        this.completeAoN = new ActivityOnNodeGraph<>();
        this.operationEalosaes = new LinkedHashMap<>();

        List<JoNComponent<LoadUnitJob>> connectionComponents = new ArrayList<>(jobOnNodeDiagramm.getConnectionComponents());
        JoNComponentComparator joNComponentComparator = new JoNComponentComparator(connectionComponents);
        Collections.sort(connectionComponents, joNComponentComparator);

        if (modeStrategies.size() != connectionComponents.size()) {
            throw new IllegalArgumentException("für die Job-Zusammenhangskomponenten müssen Strategien definiert werden." + modeStrategies.size() + "/" + connectionComponents.size());
        }
        /**
         * Gehe über alle Zusammenhangskomponenten, für jede
         * Zusammenhangskomponente wird eine Sortiermethode gewählt, dazu wird
         * die Rules-Liste verwendet.
         */
//        System.out.println("Größe:" + connectionComponents.size());

        for (int i = 0; i < connectionComponents.size(); i++) {

            JoNComponent<LoadUnitJob> component = connectionComponents.get(i);
            ArrayList<LoadUnitJob> jobs = new ArrayList<>(component.vertexSet());
            /**
             * Sortiert nach Prioritäten
             */
            Comparator<LoadUnitJob> comp = compartators.getMap(modeStrategies.get(i));
            Collections.sort(jobs, comp);

            ActivityOnNodeGraph<RoutingTransportOperation> componentAoN = new ActivityOnNodeGraph<>();
            /**
             * Gehe über alle Jobs und plane die beste Route ein, die möglich
             * ist.
             */

            for (LoadUnitJob loadUnitJob : jobs) {
                if (problem.getJobTimeWindows().get(loadUnitJob).equals(TimeSlot.nullTimeSlot)) {
                    continue;
                }
                List<JobOperationList<RoutingTransportOperation>> routings = loadUnitJob.getRoutings();
                boolean added = false;
                int r = 0;
                for (JobOperationList<RoutingTransportOperation> routing : routings) {
                    GraphBundle bundle = addRoute(routing, componentAoN);
                    if (bundle != null) {
                        operationEalosaes.putAll(bundle.ealosae);
                        componentAoN = bundle.aon;
                        added = true;
                        break;
                    }
                    r++;
                }
                routes.put(loadUnitJob, r);

                if (!added) {
                    /**
                     * Job muss DNF werden Überprüfung
                     */
                    notRoutable.add(loadUnitJob);
                    routes.put(loadUnitJob, Integer.MAX_VALUE);
                }
            }

            completeAoN.add(componentAoN);
        }
//        if (TransshipmentParameter.DEBUG) {
//            ArrayList<LoadUnitJob> jobsToprint = new ArrayList<>(jobOnNodeDiagramm.vertexSet());
//            Collections.sort(jobsToprint, new Comparator<LoadUnitJob>() {
//                @Override
//                public int compare(LoadUnitJob o1, LoadUnitJob o2) {
//                    return o1.getLoadUnit().getID().compareTo(o2.getLoadUnit().getID());
//                }
//            });
//
//            for (LoadUnitJob job : jobsToprint) {
//                Integer r = routes.get(job);
//                System.out.println(job + "\t" + r + "\t" + job.getRoutings().get(r - 1));
//                for (JobOperationList<RoutingTransportOperation> jobOperationList : job.getRoutings()) {
//                    System.out.println(jobOperationList);
//                }
//                System.out.println("---------------");
//
//            }
//        }
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

    public JobOperationList<RoutingTransportOperation> getRoute(LoadUnitJob job) {
        return job.getRoutings().get(this.routes.get(job));
    }

    /**
     * Falls null, dann nicht möglich
     *
     * @param routing
     * @param aon
     * @return
     */
    private GraphBundle addRoute(JobOperationList<RoutingTransportOperation> routing, ActivityOnNodeGraph<RoutingTransportOperation> aon) {
        for (RoutingTransportOperation transportOperation : routing) {
            TimeSlot tADest = transportOperation.getDestination().getTemporalAvailability().getAllOverTimeSlot();
            TimeSlot tsOrigin = transportOperation.getOrigin().getTemporalAvailability().getAllOverTimeSlot();
            if (tADest.getFromWhen() instanceof LongValue) {
                tADest.setFromWhen(new LongValue(tADest.getFromWhen().add(TransshipmentParameter.minTransshipDuration).longValue()));
            }
            if (tADest.getFromWhen() instanceof DiscretizedFuzzyInterval) {
                tADest.setFromWhen(FuzzyFactory.createCrispValue(TransshipmentParameter.minTransshipDuration.longValue()));
            }

            if (tsOrigin.isDisjunctTo(tADest)) {
                return null;
            }
        }

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

    public HashMap<LoadUnitJob, Integer> getRoutes() {
        return routes;
    }
    
    
}
