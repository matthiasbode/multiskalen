/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving;

import ga.algorithms.GAAlgorithm;
import ga.basics.Population;
import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public abstract class CoevolvingGA<I extends Individual> extends GAAlgorithm<I>  {

    public CoevolvingGA() {
    }
    
    public abstract Map<Population<? extends Individual>, GABundle> getSpecies();
    public  Population<? extends Individual> getSubPopulation(Class<?> typ){
        for (Population<? extends Individual> subPop : getSpecies().keySet()) {
            if(subPop.getFittestIndividual().getClass().equals(typ)){
                return subPop;
            }
        }
        return null;
    }
}
