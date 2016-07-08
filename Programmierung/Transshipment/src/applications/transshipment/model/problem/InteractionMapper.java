/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.problem;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceInteraction;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.DefaultConveyanceInteraction;
import applications.transshipment.model.resources.conveyanceSystems.lcs.HandoverPointInteractionRule;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSHandover;
import applications.transshipment.model.resources.storage.DefaultStorageInteraction;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.StorageInteraction;
import applications.transshipment.model.structs.Terminal;
import java.util.HashMap;

/**
 *
 * @author bode
 */
public class InteractionMapper {

    private HashMap<LoadUnitStorage, StorageInteraction> storageRules;
    private HashMap<ConveyanceSystem, ConveyanceInteraction> transportationRules;

    public InteractionMapper(Terminal terminal) {

        /**
         * InteractionRules
         */
        transportationRules = new HashMap<>();
        DefaultConveyanceInteraction defaultconveyanceSystemInteraction = new DefaultConveyanceInteraction();
        for (ConveyanceSystem conveyanceSystem : terminal.getConveyanceSystems()) {
            transportationRules.put(conveyanceSystem, defaultconveyanceSystemInteraction);
        }

        storageRules = new HashMap<>();
        DefaultStorageInteraction defaultStorageInteraction = new DefaultStorageInteraction();
        HandoverPointInteractionRule handoverPointInteractionRule = new HandoverPointInteractionRule();
        for (LoadUnitStorage loadUnitStorage : terminal.getStorages()) {
            if (loadUnitStorage instanceof LCSHandover) {
                storageRules.put(loadUnitStorage, handoverPointInteractionRule);
            } else {
                storageRules.put(loadUnitStorage, defaultStorageInteraction);
            }
        }

        storageRules.put(terminal.getDnfStorage(), defaultStorageInteraction);
    }

    
    public HashMap<LoadUnitStorage, StorageInteraction> getStorageRules() {
        return storageRules;
    }

  
    public HashMap<ConveyanceSystem, ConveyanceInteraction> getTransportationRules() {
        return transportationRules;
    }
}
