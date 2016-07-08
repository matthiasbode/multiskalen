/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
 
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 * @param <F>
 * @param <E>
 */
public interface ImplicitScheduleGenerationScheme<E extends Operation, F extends  SchedulingProblem> {
    public Schedule getSchedule(Collection<E> operations, List<OperationPriorityRules.Identifier> decisionStrategies, Map<E, EarliestAndLatestStartsAndEnds> ealosaes, InstanceHandler rules, F problem, ActivityOnNodeGraph<E> graph, TimeSlot timeSlot);
}
