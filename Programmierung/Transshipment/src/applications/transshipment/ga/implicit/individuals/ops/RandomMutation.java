/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals.ops;

import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import ga.Parameters;
import ga.mutation.Mutation;
import java.util.Arrays;
import java.util.List;
import util.RandomUtilities;

/**
 *
 * @author Matthias
 */
public class RandomMutation implements Mutation<ImplicitOperationIndividual> {

    public int numberOfMutations = 1;

    public RandomMutation(int numberOfMutations) {
        this.numberOfMutations = numberOfMutations;
    }

    @Override
    public ImplicitOperationIndividual mutate(ImplicitOperationIndividual ind, double xMutationRate) {
        if (Parameters.getRandom().nextDouble() > xMutationRate) {
            return ind;
        }
        ImplicitOperationIndividual clone = ind.clone();
        List<OperationPriorityRules.Identifier> auswahl = Arrays.asList(OperationPriorityRules.Identifier.values());

        for (int i = 0; i < numberOfMutations; i++) {

            int randomPosition = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, ind.size() - 1);
            OperationPriorityRules.Identifier newRule = auswahl.get(RandomUtilities.getRandomValue(Parameters.getRandom(), 0, auswahl.size() - 1));
            clone.set(randomPosition, newRule);
        }
        return clone;
    }
}
