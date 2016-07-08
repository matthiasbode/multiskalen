/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.generator.json;

import applications.transshipment.model.LoadUnitJob;

/**
 *
 * @author bode
 */
public class JobExport {
   String name;

    public JobExport(LoadUnitJob job) {
        this.name = job.getLoadUnit().getID();
    }

    @Override
    public String toString() {
        return "JobExport{" + name + '}';
    }
   
    
}
