/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeDirectJob;

import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author bode
 */
public class JobBasedPriorityDeterminator implements PriorityDeterminator<RoutingTransportOperation, PermutationJobIndividual> {

    @Override
    public List<RoutingTransportOperation> getPriorites(ActivityOnNodeGraph<RoutingTransportOperation> graph, final PermutationJobIndividual indOps) {
        List<RoutingTransportOperation> result = new ArrayList<>(graph.vertexSet());
        Collections.sort(result, new Comparator<RoutingTransportOperation>() {

            @Override
            public int compare(RoutingTransportOperation o1, RoutingTransportOperation o2) {
                return Integer.compare(indOps.indexOf(o1.getJob()), indOps.indexOf(o2.getJob()));
            }
        });

        return result;
    }

}
