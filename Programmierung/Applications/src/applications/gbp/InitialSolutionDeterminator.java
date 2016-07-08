/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp;

import applications.gbp.ma.BiPartitionIndividual;
import applications.gbp.ma.GBPHammingMetric;
import ga.basics.Population;
 

/**
 *
 * @author bode
 */
public class InitialSolutionDeterminator {
    public static void initializePopulation(Population<BiPartitionIndividual> pop, 
                            int numberOfIndividuals, int numberOfVertices,GBPHammingMetric metric){
        for (int i = 0; i < numberOfIndividuals; i++) {
            pop.add(new BiPartitionIndividual(numberOfVertices));
        }
    }
}
