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
import applications.mmrcsp.model.basics.TimeSlot;
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
import ga.listeners.analysis.FitnessEvolution;
import ga.mutation.ListIndividualSwap;
import ga.mutation.Mutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.Selection;
import ga.selection.TournamentSelection;
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
import math.FieldElement;
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
public class CalcPSPLib {

    public static int numberOfInd = 1;
    public static int GENERATIONS = 1;

    public static void main(String[] args) {
        InputStream stream = FuzzyCalcPSPLib.class.getResourceAsStream("../../PSPLib/30/j301_2.sm");
         stream = FuzzyCalcPSPLib.class.getResourceAsStream("../demo/test.sm");
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
        Selection<ListIndividual<Operation>> selOps = new TournamentSelection<>(3);
        Crossover<ListIndividual<Operation>> crossOps = new ListIndividualTwoPointCrossOver<>();
        Mutation<ListIndividual<Operation>> mutOps = new ListIndividualSwap();
        double eliteOps = 0.01;
        double xOverOps = 1.0;
        double xmutOps = 0.3;
        double acceptanceRate = 0.2;
        AcceptanceMechanism<ListIndividual<Operation>> acceptanceOps = new ResetThreshold(5, vPop); //
        NextGenerationAlgorithm nextGenOps = new SelectCrossMutateParallel<>(xOverOps, xmutOps, selOps, mutOps, crossOps);

        Population<ListIndividual> startPop = vPop.generatePopulation(numberOfInd);

        /**
         * Initialisiere ScheduleScheme
         */
//        ScheduleGenerationScheme sgs = new SerialScheduleScheme();
        ScheduleGenerationScheme sgs = new ParallelScheduleScheme();
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        MinMakeSpanEval eval = new MinMakeSpanEval(sgs, problem, priorityDeterminator, problem.getOptimizationTimeSlot());

        SimpleGA<ListIndividual<Operation>> ga = new SimpleGA(startPop, eval, nextGenOps, acceptanceOps, GENERATIONS);
//        ga.addGAListener(new FitnessEvolution(folder));
        


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
            FieldElement fromWhen = problem.getOptimizationTimeSlot().getFromWhen();
            TimeSlot ts = TimeSlot.create((long) fromWhen.longValue(), problem.getOptimizationTimeSlot().getFromWhen().longValue() + 100 * 60 * 1000);
            double dx = 1000 * 1;
            if (scheduleRule instanceof ScalarFunctionBasedRule) {
                ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;
                applications.fuzzy.plotter.FuzzyFunctionPlotter wp = new applications.fuzzy.plotter.FuzzyFunctionPlotter("Auslastung der Ressource '" + resource);

                wp.addFunction(sfb.getWorkloadFunction(), ts.getFromWhen().doubleValue(), ts.getUntilWhen().doubleValue(), dx, "Auslastung der Ressource '" + resource);

                JFreeChart aCG = wp.getAreaChart(ts, sfb.getMax() + 0.25);

                aCG.removeLegend();
                try {
                    ChartUtilities.saveChartAsPNG(new File(folder, "Gesamt" + resource.toString() + ".png"), aCG, 1200, 435);
                } catch (IOException ex) {
                    Logger.getLogger(CalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
                }

//                TimeSlot ts = TimeSlot.create(problem.getOptimizationTimeSlot().getFromWhen().longValue(), problem.getOptimizationTimeSlot().getFromWhen().longValue() + 100 * 60 * 1000);
                JFreeChart createChart = FunctionPlotter.createAreaDateChart((StepFunction) sfb.getWorkloadFunction(), ts, sfb.getMax() + 0.25, "Für Resource " + resource.toString());

                try {
                    ChartUtilities.saveChartAsPNG(new File(folder, resource.toString() + ".png"), createChart, 1200, 435);
                } catch (IOException ex) {
                    Logger.getLogger(CalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
                }

                JFrame f = new JFrame();
                f.setSize(1200, 425);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(new ChartPanel(createChart), BorderLayout.CENTER);
                f.setVisible(true);

                applications.fuzzy.plotter.FuzzyFunctionPlotter workloadplotter = new applications.fuzzy.plotter.FuzzyFunctionPlotter("Auslastung der Ressource '" + resource);

                for (Operation operation : arrayList) {

                    FieldElement start = schedule.getStartTimes().get(operation);
                    StepFunction func = new StepFunction(start, start.add(operation.getDuration()), operation.getDemand(resource));

                    workloadplotter.addFunction(
                            func, start.doubleValue(), start.add(operation.getDuration()).doubleValue(), dx, "Auslastung durch Vorgang " + operation.getId());
                }

                JFreeChart aC1 = workloadplotter.getAreaChart(ts, sfb.getMax() + 0.25);

                aC1.removeLegend();
                try {
                    ChartUtilities.saveChartAsPNG(new File(folder, "Einzeln" + resource.toString() + ".png"), aC1, 1200, 435);
                } catch (IOException ex) {
                    Logger.getLogger(CalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        OrderValid.testSchedule(problem, schedule);

        ArrayList<ArrayList<Integer>> exportList = new ArrayList<>();
        for (SubListIndividual<Operation> chromosome : fittestIndividual.getChromosome()) {
            ArrayList<Integer> operations = new ArrayList<Integer>();
            for (Operation op : chromosome.getChromosome()) {
                operations.add(op.getId());
            }
            exportList.add(operations);
        }

        JSONSerialisierung.exportJSON(new File(folder, "Individidual.txt"), exportList, true);

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
            Logger.getLogger(CalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
        }
        JFrame f = new JFrame();
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new ChartPanel(createGantt), BorderLayout.CENTER);
        f.setVisible(true);
    }
}
