/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.EALOSAE;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;
import math.function.StepFunction;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.chart.FunctionPlotter;

/**
 *
 * @author Bode
 */
public class EALOSAEPlotter {

    public static void plot(MultiJobTerminalProblem problem, File folder, final Map<? extends Operation, EarliestAndLatestStartsAndEnds> ealosaes) {
        TimeSlot optimizationTimeSlot = problem.getOptimizationTimeSlot();

        StepFunction function = new StepFunction(optimizationTimeSlot.getFromWhen(), optimizationTimeSlot.getUntilWhen(), new DoubleValue(0.0));

        for (Operation operation : ealosaes.keySet()) {
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(operation);
            FieldElement start = ealosae.getEarliestStart();
            FieldElement end = ealosae.getLatestEnd();
            StepFunction sf = new StepFunction(new LongValue(start.longValue()), new LongValue(end.longValue()), new DoubleValue(1.0));
            function = function.add(sf);
        }
        JFreeChart chart = FunctionPlotter.createAreaDateChart(function, optimizationTimeSlot, "Verfügbarkeit");
        File f = new File(folder, "EALOSAE.png");
        try {
            ChartUtilities.saveChartAsPNG(f, chart, 1900, 600);
        } catch (IOException ex) {
            Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
