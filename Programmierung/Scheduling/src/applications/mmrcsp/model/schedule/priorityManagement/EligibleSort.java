/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.priorityManagement;

import applications.mmrcsp.model.operations.Operation;
import java.util.LinkedList;

/**
 *
 * @author bode
 */
public interface EligibleSort<E extends Operation> {

    public LinkedList<E> getOrderedOperations(LinkedList<E> col);

}
