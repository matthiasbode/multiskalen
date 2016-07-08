/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.petrinet.timed;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import org.graph.petrinet.*;
import java.util.PriorityQueue;
import java.util.TimeZone;
import org.graph.petrinet.timed.Schedule;

/**
 *
 * @author rinke
 */
public class PetriNetSimulatorByCalendar {
    
    private final PetriNet net;
    
    private ScheduleByCalendar schedule;
    

    public PetriNetSimulatorByCalendar(PetriNet net, GregorianCalendar startTime) {
        this.net = net;
        schedule = new ScheduleByCalendar(startTime);
    }
    
    public void run(){
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
        df.setTimeZone(TimeZone.getDefault() ); 
        
        long globalTime  = schedule.getStartTime();
        long actualTime  = globalTime;
        long nextTime = Long.MAX_VALUE;

        boolean firstStep=true;
        System.out.println("Start: " + df.format(schedule.getStartDate().getTime()));
        while(actualTime != nextTime) {
            if (firstStep) {
                firstStep = false;
            } else {
                System.out.println("setze actual Time");
                actualTime = nextTime;
                schedule.setActualtime(actualTime);
            }
            PriorityQueue<Transition> transitions = net.getActiveTransitions();    
            while (!transitions.isEmpty()) {
                System.out.println("active Transitions: " + transitions);
                Transition transition = transitions.poll();
                Event ev = net.fireTransition(transition);
                schedule.add(ev);
            }
            nextTime = schedule.getNextTime();
            schedule.checkEvents();
            net.releaseCompletedEvents();
            System.out.println("nextTime: " +nextTime);
            Date date = new Date(nextTime);
            System.out.println("nextTime als Date: " + df.format(date));
        }
        System.out.println("Ende: " + df.format(schedule.getActualDate().getTime()));
    }

    public ScheduleByCalendar getSchedule() {
        return schedule;
    }
}