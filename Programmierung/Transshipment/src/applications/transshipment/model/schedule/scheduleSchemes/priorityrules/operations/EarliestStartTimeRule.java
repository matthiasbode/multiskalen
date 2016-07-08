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
import java.util.Comparator;
import java.util.Map;
import math.FieldElement;

/**
 * Sortiert RoutingTransportOperations dahingehend, dass die Operationen mit der
 * geringsten frühsten Startzeit vorne in der Liste zu finden sind.
 *
 * @author bode
 */
public class EarliestStartTimeRule<E extends Operation> implements OperationRules<E> {

    Map<E, EarliestAndLatestStartsAndEnds> ealosaes;

    @Override
    public int compare(E o1, E o2) {
        FieldElement earliestOperationStart1 = ealosaes.get(o1).getEarliestStart();
        FieldElement earliestOperationStart2 = ealosaes.get(o2).getEarliestStart();
        if (earliestOperationStart1.equals(earliestOperationStart2)) {
            return 0;
        }
        return earliestOperationStart1.isLowerThan(earliestOperationStart2) ? -1 : 1;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule,Collection<E> operationsToSchedule, Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {
        if (ealosaes != null) {
            this.ealosaes = ealosaes;
        }
    }

}
