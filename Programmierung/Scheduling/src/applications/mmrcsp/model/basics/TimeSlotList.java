/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics;

import java.util.TreeSet;
import math.FieldElement;

/**
 * Eine TimeSlotList bietet die Möglichkeit, mehrere Zeitfenster zu Verwalten.
 * Es gibt zusätzlich beispielsweise Anfragen, ob ein TimeSlot zu der gesamten
 * Liste von TimeSlots, etc. disjunkt ist.
 *
 * @author bode
 */
public class TimeSlotList extends TreeSet<TimeSlot> implements Cloneable {

    public TimeSlotList() {
    }

    public TimeSlotList(TimeSlot t) {
        super();
        this.add(t);
    }

    /**
     * Gibt den Zeitpunkt zurueck, an dem dieser TimeSlot beginnt.
     *
     * @return Zeitpunkt als long.
     */
    public FieldElement getFromWhen() {
        return this.first().getFromWhen();
    }
    
    public FieldElement getDuration(){
        return this.getAllOverTimeSlot().getDuration();
    }

    /**
     * Gibt den Zeitpunkt zurueck, an dem dieser TimeSlot endet.
     *
     * @return Zeitpunkt als long.
     */
    public FieldElement getUntilWhen() {
        return this.last().getUntilWhen();
    }

    public TimeSlot getAllOverTimeSlot() {
        return new TimeSlot(getFromWhen(), getUntilWhen());
    }

    /**
     * Gibt eine Deep-Copy zurück
     *
     * @return
     */
    @Override
    public TimeSlotList clone() {
        TimeSlotList res = new TimeSlotList();
        for (TimeSlot timeSlot : this) {
            res.add(timeSlot.clone());
        }
        return res;
    }

    public TimeSlotList section(TimeSlotList other) {
        TimeSlotList result = new TimeSlotList();
        for (TimeSlot timeSlot : this) {
            for (TimeSlot timeSlot1 : other) {
                TimeSlot section = timeSlot.section(timeSlot1);
                if (section != null) {
                    result.add(section);
                }
            }
        }
        return result;

    }

    @Override
    public String toString() {
        return "TimeSlotList{" + this.getAllOverTimeSlot().toString() + '}';
    }

}
