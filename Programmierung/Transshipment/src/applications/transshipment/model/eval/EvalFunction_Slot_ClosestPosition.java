/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.eval;

import applications.transshipment.model.structs.Slot;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point3d;

/**
 *
 * @author bode
 */
 

/**
 *
 * @author hofmann
 */
public class EvalFunction_Slot_ClosestPosition implements EvalFunction_Slot {

    private final Point3d point;

    public EvalFunction_Slot_ClosestPosition (Point3d point) {
        this.point=point;
    }

    /**
     * Zielfunktion bewertet die Lageroperation nach Entfernung vom uebergebenen Punkt
     * @param sto
     * @return
     */
    @Override
    public double evaluate(Slot sto) {
        Rectangle2D rect = sto.getGeneralOperatingArea().getBounds2D();
        Point3d np = new Point3d(rect.getCenterX(), rect.getCenterY(), 0);
        return np.distance(point);
    }


}
