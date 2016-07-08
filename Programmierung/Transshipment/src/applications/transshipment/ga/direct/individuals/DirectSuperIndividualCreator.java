/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.individuals;

import ga.individuals.IntegerIndividual;
import ga.individuals.subList.ListIndividual;
import ga.algorithms.coevolving.individuals.SuperIndividualCreator;
import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public class DirectSuperIndividualCreator implements SuperIndividualCreator<DirectSuperIndividual> {

    @Override
    public DirectSuperIndividual create(Map<Class, Individual> subIndividuals) {
        return new DirectSuperIndividual((ListIndividual) subIndividuals.get(ListIndividual.class), (IntegerIndividual) subIndividuals.get(IntegerIndividual.class));
    }

}
