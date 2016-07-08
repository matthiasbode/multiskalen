/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator;

import applications.transshipment.generator.xml.ExportLU;
import applications.transshipment.generator.xml.ExportLUPlan;
import applications.transshipment.generator.xml.ExportSlot;
import applications.transshipment.generator.xml.ExportStorage;
import applications.transshipment.generator.xml.ExportTrain;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.loadunits.Swapbody;
import applications.transshipment.model.loadunits.TwistLockLoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.xmlTools.XMLSerialisierung;

/**
 *
 * @author bode
 */
public class LoadUnitGeneratorFromXML {
    
    @Deprecated
    public static List<LoadUnitJob> generateJobs(List<Train> trains, Terminal terminal, File file) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
            return generateJobs(trains, terminal, stream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadUnitGeneratorFromXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Deprecated
    public static List<LoadUnitJob> generateJobs(List<Train> trains, Terminal terminal, InputStream stream) {
        ExportLUPlan luPlan = XMLSerialisierung.importXML(stream, ExportLUPlan.class, null);
        if (luPlan == null) {
            throw new NullPointerException("Datei " + stream + " konnte nicht eingelesen werden");
        }
        LoadUnitStorage lStorage = terminal.getStorages().iterator().next();

        ArrayList<LoadUnitJob> jobs = new ArrayList<>();
        for (ExportLU exportLU : luPlan.lus) {
            LoadUnit lu;
            if ("TwistLockLoadUnit".equals(exportLU.Typ)) {
                lu = new TwistLockLoadUnit(exportLU.length, exportLU.hazardous);
            } else {
                lu = new Swapbody(exportLU.length, exportLU.hazardous);
            }
            lu.setId(exportLU.id);
            if (exportLU.origin instanceof ExportSlot) {
                ExportSlot eSlot = (ExportSlot) exportLU.origin;
                ExportTrain et = eSlot.train;
                Slot slot = trains.get(et.number).getStorageLocations().get(eSlot.number);
                if (slot == null) {
                    throw new NullPointerException();
                }
                lu.setOrigin(slot);
            }
            if (exportLU.destination instanceof ExportSlot) {
                ExportSlot eSlot = (ExportSlot) exportLU.destination;
                ExportTrain et = eSlot.train;
                Slot slot = trains.get(et.number).getStorageLocations().get(eSlot.number);
                if (slot == null) {
                    throw new NullPointerException();
                }
                lu.setDestination(slot);
            }
            if (exportLU.origin instanceof ExportStorage) {
                lu.setOrigin(lStorage);
            }
            if (exportLU.destination instanceof ExportStorage) {
                lu.setDestination(lStorage);
            }
            jobs.add(new LoadUnitJob(lu));

        }
        return jobs;
    }
}
