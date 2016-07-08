/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.resources;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;

/**
 * Diese Klasse modelliert eine Resource. Eine Resource kann Operationen
 * ausführen und benötigt daher Methoden, um Operationen einzuplanen. Das
 * Einplanen erfolgt immer über einen Schedule, der alle Operationen aller
 * Resourcen global sammelt.
 *
 * @author bode
 */
public interface Resource {

    /**
     * Verfügbarkeit der Resource als Liste
     *
     * @return
     */
    public TimeSlotList getTemporalAvailability();

    /**
     * Setzt die Verfügbarkeit einer Resource.
     *
     * @param tempAvail
     */
    public void setTemporalAvailability(TimeSlotList tempAvail);

    public void setTemporalAvailability(TimeSlot tempAvail);

    public String getID();

}
