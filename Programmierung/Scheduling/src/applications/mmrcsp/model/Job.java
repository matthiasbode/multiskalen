/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model;

import applications.mmrcsp.model.modes.JobOperation;
import applications.mmrcsp.model.modes.JobOperationList;

/**
 *
 * @author Bode
 */
public class Job<E extends JobOperation> extends MultiModeJob<E> {

    public Job() {
    }

    public JobOperationList getJobOperationList() {
        return this.getRoutings().get(0);
    }
}
