/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.petrinet.timed.stochastic.distributions;

/**
 *
 * @author roeth
 */
public class ExponentialDistribution implements Distribution {

    //Mean
    double mean;
    //Deviation
    double deviation;
    
    /*
     * Generates an exponential distribution with mean = deviation 
     */
    public ExponentialDistribution(double mean) {
        this.mean = mean;
        this.deviation = mean;
    }

    /*
     * Serves the functionvalue f(x)
     */
    @Override
    public double getValue(double x) {
        return (1/mean) * Math.exp(-x / mean);
    }

    @Override
    public double getMean() {
        return mean;
    }

    /*
     * mean = deviation !!!
     */
    @Override
    public double getDeviation() {
        return mean;
    }

}
