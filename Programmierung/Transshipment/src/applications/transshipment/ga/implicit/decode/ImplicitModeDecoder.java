/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.decode;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.routeDetermination.ImplicitRouteChooser;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 */
public final class ImplicitModeDecoder {

    private final MultiJobTerminalProblem problem;
    private final ImplicitModeIndividual indMode;
    private final Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes;
    private final ActivityOnNodeGraph<RoutingTransportOperation> graph;
    private final Collection<LoadUnitJob> notRouteable;
    private HashMap<LoadUnitJob, Integer> routes;

    public ImplicitModeDecoder(
            ImplicitModeIndividual indMode,
            MultiJobTerminalProblem problem) {

        this.indMode = indMode;
        this.problem = problem;
        /**
         * Bestimmen der EALOSAEs und des AoN-Diagramms
         */
        List<LoadUnitJobPriorityRules.Identifier> modeStrategies = this.indMode.getList();
        ImplicitRouteChooser chooser = new ImplicitRouteChooser(problem, modeStrategies);

        this.ealosaes = chooser.getEalosaes();
        this.graph = chooser.getActivityOnNodeDiagramm();
        this.notRouteable = chooser.getNotRouteable();
        this.routes = chooser.getRoutes();
    }

    public JobOperationList<RoutingTransportOperation> getRoute(LoadUnitJob job) {
        return job.getRoutings().get(this.routes.get(job));
    }
    
    public int getRouteNumber(LoadUnitJob job){
        return this.routes.get(job);               
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

}
