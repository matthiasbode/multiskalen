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
 * geringsten spätesten Startzeit vorne in der Liste zu finden sind.
 *
 * @author bode
 */
public class LateStartTimeRule<E extends Operation> implements OperationRules<E> {

    Map<E, EarliestAndLatestStartsAndEnds> ealosaes;

    public LateStartTimeRule() {

    }

    @Override
    public int compare(E o1, E o2) {
        FieldElement latestOperationStart1 = ealosaes.get(o1).getLatestStart();
        FieldElement latestOperationStart2 = ealosaes.get(o2).getLatestStart();
        if (latestOperationStart1.equals(latestOperationStart2)) {
            //return 0;
            return Integer.compare(o1.getId(), o2.getId());
        }
        int erg = latestOperationStart1.isLowerThan(latestOperationStart2) ? -1 : 1;

        return erg;

    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<E> operationsToSchedule, Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {
        if (ealosaes != null) {
            this.ealosaes = ealosaes;
        }
    }

}
