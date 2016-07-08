/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.LoadUnitResource;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.routing.evaluation.EvalFunction_TransportOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.graph.algorithms.pathsearch.KShortestPathAlgorithm;
import org.graph.weighted.DoubleEdgeWeight;
import org.graph.weighted.WeightedPath;
import org.util.Pair;

/**
 *
 * @author Matthias
 */
public class KShortestPathDefaultRouteFinder extends RouteFinder {

    KShortestPathAlgorithm<TransferArea, DoubleEdgeWeight> shortestPathAlgorithm;

    public KShortestPathDefaultRouteFinder(MultiJobTerminalProblem problem, List<LoadUnitResource> availableResources, KShortestPathAlgorithm<TransferArea, DoubleEdgeWeight> shotestPathAlgorithm, EvalFunction_TransportOperation bewertung) {
        super(problem, availableResources, bewertung);
        this.shortestPathAlgorithm = shotestPathAlgorithm;
    }

    public KShortestPathDefaultRouteFinder(MultiJobTerminalProblem problem, GraphBuilder graphBuiler, KShortestPathAlgorithm<TransferArea, DoubleEdgeWeight> shotestPathAlgorithm) {
        super(problem, graphBuiler);
        this.shortestPathAlgorithm = shotestPathAlgorithm;
    }

    public List<JobOperationList<RoutingTransportOperation>> calcRoutes(LoadUnitJob job, SpecifiedTransportGraph graphForJob, int numberOfRoutes) {

        List<JobOperationList<RoutingTransportOperation>> result = new ArrayList<>();
        /**
         * Gewichten des Graphens
         */

        TimeSlot temporalAvailabilityOrigin = graphForJob.getJob().getOrigin().getTemporalAvailability().getAllOverTimeSlot();
        TimeSlot temporalAvailabilityDestination = graphForJob.getJob().getDestination().getTemporalAvailability().getAllOverTimeSlot();

        boolean notDirectTransportable = false;
        if (temporalAvailabilityOrigin.section(temporalAvailabilityDestination) == null) {
            notDirectTransportable = true;
        }
        job.setNotDirectlyTransportable(notDirectTransportable);
        if (notDirectTransportable) {
            numberOfRoutes++;
        }

        /**
         * Berechnen der besten Wege
         */
//        ArrayList<WeightedPath<TransferArea, DoubleEdgeWeight>> kShortestPaths = shortestPathAlgorithm.kShortestPaths(graphForJob, graphForJob.getStart(), graphForJob.getZiel(), numberOfRoutes);
//
//        if (notDirectTransportable) {
//            kShortestPaths.remove(0);
//        }
//
//        /**
//         * Aus dem Pfad ein Routing erzeugen
//         */
//        for (WeightedPath<TransferArea, DoubleEdgeWeight> path : kShortestPaths) {
//            JobOperationList<RoutingTransportOperation> routing = getRouting(job, graphForJob, path);
//            if (routing != null) {
//                result.add(routing);
//            }
//        }

        return result;
    }

    private JobOperationList<RoutingTransportOperation> getRouting(LoadUnitJob job, SpecifiedTransportGraph graph, WeightedPath<TransferArea, DoubleEdgeWeight> path) {
        JobOperationList<RoutingTransportOperation> routing = new JobOperationList(job);
        ConveyanceSystem oldCS = null;
        for (int i = 1; i < path.getNumberOfVertices() - 2; i++) {
            Pair<TransferArea, TransferArea> transport = path.get(i);
            ConveyanceSystem cs = graph.getConveyanceSystem(transport);

            if (oldCS != null && cs.equals(oldCS)
                    && //transport.getFirst().getStorageSystem() instanceof LCSHandover) {
                    !problem.getStorageInteractionRule(transport.getFirst().getStorageSystem()).canTransferBetween(oldCS, cs)) {
                return null;
            }
            if (cs == null) {
                throw new NoSuchElementException("Kein zugehöriges ConveyanceSystem gefunden!");
            }
            RoutingTransportOperation rtop = new RoutingTransportOperation(transport.getFirst(), transport.getSecond(), job, cs);
            routing.add(rtop);
            oldCS = cs;
        }
        routing.setWeight(path.getWeight().doubleValue());
        return routing;
    }
}
