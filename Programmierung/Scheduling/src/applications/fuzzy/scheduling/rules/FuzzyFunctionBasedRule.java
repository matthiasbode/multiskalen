/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.fuzzy.scheduling.rules;

import applications.fuzzy.functions.LinearizedFunction1d;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import math.FieldElement;

/**
 *
 * @author bode
 */
public interface FuzzyFunctionBasedRule <E extends Resource> extends ScheduleRule<E> {

    public LinearizedFunction1d getWorkloadFunction();
    public FieldElement getMax();
    
}
