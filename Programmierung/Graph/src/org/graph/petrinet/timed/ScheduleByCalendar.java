package org.graph.petrinet.timed;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeSet;
import javax.swing.JTable;
import org.graph.petrinet.Event;

/**
 *
 * @author Nils Rinke
 */
public class ScheduleByCalendar {
    private TreeSet<Event> events = new TreeSet<Event>();
    
    private GregorianCalendar startDate;
    private GregorianCalendar actualDate;
    
    
    private SimpleDateFormat df = new SimpleDateFormat("dd HH:mm:ss");
        

    public ScheduleByCalendar(GregorianCalendar startDate) {
        this.startDate = startDate;
        actualDate = startDate;
        df.setTimeZone(TimeZone.getDefault()); 
    }

    
    public void add(Event e) {
        e.setStartTime(actualDate.getTime().getTime());
        if(checkWorkingTime(e))
            events.add(e);
        else {
            correctEvent(e);
            //TODO was passiert beim korrigieren?
            events.add(e);
        }
    }
    

    public long getNextTime() {
        long tmp = Long.MAX_VALUE;
        for (Event event : events) {
            if(!event.isCompleted()) {
                tmp = Math.min(tmp, event.getEndTime());
            }
        }
        if(tmp == Long.MAX_VALUE)
            return actualDate.getTimeInMillis();
        Date date = new Date(tmp);
        actualDate.setTime(date);
        return tmp;
    }
    

    public long getStartTime() {
        return startDate.getTime().getTime();
    }
    

    public GregorianCalendar getStartDate() {
        return startDate;
    }
    

    public long getActualTime() {
        return actualDate.getTime().getTime();
    }
    

    public GregorianCalendar getActualDate() {
        return actualDate;
    }
       

    public void setActualtime(long actualTime) {
        Date date = new Date(actualTime);
        actualDate.setTime(date);
    }
    
    
    
    
    
    public void reset() {
        actualDate = startDate;
        events = new TreeSet<Event>();
    }
    

    @Override
    public String toString() {
        String msg = "Start: " + startDate + "\n";
//        msg+= "duration: " +duration +"\n";
        for(Event e : events)
            msg+=e.toString() + " - " + e.getStartTime()+ " - " + e.getDuration() + "\n";
        return msg;
    }
    

    public void checkEvents() {
        for (Event event : events){
            if(actualDate.getTime().getTime()==event.getEndTime() && !event.isCompleted()) {
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
            data[i][2]=df.format(new Date(event.getStartTime()));
            data[i][3]=df.format(new Date(event.getEndTime()));
            i++;
        }
        return new JTable(data, columnNames);
    }
    
    public boolean checkWorkingTime(Event event) {
        Date date = new Date(event.getEndTime());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        System.out.println(df.format(date));
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
        if(calendar.get(Calendar.DAY_OF_WEEK)>4)
            return false;
        if(calendar.get(Calendar.DAY_OF_WEEK) != actualDate.get(Calendar.DAY_OF_WEEK))
            return false;
        if(calendar.get(Calendar.HOUR_OF_DAY) > 17)
            return false;
        return true;
    }

    private void correctEvent(Event e) {
        System.out.println("korrigiere " + e);
    }
}