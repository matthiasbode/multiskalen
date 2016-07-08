/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations;

import applications.mmrcsp.model.operations.SingleResourceOperation;
import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.model.basics.util.LoadUnitPositionAndOrientation3DInTime;
import applications.transshipment.model.loadunits.LoadUnit;
import java.util.List;

/**
 *
 * @author bode
 */
/**
 * Eine {@link Operation}, die sich immer auf eine {@link LoadUnit} bezieht.
 *
 * @author berthold, hofmann
 */
public interface LoadUnitOperation extends Operation, SingleResourceOperation {

    /**
     * Gibt die LoadUnit zurueck, auf die sich die Operation bezieht.
     *
     * @return die LoadUnit der Operation
     */
    public LoadUnit getLoadUnit();

    /**
     * Gibt ausgezeichnete Punkte in Raum und Zeit für die Ladeeinheit an. Diese
     * sind relativ vom Operationsstart angegeben und müssen für den weiteren
     * Gebrauch unter Umständen in der Zeit verschoben werden.
     *
     * @return
     */
    public List<LoadUnitPositionAndOrientation3DInTime> getKeyPoints();

}
