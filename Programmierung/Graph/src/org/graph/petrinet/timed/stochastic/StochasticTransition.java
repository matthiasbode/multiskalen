package org.graph.petrinet.timed.stochastic;

import org.graph.petrinet.timed.TimedTransition;
import org.graph.petrinet.timed.stochastic.distributions.Distribution;

/**
 *
 * @author Roeth
 */
public abstract class StochasticTransition extends TimedTransition {

    Distribution dist;
    //TODO eine StochasticTransition sollte eine Referenz auf eine Random
    //besitzen. Siehe Randomizer

    public StochasticTransition(String name, Distribution dist) {
        super(name);
        this.dist = dist;
    }

    public StochasticTransition(String name, int priority, Distribution dist) {
        super(name, priority);
        this.dist = dist;
    }

    public StochasticTransition(String name, int priority, int numberOfSimultaneousActions, Distribution dist) {
        super(name, priority, numberOfSimultaneousActions);
        this.dist = dist;
    }


}
