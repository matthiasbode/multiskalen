/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.storage.SuperStoreOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author bode
 */
public class StartofRoutesCalculator {

    /**
     * Bestimmt die momentane Position einer Ladeeinheit bezüglich eines
     * RealTimeSchedules.
     *
     * @param jobs Menge der Jobs, für die momentane Lagerfläche bestimmt werden
     * soll.
     * @param schedule Echtzeit-Ablaufplan.
     * @return
     */
    public static HashMap<LoadUnitJob, LoadUnitStorage> getCurrentPosition(Collection<LoadUnitJob> jobs, LoadUnitJobSchedule schedule) {
        HashMap<LoadUnitJob, LoadUnitStorage> result = new HashMap<>();
        for (LoadUnitJob loadUnitJob : jobs) {
            LoadUnitPositions operationsForLoadUnit = schedule.getOperationsForLoadUnit(loadUnitJob.getLoadUnit());
            LoadUnitStorage storage = null;
            ArrayList<StoreOperation> candidates = new ArrayList<>();
            for (int i = operationsForLoadUnit.size() - 1; i > -1; i--) {
                LoadUnitOperation operation = operationsForLoadUnit.get(i);
                if (operation instanceof StoreOperation) {
                    StoreOperation store = (StoreOperation) operation;
                    candidates.add(store);
                }
            }
            if (candidates.size() == 1) {
                storage = candidates.get(0).getResource();
            } else {
                for (StoreOperation candidate : candidates) {
                    if (candidate instanceof SuperStoreOperation) {
                        storage = candidate.getResource();
                        break;
                    }
                }
            }
            result.put(loadUnitJob, storage);
        }
        return result;
    }
}
