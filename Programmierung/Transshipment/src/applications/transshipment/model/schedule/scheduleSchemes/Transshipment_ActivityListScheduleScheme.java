/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.TerminalProblem;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 */
public interface Transshipment_ActivityListScheduleScheme {

    public LoadUnitJobSchedule getSchedule(LoadUnitJobSchedule schedule, List<RoutingTransportOperation> activityList, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> originalEalosaes, TerminalProblem p, ActivityOnNodeGraph<RoutingTransportOperation> graph, TimeSlot timeSlot);

}
