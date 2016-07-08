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
public interface LocalSearch<I extends Individual> {
    public I localSearch(I start);
}
