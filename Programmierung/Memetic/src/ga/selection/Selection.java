package ga.selection;

import ga.individuals.Individual;
import ga.basics.Population;

/**
 *
 * @author milbradt
 */
public interface Selection<C extends Individual> extends Cloneable{

    public C selectFromPopulation(Population<C> pop);
    /**
     * Ermöglicht verschiedene Informationen zu updaten
     * @param o 
     */ 
    
    public Selection<C> clone();
}
