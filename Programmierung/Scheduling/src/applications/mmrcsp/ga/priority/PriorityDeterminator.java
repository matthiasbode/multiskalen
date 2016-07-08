/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.ga.priority;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import ga.individuals.Individual;
import java.util.List;

/**
 *
 * @author bode
 * @param <E>
 * @param <F>
 */
public interface PriorityDeterminator<E extends Operation, F extends Individual> {

    public List<E> getPriorites(ActivityOnNodeGraph<E> graph, F indOps);
}
