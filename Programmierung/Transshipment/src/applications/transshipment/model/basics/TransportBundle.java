/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.StorageToolMethods;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import math.FieldElement;

/**
 * Diese Klasse fasst alle Operationen, die beim Einplanen einer neuen
 * TransportOperation ein- oder ausgeplant werden müssen, zusammen. Dabei
 * handelt es sich größtenteils um Rüstfahrten. Hier sind auch die Startzeiten
 * der Operationen abgespeichert!
 *
 * @author bode
 */
public class TransportBundle {

    /**
     * Ressource, die die Operationen ausführt
     */
    private Resource resource;
    /**
     * Operation, nach der die neue TransportOperation eingefügt werden soll.
     */
    private TransportOperation q;
    /**
     * Rüstfahrt von Q zur neueinzuplanenden Operation j
     */
    private IdleSettingUpOperation sqj;
    /**
     * Die neueinzuplanende Operation j
     */
    private MultiScaleTransportOperation j;
    /**
     * Die alte SetUpOperation von q nach q+1
     */
    private IdleSettingUpOperation sqq1_old;
    /**
     * Die neue SetUpOperation von j nach q+1
     */
    private IdleSettingUpOperation sjq1_new;
    /**
     * Die nachfolgende Operation q1
     */
    private TransportOperation q1;
    /**
     * Menge der vor j eingeplanten Operationen, die auch q enthält (momentan
     * ausschließlich)
     */
    private Collection<Operation> operationsBefore;

    private StoreOperation storeAtDestination;
    private TimeSlot storageTimeSlot;

    /**
     * Startzeiten der einzelnen Operationen.
     */
    private LinkedHashMap<Operation, FieldElement> startTimes = new LinkedHashMap<>();

    public TransportBundle(Resource resource, TransportOperation q, Collection<Operation> operationsBefore, IdleSettingUpOperation sqj, MultiScaleTransportOperation j, IdleSettingUpOperation sqq1_old, IdleSettingUpOperation sjq1_new, TransportOperation q1) {
        this.resource = resource;
        this.operationsBefore = operationsBefore;
        this.q = q;
        this.sqq1_old = sqq1_old;
        this.sjq1_new = sjq1_new;
        this.q1 = q1;
        this.j = j;
        this.sqj = sqj;
    }

    public TransportBundle(Resource resource, MultiScaleTransportOperation j, FieldElement startTransport) {
        this.resource = resource;
        this.j = j;
        this.setStartTime(j, startTransport);
    }

    public Resource getResource() {
        return resource;
    }

    public TransportOperation getQ() {
        return q;
    }

    public IdleSettingUpOperation getSqq1_old() {
        return sqq1_old;
    }

    public TransportOperation getQ1() {
        return q1;
    }

    public Collection<Operation> getOperationsBefore() {
        return operationsBefore;
    }

    public IdleSettingUpOperation getSqj() {
        return sqj;
    }

    public MultiScaleTransportOperation getJ() {
        return j;
    }

    public IdleSettingUpOperation getSjq1_new() {
        return sjq1_new;
    }

    public FieldElement getStartTime(Operation o) {
        return startTimes.get(o);
    }

    public void setStartTime(Operation o, FieldElement startTime) {
        startTimes.put(o, startTime);
    }

    public StoreOperation getStoreAtDestination() {
        return storeAtDestination;
    }

    @Deprecated
    public void setStoreAtDestination(StoreOperation storeAtDestination, TimeSlot storageTimeSlot) {
        this.storeAtDestination = storeAtDestination;
        this.storageTimeSlot = storageTimeSlot;
        this.startTimes.put(storeAtDestination, storageTimeSlot.getFromWhen());
    }

    public boolean setStoreAtDestination(StoreOperation storeAtDestination, RoutingTransportOperation rtop, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, LoadUnitJobSchedule s) {
        FieldElement newStartStore = this.getStartTime(this.getJ()).add(j.getDuration());
        FieldElement storageDuration = StorageToolMethods.getStorageDuration(newStartStore, rtop, ealosaes);
        if (storageDuration == null) {
            return false;
        }
        storeAtDestination.setDuration(storageDuration);
        for (Resource requieredResource : storeAtDestination.getRequieredResources()) {
            ScheduleRule r = s.getHandler().get(requieredResource);
            if (!r.canSchedule(s, storeAtDestination, newStartStore)) {
                return false;
            }
        }
        this.setStoreAtDestination(storeAtDestination, new TimeSlot(newStartStore, newStartStore.add(storeAtDestination.getDuration())));
        return true;
    }

    public TimeSlot getStorageTimeSlot() {
        return storageTimeSlot;
    }

}
