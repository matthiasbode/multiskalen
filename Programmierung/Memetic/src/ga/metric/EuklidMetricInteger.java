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
public class EuklidMetricInteger implements Metric<IntegerIndividual> {

    @Override
    public double distance(IntegerIndividual a, IntegerIndividual b) {

        double sum = 0;
        for (int i = 0; i < a.size(); i++) {
            sum += Math.abs(a.get(i) - b.get(i));// * (aVal[i] - bVal[i]);
        }
        return sum;//Math.sqrt(sum);
    }

    public static void main(String[] args) {
        IntegerIndividual a = new IntegerIndividual(new Integer[]{5, 5, 1, 7, 4, 2, 0, 2});
        IntegerIndividual b = new IntegerIndividual(new Integer[]{6, 4, 3, 1, 4, 7, 0, 7});
        EuklidMetricInteger e = new EuklidMetricInteger();
        System.out.println(e.distance(a, b));
    }
}
