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

/**
 * Sortiert RoutingTransportOperations dahingehend, dass die Operationen mit der
 * höchsten Anzahl an Nachfolgern vorne in der Liste zu finden sind.
 *
 * @author bode
 */
public class MostImmediateSuccessorsRule<E extends Operation> implements OperationRules<E> {

    ActivityOnNodeGraph<E> aon;

    @Override
    public int compare(E o1, E o2) {
        int size1 = aon.getSuccessors(o1).size();
        int size2 = aon.getSuccessors(o2).size();

        if (size1 == size2) {
            return 0;
        }
        return size1 > size2 ? -1 : 1;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<E> operationsToSchedule, Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {
        if (graph != null) {
            this.aon = graph;
        }
    }

}
