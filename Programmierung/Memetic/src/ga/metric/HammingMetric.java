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
public class HammingMetric<C extends IntegerIndividual> implements Metric<C> {

    public double distance(C a, C b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Chromosomen der beiden "
                    + "Individuen müssen gleich lang sein");
        }
        int distance = 0;
        for (int i = 0; i < a.size(); i++) {
            Integer valueA = a.get(i);
            Integer valueB = b.get(i);
            if (valueA > 1 || valueB > 1) {
                throw new IllegalArgumentException("Chromosomen dürfen für "
                        + "Hamming-Distanz nur 0 oder 1 als Werte haben");
            }
            if (!valueA.equals(valueB)) {
                distance++;
            }
        }
        return distance;
    }
}
