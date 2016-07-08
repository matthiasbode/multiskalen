/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.DNF.DNFFromTrainToTrain;
import ga.listeners.analysis.FitnessEvolution;
import applications.transshipment.analysis.GA.ImplicitWriter;
import applications.transshipment.analysis.Problem.LoadUnitAvailableTimes;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.demo.TestClass;
import applications.transshipment.demo.ops.ImplicitOperationGA;
import applications.transshipment.demo.modes.ImplicitModeGA;
import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.HammingDistanceSuperImplicit;
import applications.transshipment.ga.implicit.individuals.modes.ModeStartPopulationGenerator;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividualCreator;
import applications.transshipment.ga.implicit.individuals.modes.HammingDistanceMode;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.ops.HammingDistanceOp;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import ga.Parameters;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.coevolving.GABundle;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingGA;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.algorithms.coevolving.individuals.FitnessProportionalSuperIndividualGenerator;
import ga.algorithms.coevolving.individuals.FittestIndividualSuperIndividualGenerator;
import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.algorithms.coevolving.individuals.RandomSuperIndividualGenerator;
import ga.basics.Population;
import ga.fittnessLandscapeAnalysis.Diversity;
import ga.metric.Metric;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class TestGAImplicit_AlternativeSuperIndividual implements TestClass {

    public static int numberOfIndOperations = 400;
    public static int numberOfIndModes = 400;
    public static int GENERATIONS = 40;

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;

    public File folder;

    public static void main(String[] args) {
        TransshipmentParameter.DEBUG = false;
//        for (int i = 0; i < 10; i++) {
        Parameters.getRandom().setSeed(System.currentTimeMillis());
        File folder = ProjectOutput.create();
        System.out.println(folder.getAbsolutePath());

        TestGAImplicit_AlternativeSuperIndividual ga = new TestGAImplicit_AlternativeSuperIndividual(folder);
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 5, false);
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
//        }
    }

    public TestGAImplicit_AlternativeSuperIndividual(File folder) {
        this.folder = folder;
    }

    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(TestGAImplicit_AlternativeSuperIndividual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestGAImplicit_AlternativeSuperIndividual.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedHashMap<Population, GABundle> species = new LinkedHashMap<>();

        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        ImplicitOperationGA gaOps = new ImplicitOperationGA();
        GABundle<ImplicitOperationIndividual> bundleOps = gaOps.getGA(vPop, parallel);
        species.put(vPop.generatePopulation(numberOfIndOperations), bundleOps);

        /**
         * GA für ModeIndividual.
         */
        ModeStartPopulationGenerator mPop = new ModeStartPopulationGenerator(problem);
        ImplicitModeGA gaMode = new ImplicitModeGA();
        GABundle<ImplicitModeIndividual> bundleMode = gaMode.getGA(mPop, parallel);
        species.put(mPop.generatePopulation(numberOfIndModes), bundleMode);

        /**
         * Coevolving GA.
         */
        ImplicitSuperIndividual fittestIndividual = null;

        MultipleSpeciesIndividualGenerator<ImplicitSuperIndividual> superIndividualGenerator = new FitnessProportionalSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator()); //new RandomSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator());//FitnessProportionalSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator()); // new FittestIndividualSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator()); //
        TransshipmentParameter.logger.warning(superIndividualGenerator.getClass().toString());
        MultipleSpeciesIndividualGenerator<ImplicitSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator());
        AcceptanceMechanism<ImplicitSuperIndividual> am = new ThresholdAcceptance<>();

        DNFToStorageTreatment dnfTreat = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfTreat);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, sgs);

        if (!parallel) {
            MultipleSpeciesCoevolvingGA<ImplicitSuperIndividual> msc = new MultipleSpeciesCoevolvingGA(ImplicitSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, GENERATIONS);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();

        } else {
            MultipleSpeciesCoevolvingParallelGA<ImplicitSuperIndividual> msc = new MultipleSpeciesCoevolvingParallelGA(ImplicitSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONS);
            FitnessEvolution<ImplicitSuperIndividual> evalFitness = new FitnessEvolution<>(folder);
            ImplicitWriter writer = new ImplicitWriter(folder);
            HashMap<Class, Metric> metrices = new HashMap();
            metrices.put(ImplicitSuperIndividual.class, new HammingDistanceSuperImplicit());
            metrices.put(ImplicitModeIndividual.class, new HammingDistanceMode());
            metrices.put(ImplicitOperationIndividual.class, new HammingDistanceOp());

            Diversity div = new Diversity(folder, metrices);

            //SOMWriter somWriter  = new SOMWriter(folder);
            msc.addGAListener(evalFitness);
            msc.addGAListener(writer);
            msc.addGAListener(div);
            //msc.addGAListener(somWriter);
//            msc.addGAListener(div);
            //msc.addGAListener(som);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
        }

        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);

        bestSchedule = eval.getSchedule(fittestIndividual);
    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return bestSchedule;
    }

}
