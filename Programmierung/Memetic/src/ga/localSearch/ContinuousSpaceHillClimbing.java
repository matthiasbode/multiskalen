/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.localSearch;

import ga.basics.FitnessEvalationFunction;
import ga.individuals.DoubleIndividual;
import java.util.ArrayList;
 

/**
 *
 * @author bode
 */
public class ContinuousSpaceHillClimbing implements LocalSearch<DoubleIndividual> {

    FitnessEvalationFunction<DoubleIndividual> eval;
   
    double epsilon = 0.001;
    double stepsize = 0.1;
    int maxIter = 100;
    
    public ContinuousSpaceHillClimbing(FitnessEvalationFunction<DoubleIndividual> eval) {
        this.eval = eval;
    }
    
    
    @Override
    public DoubleIndividual localSearch(DoubleIndividual ind) {
        DoubleIndividual best = (DoubleIndividual) ind.clone();
        double bestScore = eval.computeFitness(ind)[0];
        int times = 0;
        outer:
        while (times < maxIter) {
            times++;
            ArrayList<DoubleIndividual> neighbours = new ArrayList<DoubleIndividual>();

            for (int direction = 0; direction < best.size(); direction++) {
                DoubleIndividual candidatesGeno = best.clone();
                candidatesGeno.set(direction, candidatesGeno.get(direction) + stepsize);
                neighbours.add(candidatesGeno);
                DoubleIndividual candidatesGeno2 = best.clone();
                candidatesGeno2.set(direction, candidatesGeno2.get(direction) - stepsize);
                neighbours.add(candidatesGeno2);
            }

            double before = bestScore;
            for (DoubleIndividual neighbour : neighbours) {
                double scoreTemp = eval.computeFitness(neighbour)[0];
                if (scoreTemp > bestScore) {
                    best = neighbour;
                    bestScore = scoreTemp;

                }
                if (bestScore != before && bestScore - before < epsilon) {
                    break outer;
                }
            }
        }

        return best;
    }
}
