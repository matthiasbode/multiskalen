/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps.sorted;

import applications.transshipment.model.LoadUnitJob;
import ga.individuals.Individual;
import ga.individuals.IntegerIndividual;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class PermutationModeIndividualSorted extends Individual<Object> {

    ListIndividual<LoadUnitJob> jobsSort;
    IntegerIndividual routeOrder;

    public PermutationModeIndividualSorted(List<SubListIndividual<LoadUnitJob>> chromosome, IntegerIndividual routeOrder) {
        super();
        this.jobsSort = new ListIndividual<>(chromosome);
        this.routeOrder = routeOrder;
    }

    public PermutationModeIndividualSorted(ListIndividual<LoadUnitJob> sup, IntegerIndividual routeOrder) {
        super();
        this.jobsSort = new ListIndividual<>(sup.getChromosome());
        this.routeOrder = routeOrder;
    }

    @Override
    public PermutationModeIndividualSorted clone() {
        return new PermutationModeIndividualSorted(this.jobsSort.clone(), this.routeOrder.clone());
    }

}
