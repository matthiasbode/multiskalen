package org.graph.petrinet.timed.stochastic;

import org.graph.petrinet.timed.stochastic.distributions.Distribution;
import org.graph.petrinet.timed.stochastic.distributions.ExponentialDistribution;
import org.graph.petrinet.timed.stochastic.distributions.NormalDistribution;

/**
 *
 * @author Roeth
 */
public class SimpleStochasticTransition extends StochasticTransition {
    double avg, min, max;

    public SimpleStochasticTransition(String name, Distribution dist, double avg, double min, double max) {
        super(name, dist);
        this.avg = avg;
        this.min = min;
        this.max = max;
    }

    public SimpleStochasticTransition(String name, int priority, Distribution dist, double avg, double min, double max) {
        super(name, priority, dist);
        this.avg = avg;
        this.min = min;
        this.max = max;
    }

    public SimpleStochasticTransition(String name, int priority, int numberOfSimultaneousActions, Distribution dist, double avg, double min, double max) {
        super(name, priority, numberOfSimultaneousActions, dist);
        this.avg = avg;
        this.min = min;
        this.max = max;
    }

    /*
     * Serves the duration of the Transition
     */
    @Override
    public long getDuration() {
        double bm, time=0;
        
        //NormalDistribution?
        if(dist instanceof NormalDistribution) {
            do {
                bm = Randomizer.getNumberWithNormalDistribution_BoxMullerMethod(dist.getMean(), dist.getDeviation(),1234)[0];
//                bm = Randomizer.getNumerbWithNormalDistribution_PolarMethod(dist.getMean(), dist.getDeviation(),1234)[0];
                time = (long)(avg*dist.getValue(bm)/dist.getValue(dist.getMean()));
                if(bm>0) {
                    time = avg + Math.abs(avg-time);
                }
            }
            while(time < min || time > max);
        }
        
        //ExponentialDistribution?
        if(dist instanceof ExponentialDistribution) {
            do {
                bm = Randomizer.getNumberWithExponentialDistribtuion_InverseMethod(dist.getMean(),1234);
                time = (long)(avg*dist.getValue(bm)/dist.getValue(0));
            }
            while(time < min);
        }
        return (long)time;
    }

}
