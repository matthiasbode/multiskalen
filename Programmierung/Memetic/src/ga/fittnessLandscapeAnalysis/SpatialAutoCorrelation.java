/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis;

import ga.individuals.Individual;
import ga.individuals.Individual;
import ga.basics.Population;
import ga.metric.Metric;
import java.util.ArrayList;

/**
 *
 * @author bode
 */
public class SpatialAutoCorrelation<C extends Individual> {
     /**
     * Unter Annahme, alle bereits berechnet Individuuen sind in der Population
     */
    Population<C> unionPop;
    Metric<C> m;

    public SpatialAutoCorrelation(Population<C> p, Metric<C> m) {
        this.unionPop = p;
        this.m = m;
    }
    
    
    
    public double getAutoKorrelation(double d){
       
        double meanF = 0;
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        
        for (C x : unionPop.individuals()) {
            for (C y : unionPop.individuals()) {
                if(m.distance(x, y)==d)
                {
                  pairs.add(new Pair(x, y));
                  meanF += x.getFitness();
                  meanF += y.getFitness();
                }
            }
        }

        meanF /= new Double(pairs.size()*2);
        
        double sigmaF = 0;
        double sum = 0;
        for (Pair p : pairs) {
             sum += (p.x.getFitness() - meanF) * (p.y.getFitness() - meanF);
             sigmaF += (p.x.getFitness() - meanF)*(p.x.getFitness() - meanF);
             sigmaF += (p.y.getFitness() - meanF)*(p.y.getFitness() - meanF);
        }
        sigmaF /= Math.sqrt(new Double(pairs.size()*2));
        return 1. / (sigmaF*sigmaF*(pairs.size()*2))* sum;
    }
    
    class Pair{
        Individual x;
        Individual y;

        public Pair(Individual x, Individual y) {
            this.x = x;
            this.y = y;
        }
        
    }
}
