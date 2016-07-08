/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import ga.individuals.subList.SubListIndividual;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 */
public final class PermutationModeDecoder {

    private final MultiJobTerminalProblem problem;
    private final PermutationModeIndividual indMode;
    private final Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes;
    private final ActivityOnNodeGraph<RoutingTransportOperation> graph;
    private final Collection<LoadUnitJob> notRouteable;
    private final HashMap<LoadUnitJob, JobOperationList<RoutingTransportOperation>> routingsPerJob;

    public PermutationModeDecoder(
            PermutationModeIndividual indMode,
            MultiJobTerminalProblem problem) {

        this.indMode = indMode;
        this.problem = problem;
        /**
         * Bestimmen der EALOSAEs und des AoN-Diagramms
         */
        List<SubListIndividual<LoadUnitJob>> list = this.indMode.getList();
        PermutationRouteChooser chooser = new PermutationRouteChooser(problem, list);

        this.ealosaes = chooser.getEalosaes();
        this.graph = chooser.getActivityOnNodeDiagramm();
        this.notRouteable = chooser.getNotRouteable();
        this.routingsPerJob = chooser.getRoutingsPerJob();

//        System.out.println("Graph mit allen Routen: " + problem.getActivityOnNodeDiagramm().numberOfVertices());
//        System.out.println("Graphgröße:" + this.graph.numberOfVertices());
//        System.out.println(this.graph.hashCode());
    }

    public MultiJobTerminalProblem getProblem() {
        return problem;
    }

    public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> getEalosaes() {
        return ealosaes;
    }

    public ActivityOnNodeGraph<RoutingTransportOperation> getGraph() {
        return graph;
    }

    public Collection<LoadUnitJob> getNotRouteable() {
        return notRouteable;
    }

    public HashMap<LoadUnitJob, JobOperationList<RoutingTransportOperation>> getRoutingsPerJob() {
        return routingsPerJob;
    }
    

}
