/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.priorityManagement;

import applications.mmrcsp.model.operations.Operation;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bode
 */
public class StandardChooser< O extends Operation> implements EligibleSort<O> {

    private final List<O> activityList;

    public StandardChooser(List<O> activityList) {
        this.activityList = activityList;
    }

    @Override
    public LinkedList<O> getOrderedOperations(LinkedList<O> col) {
        LinkedList<O> res = new LinkedList<>(activityList);
        res.retainAll(col);
        return res;
    }

}
