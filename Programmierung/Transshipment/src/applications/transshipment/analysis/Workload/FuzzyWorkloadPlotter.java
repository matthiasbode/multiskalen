/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.PSPLib.demo.CalcPSPLib;
import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.operation.FuzzyOperation;
import applications.fuzzy.operation.BetaOperation;
import applications.fuzzy.plotter.FuzzyFunctionPlotter;
import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyDemandUtilities;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.transshipment.analysis.Analysis;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import bijava.math.function.ScalarFunction1d;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.interval.FuzzyInterval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.input.SingleModeFuzzyFile;

/**
 *
 * @author bode Auslastungsplotter für die einzelnen Ressourcen über
 * Zeitintervalle
 */
public class FuzzyWorkloadPlotter implements Analysis {

    public static long dx = 2 * 1000;

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {

        TimeSlot allOverTimeSlot = problem.getOptimizationTimeSlot();

        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (conveyanceSystem instanceof LCSystem) {
                LCSystem lcSystem = (LCSystem) conveyanceSystem;
                for (Agent agent : lcSystem.getSharingResources()) {
                    File f = new File(folder, "Detail_Agent" + agent.toString() + ".png");
                    getPlot(agent, allOverTimeSlot, schedule, problem, f);

                }
            }
            File f = new File(folder, "Detail_" + conveyanceSystem.toString() + ".png");
            getSinglePlot(conveyanceSystem, allOverTimeSlot, schedule, problem, f);
            f = new File(folder, "DetailGesamt_" + conveyanceSystem.toString() + ".png");
            getPlot(conveyanceSystem, allOverTimeSlot, schedule, problem, f);

        }
    }

    public void getSinglePlot(ConveyanceSystem conveyanceSystem, TimeSlot allOverTimeSlot, Schedule schedule, TerminalProblem problem, File f) {
        applications.fuzzy.plotter.FuzzyFunctionPlotter workloadplotter = new applications.fuzzy.plotter.FuzzyFunctionPlotter("Auslastung der Ressource '" + conveyanceSystem);
        if (schedule == null) {
            return;
        }
        ScheduleRule scheduleRule = schedule.getHandler().get(conveyanceSystem);
        if (scheduleRule == null) {
            return;
        }
        FuzzyNumber fuzStart = (FuzzyNumber) problem.getOptimizationTimeSlot().getFromWhen();
        FuzzyNumber fuzEnde = (FuzzyNumber) problem.getOptimizationTimeSlot().getUntilWhen();

        ArrayList<Operation> list = new ArrayList<>(schedule.getOperationsForResource(conveyanceSystem));
        Collections.sort(list, new Comparator<Operation>() {

            @Override
            public int compare(Operation o1, Operation o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });
        for (Operation operation : list) {
            if (!(operation instanceof BetaOperation)) {
                System.out.println(operation);
            }
            BetaOperation ofuz = (BetaOperation) operation;
            FuzzyInterval fuzStartOp = (FuzzyInterval) schedule.getStartTimes().get(operation);
            workloadplotter.addFunction(FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(ofuz, conveyanceSystem, fuzStartOp, schedule.fuzzyWorkloadParameters.get(operation)), fuzStart.getMean(), fuzEnde.getMean(), dx, "Auslastung durch Vorgang " + ofuz.getId());
        }
        ScalarFunctionBasedRule sr = (ScalarFunctionBasedRule) scheduleRule;

//            workloadplotter.plot(1200, 425, problem.getOptimizationTimeSlot(), false);
        JFreeChart aC1 = workloadplotter.getAreaChart(allOverTimeSlot, sr.getMax()+0.25);
        aC1.removeLegend();
        try {
            ChartUtilities.saveChartAsPNG(f, aC1,2200, 435);
        } catch (IOException ex) {
            Logger.getLogger(CalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getPlot(ConveyanceSystem conveyanceSystem, TimeSlot allOverTimeSlot, Schedule schedule, TerminalProblem problem, File f) {
        if (schedule == null) {
            return;
        }
        ScheduleRule scheduleRule = schedule.getHandler().get(conveyanceSystem);
        if (scheduleRule == null) {
            return;
        }
        if (scheduleRule instanceof ScalarFunctionBasedRule) {
            ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

            ScalarFunction1d workloadFunction = sfb.getWorkloadFunction();
            
            System.out.println(workloadFunction);
            if (workloadFunction instanceof LinearizedFunction1d) {
                LinearizedFunction1d func = (LinearizedFunction1d) workloadFunction;
                FuzzyFunctionPlotter plotter = new FuzzyFunctionPlotter("Gesamtauslastung der Ressource '" + conveyanceSystem);
                plotter.addFunction(func, allOverTimeSlot.getFromWhen().longValue(), allOverTimeSlot.getUntilWhen().longValue(), dx, "Gesamtauslastung");
                JFreeChart createChart = plotter.getAreaChart(allOverTimeSlot, sfb.getMax()+0.25);
                try {
                    ChartUtilities.saveChartAsPNG(f, createChart, 2200, 435);
                } catch (IOException ex) {
                    Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
