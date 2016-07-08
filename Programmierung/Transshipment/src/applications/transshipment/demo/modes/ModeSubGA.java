/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.modes;

import ga.algorithms.coevolving.GABundle;
import ga.basics.StartPopulationGenerator;
import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public interface ModeSubGA<I extends Individual> {

    public GABundle<I> getGA(StartPopulationGenerator<I> startPopGen, boolean parallel);

}
