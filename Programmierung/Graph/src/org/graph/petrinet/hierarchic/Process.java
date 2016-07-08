/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.petrinet.hierarchic;

import org.graph.petrinet.PetriNet;
import org.graph.petrinet.PetriNetSimulator;
import org.graph.petrinet.timed.TimedTransition;

/**
 *
 * @author rinke
 */
public class Process extends TimedTransition {

    private PetriNet subNet;
    private PetriNetSimulator simulator;

    public Process(PetriNet subNet, String name, int priority, int numberOfSimultaneousActions) {
        super(name, priority, numberOfSimultaneousActions);
        this.subNet = subNet;
        this.simulator = new PetriNetSimulator(subNet, 0, Long.MAX_VALUE);
    }
    
    
    @Override
    public long getDuration() {
        simulator.run();
        return simulator.getSchedule().getAbortionTime();
    }
    
}
