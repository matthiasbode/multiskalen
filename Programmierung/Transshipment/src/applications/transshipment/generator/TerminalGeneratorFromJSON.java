package applications.transshipment.generator;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.transshipment.generator.json.JsonTerminal;
import applications.transshipment.generator.json.JsonTerminalResource;
import applications.transshipment.generator.projekte.ParameterInputFile;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.conveyanceSystems.lcs.HandoverPoint;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.RackGroup;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.structs.RailroadTrack;
import applications.transshipment.model.structs.Terminal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author behrensd
 */
public class TerminalGeneratorFromJSON {

    public static Terminal generateTerminal(File jsonFile, ParameterInputFile parameters) {
        try {
            FileInputStream stream = new FileInputStream(jsonFile);
            return generateTerminal(stream,parameters);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TerminalGeneratorFromJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Terminal generateTerminal(InputStream stream, ParameterInputFile parameters) {

        JsonTerminal jsonTerminal = JSONSerialisierung.importJSON(stream, JsonTerminal.class);

        ArrayList<ConveyanceSystem> conveyanceSystems = new ArrayList<>();
        ArrayList<LoadUnitStorage> storages = new ArrayList<>();
        ArrayList<RailroadTrack> gleise = new ArrayList<>();
        Collection<SharedResource> sharedResources = new ArrayList<>();
        SimpleStorage storage = new SimpleStorage();

        for (JsonTerminalResource res : jsonTerminal.getResources()) {
            switch (res.getBezeichnung()) {
                case JsonTerminalResource.KEY_RAILROADTRACK: {
                    gleise.add(new RailroadTrack(res.getArea().getBounds2D(), ""));
                    break;
                }
                case JsonTerminalResource.KEY_CRANERUNWAY: {
                    CraneRunway craneRunway = new CraneRunway(res.getArea().getBounds2D(), parameters.getNumberOfCranes());
                    conveyanceSystems.addAll(craneRunway.getSharingResources());
                    sharedResources.add(craneRunway);
                    break;
                }
                case JsonTerminalResource.KEY_LCS: {
                    LCSystem lcSystem = new LCSystem(res.getArea().getBounds2D(), parameters.getNumberOfAGVs());
                    List<HandoverPoint> handoverPointsForLCSystem = HandoverPointsGenerator.getHandoverPointsForLCSystem(res.getArea().getBounds2D(), parameters);
                    RackGroup group = new RackGroup(handoverPointsForLCSystem);
                    lcSystem.setHandoverPoints(group);
                    storages.addAll(handoverPointsForLCSystem);
                    conveyanceSystems.add(lcSystem);
                    sharedResources.add(lcSystem);
                    break;
                }
                case JsonTerminalResource.KEY_STORAGEROW: {
                    storage.addStorageRow(new SimpleStorageRow(res.getArea().getBounds2D()));
                    break;
                }

            }

        }

        storages.add(storage);

        Terminal terminal = new Terminal(TimeSlot.create(parameters.getStart().getTimeInMillis(), parameters.getEnde().getTimeInMillis()), conveyanceSystems, storages, gleise, sharedResources, storage);

        return terminal;

    }

}
