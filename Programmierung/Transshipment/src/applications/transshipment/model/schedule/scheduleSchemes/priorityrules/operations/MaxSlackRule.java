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
import applications.transshipment.TransshipmentParameter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import math.FieldElement;

/**
 * Sortiert RoutingTransportOperations dahingehend, dass die Operationen mit der
 * größten Differenz zwischen spätester und frühster Endzeit vorne in der Liste
 * zu finden sind.
 *
 * @author bode
 */
public class MaxSlackRule<E extends Operation> implements OperationRules<E> {

    Map<E, EarliestAndLatestStartsAndEnds> ealosaes;

    LinkedHashMap<E, FieldElement> slack;

    @Override
    public int compare(E o1, E o2) {

        FieldElement slack1 = slack.get(o1);
        FieldElement slack2 = slack.get(o2);

        if (slack1.equals(slack2)) {
            return 0;
        }
        return slack1.isGreaterThan(slack2) ? -1 : 1;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<E> operationsToSchedule, Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {
        if (ealosaes != null) {
            this.ealosaes = ealosaes;
            this.slack = new LinkedHashMap<>();
            for (E e : operationsToSchedule) {
                FieldElement latestOperationEnd = ealosaes.get(e).getLatestEnd();
                FieldElement earliestOperationEnd = ealosaes.get(e).getEarliestEnd();
                FieldElement s = latestOperationEnd.sub(earliestOperationEnd);
                if (!TransshipmentParameter.legacy) {
                    s = s.sub(e.getDuration());
                }
                this.slack.put(e, s);
            }
        }
    }

}
