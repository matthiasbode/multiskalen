/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.RobustTests;

import applications.PSPLib.demo.MinMakeSpanEval;
import applications.fuzzy.PSPLib.FuzzyCalcPSPLib;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.ga.priority.StandardPriorityDeterminator;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.timeRestricted.DefaultTimeRestictedSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.scheduleSchemes.ParallelScheduleScheme;
import applications.mmrcsp.model.schedule.test.OrderValid;
import com.google.common.collect.TreeMultimap;
import com.google.gson.reflect.TypeToken;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import math.function.StepFunction;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.chart.FunctionPlotter;
import util.chart.TopGantt;
import util.input.SingleModeFile;
import util.jsonTools.JSONSerialisierung;
import util.workspace.ProjectOutput;

/**
 *
 * @author bode
 */
public class DeprecatedTestCalcPSPLib {

    public static int numberOfInd = 1;
    public static int GENERATIONS = 1;

    public static TreeMultimap<Integer, Operation> order;

    static DefaultTimeRestictedSchedulingProblem<Operation> problem;

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = FuzzyCalcPSPLib.class.getResourceAsStream("../../PSPLib/30/j305_4.sm");
        FileInputStream individuum = new FileInputStream("/home/bode/Dokumente/Promo/Analyse/Fuzzy_Analyse/Crisp30_05_04_63/Individidual.txt");
        run(stream, individuum, true);

    }

    public static void run(InputStream stream, FileInputStream individuum, boolean analysis) throws FileNotFoundException {
        File folder = ProjectOutput.create();
        if (stream == null) {
            final JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            try {
                stream = new FileInputStream(fc.getSelectedFile());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FuzzyCalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        SingleModeFile smf = new SingleModeFile(stream);
        /**
         * Problem
         */
        problem = smf.getScheduleFromFile();

        System.out.println("TempAvail:" + problem.getOptimizationTimeSlot().getDuration().longValue());

        Type listType = new TypeToken<ArrayList<ArrayList<Integer>>>() {
        }.getType();
        ArrayList<ArrayList<Integer>> importList = JSONSerialisierung.importJSON(individuum, listType);

        ListIndividual<Operation> fittestIndividual = new ListIndividual<>();
        for (ArrayList<Integer> topoClass : importList) {
            SubListIndividual<Operation> subInd = new SubListIndividual<>();
            for (Integer opID : topoClass) {
                for (Operation operation : problem.getOperations()) {
                    if (opID.equals(operation.getId())) {
                        subInd.getChromosome().add(operation);
                        break;
                    }
                }
            }
            fittestIndividual.getChromosome().add(subInd);
        }

        /**
         * Initialisiere ScheduleScheme
         */
        ParallelScheduleScheme sgs = new ParallelScheduleScheme();
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        MinMakeSpanEval eval = new MinMakeSpanEval(sgs, problem, priorityDeterminator, problem.getOptimizationTimeSlot());
        fittestIndividual.setFitness(eval.computeFitness(fittestIndividual));

        System.out.println("Fitness:" + fittestIndividual.getFitness());
        Schedule schedule = eval.getSchedule(fittestIndividual);
        OrderValid.testSchedule(problem, schedule);
        order = sgs.order;

        if (analysis) {
            for (Resource resource : problem.getResources()) {
                System.out.println("Resource: " + resource);
                ArrayList<Operation> arrayList = new ArrayList<>(schedule.getOperationsForResource(resource));
                Collections.sort(arrayList, new Comparator<Operation>() {

                    @Override
                    public int compare(Operation o1, Operation o2) {
                        return Integer.compare(o1.getId(), o2.getId());
                    }
                }
                );
                for (Operation operation : arrayList) {
                    System.out.println(operation + " ---> " + schedule.get(operation));
                }

                ScheduleRule scheduleRule = schedule.getHandler().get(resource);
                if (scheduleRule instanceof ScalarFunctionBasedRule) {
                    ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

                    JFreeChart createChart = FunctionPlotter.createAreaDateChart((StepFunction)sfb.getWorkloadFunction(), problem.getOptimizationTimeSlot(), "Für Resource " + resource.toString());

                    try {
                        ChartUtilities.saveChartAsPNG(new File(folder, resource.toString() + ".png"), createChart, 1200, 435);
                    } catch (IOException ex) {
                        Logger.getLogger(DeprecatedTestCalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    JFrame f = new JFrame();
                    f.setSize(1200, 425);
                    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    f.add(new ChartPanel(createChart), BorderLayout.CENTER);
                    f.setVisible(true);

                }

            }

            Map<Operation, EarliestAndLatestStartsAndEnds> ealosaes = new HashMap<>();
            for (Operation operation : problem.getOperations()) {
                EarliestAndLatestStartsAndEnds earliestAndLatestStartsAndEnds = new EarliestAndLatestStartsAndEnds(operation.getDuration());
                earliestAndLatestStartsAndEnds.setEarliest(schedule.get(operation), schedule.get(operation).add(operation.getDuration()));
                earliestAndLatestStartsAndEnds.setLatest(schedule.get(operation), schedule.get(operation).add(operation.getDuration()));
                ealosaes.put(operation, earliestAndLatestStartsAndEnds);
            }
            JFreeChart createGantt = TopGantt.createGantt(problem.getActivityOnNodeDiagramm(), ealosaes);
            try {
                ChartUtilities.saveChartAsPNG(new File(folder, "Gantt.png"), createGantt, 1200, 435);
            } catch (IOException ex) {
                Logger.getLogger(DeprecatedTestCalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
            }

            JFrame f = new JFrame();
            f.setSize(800, 600);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new ChartPanel(createGantt), BorderLayout.CENTER);
            f.setVisible(true);
        }

    }
}
