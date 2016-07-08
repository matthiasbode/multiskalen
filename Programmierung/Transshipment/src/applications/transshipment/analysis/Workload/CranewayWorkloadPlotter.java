/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;

import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.lattice.CellResource2D;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.lattice.LatticeManager;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.ga.TransshipmentSuperIndividual;
import bijava.math.function.ScalarFunction1d;
import util.bucketing.BucketPlotter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.function.StepFunction;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.bucketing.Bucketing;
import util.chart.FunctionPlotter;

/**
 *
 * @author bode
 */
public class CranewayWorkloadPlotter implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {

        TimeSlot allOverTimeSlot = problem.getTerminal().getTemporalAvailability().getAllOverTimeSlot();
        /**
         * Ausgabe des Cranerunways
         */
        CraneRunway craneRunway = null;
        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (conveyanceSystem instanceof Crane) {
                craneRunway = ((Crane) conveyanceSystem).getCraneRunway();
                break;
            }
        }
        int i = 0;
        LatticeManager<CraneRunway> manager = (LatticeManager<CraneRunway>) schedule.getHandler().getSharedManager(craneRunway);
        for (CellResource2D cellResource2D : manager.getGrid().getCells()) {
            File f = new File(folder, "Zelle" + (i) + ".png");
            ScheduleRule scheduleRule = schedule.getHandler().get(cellResource2D);

            if (scheduleRule instanceof ScalarFunctionBasedRule) {
                ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

                ScalarFunction1d sf = sfb.getWorkloadFunction();
                if (sf == null) {
                    continue;
                }
                if (sf instanceof StepFunction) {
                    StepFunction workloadFunction = (StepFunction) sf;
                    JFreeChart createChart = FunctionPlotter.createAreaDateChart(workloadFunction, allOverTimeSlot, "Für Resource " + cellResource2D.toString());
                    try {
                        ChartUtilities.saveChartAsPNG(f, createChart, 1900, 1000);
                    } catch (IOException ex) {
                        Logger.getLogger(CranewayWorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    f = new File(folder, "Bucketing: Zelle" + (i++) + ".png");
                    JFreeChart bucketChart = BucketPlotter.createChart(new Bucketing(cellResource2D, workloadFunction, allOverTimeSlot, 10 * 60 * 1000), allOverTimeSlot, "Für Resource " + cellResource2D.toString(), 1.);
                    try {
                        ChartUtilities.saveChartAsPNG(f, bucketChart, 1900, 1000);
                    } catch (IOException ex) {
                        Logger.getLogger(CranewayWorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
