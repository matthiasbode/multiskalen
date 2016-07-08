/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Problem;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
public class LoadUnitAvailableTimes implements Analysis {

    public static long lengthOfInterval = 10 * 60 * 1000;

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        TimeSlot allOverTimeSlot = problem.getTerminal().getTemporalAvailability().getAllOverTimeSlot();
        StepFunction function = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));


        for (LoadUnitJob job : problem.getJobTimeWindows().keySet()) {
            if(problem.getStammRelation().contains(job)){
                continue;
            }
            TimeSlot ts = problem.getJobTimeWindows().get(job);
            FieldElement fromWhen = ts.getFromWhen();
            FieldElement untilWhen = ts.getUntilWhen();
            StepFunction stepFunction = new StepFunction(fromWhen, untilWhen, new DoubleValue(1.0));
            function = function.add(stepFunction);
        }

//        TimeSlot allOverTimeSlot = problem.getTerminal().getTemporalAvailability().getAllOverTimeSlot();
//        LinearizedFunction1d function = new LinearizedFunction1d(allOverTimeSlot.getFromWhen().doubleValue(), allOverTimeSlot.getUntilWhen().doubleValue(), new DoubleValue(0.0));
//
//        for (LoadUnitJob job : problem.getJobTimeWindows().keySet()) {
//            if (problem.getStammRelation().contains(job)) {
//                continue;
//            }
//            TimeSlot ts = problem.getJobTimeWindows().get(job);
//            FieldElement fromWhen = ts.getFromWhen();
//            FieldElement untilWhen = ts.getUntilWhen();
//            TreeMap<Double, Double> map = new TreeMap<>();
//            map.put(fromWhen.doubleValue(), 0.0);
//            map.put(untilWhen.doubleValue(), 1.0);
//            LinearizedFunction1d f = new LinearizedFunction1d(map);
//            function = function.add(f);
//        }

        StepFunction dnffunction = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));

        for (LoadUnitJob loadUnitJob : schedule.getDnfJobs()) {
            FieldElement untilWhen = loadUnitJob.getDestination().getTemporalAvailability().getUntilWhen();
            StepFunction df = new StepFunction(untilWhen, allOverTimeSlot.getUntilWhen(), new DoubleValue(1.0));
            dnffunction = dnffunction.add(df);
        }

        Map<String, StepFunction> functions = new HashMap<>();
        functions.put("Verfügbare Ladeeinheiten im Bahnhof", function);
        functions.put("DNF", dnffunction);
        
        //JFreeChart createChart = FunctionPlotter.createDateChart(functions, problem.getOptimizationTimeSlot(), "Verfügbare Ladeeinheiten im Bahnhof vs. DNF");
        //JFreeChart createChart = FunctionPlotter.createBucketChart(function, problem.getOptimizationTimeSlot(), "Verfügbare Ladeeinheiten im Bahnhof", lengthOfInterval);

//        File f = new File(folder, "LUAvailability.png");
//
//        try {
//            ChartUtilities.saveChartAsPNG(f, createChart, 1900, 1000);
//        } catch (IOException ex) {
//            Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

}
