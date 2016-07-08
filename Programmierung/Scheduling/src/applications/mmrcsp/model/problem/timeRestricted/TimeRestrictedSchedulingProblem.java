/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.problem.timeRestricted;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import java.util.Map;

/**
 *
 * @author bode
 */
public interface TimeRestrictedSchedulingProblem<E extends Operation> extends SchedulingProblem<E> {
    public Map<E, EarliestAndLatestStartsAndEnds> getEalosaes();
   
}
