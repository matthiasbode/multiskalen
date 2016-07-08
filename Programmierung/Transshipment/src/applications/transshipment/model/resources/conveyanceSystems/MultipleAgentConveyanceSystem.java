/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems;

import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;

/**
 *
 * @author bode
 */
public interface MultipleAgentConveyanceSystem<E extends ConveyanceSystem> extends SharedResource<E>, ConveyanceSystem {
}
