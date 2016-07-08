/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage;

import applications.mmrcsp.model.resources.SuperResource;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.LoadUnitResource;
import java.awt.geom.Area;
import javax.vecmath.Point3d;
import math.FieldElement;

/**
 *
 * @author bode
 */
public interface LoadUnitStorage extends LoadUnitResource, SuperResource {

    @Override
    public LoadUnitStorage getSubResource(Area area);

    public Point3d getPosition();

    public FieldElement getDemand(LoadUnit lu);
    
    public String getID();
}
