/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule;

import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import java.util.Comparator;

/**
 *
 * @author bode
 */
/**
 * Zunächst wird nach Startzeitpunkt sortiert, dann nach ID. Es können
 * theoretisch also auch zwei Operationen zum gleichen Zeitpunkt eingeplant
 * werden
 */
public class LoadUnitJobOperationComparator implements Comparator<Operation> {

    LoadUnitJobSchedule schedule;

    public LoadUnitJobOperationComparator(LoadUnitJobSchedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public int compare(Operation o1, Operation o2) {
        if (schedule.get(o1) == null || schedule.get(o2) == null) {
            return 0;
        }
        if (schedule.get(o1) == null) {
            return 1;
        }
        if (schedule.get(o2) == null) {
            return -1;
        }
        int compare = schedule.get(o1).compareTo(schedule.get(o2));
        if (compare != 0) {
            return compare;
        } else {
            if(o1 instanceof TransportOperation && o2 instanceof IdleSettingUpOperation){
                return 1;
            }
            if(o2 instanceof TransportOperation && o1 instanceof IdleSettingUpOperation){
                return -1;
            }
            return Integer.compare(o1.getId(), (o2.getId()));
        }
    }
}
