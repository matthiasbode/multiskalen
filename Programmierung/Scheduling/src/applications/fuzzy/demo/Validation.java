/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.demo;

import applications.fuzzy.operation.BetaOperation;
import applications.fuzzy.operation.FuzzyOperation;
import applications.fuzzy.plotter.FuzzyFunctionPlotter;
import applications.fuzzy.scheduling.DefaultFuzzyScheduleRulesBuilder;
import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyDemandUtilities;
import applications.fuzzy.scheduling.rules.FuzzyFunctionBasedRule;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.DefaultSchedulingProblem;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.ResourceImplementation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.scheduleSchemes.ParallelScheduleScheme;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import fuzzy.number.discrete.interval.FuzzyInterval;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.DoubleValue;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.util.Pair;
import util.workspace.ProjectOutput;

/**
 *
 * @author Matthias
 */
public class Validation {

    public static long dx = 2 * 1000;
    static double pesLevel = 0.5;
    static int height = 500;
    static int width = 1200;

    public static void main(String[] args) {
        File folder = ProjectOutput.create();
        Date d = new Date(2015 - 1900, 4, 28, 0, 0);
        DiscretizedFuzzyInterval start = FuzzyFactory.createLinearInterval(d.getTime() + 1 * 60 * 1000, d.getTime() + 3 * 60 * 1000, 1 * 60 * 1000, 1 * 60 * 1000);

        TimeSlot slot = new TimeSlot(start, start.add(FuzzyFactory.createCrispValue(80 * 60 * 1000)));

        ArrayList<Resource> resources = new ArrayList<>();
        Resource r1 = new ResourceImplementation("Ressource 1");
        resources.add(r1);
        r1.setTemporalAvailability(slot);

        DiscretizedFuzzyInterval duration1 = FuzzyFactory.createLinearInterval(10 * 60 * 1000, 1 * 60 * 1000);
        FuzzyOperation o1 = new FuzzyOperation(duration1, pesLevel);
        o1.setDemand(r1, new DoubleValue(1.0));

        DiscretizedFuzzyInterval duration2 = FuzzyFactory.createLinearInterval(14 * 60 * 1000, 2 * 60 * 1000);
        FuzzyOperation o2 = new FuzzyOperation(duration2, pesLevel);
        o2.setDemand(r1, new DoubleValue(1.0));

        DiscretizedFuzzyInterval duration3 = FuzzyFactory.createLinearInterval(15 * 60 * 1000, 3 * 60 * 1000);
        FuzzyOperation o3 = new FuzzyOperation(duration3, pesLevel);
        o3.setDemand(r1, new DoubleValue(1.0));

        DiscretizedFuzzyInterval duration4 = FuzzyFactory.createLinearInterval(8 * 60 * 1000, 2 * 60 * 1000);
        FuzzyOperation o4 = new FuzzyOperation(duration4, pesLevel);
        o4.setDemand(r1, new DoubleValue(1.0));

        /**
         * Einplanregeln für die Ressourcen festlegen
         */
        DefaultFuzzyScheduleRulesBuilder builder = new DefaultFuzzyScheduleRulesBuilder();
        builder.put(r1, 1.0);

        /**
         * Eigentliches Erzeugen.
         */
        ParallelScheduleScheme ss = new ParallelScheduleScheme();
        ArrayList<Operation> arrayList = new ArrayList<>();
        arrayList.add(o1);
        arrayList.add(o2);
        arrayList.add(o3);
        arrayList.add(o4);
        ActivityOnNodeGraph<Operation> aon = new ActivityOnNodeGraph<>();
        aon.addVertex(o1);
        aon.addVertex(o2);
        aon.addVertex(o3);
        aon.addVertex(o4);

        aon.addEdge(new Pair<>(o1, o2));
        aon.addEdge(new Pair<>(o2, o3));
        aon.addEdge(new Pair<>(o3, o4));
        

        SchedulingProblem<Operation> problem = new DefaultSchedulingProblem(slot, arrayList, resources, builder, aon);
        Schedule result = ss.getSchedule(arrayList, problem, slot);
    
  

        FuzzyNumber fuzStart = (FuzzyNumber) problem.getOptimizationTimeSlot().getFromWhen();
        FuzzyNumber fuzEnde = (FuzzyNumber) problem.getOptimizationTimeSlot().getUntilWhen();

        for (Resource resource : resources) {
            ScheduleRule scheduleRule = result.getHandler().get(resource);
            if (scheduleRule instanceof FuzzyFunctionBasedRule) {
                FuzzyFunctionBasedRule sfb = (FuzzyFunctionBasedRule) scheduleRule;
                FuzzyFunctionPlotter plotter = new FuzzyFunctionPlotter("Gesamtauslastung der Ressource '" + resource);
                plotter.addFunction(sfb.getWorkloadFunction(), start.getC1(), slot.getUntilWhen().longValue(), dx, "Gesamtauslastung");
                JFreeChart createChart = plotter.getAreaChart(slot, sfb.getMax().doubleValue() + 0.25);
                ChartFrame f = new ChartFrame("Vis", createChart);
                try {
                    ChartUtilities.saveChartAsPNG(new File(folder, "Auslastung.png"), createChart, width, height);
                } catch (IOException ex) {
                    Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
                }
                f.setSize(800, 600);
                f.setVisible(true);

                ArrayList<Operation> list = new ArrayList<>(result.getOperationsForResource(resource));
                Collections.sort(list, new Comparator<Operation>() {

                    @Override
                    public int compare(Operation o1, Operation o2) {
                        return Integer.compare(o1.getId(), o2.getId());
                    }
                });
                FuzzyFunctionPlotter plotterForOp = new FuzzyFunctionPlotter("Gesamtauslastung der Ressource '" + resource);
                FuzzyFunctionPlotter plotterForStart = new FuzzyFunctionPlotter("Startzeiten der Ressource'" + resource);

                for (Operation operation : list) {
                    if (!(operation instanceof BetaOperation)) {
                        System.out.println(operation);
                    }
                    BetaOperation ofuz = (BetaOperation) operation;
                    FuzzyInterval fuzStartOp = (FuzzyInterval) result.getStartTimes().get(operation);
                    plotterForOp.addFunction(FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(ofuz, resource, fuzStartOp, result.fuzzyWorkloadParameters.get(operation)), fuzStart.getC1(), fuzEnde.getC2(), dx, "Auslastung durch Vorgang " + ofuz.getId());
                    plotterForStart.addFunction(fuzStartOp.membership, fuzStart.getC1(), fuzEnde.getC2(), dx, "Start der Operation " + ofuz.getId());
                    FuzzyInterval duration = (FuzzyInterval) ofuz.getDuration();

                    plotterForStart.addFunction(fuzStartOp.add(duration).membership, fuzStart.getC1(), fuzEnde.getC2(), dx, "Ende der Operation " + ofuz.getId());

                    FuzzyFunctionPlotter plotterForNotwendigkeit = new FuzzyFunctionPlotter("Notwendigkeit der Operation " + ofuz.getId());
                    plotterForNotwendigkeit.addFunction(FuzzyDemandUtilities.getNecessityFunction(ofuz, fuzStartOp), fuzStart.getC1(), fuzEnde.getC2(), dx, "Notwendigkeit der Operation " + ofuz.getId());

                    JFreeChart c = plotterForNotwendigkeit.getChart(slot, sfb.getMax().doubleValue() + 0.25);
                    try {
                        ChartUtilities.saveChartAsPNG(new File(folder, "Notwendigkeit" + operation.getId() + ".png"), c,width, height);
                    } catch (IOException ex) {
                        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ChartFrame fa = new ChartFrame("Vis", c);
                    fa.setSize(800, 600);
                    fa.setVisible(true);
                }
                JFreeChart c = plotterForOp.getAreaChart(slot, sfb.getMax().doubleValue() + 0.25);
                ChartFrame fa = new ChartFrame("Vis", c);
                fa.setSize(800, 600);
                fa.setVisible(true);

                try {
                    ChartUtilities.saveChartAsPNG(new File(folder, "Auslastung" + resource.getID() + ".png"), c, width, height);
                } catch (IOException ex) {
                    Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
                }

                JFreeChart cStart = plotterForStart.getAreaChart(slot, sfb.getMax().doubleValue() + 0.25);
                ChartFrame faStart = new ChartFrame("Vis", cStart);
                try {
                    ChartUtilities.saveChartAsPNG(new File(folder, "Start" + resource.getID() + ".png"), cStart,width, height);
                } catch (IOException ex) {
                    Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
                }
                faStart.setSize(800, 600);
                faStart.setVisible(true);
            }

        }
    }
}
