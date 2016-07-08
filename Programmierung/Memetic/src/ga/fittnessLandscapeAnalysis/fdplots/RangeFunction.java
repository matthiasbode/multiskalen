/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis.fdplots;

import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public interface RangeFunction<C extends Individual > {

    public double getValue(C individual);
    public void setOptimum(C individual);
}
