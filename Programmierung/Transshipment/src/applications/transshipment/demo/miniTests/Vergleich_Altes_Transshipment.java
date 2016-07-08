/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.miniTests;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.demo.TestClass;
import applications.transshipment.ga.directModeImplicitOps.DirectModeImplicitOpsSuperIndividual;
import applications.transshipment.ga.directModeImplicitOps.MinEvaluationDirectModeImplicitOpsSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.generator.projekte.duisburg.DuisburgTrainGenerator;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import ga.individuals.IntegerIndividual;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class Vergleich_Altes_Transshipment implements TestClass {

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;
    public File folder = ProjectOutput.create();

    public static void main(String[] args) {
        DuisburgGenerator g = new DuisburgGenerator();

        Vergleich_Altes_Transshipment ga = new Vergleich_Altes_Transshipment();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 4, false);

        ga.start(problem, 0, 0, 0, true);

        ArrayList<Analysis> analyser = new ArrayList<>();

//        analyser.add(new MaxDurationComparatorAnalysis());
        analyser.add(new WorkloadPlotter());
//        analyser.add(new LoadUnitOrientatedScheduleWriter());
        analyser.add(new ScheduleWriter());
        analyser.add(new CraneAnalysis());
//        analyser.add(new Train2TrainAnalysis());
//        analyser.add(new TrainAnalyser());
//        analyser.add(new LoadUnitAvailableTimes());
//        analyser.add(new DNF_Occurrence());
        if (ga.scale == Scale.micro) {
//            analyser.add(new CraneView());
        }
//        analyser.add(new JobAnalysis());

        for (Analysis a : analyser) {
            a.analysis(ga.getBestSchedule(), problem, ga.folder);
        }

    }

    @Override
    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE, folder);

        InputStream resourceAsStream = DuisburgTrainGenerator.class.getResourceAsStream("routen/Routes_Duisburg.json");
        HashMap<String, Double> importJSON = JSONSerialisierung.importJSON(resourceAsStream, HashMap.class);

        List<Integer> routings = new ArrayList<>();

        for (LoadUnitJob job : problem.getJobs()) {
            double value = importJSON.get(job.getLoadUnit().getID());
            routings.add((int) value - 1);
        }

        IntegerIndividual modeInd = new IntegerIndividual(routings);

        OperationPriorityRules.Identifier[] ops = new OperationPriorityRules.Identifier[]{OperationPriorityRules.Identifier.LST}; //

        ImplicitOperationIndividual opInd = new ImplicitOperationIndividual(Arrays.asList(ops));

        DirectModeImplicitOpsSuperIndividual fittestIndividual = new DirectModeImplicitOpsSuperIndividual(opInd, modeInd);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationDirectModeImplicitOpsSuperIndividual eval = new MinEvaluationDirectModeImplicitOpsSuperIndividual(problem, defaultScheduleScheme);
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
