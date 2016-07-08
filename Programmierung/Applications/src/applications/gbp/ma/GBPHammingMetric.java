/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import ga.metric.HammingMetric;

/**
 *
 * @author bode
 */
public class GBPHammingMetric extends HammingMetric<BiPartitionIndividual>{

    @Override
    public double distance(BiPartitionIndividual a, BiPartitionIndividual b) {
        double dH = super.distance(a, b);
        return Math.min(dH / 2.0, (a.size() - dH) / 2.0);
    }
}
