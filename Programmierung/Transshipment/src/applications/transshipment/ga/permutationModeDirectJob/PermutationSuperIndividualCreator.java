/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeDirectJob;

import applications.transshipment.ga.permutationModeImplicitOps.*;
import ga.algorithms.coevolving.individuals.SuperIndividualCreator;
import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public class PermutationSuperIndividualCreator implements SuperIndividualCreator<SuperPermutation> {

    @Override
    public SuperPermutation create(Map<Class, Individual> subIndividuals) {
        return new SuperPermutation((PermutationJobIndividual) subIndividuals.get(PermutationJobIndividual.class), (PermutationModeIndividual) subIndividuals.get(PermutationModeIndividual.class));
    }

}
