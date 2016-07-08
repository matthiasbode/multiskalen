/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start.debug;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Problem.TrainAnalyser;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
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
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
public class Best implements TestClass {

    public Scale scale = Scale.macro;

    public LoadUnitJobSchedule bestSchedule;
    public File folder = ProjectOutput.create();
    public ImplicitOperationIndividual opInd;
    public ImplicitModeIndividual modImplicit;

    public void start() {
        TransshipmentParameter.DEBUG = true;
//        TransshipmentParameter.allowInsert = true;
//        TransshipmentParameter.TimeStepBasedPriorityDetermination = false;
//        OperationPriorityRules.lengthOfInterval = 2 * 60 * 1000L;
//        TransshipmentParameter.legacy = true;

        InputStream streamOp = Test.class.getResourceAsStream("micro3DNFOp.txt");
        InputStream streamMode = Test.class.getResourceAsStream("micro3DNFMode.txt");
        if (scale.equals(Scale.macro)) {
//         InputStream streamOp = Test.class.getResourceAsStream("macroOp1DNFohneSetup.txt");
//        InputStream streamMode = Test.class.getResourceAsStream("macroMode1DNFohneSetup.txt");

            streamOp = Test.class.getResourceAsStream("0indOp_-1 _0_1.txt");
            streamMode = Test.class.getResourceAsStream("0indMode_-1 _0_1.txt");
//            streamOp = Test.class.getResourceAsStream("macroOp2DNF.txt");
//            streamMode = Test.class.getResourceAsStream("macroMode2DNF.txt");
        }

        Type listType = new TypeToken<ArrayList<OperationPriorityRules.Identifier>>() {
        }.getType();
        List<OperationPriorityRules.Identifier> list = JSONSerialisierung.importJSON(streamOp, listType);
        System.out.println(list);
        opInd = new ImplicitOperationIndividual(list);

        Type listType2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
        }.getType();
        List<LoadUnitJobPriorityRules.Identifier> list2 = JSONSerialisierung.importJSON(streamMode, listType2);
        System.out.println(list2);
        modImplicit = new ImplicitModeIndividual(list2);

        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();

        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, this.scale, TransshipmentParameter.numberOfRoutes, false);

        ArrayList<Analysis> analyser = new ArrayList<>();

//        analyser.add(new MaxDurationComparatorAnalysis());
        analyser.add(new WorkloadPlotter());
//        analyser.add(new LoadUnitOrientatedScheduleWriter());
        analyser.add(new ScheduleWriter());
        analyser.add(new CraneAnalysis());
////        analyser.add(new Train2TrainAnalysis());
//
//        analyser.add(new LoadUnitAvailableTimes());
//        analyser.add(new DNF_Occurrence());
//        if (this.scale == Scale.micro) {
//            analyser.add(new CraneView());
//        }
//        analyser.add(new JobAnalysis());

        this.start(problem, 0, 0, 0, true);
        for (Analysis a : analyser) {
            a.analysis(this.getBestSchedule(), problem, this.folder);
        }

        TrainAnalyser trainAnalyser = new TrainAnalyser();
        trainAnalyser.analysis(bestSchedule, problem, folder, opInd);
    }

    public static void main(String[] args) {
        Best b = new Best();
        b.start();
    }

    @Override
    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(Best.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Best.class.getName()).log(Level.SEVERE, null, ex);
        }

        ImplicitSuperIndividual superInd = new ImplicitSuperIndividual(opInd, modImplicit);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, sgs);
//        superInd.setFitness(eval.computeFitness(superInd));

        this.bestSchedule = eval.getSchedule(superInd);
        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(bestSchedule.getDnfJobs().size() + "/" + bestSchedule.getScheduledJobs().size() + "/" + problem.getJobs().size());
//        TransshipmentParameter.logger.info(superInd.getFitness() + "\t Fittest Individuum:" + superInd);

    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return this.bestSchedule;
    }

}
