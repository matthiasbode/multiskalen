/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.model;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.SubTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author bode
 */
public class MicroProblem extends SubTerminalProblem {

 
    private LoadUnitJobSchedule allOverSchedule;

    public MicroProblem(TimeSlot currentPeriod, LoadUnitJobSchedule allOverSchedule, Collection<RoutingTransportOperation> operations, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae, ActivityOnNodeGraph<RoutingTransportOperation> graph, MultiJobTerminalProblem superProblem, ScheduleManagerBuilder builder) {
        super(operations, ealosae, graph, superProblem, builder, currentPeriod, Scale.micro);

        this.allOverSchedule = allOverSchedule;
        this.getActivityOnNodeDiagramm().calculateComponentsAndNodeClasses();
    }

    

    public LoadUnitJobSchedule getAllOverSchedule() {
        return allOverSchedule;
    }

}
