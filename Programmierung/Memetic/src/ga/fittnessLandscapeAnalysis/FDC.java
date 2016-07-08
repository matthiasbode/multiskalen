/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis;

import ga.individuals.Individual;
import ga.Parameters;
import ga.metric.Metric;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

/**
 *
 * @author bode
 */
public class FDC<C extends Individual> implements GAListener<C> {

    /**
     * Unter Annahme, alle bereits berechnet Individuuen sind in der Population
     */
    private Metric<C> m;
    private LocalOptimaCollection<C> localOptimaCollection;

    public FDC(LocalOptimaCollection<C> localOptimaCollection, Metric<C> m) {
        this.localOptimaCollection = localOptimaCollection;
        this.m = m;
    }

    public double getFDC(ArrayList<C> optima, Collection<C> individuals) {
        int n = individuals.size();
        double meanF = 0;
        double meanDistanceTolocalOptimum = 0;
        for (C individual : individuals) {
            meanF += individual.getFitness();
            double distance = Double.POSITIVE_INFINITY;
            for (C localOptimum : optima) {
                double tempDis = m.distance(localOptimum, individual);
                if (tempDis < distance) {
                    distance = tempDis;
                }
            }
            meanDistanceTolocalOptimum += distance;
        }
        meanF /= new Double(n);
        meanDistanceTolocalOptimum /= n;

        double sum = 0;


        double sigmaF = 0;
        double sigmaD = 0;
        for (C individual : individuals) {
            double distance = Double.POSITIVE_INFINITY;
            for (C localOptimum : optima) {
                double tempDis = m.distance(localOptimum, individual);
                if (tempDis < distance) {
                    distance = tempDis;
                }
            }


            sum += (individual.getFitness() - meanF) * (distance - meanDistanceTolocalOptimum);
            sigmaF += (individual.getFitness() - meanF) * (individual.getFitness() - meanF);
            sigmaD += (distance - meanDistanceTolocalOptimum) * (distance - meanDistanceTolocalOptimum);
        }

        double cov = 1. / n * sum;
        sigmaF = Math.sqrt(1.0 / (n - 1.0) * sigmaF);
        sigmaD = Math.sqrt(1.0 / (n - 1.0) * sigmaD);


        return cov / (sigmaD * sigmaF);
    }

    @Override
    public void nextGeneration(GAEvent<C> event) {
        ArrayList<C> globalOptima = new ArrayList<>();
        globalOptima.add(event.population.getFittestIndividual());
        double fdC = this.getFDC(globalOptima, localOptimaCollection);
        Parameters.logger.log(Level.FINER, "FDC in Generation " + event.populationNumber + ":" + fdC);
    }

    @Override
    public void finished(GAEvent<C> event) {
    }

   
}
