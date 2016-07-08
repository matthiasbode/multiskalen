/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.miniTests;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Visualization.CraneView;
import applications.transshipment.analysis.Workload.FuzzyWorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.demo.TestClass;
import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.start.debug.Test;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class FuzzyTestDuisburg implements TestClass {

    public Scale scale = Scale.macro;

    public LoadUnitJobSchedule bestSchedule;
    public File folder = ProjectOutput.create();

    public static void main(String[] args) {
        TransshipmentParameter.fuzzyMode =  TransshipmentParameter.FuzzyMode.fuzzy;
        TransshipmentParameter.DEBUG = true;
        DuisburgGenerator g = new DuisburgGenerator();

        FuzzyTestDuisburg ga = new FuzzyTestDuisburg();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters,ga.scale, 4, false);

        ga.start(problem, 0, 0, 0, true);

        ArrayList<Analysis> analyser = new ArrayList<>();

//        analyser.add(new MaxDurationComparatorAnalysis());
        analyser.add(new FuzzyWorkloadPlotter());
//        analyser.add(new LoadUnitOrientatedScheduleWriter());
        analyser.add(new ScheduleWriter());
//        analyser.add(new CraneAnalysis());
//        analyser.add(new Train2TrainAnalysis());
//        analyser.add(new TrainAnalyser());
//        analyser.add(new LoadUnitAvailableTimes());
//        analyser.add(new DNF_Occurrence());
        if (ga.scale == Scale.micro) {
            analyser.add(new CraneView());
        }
//        analyser.add(new JobAnalysis());

        for (Analysis a : analyser) {
            a.analysis(ga.getBestSchedule(), problem, ga.folder);
        }

    }

    @Override
    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE, folder);

        
        InputStream streamOp = Test.class.getResourceAsStream("macroOp2DNF.txt");
        InputStream streamMode = Test.class.getResourceAsStream("macroMode2DNF.txt");

        Type listType = new TypeToken<ArrayList<OperationPriorityRules.Identifier>>() {
        }.getType();
        List<OperationPriorityRules.Identifier> list = JSONSerialisierung.importJSON(streamOp, listType);
        System.out.println(list);
        ImplicitOperationIndividual opInd = new ImplicitOperationIndividual(list);

        Type listType2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
        }.getType();
        List<LoadUnitJobPriorityRules.Identifier> list2 = JSONSerialisierung.importJSON(streamMode, listType2);
        System.out.println(list2);
        ImplicitModeIndividual modImplicit = new ImplicitModeIndividual(list2);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, defaultScheduleScheme);
        
        ImplicitSuperIndividual superInd = new ImplicitSuperIndividual(opInd, modImplicit);
        this.bestSchedule =  eval.getSchedule(superInd);
        

       
        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(bestSchedule.getDnfJobs().size() + "/" + bestSchedule.getScheduledJobs().size() + "/" + problem.getJobs().size());
//        TransshipmentParameter.logger.info(fittestIndividual.getFitness() + "\t Fittest Individuum:" + fittestIndividual);

    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return this.bestSchedule;
    }

}
