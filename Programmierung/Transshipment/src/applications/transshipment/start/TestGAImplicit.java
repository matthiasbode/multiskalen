/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start;

import applications.transshipment.TransshipmentParameter;
import static applications.transshipment.TransshipmentParameter.exactSetupTime;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.DNF.DNFFromTrainToTrain;
import ga.listeners.analysis.FitnessEvolution;
import applications.transshipment.analysis.GA.ImplicitWriter;
import applications.transshipment.analysis.Problem.LoadUnitAvailableTimes;
import applications.transshipment.analysis.Problem.TrainAnalyser;
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
import ga.algorithms.coevolving.individuals.FittestIndividualSuperIndividualGenerator;
import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.algorithms.coevolving.individuals.RandomSuperIndividualGenerator;
import ga.basics.Population;
import ga.fittnessLandscapeAnalysis.Diversity;
import ga.metric.Metric;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;

/**
 *
 * @author bode
 */
public class TestGAImplicit implements TestClass {

    public static int numberOfIndOperations = 100;
    public static int numberOfIndModes = 100;
    public static int GENERATIONS = 20;

    public static Scale scale = Scale.macro;

    public LoadUnitJobSchedule bestSchedule;
    ImplicitSuperIndividual fittestIndividual;

    public File folder;

    public static void main(String[] args) {
        TransshipmentParameter.DEBUG = false;
        Parameters.getRandom().setSeed(System.currentTimeMillis());

        String ruest =  exactSetupTime ? "exactSetup_" : "notExactSetup_";
        File folder = ProjectOutput.create(scale.toString() + ruest);
        TransshipmentParameter.initializeLogger(Level.FINE, folder);
        System.out.println(folder.getAbsolutePath());
        TestGAImplicit ga = new TestGAImplicit(folder);

        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
//        parameters.use_LCS = 0;
//        parameters.numberOfCranes = 4;
//        parameters.numberOfTrains = 10;
//        TransshipmentParameter.numberOfRoutes = 4;
//        parameters.resource = DuisburgTerminalGenerator.class.getResourceAsStream("transportprogramme/10zuege.json");

        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, TestGAImplicit.scale, TransshipmentParameter.numberOfRoutes, false);

        ArrayList<Analysis> analyser = new ArrayList<>();

        analyser.add(new ScheduleWriter());
        analyser.add(new LoadUnitOrientatedScheduleWriter());

        analyser.add(new WorkloadPlotter());
        analyser.add(new CraneAnalysis());

        analyser.add(new LoadUnitAvailableTimes());
        analyser.add(new DNFFromTrainToTrain());

        if (ga.scale.equals(Scale.micro)) {
            analyser.add(new CraneAnalysis());
        }
        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);
        for (Analysis a : analyser) {
            a.analysis(ga.bestSchedule, problem, folder);
        }
        new TrainAnalyser().analysis(null, problem, folder, ga.fittestIndividual.getOperationIndividual());
    }

    public TestGAImplicit(File folder) {
        this.folder = folder;
    }

    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        

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

        gaMode.print(folder);
        gaOps.print(folder);

        /**
         * Coevolving GA.
         */
        fittestIndividual = null;

        MultipleSpeciesIndividualGenerator<ImplicitSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator()); //
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

            HashMap<Class, Metric> metrices = new HashMap();
            metrices.put(ImplicitSuperIndividual.class, new HammingDistanceSuperImplicit());
            metrices.put(ImplicitModeIndividual.class, new HammingDistanceMode());
            metrices.put(ImplicitOperationIndividual.class, new HammingDistanceOp());

            FitnessEvolution<ImplicitSuperIndividual> evalFitness = new FitnessEvolution<>(folder);
            ImplicitWriter writer = new ImplicitWriter(folder);
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
