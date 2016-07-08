/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.rules;

import applications.mmrcsp.model.resources.sharedResources.SharedResource;

/**
 *
 * @author bode
 * @param <E>
 */
public interface SharedResourceManager<E extends SharedResource> {

    public E getResource();
}
