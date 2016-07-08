/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale;

import static applications.transshipment.TransshipmentParameter.numberOfRoutes;
import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ModeStartPopulationGenerator;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;

import applications.transshipment.multiscale.model.Scale;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class Demo {

    public static void main(String[] args) {
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem originalProblem = g.generateTerminalProblem(parameters, Scale.micro, numberOfRoutes);
        List<LoadUnitJob> jobsToSchedule = new ArrayList<>(originalProblem.getJobs());
        System.out.println("Gesamt-TimeSlot: " + originalProblem.getTerminal().getTemporalAvailability());
        /**
         * Der endgültig Plan, der immer aktualisiert wird.
         */
        LoadUnitJobSchedule schedule = MultiJobTerminalProblemFactory.createNewSchedule(originalProblem);

        MultiJobTerminalProblem problem = MultiJobTerminalProblemFactory.createMacroForMultiScale(schedule, originalProblem, jobsToSchedule, originalProblem.getOptimizationTimeSlot());
        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(schedule, problem, defaultScheduleScheme);

        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        ModeStartPopulationGenerator mPop = new ModeStartPopulationGenerator(problem);

        ArrayList<ImplicitOperationIndividual> individualsOps = new ArrayList<>(vPop.generatePopulation(1).individuals());
        ArrayList<ImplicitModeIndividual> individualsMods = new ArrayList<>(mPop.generatePopulation(1).individuals());

        for (int i = 0; i < individualsMods.size(); i++) {
            ImplicitModeIndividual indMod = individualsMods.get(i);
            ImplicitOperationIndividual indOps = individualsOps.get(i);
            ImplicitSuperIndividual superI = new ImplicitSuperIndividual(indOps, indMod);

            double computeFitness = eval.computeFitness(superI)[0];
            LoadUnitJobSchedule schedule1 = eval.getSchedule(superI);
            System.out.println(computeFitness);
        }

    }
}
