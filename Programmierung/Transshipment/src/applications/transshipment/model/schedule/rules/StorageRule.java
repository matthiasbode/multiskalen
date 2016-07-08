/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.rules;

import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.List;
import math.FieldElement;

/**
 *
 * @author bode
 * @param <E>
 */
public interface StorageRule<E extends LoadUnitStorage> extends LoadUnitScheduleRule<E> {

    /**
     * Gibt die möglichen Lageroperationen zum Zeitpunkt start an.
     * @param lu
     * @param s
     * @param start
     * @param duration
     * @return
     */
    public List<StoreOperation> getPossibleStoreOperations(LoadUnit lu, Schedule s, FieldElement start, FieldElement duration);
}
