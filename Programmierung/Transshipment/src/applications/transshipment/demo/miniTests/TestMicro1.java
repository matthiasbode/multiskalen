/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.miniTests;

import applications.mmrcsp.model.basics.JoNComponent;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Visualization.CraneView;
import applications.transshipment.analysis.DNF.DNFFromTrainToTrain;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.demo.TestClass;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.ga.permutationModeImplicitOps.MinEvaluationPermutationMixed;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeImplicitOpsSuperIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class TestMicro1 implements TestClass {

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;
    File folder = ProjectOutput.create();

    public static void main(String[] args) {
        TestMicro1 ga = new TestMicro1();
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 5, false);
        ga.start(problem, 0, 0, 0, true);

        ArrayList<Analysis> analyser = new ArrayList<>();

        analyser.add(new WorkloadPlotter());
        analyser.add(new LoadUnitOrientatedScheduleWriter());
        analyser.add(new ScheduleWriter());
        analyser.add(new CraneAnalysis());
//        analyser.add(new Train2TrainAnalysis());
        analyser.add(new CraneView());
        analyser.add(new DNFFromTrainToTrain());

        for (Analysis a : analyser) {
            a.analysis(ga.getBestSchedule(), problem, ga.folder);
        }

    }

    @Override
    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(TestMicro1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestMicro1.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Set<LoadUnitJob>> connectionComponents = new ArrayList<>();
        for (JoNComponent<LoadUnitJob> joNComponent : problem.getJobOnNodeDiagramm().getConnectionComponents()) {
            connectionComponents.add(new HashSet<>(joNComponent.vertexSet()));
        }

        OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
        PermutationModeStartPopulationGenerator mPop = new PermutationModeStartPopulationGenerator(connectionComponents);

        ImplicitOperationIndividual nextOp = vPop.generatePopulation(1).individuals().iterator().next();
        PermutationModeIndividual nextMode = mPop.generatePopulation(1).individuals().iterator().next();

        PermutationModeImplicitOpsSuperIndividual fittestIndividual = new PermutationModeImplicitOpsSuperIndividual(nextOp, nextMode);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationPermutationMixed eval = new MinEvaluationPermutationMixed(problem, defaultScheduleScheme);
        fittestIndividual.setFitness(eval.computeFitness(fittestIndividual));

        this.bestSchedule = eval.getSchedule(fittestIndividual);
        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(bestSchedule.getDnfJobs().size() + "/" + bestSchedule.getScheduledJobs().size() + "/" + problem.getJobs().size());
        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);

    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return this.bestSchedule;
    }

}
