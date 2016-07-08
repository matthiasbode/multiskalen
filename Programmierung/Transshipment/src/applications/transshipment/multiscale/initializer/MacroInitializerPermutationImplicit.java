/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.initializer;

import applications.transshipment.demo.ops.ImplicitOperationGA;
import applications.transshipment.demo.modes.PermutationModeGA;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.ga.permutationModeImplicitOps.MinEvaluationPermutationMixed;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeImplicitOpsSuperIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeImplicitOpsSuperIndividualCreator;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeStartPopulationGenerator;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;

import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.coevolving.GABundle;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.algorithms.coevolving.individuals.FittestIndividualSuperIndividualGenerator;
import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.algorithms.coevolving.individuals.RandomSuperIndividualGenerator;
import ga.basics.Population;
import ga.individuals.Individual;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bode
 */
public class MacroInitializerPermutationImplicit {

    public static int numberOfIndOperationsMacro = 5;
    public static int numberOfIndModesMacro = 5;
    public static int GENERATIONSMacro = 2;

    public static MultipleSpeciesCoevolvingParallelGA<PermutationModeImplicitOpsSuperIndividual> initMacro(List<Set<LoadUnitJob>> connectionComponents, MultiJobTerminalProblem problem, Population<ImplicitOperationIndividual> startOps, Population<PermutationModeIndividual> startmodes, long currentStart) {

        PermutationModeStartPopulationGenerator mPop = new PermutationModeStartPopulationGenerator(connectionComponents);
        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
//        Population<ImplicitOperationIndividual> ops = vPop.generatePopulation(numberOfIndOperationsMacro);
//        Population<PermutationModeIndividual> mods = mPop.generatePopulation(numberOfIndModesMacro);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);

        Transshipment_ImplicitScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationPermutationMixed eval = new MinEvaluationPermutationMixed(problem, defaultScheduleScheme);

        LinkedHashMap<Population<? extends Individual>, GABundle> species = new LinkedHashMap<>();

        ImplicitOperationGA gaOps = new ImplicitOperationGA();
        GABundle<ImplicitOperationIndividual> bundleOps = gaOps.getGA(vPop, true);
        species.put(vPop.generatePopulation(numberOfIndOperationsMacro), bundleOps);

        /**
         * GA für ModeIndividual.
         */
        PermutationModeGA gaMode = new PermutationModeGA();
        GABundle<PermutationModeIndividual> bundleMode = gaMode.getGA(mPop, true);
        species.put(mPop.generatePopulation(numberOfIndModesMacro), bundleMode);

        /**
         * Coevolving GA.
         */
        MultipleSpeciesIndividualGenerator<PermutationModeImplicitOpsSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new PermutationModeImplicitOpsSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<PermutationModeImplicitOpsSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator(new PermutationModeImplicitOpsSuperIndividualCreator());
        AcceptanceMechanism<PermutationModeImplicitOpsSuperIndividual> am = new ThresholdAcceptance<>();
        MultipleSpeciesCoevolvingParallelGA<PermutationModeImplicitOpsSuperIndividual> alg = new MultipleSpeciesCoevolvingParallelGA<>(PermutationModeImplicitOpsSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONSMacro);
        return alg;
    }
}
