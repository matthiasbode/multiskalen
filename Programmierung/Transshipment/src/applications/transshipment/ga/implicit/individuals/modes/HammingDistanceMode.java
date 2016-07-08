/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals.modes;

import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import ga.metric.Metric;
import java.util.List;

/**
 *
 * @author bode
 */
public class HammingDistanceMode implements Metric<ImplicitModeIndividual> {

    @Override
    public double distance(ImplicitModeIndividual a, ImplicitModeIndividual b) {
        double distance = 0;
        List<LoadUnitJobPriorityRules.Identifier> chromosome1 = a.getChromosome();
        List<LoadUnitJobPriorityRules.Identifier> chromosome2 = b.getChromosome();
        for (int i = 0; i < chromosome1.size(); i++) {
            LoadUnitJobPriorityRules.Identifier allel1 = chromosome1.get(i);
            LoadUnitJobPriorityRules.Identifier allel2 = chromosome2.get(i);
            if (!allel1.equals(allel2)) {
                distance++;
            }
        }
        return distance;
    }

}
