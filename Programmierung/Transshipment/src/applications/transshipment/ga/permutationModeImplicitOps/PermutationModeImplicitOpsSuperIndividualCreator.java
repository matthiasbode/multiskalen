/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps;

import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import ga.algorithms.coevolving.individuals.SuperIndividualCreator;
import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public class PermutationModeImplicitOpsSuperIndividualCreator implements SuperIndividualCreator<PermutationModeImplicitOpsSuperIndividual> {

    @Override
    public PermutationModeImplicitOpsSuperIndividual create(Map<Class, Individual> subIndividuals) {
        return new PermutationModeImplicitOpsSuperIndividual((ImplicitOperationIndividual) subIndividuals.get(ImplicitOperationIndividual.class), (PermutationModeIndividual) subIndividuals.get(PermutationModeIndividual.class));
    }

}
