/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicitModeDirectOps.individuals;

import ga.individuals.subList.ListIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import ga.algorithms.coevolving.individuals.SuperIndividualCreator;
import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public class MixedTransshipmentSuperIndividualCreator implements SuperIndividualCreator<ImplicitModeDirectOpsSuperIndividual> {

    @Override
    public ImplicitModeDirectOpsSuperIndividual create(Map<Class, Individual> subIndividuals) {
        return new ImplicitModeDirectOpsSuperIndividual((ListIndividual) subIndividuals.get(ListIndividual.class), (ImplicitModeIndividual) subIndividuals.get(ImplicitModeIndividual.class));
    }

}
