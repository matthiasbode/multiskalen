/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps.sorted;

import applications.transshipment.model.LoadUnitJob;
import ga.crossover.Crossover;
import ga.crossover.LOXSingle;
import ga.crossover.ListIndividualTwoPointCrossOver;
import ga.individuals.IntegerIndividual;
import ga.individuals.subList.ListIndividual;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author bode
 */
public class TwoPartCrossover implements Crossover<PermutationModeIndividualSorted> {

    ListIndividualTwoPointCrossOver<LoadUnitJob> listIndCross = new ListIndividualTwoPointCrossOver();
    LOXSingle<IntegerIndividual> routeOrder = new LOXSingle();

    @Override
    public Collection<? extends PermutationModeIndividualSorted> recombine(PermutationModeIndividualSorted c1, PermutationModeIndividualSorted c2, double xOverRate) {
        Collection<? extends ListIndividual<LoadUnitJob>> recombine1 = listIndCross.recombine(c1.jobsSort, c2.jobsSort, xOverRate);
        Collection<? extends IntegerIndividual> recombine2 = routeOrder.recombine(c1.routeOrder, c2.routeOrder, xOverRate);
        ArrayList<PermutationModeIndividualSorted> result = new ArrayList<>();
        for (ListIndividual<LoadUnitJob> listIndividual : recombine1) {
            for (IntegerIndividual integerIndividual : recombine2) {
                result.add(new PermutationModeIndividualSorted(listIndividual, integerIndividual));
            }
        }
        return result;
    }

}
