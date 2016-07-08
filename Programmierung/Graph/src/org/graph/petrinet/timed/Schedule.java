package org.graph.petrinet.timed;

import org.graph.petrinet.Transition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JTable;
import org.graph.petrinet.Event;

/**
 *
 * @author Nils Rinke
 */
public class Schedule {
    private long startTime;
    private long duration;
    private long endTime;

    private long actualTime;
    private long abortionTime;

    private TreeSet<Event> events = new TreeSet<Event>();
    
    private GregorianCalendar starttime;
    private GregorianCalendar actualtime;
    
    

    public Schedule(long startTime, long duration) {
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime + duration;
        starttime = new GregorianCalendar(2012, 2, 6, 8, 0);
        actualtime = starttime;
    }
    

    public Schedule() {
        this(0,(3600*24));
    }

    
    public void add(Event e) {
        e.setStartTime(actualTime);
        events.add(e);
        //actualTime = Math.max(duration, e.getStartTime()+e.getDuration());
    }
    

    public long getDuration() {
        return duration;
    }
    

    public long getNextTime() {
        long tmp = startTime+duration;
        for (Event event : events) {
            if(!event.isCompleted()) {
                tmp = Math.min(tmp, event.getEndTime());
            }
        }
        if(tmp!=(startTime+duration))
            abortionTime=tmp;
        System.out.println("nextTime: " + tmp);
        return tmp;
    }
    

    public long getStartTime() {
        return startTime;
    }
    

    public long getActualTime() {
        return actualTime;
    }
    
    
    public void setActualTime(long globalTime) {
        actualTime = globalTime;
    }
    

    public long getAbortionTime() {
        return abortionTime;
    }
    

    public long getEndTime() {
        return endTime;
    }

    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    
    public void reset() {
        actualTime =0;
        abortionTime = 0;
        events = new TreeSet<Event>();
    }
    

    @Override
    public String toString() {
        String msg = "Start: " + startTime + "\n";
//        msg+= "duration: " +duration +"\n";
        for(Event e : events)
            msg+=e.toString() + " - " + e.getStartTime()+ " - " + e.getDuration() + "\n";
        return msg;
    }
    

    public void checkEvents() {
        for (Event event : events){
            if(actualTime==event.getEndTime() && !event.isCompleted()) {
                System.out.println(event + " completed gesetzt");
                event.setCompleted(true);
            }
        }
    }
    
    
    public JTable getEventTable() {
        String[] columnNames = {"ID", "Name", "Start","Ende"};
        System.out.println(events.size() + " Größe Events");

        Object[][] data = new Object[events.size()][4];
        int i = 0;
        for (Event event : events) {
            data[i][0]=i;
            data[i][1]=event.getTransition().getName();
            data[i][2]=event.getStartTime();
            data[i][3]=event.getEndTime();
            i++;
        }
        return new JTable(data, columnNames);
    }   
}