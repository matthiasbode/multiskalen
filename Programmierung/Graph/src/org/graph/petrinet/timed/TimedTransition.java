package org.graph.petrinet.timed;

import org.graph.petrinet.Transition;

/**
 *
 * @author Nils Rinke
 */
public abstract class TimedTransition extends Transition{
    protected int numberOfSimultaneousActions;

    
    public TimedTransition(String name) {
        super(name);
        numberOfSimultaneousActions = 1;
    }
    
    
    public TimedTransition(String name, int priority) {
        super(name, priority);
        numberOfSimultaneousActions = 1;
    }
    
    
    public TimedTransition(String name, int priority, int numberOfSimultaneousActions) {
        super(name, priority);
        this.numberOfSimultaneousActions = numberOfSimultaneousActions;
    }
    

    public abstract long getDuration();

    
    public int getNumberOfSimultaneousActions() {
        return numberOfSimultaneousActions;
    }

    public void setNumberOfSimultaneousActions(int numberOfSimultaneousActions) {
        this.numberOfSimultaneousActions = numberOfSimultaneousActions;
    }
}