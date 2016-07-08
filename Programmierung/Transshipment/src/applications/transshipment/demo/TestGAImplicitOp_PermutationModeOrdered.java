/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import ga.listeners.analysis.FitnessEvolution;
import applications.transshipment.analysis.Workload.HeatMapPlotter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ops.ImplicitOperationGA;
import applications.transshipment.demo.modes.PermutationModeOrderedGA;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.ga.permutationModeImplicitOps.sorted.MinEvaluationPermutationSortedMixed;
import applications.transshipment.ga.permutationModeImplicitOps.sorted.MixedPerSortedSuperIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.sorted.MixedPerTransshipmentSortedSuperIndividualCreator;
import applications.transshipment.ga.permutationModeImplicitOps.sorted.PermutationModeIndividualSorted;
import applications.transshipment.ga.permutationModeImplicitOps.sorted.PermutationModeSortedStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.coevolving.GABundle;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingGA;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.algorithms.coevolving.individuals.FittestIndividualSuperIndividualGenerator;
import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.algorithms.coevolving.individuals.RandomSuperIndividualGenerator;
import ga.basics.Population;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class TestGAImplicitOp_PermutationModeOrdered implements TestClass {

    public static int numberOfIndOperations = 50;
    public static int numberOfIndModes = 50;
    public static int GENERATIONS = 15;

    public Scale scale = Scale.macro;

    public LoadUnitJobSchedule bestSchedule;
    public final File folder;

    public TestGAImplicitOp_PermutationModeOrdered(File folder) {
        this.folder = folder;
    }

    public static void main(String[] args) {
        File folder = ProjectOutput.create();
        TestGAImplicitOp_PermutationModeOrdered ga = new TestGAImplicitOp_PermutationModeOrdered(folder);

        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 9, false);

        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);

        ArrayList<Analysis> analyser = new ArrayList<>();

        analyser.add(new ScheduleWriter());
        analyser.add(new WorkloadPlotter());
        analyser.add(new HeatMapPlotter());

        for (Analysis a : analyser) {
            a.analysis(ga.bestSchedule, problem, folder);
        }
    }

    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(TestGAImplicitOp_PermutationModeOrdered.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestGAImplicitOp_PermutationModeOrdered.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedHashMap<Population, GABundle> species = new LinkedHashMap<>();

        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        ImplicitOperationGA gaOps = new ImplicitOperationGA();
        GABundle<ImplicitOperationIndividual> bundleOps = gaOps.getGA(vPop, parallel);
        species.put(vPop.generatePopulation(numberOfIndOperations), bundleOps);

        /**
         * GA für ModeIndividual.
         */
        PermutationModeSortedStartPopulationGenerator mPop = new PermutationModeSortedStartPopulationGenerator(problem, 9);
        PermutationModeOrderedGA gaMode = new PermutationModeOrderedGA();
        GABundle<PermutationModeIndividualSorted> bundleMode = gaMode.getGA(mPop, parallel);
        species.put(mPop.generatePopulation(numberOfIndModes), bundleMode);

        /**
         * Coevolving GA.
         */
        MixedPerSortedSuperIndividual fittestIndividual = null;
        MultipleSpeciesIndividualGenerator<MixedPerSortedSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new MixedPerTransshipmentSortedSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<MixedPerSortedSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator<>(new MixedPerTransshipmentSortedSuperIndividualCreator());
        AcceptanceMechanism<MixedPerSortedSuperIndividual> am = new ThresholdAcceptance<>();

        DNFToStorageTreatment dnfTreat = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfTreat);
        MinEvaluationPermutationSortedMixed eval = new MinEvaluationPermutationSortedMixed(problem, sgs);

        Population<MixedPerSortedSuperIndividual> pop = null;
        if (!parallel) {
            MultipleSpeciesCoevolvingGA<MixedPerSortedSuperIndividual> msc = new MultipleSpeciesCoevolvingGA(MixedPerSortedSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, GENERATIONS);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
            pop = msc.getPopulation();

        } else {
            MultipleSpeciesCoevolvingParallelGA<MixedPerSortedSuperIndividual> msc = new MultipleSpeciesCoevolvingParallelGA(MixedPerSortedSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONS);
            FitnessEvolution<MixedPerSortedSuperIndividual> evalFitness = new FitnessEvolution<>(folder);
            msc.addGAListener(evalFitness);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
            pop = msc.getPopulation();
        }

        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);
        for (MixedPerSortedSuperIndividual implicitTransshipmentSuperIndividual : pop.getIndividualsSortedList()) {
            System.out.println(implicitTransshipmentSuperIndividual + "\t" + implicitTransshipmentSuperIndividual.getFitness());
        }
        this.bestSchedule = eval.getSchedule(fittestIndividual);

    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return bestSchedule;
    }

}
