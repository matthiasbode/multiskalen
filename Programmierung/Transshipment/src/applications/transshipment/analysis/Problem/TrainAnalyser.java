/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Problem;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.structs.Train;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;
import math.function.StepFunction;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import util.chart.FunctionPlotter;

/**
 *
 * @author Matthias
 */
public class TrainAnalyser {

    public static long lengthOfInterval = 2 * 60 * 1000L;

    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder, ImplicitOperationIndividual ind) {
        TimeSlot optimizationTimeSlot = problem.getOptimizationTimeSlot();
        int n = (int) (optimizationTimeSlot.getDuration().longValue() / lengthOfInterval);
        FieldElement start = optimizationTimeSlot.getFromWhen();
        TreeMap<FieldElement, FieldElement> numberOfTrainsInTerminal = new TreeMap<>();

        for (int i = 0; i < n; i++) {
            Long t = start.longValue() + (i * lengthOfInterval);
            int numberOfTrains = 0;
            for (Train train : problem.getTrains()) {
                if (train.getTemporalAvailability().getAllOverTimeSlot().contains(t)) {
                    numberOfTrains++;
                }
            }
            numberOfTrainsInTerminal.put(new LongValue(t), new DoubleValue(numberOfTrains));

        }
        StepFunction function = new StepFunction(numberOfTrainsInTerminal);
        File f = new File(folder, "Trains.png");
        JFreeChart chart = FunctionPlotter.createAreaDateChart(function, optimizationTimeSlot, null, "", "Zeit", "Anzahl an Zügen");
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(0, 80, 155));
        DateAxis domain = (DateAxis) plot.getDomainAxis();
        domain.setTickUnit(new DateTickUnit(DateTickUnitType.MINUTE, (int) (OperationPriorityRules.lengthOfInterval / 1000. / 60)));
        domain.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        domain.setVerticalTickLabels(true);

        if (ind != null) {
            String[] values = new String[ind.size()];
            for (int i = 0; i < ind.getChromosome().size(); i++) {
                OperationPriorityRules.Identifier identifier = ind.getChromosome().get(i);
                values[i] = identifier.toString();
            }
            SymbolAxis xAxis2 = new SymbolAxis("Prioritätsregeln", values);
            xAxis2.setVerticalTickLabels(true);

            plot.setDomainAxis(1, xAxis2);
            plot.mapDatasetToDomainAxis(1, 1);
        }

        try {
            ChartUtilities.saveChartAsPNG(f, chart, 1900, 600);
        } catch (IOException ex) {
            Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
