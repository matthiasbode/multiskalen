/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.decode;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
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
public class ExplicitOperationDecoder {

    private final ListIndividual indOps;

    private final MultiJobTerminalProblem problem;
    private final Transshipment_ActivityListScheduleScheme sgs;
    private final Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes;
    private final ActivityOnNodeGraph<RoutingTransportOperation> graph;
    private final PriorityDeterminator<RoutingTransportOperation, ListIndividual<RoutingTransportOperation>> determinator;
    private final Collection<RoutingTransportOperation> operationToSchedule;

    private LoadUnitJobSchedule initialSchedule;
    private TimeSlot timeSlot;

    public ExplicitOperationDecoder(PriorityDeterminator determinator,
            ListIndividual indOps,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes,
            Collection<RoutingTransportOperation> operationsToSchedule,
            ActivityOnNodeGraph<RoutingTransportOperation> graph,
            MultiJobTerminalProblem problem,
            Transshipment_ActivityListScheduleScheme sgs, TimeSlot timeSlot) {

        this.indOps = indOps;
        this.ealosaes = ealosaes;
        this.problem = problem;
        this.sgs = sgs;
        this.operationToSchedule = operationsToSchedule;
        this.determinator = determinator;
        this.graph = graph;
        this.timeSlot = timeSlot;
    }

    public ExplicitOperationDecoder(LoadUnitJobSchedule initialSchedule, PriorityDeterminator determinator,
            ListIndividual indOps,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes,
            Collection<RoutingTransportOperation> operationToSchedule,
            ActivityOnNodeGraph<RoutingTransportOperation> graph,
            MultiJobTerminalProblem problem,
            Transshipment_ActivityListScheduleScheme sgs, TimeSlot timeSlot) {

        this.indOps = indOps;
        this.ealosaes = ealosaes;
        this.problem = problem;
        this.sgs = sgs;
        this.operationToSchedule = operationToSchedule;
        this.determinator = determinator;
        this.initialSchedule = initialSchedule;
        this.graph = graph;
        this.timeSlot = timeSlot;
    }

    public LoadUnitJobSchedule getSchedule() {

        /**
         * EALOSAE bestimmen und ggf. Anpassungen durch Listener.
         */
        List<RoutingTransportOperation> activityList = buildList();

        SubTerminalProblem subProblem = new SubTerminalProblem(activityList, ealosaes, graph, problem, timeSlot, problem.getScale());
        for (RoutingTransportOperation routingTransportOperation : graph.vertexSet()) {
            if (ealosaes.get(routingTransportOperation) == null) {
                throw new UnknownError("Keine EALOSAES vorhanden ");
            }
        }

        LoadUnitJobSchedule schedule;
        if (initialSchedule == null) {
            schedule = MultiJobTerminalProblemFactory.createNewSchedule(problem);
        } else {
            InstanceHandler handler = new InstanceHandler(problem.getScheduleManagerBuilder());
            schedule = new LoadUnitJobSchedule(initialSchedule, handler);
        }

        schedule = sgs.getSchedule(schedule, activityList, ealosaes, subProblem, graph, subProblem.getOptimizationTimeSlot());
        return schedule;
    }

    private List<RoutingTransportOperation> buildList() {
        /**
         * Sortieren der Operationen.
         */
        List<RoutingTransportOperation> activityList = determinator.getPriorites(graph, indOps);

        activityList.retainAll(operationToSchedule);
        if (!activityList.containsAll(operationToSchedule)) {
//            throw new UnknownError("Nicht für alle ausgewählten Operationen ist eine Priorität festgelegt.");
        }
        return activityList;

    }
}
