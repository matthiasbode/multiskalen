/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.PSPLib.demo;

import applications.fuzzy.PSPLib.FuzzyCalcPSPLib;

import applications.mmrcsp.ga.populationGenerators.VertexClassStartPopulationGenerator;
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
import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;
import applications.mmrcsp.model.schedule.test.OrderValid;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.reset.ResetThreshold;
import ga.algorithms.SimpleGA;
import ga.basics.Population;
import ga.crossover.Crossover;
import ga.crossover.ListIndividualTwoPointCrossOver;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import ga.mutation.ListIndividualSwap;
import ga.mutation.Mutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.FitnessProportionalSelection;
import ga.selection.Selection;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
public class ImportedTestCalcPSPLib {

    public static int numberOfInd = 1;
    public static int GENERATIONS = 1;

    public static void main(String[] args) {
        InputStream stream = FuzzyCalcPSPLib.class.getResourceAsStream("../../PSPLib/30/j301_2.sm");
        run(stream);

    }

    public static void run(InputStream stream) {
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
        DefaultTimeRestictedSchedulingProblem<Operation> problem = smf.getScheduleFromFile();

        System.out.println("TempAvail:" + problem.getOptimizationTimeSlot().getDuration().longValue());

        VertexClassStartPopulationGenerator vPop = new VertexClassStartPopulationGenerator(problem);

        /**
         * GA für Operationen.
         */
        Selection<ListIndividual<Operation>> selOps = new FitnessProportionalSelection<>();//TournamentSelection<>(3);
        Crossover<ListIndividual<Operation>> crossOps = new ListIndividualTwoPointCrossOver<>();
        Mutation<ListIndividual<Operation>> mutOps = new ListIndividualSwap();
        double eliteOps = 0.01;
        double xOverOps = 1.0;
        double xmutOps = 0.3;
        double acceptanceRate = 0.2;
        AcceptanceMechanism<ListIndividual<Operation>> acceptanceOps = new ResetThreshold(5, vPop); //
        NextGenerationAlgorithm nextGenOps = new SelectCrossMutateParallel<>(xOverOps, xmutOps, selOps, mutOps, crossOps);

        Population<ListIndividual<Operation>> startPop = vPop.generatePopulation(numberOfInd);

        ListIndividual<Operation> ind = startPop.individuals().iterator().next();
        ArrayList<ArrayList<Integer>> exportList = new ArrayList<>();
        for (SubListIndividual<Operation> chromosome : ind.getChromosome()) {
            ArrayList<Integer> operations = new ArrayList<Integer>();
            for (Operation op : chromosome.getChromosome()) {
                operations.add(op.getId());
            }
            exportList.add(operations);
        }

        JSONSerialisierung.exportJSON(new File(folder, "Individidual.txt"), exportList, true);

        /**
         * Initialisiere ScheduleScheme
         */
//        ScheduleGenerationScheme sgs = new SerialScheduleScheme();
        ScheduleGenerationScheme sgs = new ParallelScheduleScheme();
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        MinMakeSpanEval eval = new MinMakeSpanEval(sgs, problem, priorityDeterminator, problem.getOptimizationTimeSlot());

        SimpleGA<ListIndividual<Operation>> ga = new SimpleGA(startPop, eval, nextGenOps, acceptanceOps, GENERATIONS);



//        ga.changeSupport.addPropertyChangeListener(new PropertyChangeListener() {
//
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if(evt.getPropertyName().equals("NewGeneration")){
//                    Population<ListIndividual<Operation>> newValue = (Population) evt.getNewValue();
//                    for (ListIndividual<Operation> ind : newValue.getIndividualsSortedList()) {
//                        ListIndividual<Operation> localSearch = local.localSearch(ind);
//                    }
//                }
//            }
//        });
        ga.run();
        ListIndividual<Operation> fittestIndividual = ga.getPopulation().getFittestIndividual();
        System.out.println("Fitness:" + fittestIndividual.getFitness());

        Schedule schedule = eval.getSchedule(fittestIndividual);

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
                    Logger.getLogger(ImportedTestCalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
                }

                JFrame f = new JFrame();
                f.setSize(1200, 425);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(new ChartPanel(createChart), BorderLayout.CENTER);
                f.setVisible(true);

            }

        }

        OrderValid.testSchedule(problem, schedule);

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
            Logger.getLogger(ImportedTestCalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
        }
        JFrame f = new JFrame();
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new ChartPanel(createGantt), BorderLayout.CENTER);
        f.setVisible(true);
    }
}
