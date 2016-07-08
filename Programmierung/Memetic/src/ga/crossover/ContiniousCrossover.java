/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.Parameters;
import ga.individuals.DoubleIndividual;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author bode
 */
public class ContiniousCrossover implements Crossover<DoubleIndividual> {

    @Override
    public Collection<DoubleIndividual> recombine(DoubleIndividual c1, DoubleIndividual c2, double xOverRate) {
        if (Parameters.getRandom().nextDouble() < xOverRate) {
            Double[] newCoding = new Double[c1.size()];
            double lambda = 0.5;
            for (int i = 0; i < newCoding.length; i++) {
                newCoding[i] = c1.get(i) + lambda * (c2.get(i) - c1.get(i));
            }
            DoubleIndividual doubleCoding = new DoubleIndividual(newCoding);
            ArrayList<DoubleIndividual> res = new ArrayList<>();
            res.add(doubleCoding);
            return res;
        } else {
            return new ArrayList<>();
        }
    }
}
