/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Schedule;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author bode
 */
public class TimePerOperation implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        ArrayList<Operation> arrayList = new ArrayList<>(schedule.getScheduledOperations());
        Collections.sort(arrayList, new Comparator<Operation>() {

            @Override
            public int compare(Operation o1, Operation o2) {
                return Long.compare(o1.getDuration().longValue(), o2.getDuration().longValue());
            }
        });
        for (Operation operation : arrayList) {
            if (operation instanceof TransportOperation) {
                System.out.println(operation + "\t:" + TimeSlot.longToFormattedDuration(operation.getDuration().longValue()));
            }
        }
    }

}
