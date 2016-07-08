/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.miniTests;

import applications.transshipment.demo.*;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ModeStartPopulationGenerator;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class TestMicroImplicit implements TestClass {

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;

    public File folder;

    public static void main(String[] args) {
        File folder = ProjectOutput.create();

        TestMicroImplicit ga = new TestMicroImplicit(folder);
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 5, false);
        ga.start(problem, 0, 0, 0, true);

        ArrayList<Analysis> analyser = new ArrayList<>();

        analyser.add(new ScheduleWriter());
        analyser.add(new WorkloadPlotter());

        analyser.add(new LoadUnitOrientatedScheduleWriter());

        if (ga.scale == Scale.micro) {
            analyser.add(new CraneAnalysis());
        }

        for (Analysis a : analyser) {
            a.analysis(ga.bestSchedule, problem, folder);
        }

    }

    public TestMicroImplicit(File folder) {
        this.folder = folder;
    }

    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(TestMicroImplicit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestMicroImplicit.class.getName()).log(Level.SEVERE, null, ex);
        }

        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        ImplicitOperationIndividual nextOp = vPop.generatePopulation(1).individuals().iterator().next();

        /**
         * GA für ModeIndividual.
         */
        ModeStartPopulationGenerator mPop = new ModeStartPopulationGenerator(problem);
        ImplicitModeIndividual nextMode = mPop.generatePopulation(1).individuals().iterator().next();

        DNFToStorageTreatment dnfTreat = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfTreat);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, sgs);
        ImplicitSuperIndividual fittestIndividual = new ImplicitSuperIndividual(nextOp, nextMode);
        fittestIndividual.setFitness(eval.computeFitness(fittestIndividual));

        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);
        JSONSerialisierung.exportJSON(new File(folder, "ind.txt"), fittestIndividual.getOperationIndividual().getChromosome(), true);
        JSONSerialisierung.exportJSON(new File(folder, "ind.txt"), fittestIndividual.getModeIndividual().getChromosome(), true);
        bestSchedule = eval.getSchedule(fittestIndividual);
    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return bestSchedule;
    }

}
