/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.generator;

import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import java.util.List;

/**
 *
 * @author bode
 */
public interface LoadUnitGenerator {
    public List<LoadUnitJob> generateJobs(List<Train> trains, Terminal terminal);
}
