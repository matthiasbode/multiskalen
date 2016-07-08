/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis;

import ga.individuals.Individual;


/**
 *
 * @author bode
 */
public class CorrelationLength<C extends Individual> {

    SpatialAutoCorrelation<C> sac;

    public CorrelationLength(SpatialAutoCorrelation sac) {
        this.sac = sac;
    }

    public double getCorrelationLength() {
        return -1. / (Math.log(sac.getAutoKorrelation(1)));
    }
}
