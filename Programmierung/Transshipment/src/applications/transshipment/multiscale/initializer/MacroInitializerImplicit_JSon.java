/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.initializer;

import applications.transshipment.demo.ops.ImplicitOperationGA;
import applications.transshipment.demo.modes.IntegerModeGA;
import applications.transshipment.ga.directModeImplicitOps.DirectModeImplicitOpsSuperIndividual;
import applications.transshipment.ga.directModeImplicitOps.DirectModeImplicitOpsSuperIndividualCreator;
import applications.transshipment.ga.directModeImplicitOps.MinEvaluationDirectModeImplicitOpsSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
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
import ga.individuals.IntegerIndividual;
import java.util.LinkedHashMap;

/**
 *
 * @author bode
 */
public class MacroInitializerImplicit_JSon {

    public static int numberOfIndOperationsMacro = 20;
    public static int numberOfIndModesMacro = 10;
    public static int GENERATIONSMacro = 8;

    public static MultipleSpeciesCoevolvingParallelGA<DirectModeImplicitOpsSuperIndividual> initMacro(MultiJobTerminalProblem problem, Population<ImplicitOperationIndividual> startOps, Population<IntegerIndividual> startmodes) {

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationDirectModeImplicitOpsSuperIndividual eval = new MinEvaluationDirectModeImplicitOpsSuperIndividual(problem, defaultScheduleScheme);

        LinkedHashMap<Population<? extends Individual>, GABundle> species = new LinkedHashMap<>();

        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        ImplicitOperationGA gaOps = new ImplicitOperationGA();
        GABundle<ImplicitOperationIndividual> bundleOps = gaOps.getGA(vPop, true);
        species.put(vPop.generatePopulation(numberOfIndOperationsMacro), bundleOps);
        /**
         * GA für ModeIndividual.
         */
        applications.transshipment.ga.direct.individuals.ModeStartPopulationGenerator mPop = new applications.transshipment.ga.direct.individuals.ModeStartPopulationGenerator(problem, problem.notDirectlyTransportable, true);
        IntegerModeGA gaMode = new IntegerModeGA();
        GABundle<IntegerIndividual> bundleMode = gaMode.getGA(mPop, true);
        species.put(mPop.generatePopulation(numberOfIndModesMacro), bundleMode);

        /**
         * Coevolving GA.
         */
        AcceptanceMechanism<DirectModeImplicitOpsSuperIndividual> am = new ThresholdAcceptance<>();
        MultipleSpeciesIndividualGenerator<DirectModeImplicitOpsSuperIndividual> superIndividualGenerator = new FittestIndividualSuperIndividualGenerator<>(new DirectModeImplicitOpsSuperIndividualCreator());
        MultipleSpeciesIndividualGenerator<DirectModeImplicitOpsSuperIndividual> startSuperIndividualGenerator = new RandomSuperIndividualGenerator(new DirectModeImplicitOpsSuperIndividualCreator());
        MultipleSpeciesCoevolvingParallelGA<DirectModeImplicitOpsSuperIndividual> alg = new MultipleSpeciesCoevolvingParallelGA<>(DirectModeImplicitOpsSuperIndividual.class, species, eval, superIndividualGenerator, startSuperIndividualGenerator, am, GENERATIONSMacro);
        return alg;
    }
}
