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
 * Sortiert RoutingTransportOperations dahingehend, dass die Operationen mit der
 * geringsten Prozessdauer vorne in der Liste zu finden sind.
 *
 * @author bode
 */
public class ShortestProcessingTimeRule<E extends Operation> implements OperationRules<E> {

    @Override
    public int compare(E o1, E o2) {
        FieldElement duration1 = o1.getDuration();
        FieldElement duration2 = o2.getDuration();

        if (duration1.equals(duration2)) {
            return 0;
        }
        return duration1.isLowerThan(duration2) ? -1 : 1;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<E> operationsToSchedule,Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {

    }

}
