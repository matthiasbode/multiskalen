/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals;

import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import ga.algorithms.coevolving.individuals.SuperIndividualCreator;
import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public class ImplicitSuperIndividualCreator implements SuperIndividualCreator<ImplicitSuperIndividual> {

    @Override
    public ImplicitSuperIndividual create(Map<Class, Individual> subIndividuals) {
        return new ImplicitSuperIndividual((ImplicitOperationIndividual) subIndividuals.get(ImplicitOperationIndividual.class), (ImplicitModeIndividual) subIndividuals.get(ImplicitModeIndividual.class));
    }

}
