/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Validation;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.basics.util.TransshipmentEALOSAEBuilder;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.File;
import java.util.Map;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class TimeWindowTest implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        Map<LoadUnitJob, TimeSlot> calcJobTimeWindows = TransshipmentEALOSAEBuilder.calcJobTimeWindows(problem);
        for (LoadUnitJob loadUnitJob : problem.getJobs()) {
            TimeSlot timeWindow = calcJobTimeWindows.get(loadUnitJob);
            LoadUnitPositions operationsForLoadUnit = schedule.getOperationsForLoadUnit(loadUnitJob.getLoadUnit());
            for (LoadUnitOperation loadUnitOperation : operationsForLoadUnit) {
                FieldElement startTime = schedule.get(loadUnitOperation);
                if (timeWindow.getFromWhen().isGreaterThan(startTime)) {
                    System.err.println("Fehlerhafte Einplanung:");
                    System.err.println("loadUnitoperation: " + loadUnitOperation);
                    System.err.println(timeWindow);
                    System.err.println(TimeSlot.longToFormattedDateString(startTime.longValue()));

//                    throw new IllegalArgumentException("Falsche eingeplant!");
                }
            }

        }

    }

}
