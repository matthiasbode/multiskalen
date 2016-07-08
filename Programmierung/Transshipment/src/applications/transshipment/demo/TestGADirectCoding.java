/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.mmrcsp.ga.populationGenerators.VertexClassStartPopulationGenerator;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.ga.priority.StandardPriorityDeterminator;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.EALOSAE.JobEalosaeAnalysis;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.demo.ops.DirectOperationGA;
import applications.transshipment.demo.modes.IntegerModeGA;
import applications.transshipment.ga.direct.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.direct.individuals.DirectSuperIndividual;
import applications.transshipment.ga.direct.individuals.DirectSuperIndividualCreator;
import applications.transshipment.ga.direct.individuals.ModeStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
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
import ga.individuals.IntegerIndividual;
import ga.individuals.subList.ListIndividual;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class TestGADirectCoding implements TestClass {

    public static int numberOfIndOperations = 40;
    public static int numberOfIndModes = 2;
    public static int GENERATIONS = 10;

    public Scale scale = Scale.macro;

    public LoadUnitJobSchedule bestSchedule;
    File folder = ProjectOutput.create();

    public static void main(String[] args) {
        TestGADirectCoding ga = new TestGADirectCoding();

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
            Logger.getLogger(TestGADirectCoding.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestGADirectCoding.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedHashMap<Population, GABundle> species = new LinkedHashMap<>();

        /**
         * GA für Operationen.
         */
        VertexClassStartPopulationGenerator vPop = new VertexClassStartPopulationGenerator(problem);
        DirectOperationGA gaOps = new DirectOperationGA();
        GABundle<ListIndividual<RoutingTransportOperation>> bundleOps = gaOps.getGA(vPop, parallel);
        species.put(vPop.generatePopulation(numberOfIndOperations), bundleOps);

        /**
         * GA für ModeIndividual.
         */
        ModeStartPopulationGenerator mPop = new ModeStartPopulationGenerator(problem, problem.notDirectlyTransportable, false);
        IntegerModeGA gaMode = new IntegerModeGA();
        GABundle<IntegerIndividual> bundleMode = gaMode.getGA(mPop, parallel);
        species.put(mPop.generatePopulation(numberOfIndModes), bundleMode);

        /**
         * Coevolving GA.
         */
        DirectSuperIndividual fittestIndividual = null;

        MultipleSpeciesIndividualGenerator<DirectSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new DirectSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<DirectSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator(new DirectSuperIndividualCreator());
        AcceptanceMechanism<DirectSuperIndividual> am = new ThresholdAcceptance<>();

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        Transshipment_ActivityListScheduleScheme defaultScheduleScheme = new ParallelScheduleGenerationScheme(dnfToStorageTreatment);
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, defaultScheduleScheme, priorityDeterminator);

        if (!parallel) {
            MultipleSpeciesCoevolvingGA<DirectSuperIndividual> msc = new MultipleSpeciesCoevolvingGA(DirectSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, GENERATIONS);

            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
        } else {
            MultipleSpeciesCoevolvingParallelGA<DirectSuperIndividual> msc = new MultipleSpeciesCoevolvingParallelGA(DirectSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONS);

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
