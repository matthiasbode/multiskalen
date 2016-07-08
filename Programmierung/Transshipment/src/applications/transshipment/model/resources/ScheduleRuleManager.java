/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources;

import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceInteraction;
import applications.transshipment.model.resources.storage.StorageInteraction;
import java.util.HashMap;

/**
 *
// * @author bode
// */
//public interface ScheduleRuleManager {
//
//    public static enum Scale{
//        micro,
//        macro
//    }
//    
//    /**
//     * Liefert einen ScheduleManagerBuilder, der Regeln und Manager für die
//     * einzelnen ConveyanceSystems und Lager zurückgibt um die Einplanung zu
//     * beschreiben und eingeplante Operationen zu verwalten.
//     *
//     * @param scale
//     * @return
//     */
//    public ScheduleManagerBuilder getScheduleRuleBuilder(Scale scale);
// 
//}
