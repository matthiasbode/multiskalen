/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics.util;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;

/**
 *
 * @author bode
 */
public class MaxDurationSum {

    public static <E extends Operation> long getMaxDuration(SchedulingProblem<E> problem) {
        long result = 0;
        for (E e : problem.getOperations()) {
            result += e.getDuration().longValue();
        }
        return result;
    }
}
