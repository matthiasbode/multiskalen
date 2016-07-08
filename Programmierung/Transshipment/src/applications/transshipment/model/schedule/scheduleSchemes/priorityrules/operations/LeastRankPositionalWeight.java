/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import java.util.Collection;
import java.util.Map;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class LeastRankPositionalWeight<E extends Operation> implements OperationRules<E> {

    ActivityOnNodeGraph<E> aon;

    @Override
    public int compare(E o1, E o2) {
        FieldElement totalDuration1 = o1.getDuration().clone();
        for (E suc : aon.getSuccessors(o1)) {
            totalDuration1 = totalDuration1.add(suc.getDuration());
        }
        FieldElement totalDuration2 = o2.getDuration().clone();
        for (E suc : aon.getSuccessors(o2)) {
            totalDuration2 = totalDuration2.add(suc.getDuration());
        }

        if (totalDuration1.equals(totalDuration2)) {
            return 0;
        }
        return totalDuration1.isLowerThan(totalDuration2) ? -1 : 1;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<E> operationsToSchedule,Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {
        if (graph!= null) {
            this.aon = graph;
        }
    }

}
