package org.graph.petrinet.timed.stochastic.distributions;

/**
 *
 * @author Roeth
 */
public interface Distribution {

     /*
     * Serves the functionvalue f(x)
     */
    public double getValue(double x);

    /*
     * Serves the mean
     */
    public double getMean();

    public double getDeviation();

}
