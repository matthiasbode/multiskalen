/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.petrinet;

import java.util.HashMap;
import java.util.LinkedList;
import org.graph.petrinet.timed.TimedTransition;

/**
 *
 * @author nilsrinke
 */
public class Event implements Comparable<Event> {
    
    private final Transition transition;
   
    private boolean completed;
    
    
    protected HashMap<TokenType, LinkedList<Token>> token;
    
    private long startTime;
    private long duration;

    public Event(Transition transition, HashMap<TokenType, LinkedList<Token>> token) {
        this.transition = transition;
        transition.setState(Transition.State.ISFIRING);
        this.token = token;
        this.completed = false;
        if(transition instanceof TimedTransition)
            this.duration = ((TimedTransition) transition).getDuration();
        else
            this.duration = 0;
    }
    

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    
    public HashMap<TokenType, LinkedList<Token>> getToken() {
        return token;
    }


    public Transition getTransition() {
        return transition;
    }
    
    public long getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return startTime + duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (this.transition != other.transition && (this.transition == null || !this.transition.equals(other.transition))) {
            return false;
        }
        if (this.token != other.token && (this.token == null || !this.token.equals(other.token))) {
            return false;
        }
        if (this.startTime != other.startTime) {
            return false;
        }
        if (this.duration != other.duration) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.transition != null ? this.transition.hashCode() : 0);
        hash = 79 * hash + (this.token != null ? this.token.hashCode() : 0);
        hash = 79 * hash + (int) (this.startTime ^ (this.startTime >>> 32));
        hash = 79 * hash + (int) (this.duration ^ (this.duration >>> 32));
        return hash;
    }

    


    @Override
    public int compareTo(Event event) {
        if(startTime < event.startTime)
            return -1;
        else if(startTime > event.startTime)
            return 1;
        else if(duration < event.duration)
            return -1;
        else if(duration > event.duration)
            return 1;
        else
            return hashCode()-event.hashCode();
    }

    @Override
    public String toString() {
        return "Event{" + "transition=" + transition + ", token=" + token + ", startTime=" + startTime + ", duration=" + duration + '}';
    }
    
    
}
