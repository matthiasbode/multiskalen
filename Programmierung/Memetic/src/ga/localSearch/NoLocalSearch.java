/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.localSearch;

import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public class NoLocalSearch<I extends Individual> implements LocalSearch<I> {

    @Override
    public I localSearch(I start) {
        return start;
    }
    
}
