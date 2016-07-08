/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.ga.priority.StandardPriorityDeterminator;
import applications.transshipment.TransshipmentParameter;
import ga.listeners.analysis.FitnessEvolution;
import applications.transshipment.demo.ops.DirectOperationGA;
import applications.transshipment.demo.modes.ImplicitModeGA;
import applications.transshipment.ga.implicitModeDirectOps.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ModeStartPopulationGenerator;
import applications.transshipment.ga.implicitModeDirectOps.individuals.AdaptedVertexClassStartPopulationGenerator;
import applications.transshipment.ga.implicitModeDirectOps.individuals.ImplicitModeDirectOpsSuperIndividual;
import applications.transshipment.ga.implicitModeDirectOps.individuals.MixedTransshipmentSuperIndividualCreator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes.ParallelScheduleGenerationScheme;
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
public class TestGA_DirectOp_ImplicitMode implements TestClass {

    public static int numberOfIndOperations = 1;
    public static int numberOfIndModes = 1;
    public static int GENERATIONS = 1;

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;

    public final File folder;

    public TestGA_DirectOp_ImplicitMode(File folder) {
        this.folder = folder;
    }

    public static void main(String[] args) {
        File folder = ProjectOutput.create();
        TestGA_DirectOp_ImplicitMode ga = new TestGA_DirectOp_ImplicitMode(folder);
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 4, true);
        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);

    }

    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINER);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(TestGA_DirectOp_ImplicitMode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestGA_DirectOp_ImplicitMode.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedHashMap<Population, GABundle> species = new LinkedHashMap<>();
        /**
         * GA für ModeIndividual.
         */
        ModeStartPopulationGenerator mPop = new ModeStartPopulationGenerator(problem);
        ImplicitModeGA gaMode = new ImplicitModeGA();
        GABundle<ImplicitModeIndividual> bundleMode = gaMode.getGA(mPop, parallel);
        species.put(mPop.generatePopulation(numberOfIndModes), bundleMode);

        /**
         * GA für Operationen.
         */
        DirectOperationGA gaOps = new DirectOperationGA();
        AdaptedVertexClassStartPopulationGenerator vPop = new AdaptedVertexClassStartPopulationGenerator(problem);
        GABundle<ListIndividual<RoutingTransportOperation>> bundleOps = gaOps.getGA(vPop, parallel);
        species.put(vPop.generatePopulation(numberOfIndOperations), bundleOps);

        /**
         * Coevolving GA.
         */
        ImplicitModeDirectOpsSuperIndividual fittestIndividual = null;
        MultipleSpeciesIndividualGenerator<ImplicitModeDirectOpsSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator(new MixedTransshipmentSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<ImplicitModeDirectOpsSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new MixedTransshipmentSuperIndividualCreator());
        AcceptanceMechanism<ImplicitModeDirectOpsSuperIndividual> am = new ThresholdAcceptance<>();

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        ParallelScheduleGenerationScheme defaultScheduleScheme = new ParallelScheduleGenerationScheme(dnfToStorageTreatment);//new SerialScheduleGenerationWithStorageConsideration(problem, dnfToStorageTreatment, false);
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, defaultScheduleScheme, priorityDeterminator);

        if (!parallel) {
            MultipleSpeciesCoevolvingGA<ImplicitModeDirectOpsSuperIndividual> msc = new MultipleSpeciesCoevolvingGA(ImplicitModeDirectOpsSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, GENERATIONS);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();

        } else {
            MultipleSpeciesCoevolvingParallelGA<ImplicitModeDirectOpsSuperIndividual> msc = new MultipleSpeciesCoevolvingParallelGA(ImplicitModeDirectOpsSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONS);
            FitnessEvolution<ImplicitModeDirectOpsSuperIndividual> plot = new FitnessEvolution<>(folder);
            msc.addGAListener(plot);
            msc.run();
            fittestIndividual = msc.getPopulation().getFittestIndividual();
        }

        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);
        bestSchedule = eval.getSchedule(fittestIndividual);

//        StrategyListVergleich strategyListVergleich = new StrategyListVergleich();
//        defaultScheduleScheme.changeSupport.addPropertyChangeListener(strategyListVergleich);
//        eval.computeFitness(fittestIndividual);
//        for (OperationPriorityRules.Identifier identifier : strategyListVergleich.durchschnitt.keySet()) {
//            System.out.println(identifier + ":\t" + strategyListVergleich.durchschnitt.get(identifier) / strategyListVergleich.gesamt);
//        }
//        System.out.println("Gesamt: " + strategyListVergleich.gesamt);
//        System.out.println("Gefunden: " + strategyListVergleich.gefunden);
    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return bestSchedule;
    }

}
