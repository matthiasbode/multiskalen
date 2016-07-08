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

/**
 *
 * @author bode
 */
public class LeastVertexClassRule<E extends Operation> implements OperationRules<E> {

    ActivityOnNodeGraph<E> aon;

    @Override
    public int compare(E o1, E o2) {
        int vertexClass1 = aon.getOperation2NodeClasses().get(o1);
        int vertexClass2 = aon.getOperation2NodeClasses().get(o2);

        if (vertexClass1 == vertexClass2) {
            return 0;
        }
        return vertexClass1 < vertexClass2 ? -1 : 1;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<E> operationsToSchedule,Map<E, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<E> graph) {
        if (graph != null) {
            this.aon = graph;
        }
    }

}
