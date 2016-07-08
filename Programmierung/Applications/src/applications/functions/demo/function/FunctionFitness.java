/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions.demo.function;

import ga.basics.FitnessEvalationFunction;
import ga.individuals.DoubleIndividual;

/**
 *
 * @author bode
 */
public class FunctionFitness implements FitnessEvalationFunction<DoubleIndividual> {

    @Override
    public double[] computeFitness(DoubleIndividual i) {
        DoubleIndividual p = i;
        double x = p.get(0);
        double y = p.get(1);


        if ((y > Math.sqrt(Math.PI) && y < Math.sqrt(2 * Math.PI))
                && (x > Math.PI / 2. && x < (3 / 2.) * Math.PI)) {
            return new double[]{-(1.5 * Math.cos((x)) + Math.sin((y) * (y)))};
        }
        return new double[]{-(Math.cos((x)) + Math.sin((y) * (y)))};
    }
}
