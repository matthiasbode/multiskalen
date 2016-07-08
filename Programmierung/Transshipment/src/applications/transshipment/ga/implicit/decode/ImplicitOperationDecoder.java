/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.decode;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 */
public final class ImplicitOperationDecoder {

    private final TerminalProblem problem;
    private final ImplicitOperationIndividual indOps;
    private final Transshipment_ImplicitScheduleGenerationScheme sgs;
    private final Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes;
    private final ActivityOnNodeGraph<RoutingTransportOperation> graph;

    private LoadUnitJobSchedule initialSchedule;
    private Collection<RoutingTransportOperation> operationsToSchedule;

    public ImplicitOperationDecoder(Collection<RoutingTransportOperation> operationsToSchedule, ImplicitOperationIndividual indOps,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes,
            ActivityOnNodeGraph<RoutingTransportOperation> graph,
            TerminalProblem problem,
            Transshipment_ImplicitScheduleGenerationScheme sgs) {

        this.indOps = indOps;
        this.problem = problem;
        this.sgs = sgs;
        this.graph = graph;
        this.ealosaes = ealosaes;
        this.operationsToSchedule = operationsToSchedule;

    }

    public ImplicitOperationDecoder(Collection<RoutingTransportOperation> operationsToSchedule, LoadUnitJobSchedule initialSchedule, ImplicitOperationIndividual indOps,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes,
            ActivityOnNodeGraph<RoutingTransportOperation> graph,
            TerminalProblem problem,
            Transshipment_ImplicitScheduleGenerationScheme sgs) {

        this.indOps = indOps;
        this.problem = problem;
        this.sgs = sgs;
        this.graph = graph;
        this.ealosaes = ealosaes;
        this.initialSchedule = initialSchedule;
        this.operationsToSchedule = operationsToSchedule;
    }

    public LoadUnitJobSchedule getSchedule() {
        if (graph == null) {
            throw new UnknownError("Graph nicht hinterlegt!");
        }
        List<RoutingTransportOperation> activities = new ArrayList<>(graph.vertexSet());
        activities.retainAll(operationsToSchedule);
        LoadUnitJobSchedule schedule;
        if (initialSchedule == null) {
            schedule = MultiJobTerminalProblemFactory.createNewSchedule(problem);
        } else {
            InstanceHandler handler = new InstanceHandler(problem.getScheduleManagerBuilder());
            schedule = new LoadUnitJobSchedule(initialSchedule, handler);
        }

        schedule = sgs.getSchedule(schedule, activities, indOps.getChromosome(), ealosaes, problem, graph, problem.getOptimizationTimeSlot());
        return schedule;
    }

}
