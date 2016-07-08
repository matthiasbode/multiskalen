/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals.ops;

import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import ga.metric.Metric;

/**
 *
 * @author bode
 */
public class HammingDistanceOp implements Metric<ImplicitOperationIndividual> {

    @Override
    public double distance(ImplicitOperationIndividual a, ImplicitOperationIndividual b) {
        int size = a.getChromosome().size();
        int distance = 0;
        for (int i = 0; i < size; i++) {
            OperationPriorityRules.Identifier i1 = a.getChromosome().get(i);
            OperationPriorityRules.Identifier i2 = b.getChromosome().get(i);
            if(!i1.equals(i2)){
                distance++;
            }
        }
        return distance;
    }
    
}
