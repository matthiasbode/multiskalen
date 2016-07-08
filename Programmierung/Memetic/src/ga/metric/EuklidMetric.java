/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.metric;

import ga.individuals.DoubleIndividual;

/**
 *
 * @author bode
 */
public class EuklidMetric implements Metric<DoubleIndividual> {

    @Override
    public double distance(DoubleIndividual a, DoubleIndividual b) {
   
        double sum = 0;
        for (int i = 0; i < a.size(); i++) {
            sum += (a.get(i) - b.get(i)) * (a.get(i) - b.get(i));
        }
        return Math.sqrt(sum);
    }
}

