/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.setup;

import applications.mmrcsp.model.operations.SubOperations;
import applications.mmrcsp.model.operations.SingleResourceOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 *
 * @author bode
 */
public interface IdleSettingUpOperation extends SingleResourceOperation {

    public LoadUnitStorage getStart();

    public LoadUnitStorage getEnd();

    public SubOperations getSubOperations();

    public void setSubOperations(SubOperations demand);
}
