/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.eval;

 
import applications.transshipment.model.operations.storage.StoreOperation;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point3d;

/**
 *
 * @author hofmann
 */
public class EvalFunction_StoreOperation_ClosestPosition implements EvalFunction_StoreOperation {

    private final Point3d point;

    public EvalFunction_StoreOperation_ClosestPosition (Point3d point) {
        this.point=point;
    }

    /**
     * Zielfunktion bewertet die Lageroperation nach Entfernung vom uebergebenen Punkt
     * @param sto
     * @return
     */
    @Override
    public double evaluate(StoreOperation sto) {
        Rectangle2D rect = sto.getResource().getGeneralOperatingArea().getBounds2D();
        Point3d np = new Point3d(rect.getCenterX(), rect.getCenterY(), 0);
        return np.distance(point);
    }


}
