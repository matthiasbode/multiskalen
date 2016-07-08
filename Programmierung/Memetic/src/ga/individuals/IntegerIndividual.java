/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.individuals;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class IntegerIndividual extends Individual<Integer> {

    public IntegerIndividual(Integer... gens) {
        super(gens);
    }

    public IntegerIndividual(List<Integer> chromosome) {
        super();
        this.chromosome = chromosome;
    }

    @Override
    public IntegerIndividual clone() {
        return new IntegerIndividual(new ArrayList<>(this.chromosome));
    }

    @Override
    public String toString() {
        return "IntegerIndividual{" + getNumber() + '}';
    }

    public List<Integer> getChromosome() {
        return chromosome;
    }

    @Override
    public void setChromosome(List<Integer> chromosome) {
        this.chromosome = chromosome;
    }
}
