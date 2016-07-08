/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.metric;

import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public interface Metric<I extends Individual>{
    public double distance(I a, I b);
}
