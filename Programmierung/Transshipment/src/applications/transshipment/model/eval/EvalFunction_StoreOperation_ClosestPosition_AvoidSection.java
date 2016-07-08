/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.eval;

import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.routing.TransferArea;
import applications.transshipment.routing.TransportGraph;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point3d;

/**
 *
 * @author hofmann
 */
public class EvalFunction_StoreOperation_ClosestPosition_AvoidSection implements EvalFunction_StoreOperation {

    private final Point3d point;
    TerminalProblem p;

    public EvalFunction_StoreOperation_ClosestPosition_AvoidSection(Point3d point, TerminalProblem p) {
        this.point = point;
        this.p = p;
    }

    /**
     * Zielfunktion bewertet die Lageroperation nach Entfernung vom uebergebenen
     * Punkt
     *
     * @param sto
     * @return
     */
    @Override
    public double evaluate(StoreOperation sto) {
        double fitness = 0;
        TransportGraph staticGraph = p.getStaticTransportGraph();
        Area areaOfStoreOperation = sto.getResource().getGeneralOperatingArea();

        for (TransferArea transferArea : staticGraph.vertexSet()) {
            if (transferArea.getConveyanceSystems().size() > 1) {
                Area schnitt = new Area(transferArea.getStorageSystem().getGeneralOperatingArea());
                schnitt.intersect(areaOfStoreOperation);
                if (!schnitt.isEmpty()) {
                    fitness = 500;
                }
            }
        }
        Rectangle2D rect = sto.getResource().getGeneralOperatingArea().getBounds2D();
        Point3d np = new Point3d(rect.getCenterX(), rect.getCenterY(), 0);
        fitness += np.distance(point);
        return fitness;
    }

}
