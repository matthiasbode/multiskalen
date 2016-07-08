/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis;

import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.File;

/**
 *
 * @author bode
 */
public interface Analysis {

    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder);

}
