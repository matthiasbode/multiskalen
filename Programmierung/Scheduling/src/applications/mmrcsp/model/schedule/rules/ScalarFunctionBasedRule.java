/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.rules;

import applications.mmrcsp.model.resources.Resource;
import bijava.math.function.ScalarFunction1d;

/**
 *
 * @author bode
 */
public interface ScalarFunctionBasedRule<E extends Resource> extends ScheduleRule<E> {

    public ScalarFunction1d getWorkloadFunction();
    public double getMax();

}
