/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeDirectJob;

import applications.transshipment.model.LoadUnitJob;
import ga.individuals.Individual;
import java.util.List;

/**
 *
 * @author bode
 */
public class PermutationJobIndividual extends Individual<LoadUnitJob> {

    public PermutationJobIndividual() {
    }

    public PermutationJobIndividual(LoadUnitJob... gens) {
        super(gens);
    }

    public PermutationJobIndividual(List<LoadUnitJob> chromosome) {
        super(chromosome);
    }

}
