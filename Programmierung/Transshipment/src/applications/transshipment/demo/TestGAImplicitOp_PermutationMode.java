/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.mmrcsp.model.basics.JoNComponent;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.DNF.DNFFromTrainToTrain;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import ga.listeners.analysis.FitnessEvolution;
import applications.transshipment.analysis.GA.ImplicitOpsPermutationModeWriter;
import applications.transshipment.analysis.Problem.LoadUnitAvailableTimes;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ops.ImplicitOperationGA;
import applications.transshipment.demo.modes.PermutationModeGA;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.ga.permutationModeImplicitOps.MinEvaluationPermutationMixed;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeImplicitOpsSuperIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeImplicitOpsSuperIndividualCreator;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.LoadUnitJob;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class TestGAImplicitOp_PermutationMode implements TestClass {

    public static int numberOfIndOperations = 400;
    public static int numberOfIndModes = 400;
    public static int GENERATIONS = 50;

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;
    public File folder;

    public TestGAImplicitOp_PermutationMode(File folder) {
        this.folder = folder;
    }

    public static void main(String[] args) {
        File folder = ProjectOutput.create();
        TestGAImplicitOp_PermutationMode ga = new TestGAImplicitOp_PermutationMode(folder);
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 3, false);
        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);

        ArrayList<Analysis> analyser = new ArrayList<>();

        analyser.add(new ScheduleWriter());
        analyser.add(new LoadUnitOrientatedScheduleWriter());

        analyser.add(new WorkloadPlotter());
        analyser.add(new CraneAnalysis());

        analyser.add(new LoadUnitAvailableTimes());
        analyser.add(new DNFFromTrainToTrain());

        if (ga.scale == Scale.micro) {
            analyser.add(new CraneAnalysis());
        }

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
            Logger.getLogger(TestGAImplicitOp_PermutationMode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestGAImplicitOp_PermutationMode.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Set<LoadUnitJob>> connectionComponents = new ArrayList<>();
        for (JoNComponent<LoadUnitJob> joNComponent : problem.getJobOnNodeDiagramm().getConnectionComponents()) {
            connectionComponents.add(new HashSet<>(joNComponent.vertexSet()));
        }

        LinkedHashMap<Population, GABundle> species = new LinkedHashMap<>();

        /**
         * GA für Operationen.
         */
        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        ImplicitOperationGA gaOps = new ImplicitOperationGA();
        GABundle<ImplicitOperationIndividual> bundleOps = gaOps.getGA(vPop, parallel);
        species.put(vPop.generatePopulation(numberOfIndOperations), bundleOps);
        /**
         * GA für ModeIndividual.
         */
        PermutationModeStartPopulationGenerator mPop = new PermutationModeStartPopulationGenerator(connectionComponents);
        PermutationModeGA gaMode = new PermutationModeGA();
        GABundle<PermutationModeIndividual> bundleMode = gaMode.getGA(mPop, parallel);
        Population<PermutationModeIndividual> generatePopulation = mPop.generatePopulation(numberOfIndModes);
        species.put(generatePopulation, bundleMode);

        /**
         * Coevolving GA.
         */
        PermutationModeImplicitOpsSuperIndividual fittestIndividual = null;

        MultipleSpeciesIndividualGenerator<PermutationModeImplicitOpsSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new PermutationModeImplicitOpsSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<PermutationModeImplicitOpsSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator<>(new PermutationModeImplicitOpsSuperIndividualCreator());
        AcceptanceMechanism<PermutationModeImplicitOpsSuperIndividual> am = new ThresholdAcceptance<>();

        DNFToStorageTreatment dnfTreat = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfTreat);
        MinEvaluationPermutationMixed eval = new MinEvaluationPermutationMixed(problem, sgs);

        Population<PermutationModeImplicitOpsSuperIndividual> pop = null;
        if (!parallel) {
            MultipleSpeciesCoevolvingGA<PermutationModeImplicitOpsSuperIndividual> msc = new MultipleSpeciesCoevolvingGA(PermutationModeImplicitOpsSuperIndividual.class, species, eval, startSuperIndividualGenerator, startSuperIndividualGenerator, GENERATIONS);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
            pop = msc.getPopulation();

        } else {
            MultipleSpeciesCoevolvingParallelGA<PermutationModeImplicitOpsSuperIndividual> msc = new MultipleSpeciesCoevolvingParallelGA(PermutationModeImplicitOpsSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONS);
            FitnessEvolution<PermutationModeImplicitOpsSuperIndividual> evalFitness = new FitnessEvolution<>(folder);
            ImplicitOpsPermutationModeWriter writer = new ImplicitOpsPermutationModeWriter(folder);
            msc.addGAListener(evalFitness);
            msc.addGAListener(writer);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
            pop = msc.getPopulation();
        }

        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);
        for (PermutationModeImplicitOpsSuperIndividual implicitTransshipmentSuperIndividual : pop.getIndividualsSortedList()) {
            System.out.println(implicitTransshipmentSuperIndividual + "\t" + implicitTransshipmentSuperIndividual.getFitness());
        }

        this.bestSchedule = eval.getSchedule(fittestIndividual);
    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return bestSchedule;
    }

}
