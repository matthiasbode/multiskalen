/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.resources;

import java.awt.geom.Area;

/**
 *
 * @author bode
 */
public interface SuperResource extends Resource{
    public Resource getSubResource(Area area);
}
