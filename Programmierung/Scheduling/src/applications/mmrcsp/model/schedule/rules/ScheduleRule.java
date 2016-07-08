/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.rules;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import math.FieldElement;

/**
 * Eine ScheduleRule testet zunächst für ein bestimmtes Conveyance-System, ob
 * eine Operation passt und wann sie passt. Überlegung: im Makroskopischen Fall
 * --> Aktivitätsliste makroskopisch, es wird geguckt, wann "Auslastung" passt,
 * Startzeit zurückgegeben. im Mikroskopischen Fall --> Aktivitätsliste auch
 * makroskopisch. Wie komme ich zu den mikroskopischen Operationen?
 *
 * Abgeleitetes Interface, dass auch noch Methoden mappt
 *
 * @author bode
 * @param <E>
 */
public interface ScheduleRule<E extends Resource> {

    /**
     * Gibt die Ressource zurück, für die diese ScheduleRule gesetzt wurde.
     *
     * @return
     */
    public E getResource();

    /**
     * Diese Methode beantwortet die Frage, ob eine Transportoperation im Rahmen
     * eines übergebenen Schedules einplanbar ist. Dabei wird die mögliche
     * Startzeit der ggf. nötigen Rüstfahrt übergeben.
     *
     * @param s
     * @param o
     * @param startBundle
     * @return
     */
    public boolean canSchedule(Schedule s, Operation o, FieldElement startBundle);

  

    /**
     * Plant die Operation in den ResourceUtilizationManger der Resource ein
     *
     * @param o
     * @param s
     * @param start
     */
    public void schedule(Operation o, Schedule s, FieldElement start);

    /**
     * Plant die Operation aus dem ResourceUtilizationManager der Resource aus.
     *
     * @param o
     * @param s
     */
    public void unSchedule(Operation o, Schedule s);

    /**
     * Liste der möglichen Zeitfenster, in denen ein bestimme Resourcenbedarf
     * von der Resource zur Verfügung steht.
     *
     * @param s
     * @param demand
     * @param duration
     * @param interval
     * @return
     */
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval);

    /**
     *
     * @param s
     * @param o
     * @param start
     * @return
     */
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start);
}
