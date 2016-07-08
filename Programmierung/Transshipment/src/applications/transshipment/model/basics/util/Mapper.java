/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.multiscale.model.Scale;

/**
 *
 * @author bode
 */
public interface Mapper {
     public ScheduleManagerBuilder getScheduleRuleBuilder(Terminal terminal, Scale scale);
}
