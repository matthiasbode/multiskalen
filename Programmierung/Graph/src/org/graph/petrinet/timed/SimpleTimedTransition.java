/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.petrinet.timed;

/**
 *
 * @author rinke
 */
public class SimpleTimedTransition extends TimedTransition {
    long duration;

    public SimpleTimedTransition(String name, long duration) {
        super(name);
        this.duration = duration;
    }
    
    
    public SimpleTimedTransition(String name, int priority, long duration) {
        super(name, priority);
        this.duration = duration;
    }
    
    
    public SimpleTimedTransition(String name, int priority, int numberOfSimultaneousActions, long duration) {
        super(name, priority);
        this.duration = duration;
    }

    
    @Override
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
