///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.transshipment.demo;
//
//import applications.transshipment.TransshipmentParameter;
//import applications.transshipment.analysis.Analysis;
//import ga.listeners.analysis.FitnessEvolution;
//import applications.transshipment.analysis.Schedule.TimePerOperation;
//import applications.transshipment.analysis.Workload.WorkloadPlotter;
//import applications.transshipment.demo.ops.ImplicitOperationGA;
//import applications.transshipment.demo.modes.ImplicitModeGA;
//import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
//import applications.transshipment.ga.implicit.individuals.modes.ModeStartPopulationGenerator;
//import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
//import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividualCreator;
//import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
//import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
//import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
//import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
//import applications.transshipment.model.dnf.DNFToStorageTreatment;
//import applications.transshipment.model.problem.MultiJobTerminalProblem;
//import applications.transshipment.model.schedule.LoadUnitJobSchedule;
//import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StrategySerialScheduleGenerationScheme;
//import applications.transshipment.multiscale.model.Scale;
//import ga.acceptance.AcceptanceMechanism;
//import ga.acceptance.ThresholdAcceptance;
//import ga.algorithms.coevolving.GABundle;
//import ga.algorithms.coevolving.MultipleSpeciesCoevolvingGA;
//import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
//import ga.algorithms.coevolving.individuals.FittestIndividualSuperIndividualGenerator;
//import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
//import ga.algorithms.coevolving.individuals.RandomSuperIndividualGenerator;
//import ga.basics.Population;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.logging.FileHandler;
//import java.util.logging.Handler;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author bode
// */
//public class TestGAImplicitSerial implements TestClass {
//
//    public static int numberOfIndOperations = 10;
//    public static int numberOfIndModes = 10;
//    public static int GENERATIONS = 10;
//
//    public Scale scale = Scale.macro;
// 
//    public LoadUnitJobSchedule bestSchedule;
//    public final File folder;
//
//    public TestGAImplicitSerial(File folder) {
//        this.folder = folder;
//    }
//
//    public static void main(String[] args) {
//        File folder = ProjectOutput.create();
//        TestGAImplicitSerial ga = new TestGAImplicitSerial(folder);
//        DuisburgGenerator g = new DuisburgGenerator();
//        MultiJobTerminalProblem problem = g.generateTerminalProblem(ga.scale, 5, false);
//        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);
//
//        ArrayList<Analysis> analyser = new ArrayList<>();
//
//        analyser.add(new WorkloadPlotter());
//        analyser.add(new TimePerOperation());
//
//        for (Analysis a : analyser) {
//            a.analysis(ga.bestSchedule,  problem, folder);
//        }
//    }
//
//    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
//        TransshipmentParameter.initializeLogger(Level.FINE);
//
//        try {
//            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
//            TransshipmentParameter.logger.addHandler(loggerHandler);
//        } catch (IOException ex) {
//            Logger.getLogger(TestGAImplicitSerial.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(TestGAImplicitSerial.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        LinkedHashMap<Population, GABundle> species = new LinkedHashMap<>();
//
//        /**
//         * GA für Operationen.
//         */
//        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, false);
//        ImplicitOperationGA gaOps = new ImplicitOperationGA();
//        GABundle<ImplicitOperationIndividual> bundleOps = gaOps.getGA(vPop, parallel);
//        species.put(vPop.generatePopulation(numberOfIndOperations), bundleOps);
//
//        /**
//         * GA für ModeIndividual.
//         */
//        ModeStartPopulationGenerator mPop = new ModeStartPopulationGenerator(problem);
//        ImplicitModeGA gaMode = new ImplicitModeGA();
//        GABundle<ImplicitModeIndividual> bundleMode = gaMode.getGA(mPop, parallel);
//        species.put(mPop.generatePopulation(numberOfIndModes), bundleMode);
//
//        /**
//         * Coevolving GA.
//         */
//        ImplicitSuperIndividual fittestIndividual = null;
//        MultipleSpeciesIndividualGenerator<ImplicitSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator());
//        MultipleSpeciesIndividualGenerator<ImplicitSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator());
//
//        AcceptanceMechanism<ImplicitSuperIndividual> am = new ThresholdAcceptance<>();
//
//        DNFToStorageTreatment dnfTreat = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
//        StrategySerialScheduleGenerationScheme sgs = new StrategySerialScheduleGenerationScheme(dnfTreat);
//        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, sgs);
//
//        Population<ImplicitSuperIndividual> pop = null;
//        if (!parallel) {
//            MultipleSpeciesCoevolvingGA<ImplicitSuperIndividual> msc = new MultipleSpeciesCoevolvingGA(ImplicitSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, GENERATIONS);
//            msc.run();
//            fittestIndividual = msc.getPopulation().getFittestIndividual();
//            pop = msc.getPopulation();
//
//        } else {
//            MultipleSpeciesCoevolvingParallelGA<ImplicitSuperIndividual> msc = new MultipleSpeciesCoevolvingParallelGA(ImplicitSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONS);
//            FitnessEvolution<ImplicitSuperIndividual> evalFitness = new FitnessEvolution<>(folder);
//            msc.addGAListener(evalFitness);
//            msc.run();
//            fittestIndividual = msc.getPopulation().getFittestIndividual();
//            pop = msc.getPopulation();
//        }
//
//        TransshipmentParameter.logger.info("############# Ende #################");
//        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);
//        for (ImplicitSuperIndividual implicitTransshipmentSuperIndividual : pop.getIndividualsSortedList()) {
//            System.out.println(implicitTransshipmentSuperIndividual + "\t" + implicitTransshipmentSuperIndividual.getFitness());
//        }
//        this.bestSchedule = eval.getSchedule(fittestIndividual);
//    }
//
//    @Override
//    public LoadUnitJobSchedule getBestSchedule() {
//        return bestSchedule;
//    }
//
//}
