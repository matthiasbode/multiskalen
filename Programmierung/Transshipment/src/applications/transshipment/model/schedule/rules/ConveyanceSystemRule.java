/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.rules;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.structs.SpaceTimeElement;
import java.util.Collection;
import math.FieldElement;

/**
 *
 * @author bode
 */
public interface ConveyanceSystemRule<E extends ConveyanceSystem> extends LoadUnitScheduleRule<E> {

    /**
     * Bestimmt die Dauer und den Bedarf
     *
     * @param operation
     */
//    public void determineDurationAndDemands(LoadUnitOperation operation);
    public FieldElement getTransportationTime(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit lu);

    public IdleSettingUpOperation findIdleSettingUpOperation(Operation predecessor, MultiScaleTransportOperation transOp);

    /**
     * Bestimmt für eine Transportoperation die Operationen, die zusätzlich zur
     * Transportoperation eingeplant werden müssen, also beispielsweise
     * Rüstfahrten.
     *
     * @param s
     * @param top
     * @param startTimeTransport
     * @return
     */
    public TransportBundle getBundle(Schedule s, MultiScaleTransportOperation top, FieldElement startTimeTransport);

    /**
     * Erstellt anhand einer RoutingTransportOperation die entsprechende
     * Transportoperation im jeweiligen Skalenbereich.
     *
     * @param o
     * @return
     */
    public MultiScaleTransportOperation getDetailedOperation(RoutingTransportOperation o, LoadUnitStorage origin, LoadUnitStorage destination);

    /**
     * Gibt die Startzeit für eine Teiloperation an. Dahinter stecken vielleicht
     * mehr Information (siehe abgeleitetes Interface)
     *
     * @param s
     * @param o Hier eigentlich nur makroskopische TransportOperationen rein
     * @param interval
     * @return
     */
    public FieldElement getNextPossibleBundleStartTime(Schedule s, MultiScaleTransportOperation o, TimeSlot interval);

    public void initPositions(Schedule s, Collection<SpaceTimeElement> initialPositions);

}
