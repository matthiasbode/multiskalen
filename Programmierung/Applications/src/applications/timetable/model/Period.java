/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

/**
 *
 * @author bode
 */
public class Period implements MatrixColumn {

    private static int counter = 0;
    private int index;
    private TimeSlot timeSlot;
    private String idInSchedule;

    public Period() {
    }

    public Period(TimeSlot p, String idInSchedule) {
        this.timeSlot = p;
        this.idInSchedule = idInSchedule;
        this.index = counter++;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIdInSchedule() {
        return idInSchedule;
    }

    public void setIdInSchedule(String idInSchedule) {
        this.idInSchedule = idInSchedule;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public String toString() {
        return "Period{" + "index=" + index + ", Slot=" + timeSlot + ", idInSchedule=" + idInSchedule + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Period other = (Period) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.index;
        return hash;
    }
    
    
}
