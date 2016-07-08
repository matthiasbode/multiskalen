/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.rules;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;

/**
 *
 * @author bode
 * @param <E>
 */
public interface LoadUnitScheduleRule<E extends Resource> extends ScheduleRule<E> {
}
