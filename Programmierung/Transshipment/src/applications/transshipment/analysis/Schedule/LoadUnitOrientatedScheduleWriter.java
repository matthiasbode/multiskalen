/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Schedule;

import applications.mmrcsp.model.basics.JoNComponent;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.problem.MultiJobTerminalProblem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author bode
 */
public class LoadUnitOrientatedScheduleWriter implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule s, MultiJobTerminalProblem problem, File folder) {
        /**
         * Ausgabe in Datei
         */
        if (!(s instanceof LoadUnitJobSchedule)) {
            return;
        }
        LoadUnitJobSchedule jujs = (LoadUnitJobSchedule) s;
        try {
            File f = new File(folder, "LoadUnitPlan.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("Eingeplante Operationen per Job");
            bw.newLine();
            bw.write("#########################");
            bw.newLine();

            for (LoadUnitJob loadUnitJob : problem.getJobs()) {
                bw.write(loadUnitJob.toString());
                bw.newLine();

//                if (s.routingsPerJob != null) {
//                    JobOperationList<RoutingTransportOperation> list = s.routingsPerJob.get(loadUnitJob);
//                    if (list == null) {
//                        bw.write("\tKeine Route hinterlegt!");
//                        bw.newLine();
//                    } else {
//                        for (RoutingTransportOperation routingTransportOperation : list) {
//                            bw.write("\t" + routingTransportOperation.toString());
//                            bw.newLine();
//                        }
//                    }
//
//                    bw.newLine();
//                }
                LoadUnitPositions operationsForLoadUnit = jujs.getOperationsForLoadUnit(loadUnitJob.getLoadUnit());
                for (LoadUnitOperation loadUnitOperation : operationsForLoadUnit) {
                    String append = "";
                    if (loadUnitOperation instanceof MultiScaleTransportOperation) {
                        MultiScaleTransportOperation mso = (MultiScaleTransportOperation) loadUnitOperation;
                        append = "RoutingTransportOperation " + mso.getRoutingTransportOperation().getId();
                    }
                    bw.write("\t" + TimeSlot.longToFormattedDateString(operationsForLoadUnit.startTimes.get(loadUnitOperation).longValue()) + "\t" + TimeSlot.longToFormattedDateString(operationsForLoadUnit.startTimes.get(loadUnitOperation).add(loadUnitOperation.getDuration()).longValue()) + "\t" + loadUnitOperation.toString() + "\t" + append);
                    bw.newLine();
                }
                bw.write("-----------------");
                bw.newLine();
                bw.newLine();
            }

            bw.write("#########################");
            bw.newLine();
            bw.write("Nicht Eingeplante Operationen");
            bw.newLine();

            for (LoadUnitJob loadUnitJob : jujs.getDnfJobs()) {
                int connectionComponent = Integer.MAX_VALUE;
                for (JoNComponent<LoadUnitJob> object : problem.getJobOnNodeDiagramm().getConnectionComponents()) {
                    if (object.vertexSet().contains(loadUnitJob)) {
                        connectionComponent = object.getNumber();
                    }
                    break;
                }
                bw.write(Integer.toString(connectionComponent) + " : " + loadUnitJob.toString());
                bw.newLine();
//                bw.write("Von nach: " + loadUnitJob.getOrigin() + "/" + loadUnitJob.getDestination());
//                bw.newLine();
            }

            bw.close();
            fw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

    }
}
