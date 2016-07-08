/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.DNF;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.DoubleValue;
import math.FieldElement;
import math.function.StepFunction;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.chart.FunctionPlotter;

/**
 *
 * @author Matthias
 */
public class DNF_Occurrence implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        TimeSlot allOverTimeSlot = problem.getTerminal().getTemporalAvailability().getAllOverTimeSlot();
        StepFunction function = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));

        for (LoadUnitJob loadUnitJob : schedule.getDnfJobs()) {
            FieldElement untilWhen = loadUnitJob.getDestination().getTemporalAvailability().getUntilWhen();
            StepFunction f = new StepFunction(untilWhen, allOverTimeSlot.getUntilWhen(), new DoubleValue(1.0));
            function = function.add(f);
        }
        
        
        File f = new File(folder, "DNFs.png");
        JFreeChart createChart = FunctionPlotter.createAreaDateChart(function, problem.getOptimizationTimeSlot(), "DNFs");
        //JFreeChart createChart = FunctionPlotter.createBucketChart(function, problem.getOptimizationTimeSlot(), "Verfügbare Ladeeinheiten im Bahnhof", lengthOfInterval);

        try {
            ChartUtilities.saveChartAsPNG(f, createChart, 1900, 1000);
        } catch (IOException ex) {
            Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
