/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.TerminalProblem;
import java.util.List;

/**
 *
 * @author bode
 */
public abstract class TransshipmentScheduleScheme implements ScheduleGenerationScheme<RoutingTransportOperation, TerminalProblem> {

    public TransshipmentScheduleScheme() {

    }

    /**
     *
     * @param activityList
     * @param ealosaes
     * @param instanceHandler
     * @param p
     * @param graph
     * @param timeSlot
     * @return
     */
    @Override
    public abstract LoadUnitJobSchedule getSchedule(List<RoutingTransportOperation> activityList, TerminalProblem p, TimeSlot timeSlot);

}
