/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.ga;

import applications.mmrcsp.model.operations.Operation;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author bode
 * @param <E>
 */
public class ListIndividualPriorityMapComparator<E extends Operation> implements Comparator<E> {

    final private HashMap<E, PriorityItem> priorities;

    public ListIndividualPriorityMapComparator(HashMap<E, PriorityItem> priorities) {
        this.priorities = priorities;
    }

    @Override
    public int compare(E o1, E o2) {
        PriorityItem pO1 = priorities.get(o1);
        PriorityItem pO2 = priorities.get(o2);
        int compare = Integer.compare(pO1.priority, pO2.priority);
        /**
         * Bei Prioritätsgleichheit
         */
        if (compare == 0) {
            /**
             * Falls beide aus der selben Knotenklasse kommen, entscheidet die
             * Position innerhalb der Knotenklasse, falls aus unterschiedlichen
             * Knotenklassen, hat derjenige eine höhere Priorität, der in einer
             * kleineren Knotenklasse ist.
             */
            int compare2 = Integer.compare(pO1.vertexClass, pO2.vertexClass);
            if (compare2 == 0) {
                return Integer.compare(pO1.priorityInClass, pO2.priorityInClass);
            } else {
                return compare2;
            }
        }
        return compare;
    }

    public static class PriorityItem {

        public int priority;
        public int priorityInClass;
        public int vertexClass;

        public PriorityItem(int priority, int priorityInClass, int vertexClass) {
            this.priority = priority;
            this.vertexClass = vertexClass;
        }
    }
}
