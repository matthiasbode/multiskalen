/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.resources.sharedResources;

import applications.mmrcsp.model.resources.PositionedResource;
import applications.mmrcsp.model.resources.Resource;
import java.util.Collection;

/**
 * Resource, die von mehreren anderen Ressourcen geteilt wird, also
 * beispielsweise ein CraneRunway oder ein LCSystem.
 *
 * @author bode
 * @param <E>
 */
public interface SharedResource<E extends Resource> extends PositionedResource {

    public Collection<E> getSharingResources();

    public void addSharingResource(E r);
   
}
