/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule;

import applications.mmrcsp.model.operations.Operation;
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
public class OperationComparator<E extends Operation> implements Comparator<E> {

    Schedule schedule;

    public OperationComparator(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public int compare(E o1, E o2) {
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
            return Integer.compare(o1.getId(), (o2.getId()));
        }
    }
}
