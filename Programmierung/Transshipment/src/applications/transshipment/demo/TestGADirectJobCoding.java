/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.basics.JoNComponent;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.EALOSAE.JobEalosaeAnalysis;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.demo.modes.PermutationModeGA;
import applications.transshipment.demo.ops.DirectJobsForOperationGA;
import applications.transshipment.ga.permutationModeDirectJob.JobBasedPriorityDeterminator;
import applications.transshipment.ga.permutationModeDirectJob.MinEvaluationPermutationJobPermutationMode;
import applications.transshipment.ga.permutationModeDirectJob.PermutationJobIndividual;
import applications.transshipment.ga.permutationModeDirectJob.PermutationJobStartPopulationGenerator;
import applications.transshipment.ga.permutationModeDirectJob.PermutationSuperIndividualCreator;
import applications.transshipment.ga.permutationModeDirectJob.SuperPermutation;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes.ParallelScheduleGenerationScheme;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
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
public class TestGADirectJobCoding implements TestClass {

    public static int numberOfIndOperations = 20;
    public static int numberOfIndModes = 20;
    public static int GENERATIONS = 10;

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;
    File folder = ProjectOutput.create();

    public static void main(String[] args) {
        TestGADirectJobCoding ga = new TestGADirectJobCoding();
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 3, true);
        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);
        LoadUnitJobSchedule fittest = ga.getBestSchedule();
        ScheduleWriter sw = new ScheduleWriter();

        sw.analysis(fittest, problem, ga.folder);
        JobEalosaeAnalysis jobEalosaeAnalysis = new JobEalosaeAnalysis();
        jobEalosaeAnalysis.analysis(fittest, problem, ga.folder);

//        start(numberOfIndOperations, numberOfIndModes, GENERATIONS, false);
    }

    @Override
    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(TestGADirectJobCoding.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestGADirectJobCoding.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedHashMap<Population, GABundle> species = new LinkedHashMap<>();

        /**
         * GA für Operationen.
         */
        PermutationJobStartPopulationGenerator vPop = new PermutationJobStartPopulationGenerator(problem.getJobs());
        DirectJobsForOperationGA gaOps = new DirectJobsForOperationGA();
        GABundle<PermutationJobIndividual> bundleOps = gaOps.getGA(vPop, parallel);
        species.put(vPop.generatePopulation(numberOfIndOperations), bundleOps);

        /**
         * GA für ModeIndividual.
         */
        List<Set<LoadUnitJob>> connectionComponents = new ArrayList<>();
        for (JoNComponent<LoadUnitJob> joNComponent : problem.getJobOnNodeDiagramm().getConnectionComponents()) {
            connectionComponents.add(new HashSet<>(joNComponent.vertexSet()));
        }
        PermutationModeStartPopulationGenerator mPop = new PermutationModeStartPopulationGenerator(connectionComponents);
        PermutationModeGA gaMode = new PermutationModeGA();
        GABundle<PermutationModeIndividual> bundleMode = gaMode.getGA(mPop, parallel);
        species.put(mPop.generatePopulation(numberOfIndModes), bundleMode);

        /**
         * Coevolving GA.
         */
        SuperPermutation fittestIndividual = null;

        MultipleSpeciesIndividualGenerator<SuperPermutation> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new PermutationSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<SuperPermutation> startSuperIndividualGenerator = new RandomSuperIndividualGenerator(new PermutationSuperIndividualCreator());
        AcceptanceMechanism<SuperPermutation> am = new ThresholdAcceptance<>();

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        Transshipment_ActivityListScheduleScheme defaultScheduleScheme = new ParallelScheduleGenerationScheme(dnfToStorageTreatment);
        PriorityDeterminator priorityDeterminator = new JobBasedPriorityDeterminator();
        MinEvaluationPermutationJobPermutationMode eval = new MinEvaluationPermutationJobPermutationMode(problem, priorityDeterminator, defaultScheduleScheme);

        if (!parallel) {
            MultipleSpeciesCoevolvingGA<SuperPermutation> msc = new MultipleSpeciesCoevolvingGA(SuperPermutation.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, GENERATIONS);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
        } else {
            MultipleSpeciesCoevolvingParallelGA<SuperPermutation> msc = new MultipleSpeciesCoevolvingParallelGA(SuperPermutation.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONS);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
        }
        this.bestSchedule = eval.getSchedule(fittestIndividual);
        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);

    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return bestSchedule;
    }

}
