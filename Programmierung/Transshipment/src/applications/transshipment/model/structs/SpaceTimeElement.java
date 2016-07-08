/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import javax.vecmath.Point3d;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class SpaceTimeElement {

    public Point3d p;
    public FieldElement time;

    public SpaceTimeElement(Point3d p, FieldElement time) {
        this.p = p;
        this.time = time;
    }

}
