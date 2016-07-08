/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.strategyScheme;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.model.schedule.scheduleSchemes.ImplicitScheduleGenerationScheme;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.TerminalProblem;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 */
public abstract class TransshipmentStrategyScheduleScheme implements ImplicitScheduleGenerationScheme<RoutingTransportOperation, TerminalProblem> {

    public TransshipmentStrategyScheduleScheme() {

    }

    /**
     *
     * @param operations
     * @param decisionStrategies
     * @param ealosaes
     * @param handler
     * @param p
     * @param graph
     * @param timeSlot
     * @return
     */
    @Override
    public abstract LoadUnitJobSchedule getSchedule(Collection<RoutingTransportOperation> operations, List<OperationPriorityRules.Identifier> decisionStrategies, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, InstanceHandler handler, TerminalProblem p, ActivityOnNodeGraph<RoutingTransportOperation> graph, TimeSlot timeSlot);

}
