/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.directModeImplicitOps;

import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import ga.algorithms.coevolving.individuals.SuperIndividualCreator;
import ga.individuals.Individual;
import ga.individuals.IntegerIndividual;
import java.util.Map;

/**
 *
 * @author bode
 */
public class DirectModeImplicitOpsSuperIndividualCreator implements SuperIndividualCreator<DirectModeImplicitOpsSuperIndividual> {

    @Override
    public DirectModeImplicitOpsSuperIndividual create(Map<Class, Individual> subIndividuals) {
        return new DirectModeImplicitOpsSuperIndividual((ImplicitOperationIndividual) subIndividuals.get(ImplicitOperationIndividual.class), (IntegerIndividual) subIndividuals.get(IntegerIndividual.class));
    }

}
