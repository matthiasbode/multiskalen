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
 * geringsten frühsten Endzeit vorne in der Liste zu finden sind.
 *
 * @author bode
 */
public class EarliestFinishTimeRule<E extends Operation> implements OperationRules<E> {

    Map<E, EarliestAndLatestStartsAndEnds> ealosaes;

    @Override
    public int compare(E o1, E o2) {
        FieldElement earliestOperationEnd1 = ealosaes.get(o1).getEarliestEnd();
        FieldElement earliestOperationEnd2 = ealosaes.get(o2).getEarliestEnd();
        if (earliestOperationEnd1.equals(earliestOperationEnd2)) {
            return 0;
        }
        return earliestOperationEnd1.isLowerThan(earliestOperationEnd2) ? -1 : 1;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<E> operationsToSchedule,Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {
        if (ealosaes != null) {
            this.ealosaes = ealosaes;
        }
    }

}
