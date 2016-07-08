/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.petrinet;

import java.util.PriorityQueue;
import org.graph.petrinet.timed.Schedule;

/**
 *
 * @author rinke
 */
public class PetriNetSimulator {
    
    private final PetriNet net;
    
    private Schedule schedule;
    

    public PetriNetSimulator(PetriNet net, long startTime, long duration) {
        this.net = net;
        schedule = new Schedule(startTime, duration);
    }
    
    public void run(){
        long globalTime  = schedule.getStartTime();
        long endTime     = schedule.getEndTime();
        System.out.println(globalTime);
        System.out.println(endTime);
        System.out.println("---------");
        while(globalTime < endTime) {
            PriorityQueue<Transition> transitions = net.getActiveTransitions();    
            while (!transitions.isEmpty()) {
                System.out.println("active Transitions: " + transitions);
                Transition transition = transitions.poll();
                Event ev = net.fireTransition(transition);
                System.out.println("ev: " +ev + " / " + transition.getName());
                schedule.add(ev);
            }
            globalTime = schedule.getNextTime();
            schedule.checkEvents();
            net.releaseCompletedEvents();            
            schedule.setActualTime(globalTime);
            System.out.println("globalTime: " +globalTime);
        }
        System.out.println("schedule: " + schedule);
        System.out.println("Dauer: "+ schedule.getAbortionTime());
    }

    public Schedule getSchedule() {
        return schedule;
    }
}