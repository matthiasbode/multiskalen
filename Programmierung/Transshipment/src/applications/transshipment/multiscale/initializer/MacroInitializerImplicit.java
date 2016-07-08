/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.initializer;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.demo.ops.ImplicitOperationGA;
import applications.transshipment.demo.modes.ImplicitModeGA;
import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividualCreator;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;

import applications.transshipment.ga.implicit.individuals.modes.ModeStartPopulationGenerator;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.GAAlgorithm;
import ga.algorithms.coevolving.CoevolvingGA;
import ga.algorithms.coevolving.GABundle;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingGA;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.algorithms.coevolving.individuals.FittestIndividualSuperIndividualGenerator;
import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.algorithms.coevolving.individuals.RandomSuperIndividualGenerator;
import ga.basics.Population;
import ga.individuals.Individual;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author bode
 */
public class MacroInitializerImplicit {

    public static int numberOfIndOperationsMacro = 1;
    public static int numberOfIndModesMacro = 1;
    public static int GENERATIONSMacro = 0;
    public static boolean parallel = true;

    public static GAAlgorithm<ImplicitSuperIndividual> initMacro(MultiJobTerminalProblem problem, List<ImplicitOperationIndividual> startOps, List<ImplicitModeIndividual> startmodes) {
        return initMacro(null, problem, startOps, startmodes, null);
    }

    public static CoevolvingGA<ImplicitSuperIndividual> initMacro(LoadUnitJobSchedule initialschedule, MultiJobTerminalProblem problem, List<ImplicitOperationIndividual> startOps, List<ImplicitModeIndividual> startmodes, TimeSlot alloverTimeSlot) {
        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(initialschedule, problem, defaultScheduleScheme, alloverTimeSlot);

        LinkedHashMap<Population<? extends Individual>, GABundle> species = new LinkedHashMap<>();

        /**
         * Bestimmung der Startpopulation aus dem Startzeitpunkt des aktuellen
         * Zeitschrittes. Die Chromosomen werden demenstprechend abgeschnitten.
         */
        ImplicitOperationGA gaOps = new ImplicitOperationGA();
        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        GABundle<ImplicitOperationIndividual> bundleOps = gaOps.getGA(vPop, parallel);
        Population<ImplicitOperationIndividual> startPop = getStartPopulationOperation(problem, startOps);
        species.put(startPop, bundleOps);

        /**
         * GA für ModeIndividual.
         */
        ModeStartPopulationGenerator mPop = new ModeStartPopulationGenerator(problem);
        ImplicitModeGA gaMode = new ImplicitModeGA();
        GABundle<ImplicitModeIndividual> bundleMode = gaMode.getGA(mPop, parallel);
        if (startmodes == null || startmodes.isEmpty()) {
            Population<ImplicitModeIndividual> pop = mPop.generatePopulation(numberOfIndModesMacro);
            species.put(pop, bundleMode);
        } else {
            Population population = new Population(ImplicitModeIndividual.class, startmodes);
            species.put(population, bundleMode);
        }

        /**
         * Coevolving GA.
         */
        AcceptanceMechanism<ImplicitSuperIndividual> am = new ThresholdAcceptance<>();
        MultipleSpeciesIndividualGenerator<ImplicitSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new ImplicitSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<ImplicitSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator(new ImplicitSuperIndividualCreator());
        if (!parallel) {
            return new MultipleSpeciesCoevolvingGA<>(ImplicitSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONSMacro);
        } else {
            return new MultipleSpeciesCoevolvingParallelGA<>(ImplicitSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONSMacro);
        }

    }

    public static Population<ImplicitOperationIndividual> getStartPopulationOperation(MultiJobTerminalProblem problem, List<ImplicitOperationIndividual> startOps) {
        Population<ImplicitOperationIndividual> startPop = null;
        if (startOps == null || startOps.isEmpty()) {
            OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
            startPop = vPop.generatePopulation(numberOfIndOperationsMacro);

        } else {
            startPop = new Population(ImplicitOperationIndividual.class, 0);
            int numberOfGensNeeded = (int) (problem.getOptimizationTimeSlot().getDuration().longValue() / OperationPriorityRules.lengthOfInterval);
            for (int i = 0; i < startOps.size(); i++) {
                ImplicitOperationIndividual startOp = startOps.get(i);
                int start = startOp.getChromosome().size() - numberOfGensNeeded;
                List<OperationPriorityRules.Identifier> subList = startOp.getChromosome().subList(start, startOp.getChromosome().size());
                startPop.add(new ImplicitOperationIndividual(subList));
            }
            OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
            startPop.addAll(vPop.generatePopulation(numberOfIndOperationsMacro-startOps.size()).individuals());
        }
        return startPop;
    }
}
