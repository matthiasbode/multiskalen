/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.modes;

import applications.mmrcsp.model.operations.Operation;

/**
 *
 * @author bode
 */
public interface JobOperation<R extends JobOperation> extends Operation {

    public void setRouting(JobOperationList<R> r);

    public JobOperationList<R> getRouting();
}
