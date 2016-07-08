/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing;

import applications.transshipment.routing.evaluation.EvalFunction_TransportOperation;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.resources.LoadUnitResource;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;
import org.graph.weighted.DoubleEdgeWeight;
import org.util.Pair;

/**
 * Klasse, die einige Methode und Zuweisungen kapselt, um Routen für einen oder
 * mehrere LoadUnitJobs zu finden.
 *
 * @author bode
 */
public abstract class RouteFinder {

    protected GraphBuilder graphBuilder;
    protected EvalFunction_TransportOperation bewertung;
    protected MultiJobTerminalProblem problem;
    public PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public RouteFinder(MultiJobTerminalProblem problem, List<LoadUnitResource> availableResources, EvalFunction_TransportOperation bewertung) {
        this.graphBuilder = new DefaultGraphBuilder(availableResources, problem);
        this.problem = problem;

        this.bewertung = bewertung;
    }

    public RouteFinder(MultiJobTerminalProblem problem, GraphBuilder graphBuiler) {
        this.problem = problem;
        this.graphBuilder = graphBuiler;

    }

    public void setProblem(MultiJobTerminalProblem problem) {
        this.problem = problem;
    }

    public void calculateRoutes(int anzahl) {
        problem.getOperations().clear();

        for (int i = 0; i < problem.getJobs().size(); i++) {
            LoadUnitJob job = problem.getJobs().get(i);
            changeSupport.firePropertyChange("COUNT", i-1, i);
            job.getRoutings().clear();
            SpecifiedTransportGraph graphForJob = getSpecifiedTransportGraph(job);
            List<JobOperationList<RoutingTransportOperation>> routings = calcRoutes(job, graphForJob, anzahl);
            for (JobOperationList<RoutingTransportOperation> route : routings) {
                job.addRouting(route);
                for (RoutingTransportOperation routingTransportOperation : route) {
                    problem.getOperations().add(routingTransportOperation);
                }
            }
        }
        ArrayList<LoadUnitJob> jobsToprint = new ArrayList<>(problem.getJobs());
        Collections.sort(jobsToprint, new Comparator<LoadUnitJob>() {

            @Override
            public int compare(LoadUnitJob o1, LoadUnitJob o2) {
                return o1.getLoadUnit().getID().compareTo(o2.getLoadUnit().getID());
            }
        });
//        if (TransshipmentParameter.DEBUG) {
//            for (LoadUnitJob job : jobsToprint) {
//
//                System.out.println(job);
//                for (JobOperationList<RoutingTransportOperation> jobOperationList : job.getRoutings()) {
//                    System.out.println(jobOperationList);
//                }
//                System.out.println("----");
//            }
//        }
    }

    public abstract List<JobOperationList<RoutingTransportOperation>> calcRoutes(LoadUnitJob job, SpecifiedTransportGraph graphForJob, int numberOfRoutes);

    public SpecifiedTransportGraph getSpecifiedTransportGraph(LoadUnitJob job) {
        /**
         * Gewichten des Graphens
         */
        SpecifiedTransportGraph graphForJob = graphBuilder.getGraphForJob(job, 0);
        for (Pair<TransferArea, TransferArea> pair : graphForJob.edgeSet()) {
            ConveyanceSystem conveyanceSystem = graphForJob.getConveyanceSystem(pair);
            double evaluation = bewertung.evaluate(pair, conveyanceSystem, job.getLoadUnit());
            graphForJob.setEdgeWeight(pair, new DoubleEdgeWeight(evaluation));
        }
        return graphForJob;
    }

    public TransportGraph getStaticGraph() {
        return this.graphBuilder.getStaticTransportGraph();
    }

    public EvalFunction_TransportOperation getBewertung() {
        return bewertung;
    }

}
