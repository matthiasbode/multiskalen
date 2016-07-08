/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.resources.storage.simpleStorage.rules;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.operations.SubOperations;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.storage.DefaultStoreOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.storage.SubStoreOperation;
import applications.transshipment.model.operations.storage.SuperStoreOperation;
import applications.transshipment.model.resources.conveyanceSystems.lcs.HandoverPoint;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSHandover;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import applications.transshipment.model.schedule.rules.StorageRule;
import java.util.ArrayList;
import java.util.List;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class RackMicroscopicRule implements StorageRule<LCSHandover> {

    private final LCSHandover resource;

    public RackMicroscopicRule(LCSHandover s, InstanceHandler handler) {
        this.resource = s;
    }

    @Override
    public LCSHandover getResource() {
        return resource;
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement start) {
        StoreOperation storeOp = (StoreOperation) o;
        for (StorageLocation storageLocation : resource.getStorageLocations()) {
            DefaultStoreOperation ssOp = new DefaultStoreOperation(storeOp.getLoadUnit(), storageLocation, storeOp.getDuration());
            if (!s.getHandler().get(storageLocation).canSchedule(s, ssOp, start)) {
                return false;
            }
        }
        return true;
    }



    @Override
    public List<StoreOperation> getPossibleStoreOperations(LoadUnit loadUnit, Schedule s, FieldElement start, FieldElement duration) {
        ArrayList<StoreOperation> result = new ArrayList<>();
        if (!resource.canHandleLoadUnit(loadUnit)) {
            return result;
        }

        for (SimpleStorageRow row : this.resource.getRows()) {

            //Wie viele StorageLocations brauchen wir, um diese LU zu behandeln?
            int need = (int) Math.ceil((loadUnit.getLength() + 2 * loadUnit.getLongitudinalDistance()) / row.getMinLocLen());
            /**
             * Der Fall bei angepassten SimpleSubStorageRows, extra für die
             * Anfrage
             */
            if (need == resource.getNumberOfStorageLocations()) {
                /**
                 * Test der einzelnen StorageLocations.
                 */
                SubOperations subOps = new SubOperations();
                for (StorageLocation storageLocation : resource.getStorageLocations()) {
                    StorageRule rule = (StorageRule) s.getHandler().get(storageLocation);
                    SubStoreOperation storeOp = new SubStoreOperation(loadUnit, storageLocation, duration);
                    subOps.put(storeOp);
                    if (!rule.canSchedule(s, storeOp, start)) {
                        return result;
                    }
                }
                /**
                 * Einplanen würde überall passen, erzeuge Operation
                 */
                SuperStoreOperation storeOp = new SuperStoreOperation(loadUnit, resource, subOps, duration);
                result.add(storeOp);
                return result;
            } /**
             * Erzeuge angepasste SubResource
             */
            else if (need < resource.getNumberOfStorageLocations()) {
                //set: Die Plaetze, fuer die die Operation erzeugt werden soll; tmp: Arbeitskopie aller Plaetze
                ArrayList<StorageLocation> set = new ArrayList<>();
                ArrayList<StorageLocation> tmp = new ArrayList<>(row.getStorageLocations());
                 
                //soviele Plaetze in set packen, wie wir brauchen (wird von links begonnen)
                for (int i = 0; i < need; i++) {
                    set.add(tmp.remove(0));
                }
                //erzeuge daraus ein SubStorage ...
                SimpleStorageRow.SimpleSubStorageRow sdssr = new SimpleStorageRow.SimpleSubStorageRow(set, row);
                HandoverPoint.SubHandoverPoint subHand = new HandoverPoint.SubHandoverPoint(resource, sdssr);
                //... und fuege dessen PosOps hinzu.
                StorageRule rule = (StorageRule) s.getHandler().get(subHand);
                result.addAll(rule.getPossibleStoreOperations(loadUnit, s, start, duration));
                while (!tmp.isEmpty()) {
                    set.add(tmp.remove(0));
                    set.remove(0);
                    sdssr = new SimpleStorageRow.SimpleSubStorageRow(set, row);
                    HandoverPoint.SubHandoverPoint subHand2 = new HandoverPoint.SubHandoverPoint(resource, sdssr);
                    StorageRule rule2 = (StorageRule) s.getHandler().get(subHand2);
                    result.addAll(rule2.getPossibleStoreOperations(loadUnit, s, start, duration));
                }
            }
        }

        return result;
    }

    @Override
    public void schedule(Operation o, Schedule s, FieldElement start) {
//        StoreOperation storeOp = (StoreOperation) o;
//        for (StorageLocation storageLocation : resource.getStorageLocations()) {
//            DefaultStoreOperation ssOp = new DefaultStoreOperation(storeOp.getLoadUnit(), storageLocation, storeOp.getDuration());
//            s.getHandler().get(storageLocation).schedule(ssOp, s, start);
//        }
    }

    @Override
    public void unSchedule(Operation o, Schedule s) {
//        SuperStoreOperation storeOp = (SuperStoreOperation) o;
//        for (Operation operation : storeOp.getSubOperations().getSubOperations()) {
//            StoreOperation stop = (StoreOperation) operation;
//            s.getHandler().get(stop.getResource()).unSchedule(stop, s);
//        }
    }

    @Override
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}

