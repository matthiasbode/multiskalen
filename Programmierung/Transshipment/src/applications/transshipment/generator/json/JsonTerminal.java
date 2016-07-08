/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.json;

import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.conveyanceSystems.lcs.HandoverPoint;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.structs.RailroadTrack;
import applications.transshipment.model.structs.Terminal;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author behrensd
 */
public class JsonTerminal {

    private List<JsonTerminalResource> resources;

    public JsonTerminal(Terminal terminal) {
        // Convert Operating Area to path segments
        this.resources = new ArrayList<>();

        for (ConveyanceSystem cs : terminal.getConveyanceSystems()) {
            if (cs instanceof Crane) {
            }
        }

        for (RailroadTrack rrt : terminal.getGleise()) {
            this.resources.add(new JsonTerminalResource(JsonTerminalResource.KEY_RAILROADTRACK, JsonPathSegment.areaToPathSegments(rrt.getGeneralOperatingArea())));
        }

        for (SharedResource sr : terminal.getSharedResources()) {
            if (sr instanceof CraneRunway) {
                this.resources.add(new JsonTerminalResource(JsonTerminalResource.KEY_CRANERUNWAY, JsonPathSegment.areaToPathSegments(sr.getGeneralOperatingArea())));
            }

            if (sr instanceof LCSystem) {
                this.resources.add(new JsonTerminalResource(JsonTerminalResource.KEY_LCS, JsonPathSegment.areaToPathSegments(sr.getGeneralOperatingArea())));
            }
        }

        for (LoadUnitStorage lus : terminal.getStorages()) {
            /**
             * Handover-Points werden beim Einlesen erst wieder erzeugt.
             */
            if (lus instanceof HandoverPoint) {
                this.resources.add(new JsonTerminalResource(JsonTerminalResource.KEY_HANDOVER, JsonPathSegment.areaToPathSegments(lus.getGeneralOperatingArea())));
            } /**
             * Speicher lediglich Lagerbereiche ab.
             */
            else if (lus instanceof SimpleStorage) {
                SimpleStorage ss = (SimpleStorage) lus;
                for (SimpleStorageRow sr : ss.getRows()) {
                    this.resources.add(new JsonTerminalResource(JsonTerminalResource.KEY_STORAGEROW, JsonPathSegment.areaToPathSegments(sr.getGeneralOperatingArea())));
                }
            } 
            else if (lus instanceof SimpleStorageRow) {
                SimpleStorageRow sr = (SimpleStorageRow) lus;
                this.resources.add(new JsonTerminalResource(JsonTerminalResource.KEY_STORAGEROW, JsonPathSegment.areaToPathSegments(sr.getGeneralOperatingArea())));

            }

        }

    }

   

    public List<JsonTerminalResource> getResources() {
        return resources;
    }

    public void setResources(List<JsonTerminalResource> resources) {
        this.resources = resources;
    }

}
