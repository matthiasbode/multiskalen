/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
 
import java.util.Collection;
import java.util.List;
import java.util.Map;

 
public interface Transshipment_ImplicitScheduleGenerationScheme {
    
    public LoadUnitJobSchedule getSchedule(LoadUnitJobSchedule initialSchedule, Collection<RoutingTransportOperation> operations, List<OperationPriorityRules.Identifier> decisionStrategies, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes,   TerminalProblem problem, ActivityOnNodeGraph<RoutingTransportOperation> graph, TimeSlot timeSlot);
}
