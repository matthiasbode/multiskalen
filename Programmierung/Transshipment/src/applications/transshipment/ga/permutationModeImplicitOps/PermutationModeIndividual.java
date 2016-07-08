/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps;

import applications.transshipment.model.LoadUnitJob;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import java.util.List;

/**
 *
 * @author bode
 */
public class PermutationModeIndividual extends ListIndividual<LoadUnitJob> {

    public PermutationModeIndividual(List<SubListIndividual<LoadUnitJob>> chromosome) {
        super(chromosome);
    }

    public PermutationModeIndividual(ListIndividual<LoadUnitJob> sup) {
        super(sup.getChromosome());
    }

    @Override
    public PermutationModeIndividual clone() {
        return new PermutationModeIndividual(super.clone());
    }


}
