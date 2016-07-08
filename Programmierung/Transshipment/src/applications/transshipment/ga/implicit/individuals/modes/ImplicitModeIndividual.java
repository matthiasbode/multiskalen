/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals.modes;

import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import ga.individuals.Individual;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class ImplicitModeIndividual extends Individual<LoadUnitJobPriorityRules.Identifier> {

    public ImplicitModeIndividual(LoadUnitJobPriorityRules.Identifier... gens) {
        super(gens);
    }

    public ImplicitModeIndividual(List<LoadUnitJobPriorityRules.Identifier> chromosome) {
        super(chromosome);
    }

    @Override
    public ImplicitModeIndividual clone() {
        return new ImplicitModeIndividual(new ArrayList<>(chromosome));
    }

    @Override
    public String toString() {
        return "ImplicitModeIndividual{" + this.hashCode() + '}';
    }

}
