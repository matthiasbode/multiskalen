/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Schedule;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import fuzzy.number.discrete.interval.FuzzyInterval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class ScheduleWriter implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule s, MultiJobTerminalProblem problem, File folder) {
        /**
         * Ausgabe in Datei
         */
        if (!(s instanceof LoadUnitJobSchedule)) {
            return;
        }

        try {
            File f = new File(folder, "Schedule.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("Schedule");
            bw.newLine();
            bw.write("#########################");
            bw.newLine();
            bw.newLine();
            bw.newLine();
            List<Resource> resources = new ArrayList<>(s.getResources());
            Collections.sort(resources, new Comparator<Resource>() {
                @Override
                public int compare(Resource o1, Resource o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

            for (Resource resource : resources) {
                bw.write(resource.toString());
                bw.newLine();
                bw.write("----------------------------------");
                bw.newLine();
                for (Operation operation : s.getOperationsForResource(resource)) {
                    FieldElement start = s.getStartTimes().get(operation);

                    bw.write(TimeSlot.longToFormattedDateString(start.longValue()).toString());
                    bw.write(" - ");
                    bw.write(TimeSlot.longToFormattedDateString(start.add(operation.getDuration()).longValue()).toString());

                    bw.write("\t :");
                    bw.write(operation.toString());
                    if (operation instanceof MultiScaleTransportOperation) {
                        MultiScaleTransportOperation mst = (MultiScaleTransportOperation) operation;
                        bw.write("\t" + mst.getRoutingTransportOperation().getId());
                    }
                    if (start instanceof FuzzyInterval) {
                        bw.newLine();
                        FuzzyInterval fuzStart = (FuzzyInterval) start;
                        FuzzyInterval fuzEnd = (FuzzyInterval) start.add(operation.getDuration());
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzStart.getC1()).toString());
                        bw.write(" - ");
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzEnd.getC1()).toString());
                        bw.newLine();
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzStart.getC2()).toString());
                        bw.write(" - ");
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzEnd.getC2()).toString());
                    }

                    bw.newLine();
                }
                bw.newLine();
                bw.newLine();
                bw.write("####################################");
                bw.newLine();

            }

            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.write("####################################");
            bw.newLine();
            bw.write("DNF-Jobs");
            bw.newLine();
            for (LoadUnitJob dnfJob : s.getDnfJobs()) {
                bw.write(dnfJob.toString());
                bw.newLine();
            }

            bw.close();
            fw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

    }
}
