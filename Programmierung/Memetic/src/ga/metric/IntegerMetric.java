/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.metric;

import ga.individuals.IntegerIndividual;

/**
 *
 * @author bode
 */
public class IntegerMetric implements Metric<IntegerIndividual> {

    @Override
    public double distance(IntegerIndividual a, IntegerIndividual b) {
     int numberOfDifferences = 0;
        for (int i = 0; i < a.size(); i++) {
            if(a.get(i) != b.get(i))
                    numberOfDifferences++;
        }
        return numberOfDifferences;
    }
    
}
