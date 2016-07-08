/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions.demo.function;

import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import ga.individuals.DoubleIndividual;

/**
 *
 * @author bode
 */
public class FunctionFitness2 implements FitnessEvalationFunction<DoubleIndividual> {

    @Override
    public double[] computeFitness(DoubleIndividual i) {
        DoubleIndividual c = i;
        double x = c.get(0);
        double y = c.get(1);
        if(x <= 5.0 && x >= -5.0 && y <=5.0 && y>= -5.0)
          return new double[]{-(0.3*(x*x-y*y+(y))/(3+Math.cos(x+y)+Math.sin(x-y))+Math.exp((Math.cos((x)*(y))))/1.5)};
        else
            return new double[]{-Double.POSITIVE_INFINITY};
    }
    
    
}
