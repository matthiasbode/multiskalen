/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.resources;

import java.awt.geom.Area;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */
public interface PositionedResource extends Resource {
    public Area getGeneralOperatingArea();
    public Point2d getCenterOfGeneralOperatingArea();
}
