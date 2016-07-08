/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.rules;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;

/**
 *
 * Für nicht-makroskopische Operationen wird zusätzlich noch ein Mapping 
 * zwischen Makroskopischen und exakteren Operationen benötigt.
 * MOMENTAN NICHT IN BENUTZUNG
 * @author bode
 * @param <E>
 */
public interface DetailedScheduleRule<E extends Resource> extends ScheduleRule<E>{

}
