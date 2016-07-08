/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.PSPLib;

import applications.PSPLib.demo.CalcPSPLib;
import applications.fuzzy.operation.FuzzyOperation;
import applications.fuzzy.scheduling.rules.defaultImplementation.DefaultEarliestFuzzyScheduleRule;
import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyDemandUtilities;
import applications.fuzzy.scheduling.rules.FuzzyFunctionBasedRule;
import applications.mmrcsp.ga.populationGenerators.VertexClassStartPopulationGenerator;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.ga.priority.StandardPriorityDeterminator;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.timeRestricted.DefaultTimeRestictedSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.scheduleSchemes.ParallelScheduleScheme;
import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;
import applications.mmrcsp.model.schedule.test.OrderValid;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.interval.FuzzyInterval;
import ga.Parameters;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.reset.ResetThreshold;
import ga.algorithms.SimpleGA;
import ga.basics.FitnessEvalationFunction;
import ga.basics.Population;
import ga.crossover.Crossover;
import ga.crossover.ListIndividualTwoPointCrossOver;
import ga.individuals.subList.ListIndividual;
import ga.listeners.analysis.FitnessEvolution;
import ga.mutation.ListIndividualSwap;
import ga.mutation.Mutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.FitnessProportionalSelection;
import ga.selection.Selection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import math.FieldElement;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.ScheduleFitnessEvalationFunction;
import util.input.SingleModeFuzzyFile;
import util.workspace.ProjectOutput;

/**
 *
 * @author bode
 */
public class FuzzyCalcPSPLib {

    public static int numberOfInd = 1;
    public static int GENERATIONS = 1;

    public static void main(String[] args) throws FileNotFoundException {
        SingleModeFuzzyFile.DurationFuzzyness = 10 * 1000;
//        run(null);
        InputStream stream = FuzzyCalcPSPLib.class.getResourceAsStream("../../PSPLib/30/j301_2.sm");
        stream = FuzzyCalcPSPLib.class.getResourceAsStream("../demo/test.sm");
        run(stream);
    }

    public static void run(InputStream stream) {

        Parameters.initializeLogger(Level.INFO);
        File folder = ProjectOutput.create();
        System.out.println(folder.getAbsolutePath());
        if (stream == null) {
            final JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            try {
                stream = new FileInputStream(fc.getSelectedFile());

            } catch (FileNotFoundException ex) {
                Logger.getLogger(FuzzyCalcPSPLib.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        SingleModeFuzzyFile smf = new SingleModeFuzzyFile(stream);

        /**
         * Problem
         */
        DefaultTimeRestictedSchedulingProblem<Operation> problem = smf.getScheduleFromFile();

        /**
         * Startunschärfe, die benötigt wird. Minimal bei 1 sekunde +/-
         */
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

        Population<ListIndividual> startPop = vPop.generatePopulation(numberOfInd);

        /**
         * Initialisiere ScheduleScheme
         */
        ScheduleGenerationScheme sgs = new ParallelScheduleScheme();
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        ScheduleFitnessEvalationFunction<ListIndividual<Operation>> eval = new RessourceLeveling(sgs, problem, priorityDeterminator, problem.getOptimizationTimeSlot()); ////// new MinMakeSpanEval(sgs, problem, priorityDeterminator, problem.getOptimizationTimeSlot());// 
        SimpleGA<ListIndividual<Operation>> ga = new SimpleGA(startPop, eval, nextGenOps, acceptanceOps, GENERATIONS);
//        ga.addGAListener(new FitnessEvolution(folder));
        ga.run();

        ListIndividual<Operation> fittestIndividual = ga.getPopulation().getFittestIndividual();

        System.out.println(fittestIndividual.getFitness());
        Schedule schedule = eval.getSchedule(fittestIndividual);
        FuzzyNumber fuzStart = (FuzzyNumber) problem.getOptimizationTimeSlot().getFromWhen();
        FuzzyNumber fuzEnde = (FuzzyNumber) problem.getOptimizationTimeSlot().getUntilWhen();

        double dx = 1000 * 1;

        FuzzyInterval start = (FuzzyInterval) problem.getOptimizationTimeSlot().getFromWhen();
        TimeSlot ts = TimeSlot.create(start.longValue() - SingleModeFuzzyFile.DurationFuzzyness * 10, problem.getOptimizationTimeSlot().getFromWhen().longValue() + 100 * 60 * 1000);

        //System.out.println("##########################");
        for (Resource resource : problem.getResources()) {
            applications.fuzzy.plotter.FuzzyFunctionPlotter workloadplotter = new applications.fuzzy.plotter.FuzzyFunctionPlotter("Auslastung der Ressource '" + resource + "' (β=" + SingleModeFuzzyFile.pesLevel + ")");
            ScheduleRule rule = schedule.getHandler().get(resource);
            DefaultEarliestFuzzyScheduleRule fuzzyRule = (DefaultEarliestFuzzyScheduleRule) rule;

            applications.fuzzy.plotter.FuzzyFunctionPlotter workloadplotterGes = new applications.fuzzy.plotter.FuzzyFunctionPlotter("Gesamtauslastung der Ressource '" + resource + "' (β=" + SingleModeFuzzyFile.pesLevel + ")" + fuzzyRule.capacity + " max");

            ScheduleRule scheduleRule = schedule.getHandler().get(resource);
            FuzzyFunctionBasedRule sfb = (FuzzyFunctionBasedRule) scheduleRule;
            /**
             * Für die einzelenen Operationen
             */
            //System.out.println("Resource: " + resource);
            for (Operation operation : schedule.getOperationsForResource(resource)) {
                FuzzyOperation ofuz = (FuzzyOperation) operation;
                FuzzyInterval fuzStartOp = (FuzzyInterval) schedule.getStartTimes().get(operation);
                System.out.println(schedule.fuzzyWorkloadParameters.get(operation));
                workloadplotter.addFunction(FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(ofuz, resource, fuzStartOp, schedule.fuzzyWorkloadParameters.get(operation)),  fuzStart.getC1(), fuzEnde.getC2(), dx, "Auslastung durch Vorgang " + ofuz.getId());
            }

            workloadplotter.plot(1200, 425, ts, false);
            JFreeChart aC1 = workloadplotter.getAreaChart(ts, sfb.getMax().doubleValue() + 0.25);
            try {
                ChartUtilities.saveChartAsPNG(new File(folder, "Einzeln" + resource.toString() + ".png"), aC1, 1200, 435);

            } catch (IOException ex) {
                Logger.getLogger(CalcPSPLib.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

            /**
             * Gesamt
             */
            workloadplotterGes.addFunction(sfb.getWorkloadFunction(), fuzStart.getC1(), fuzEnde.getC2(), dx, "Gesamtauslastung");
            workloadplotterGes.plot(1200, 425, ts, false);

            JFreeChart areaChart = workloadplotterGes.getAreaChart(ts, sfb.getMax().doubleValue() + 0.25);
            try {
                ChartUtilities.saveChartAsPNG(new File(folder, "Gesamt_" + resource.toString() + ".png"), areaChart, 1200, 435);

            } catch (IOException ex) {
                Logger.getLogger(CalcPSPLib.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Gesamtauslastung hinzugefügt.");

        }

        System.out.println("anzeigen");
        OrderValid.testSchedule(problem, schedule);

        for (Operation k : schedule.fuzzyWorkloadParameters.keySet()) {
            System.out.println(k.getId() + ":" + schedule.fuzzyWorkloadParameters.get(k));

        }

    }
}
