/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals.ops;

import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
 
import ga.individuals.Individual;
import java.util.ArrayList;
import java.util.List;

/**
 * Für jeden Entscheidungszeitpunkt wird eine Entscheidungsstrategie codiert.
 *
 * @author bode
 */
public class ImplicitOperationIndividual extends Individual<OperationPriorityRules.Identifier> {
    
//    public LoadUnitJobSchedule schedule;
    
    public ImplicitOperationIndividual(OperationPriorityRules.Identifier... gens) {
        super(gens);
    }

    public ImplicitOperationIndividual(List<OperationPriorityRules.Identifier> chromosome) {
        super(chromosome);
    }

    public ImplicitOperationIndividual() {
    }

    @Override
    public ImplicitOperationIndividual clone() {
        return new ImplicitOperationIndividual(new ArrayList<>(chromosome));
    }

    @Override
    public String toString() {
        return "ImplicitOperationIndividual{" + this.hashCode() + '}';
    }

}
