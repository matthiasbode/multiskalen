
package org.graph.petrinet.timed.stochastic;

import java.util.Random;

/**
 * Generates random numbers with different distributions
 * @author Roeth
 */
public class Randomizer {

    /*
     * Generates two normal distributed random numbers with the Bux Muller method
     */
    public static double[] getNumberWithNormalDistribution_BoxMullerMethod(double mean, double deviation, long seed) {
        Random rand = new Random(seed);
        double[] randoms = new double[2];
        //uniform distributed numbers:
        double u1 = rand.nextDouble();
        double u2 = rand.nextDouble();
        //Box Muller normal distributed numbers:
        randoms[0] = Math.sqrt(deviation)*Math.cos(2*Math.PI*u1)*Math.sqrt(-2*Math.log(u2))+mean;
        randoms[1] = Math.sqrt(deviation)*Math.sin(2*Math.PI*u1)*Math.sqrt(-2*Math.log(u2))+mean;
        return randoms;
    }

    /*
     * Generates two normal distributed random numbers with the polar method
     */
    public static double[] getNumerbWithNormalDistribution_PolarMethod(double mean, double deviation, long seed) {
        Random rand = new Random(seed);
        double[] randoms = new double[2];
        //uniform distributed numbers:
        double q,u1,u2;
        do {
            u1 = rand.nextDouble();
            u2 = rand.nextDouble();
            q = u1*u1 + u2*u2;
        }
        while(q == 0 || q>1);
        double p=Math.sqrt(-2 * Math.log(q) / q);
        randoms[0] = Math.sqrt(deviation) * u1 * p + mean;
        randoms[1] = Math.sqrt(deviation) * u2 * p + mean;
        return randoms;
    }

    /*
     * Generates an exponential distributed random number with the inverse method
     */
    public static double getNumberWithExponentialDistribtuion_InverseMethod(double mean, long seed) {
        Random rand = new Random(seed);
        return -mean * Math.log(1-rand.nextDouble());
    }

}
