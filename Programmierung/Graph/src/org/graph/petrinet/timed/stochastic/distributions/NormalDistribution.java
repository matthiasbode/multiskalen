
package org.graph.petrinet.timed.stochastic.distributions;

import org.graph.petrinet.timed.stochastic.Randomizer;
import org.graph.petrinet.timed.stochastic.SimpleStochasticTransition;

/**
 *
 * @author Roeth
 */
public class NormalDistribution implements Distribution {

    //Mean
    double mean;
    //Deviation
    double deviation;

    /*
     * Generates a normal distribution with mean and variance
     * @param mean Mean
     * @param var Variance
     */
    public NormalDistribution(double mean, double deviation) {
        this.mean = mean;
        this.deviation = deviation;
    }

    /*
     * Generates a 0-1-normal distribution
     */
    public NormalDistribution() {
        this.mean = 0;
        this.deviation = 1;
    }

    @Override
    public double getMean() {
        return mean;
    }

    @Override
    public double getDeviation() {
        return deviation;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setDeviation(double var) {
        this.deviation = var;
    }

    /*
     * Serves the functionvalue f(x)
     */
    @Override
    public double getValue(double x) {        
        return Math.exp(-((x - mean)*(x - mean) / (2*deviation*deviation))) / (deviation * Math.sqrt(2*Math.PI));
    }

}
