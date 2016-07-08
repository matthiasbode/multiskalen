/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeDirectJob;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import ga.individuals.subList.ListIndividual;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.problem.SubTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 */
public class ExplicitJobDecoder {

    private final PermutationJobIndividual indOps;

    private final MultiJobTerminalProblem problem;
    private final Transshipment_ActivityListScheduleScheme sgs;
    private final Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes;
    private final PriorityDeterminator<RoutingTransportOperation, PermutationJobIndividual> determinator;

    private final Collection<RoutingTransportOperation> choosenOps;

    public ExplicitJobDecoder(PriorityDeterminator<RoutingTransportOperation, PermutationJobIndividual> determinator,
            PermutationJobIndividual indOps,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes,
            Collection<RoutingTransportOperation> choosenOps,
            MultiJobTerminalProblem problem,
            Transshipment_ActivityListScheduleScheme sgs) {

        this.indOps = indOps;
        this.ealosaes = ealosaes;
        this.problem = problem;
        this.sgs = sgs;
        this.choosenOps = choosenOps;
        this.determinator = determinator;
    }

    public LoadUnitJobSchedule getSchedule() {

        /**
         * EALOSAE bestimmen und ggf. Anpassungen durch Listener.
         */
        List<RoutingTransportOperation> activityList = buildList();
        ActivityOnNodeGraph<RoutingTransportOperation> graph = problem.getActivityOnNodeDiagramm().getSubGraph(activityList);

        SubTerminalProblem subProblem = new SubTerminalProblem(activityList, ealosaes, graph, problem, problem.getOptimizationTimeSlot(), problem.getScale());
        for (RoutingTransportOperation routingTransportOperation : graph.vertexSet()) {
            if (ealosaes.get(routingTransportOperation) == null) {
                throw new UnknownError("bla!!!!");
            }
        }
        LoadUnitJobSchedule schedule = MultiJobTerminalProblemFactory.createNewSchedule(problem);
        schedule = sgs.getSchedule(schedule, activityList, ealosaes, subProblem, subProblem.getActivityOnNodeDiagramm(), subProblem.getOptimizationTimeSlot());
        return schedule;
    }

    private List<RoutingTransportOperation> buildList() {
        /**
         * Sortieren der Operationen.
         */
        List<RoutingTransportOperation> activityList = determinator.getPriorites(problem.getActivityOnNodeDiagramm(), indOps);

        activityList.retainAll(choosenOps);
        if (!activityList.containsAll(choosenOps)) {
//            throw new UnknownError("Nicht für alle ausgewählten Operationen ist eine Priorität festgelegt.");
        }
        return activityList;

    }
}
