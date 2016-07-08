/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.localSearch;

import ga.basics.FitnessEvalationFunction;
import ga.basics.IndividualComparator;
import ga.individuals.IntegerIndividual;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author bode
 */
public class LocalSearchCombinatoricInteger implements LocalSearch<IntegerIndividual> {

    FitnessEvalationFunction<IntegerIndividual> evalation;
    int min;
    int max;

    public LocalSearchCombinatoricInteger(FitnessEvalationFunction<IntegerIndividual> evalation, int min, int max) {
        this.min = min;
        this.max = max;
        this.evalation = evalation;
    }

    @Override
    public IntegerIndividual localSearch(IntegerIndividual start) {
//(1) Sei y (0) eine Lösung für die Eingabe x. Setze i = 0.
//(2) Wiederhole solange, bis eine lokal optimale Lösung gefunden ist:
//(2a) Bestimme einen Nachbarn y ∈ N (y (i) ), so dass f (x, y ) < f (x, y (i) )
//und y eine Lösung ist.
//(2b) Setze y (i+1) = y und i = i + 1.
        IntegerIndividual current = start;
        current.setFitness(evalation.computeFitness(current));
        while (true) {
            ArrayList<IntegerIndividual> neighbours = getNeighbours(current);
            if (neighbours.isEmpty()) {
                break;
            }

            if (neighbours.get(neighbours.size() - 1).getFitness() > current.getFitness()) {
                current = neighbours.get(neighbours.size() - 1);
            } else {
                break;
            }
        }
        return current;
    }

    private ArrayList<IntegerIndividual> getNeighbours(IntegerIndividual start) {
        ArrayList<IntegerIndividual> res = new ArrayList<>();
        for (int gen = 0; gen < start.size(); gen++) {
            int allel = start.get(gen);
            if (allel < max) {
                IntegerIndividual newI = start.clone();
                newI.set(gen, allel + 1);
                newI.setFitness(evalation.computeFitness(newI));
                res.add(newI);
            }
            if (allel > min + 1) {
                IntegerIndividual newI = start.clone();
                newI.set(gen, allel - 1);
                newI.setFitness(evalation.computeFitness(newI));
                res.add(newI);
            }
        }
        Collections.sort(res, new IndividualComparator<IntegerIndividual>());
        return res;
    }
}
