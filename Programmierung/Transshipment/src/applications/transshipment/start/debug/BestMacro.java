/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start.debug;

import applications.mmrcsp.model.basics.JoNComponent;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
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
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.multiscale.transform.TransformMicroToMacro;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.FieldElement;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class BestMacro implements TestClass {

    public Scale scale = Scale.macro;

    public LoadUnitJobSchedule bestSchedule;
    public File folder = ProjectOutput.create();
    public ImplicitOperationIndividual opInd;
    public ImplicitModeIndividual modImplicit;
    public LoadUnitJobSchedule alternative;
    public LoadUnitJobSchedule macroSchedule;

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
            streamOp = Test.class.getResourceAsStream("macroOp2DNF.txt");
            streamMode = Test.class.getResourceAsStream("macroMode2DNF.txt");
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

        MultiJobTerminalProblem originalProblem = g.generateTerminalProblem(parameters, Scale.micro, TransshipmentParameter.numberOfRoutes, false);
//        
//        List<JoNComponent<LoadUnitJob>> connectionComponents = originalProblem.getJobOnNodeDiagramm().getConnectionComponents();
//        for (JoNComponent<LoadUnitJob> connectionComponent : connectionComponents) {
//            System.out.println(connectionComponent);
//        }

        LoadUnitJobSchedule microAllOverSchedule = MultiJobTerminalProblemFactory.createNewSchedule(originalProblem);

        TransformMicroToMacro transform = new TransformMicroToMacro();
        List<LoadUnitJob> jobsToSchedule = new ArrayList<>(originalProblem.getJobs());
        jobsToSchedule.addAll(originalProblem.getStammRelation());

        MultiJobTerminalProblem macroProblem = MultiJobTerminalProblemFactory.createMacroForMultiScale(microAllOverSchedule, originalProblem, jobsToSchedule, originalProblem.getOptimizationTimeSlot());
        macroSchedule = transform.transform(microAllOverSchedule, macroProblem);

        ScheduleWriter sw = new ScheduleWriter();

        File file = new File(folder, "mirko");
        file.mkdir();
        sw.analysis(microAllOverSchedule, originalProblem, file);

        File file2 = new File(folder, "makro");
        file2.mkdir();
        sw.analysis(macroSchedule, macroProblem, file2);

        alternative = MultiJobTerminalProblemFactory.createNewSchedule(macroProblem);

        /**
         * Alternative ist gut, macroSchedule nicht
         */
        System.out.println("###################");
        System.out.println(alternative.equals(macroSchedule));

//        for (LoadUnitJob key : originalProblem.getJobTimeWindows().keySet()) {
//            TimeSlot originalEalosae = originalProblem.getJobTimeWindows().get(key);
//            TimeSlot macroEalosae = macroProblem.getJobTimeWindows().get(key);
//            if (!originalEalosae.equals(macroEalosae)) {
//                System.out.println("passt nicht");
//            }
//        }
//
//        for (Operation scheduledOperation : macroSchedule.getScheduledOperations()) {
//            FieldElement macroTransformed = macroSchedule.get(scheduledOperation);
//
//            StoreOperation scheduledOperationStore = (StoreOperation) scheduledOperation;
//
//            Operation other = null;
//
//            for (Operation scheduledOperation1 : alternative.getScheduledOperations()) {
//                StoreOperation scheduledOperationStore2 = (StoreOperation) scheduledOperation1;
//                if (scheduledOperationStore.getLoadUnit().equals(scheduledOperationStore2.getLoadUnit())) {
//                    if (scheduledOperationStore.getResource().equals(scheduledOperationStore2.getResource())) {
//                        other = scheduledOperationStore2;
//                        break;
//                    }
//                }
//            }
//
//            if (other == null) {
//                System.out.println("keine andere gefunden");
//                for (Operation scheduledOperation1 : alternative.getScheduledOperations()) {
//                    StoreOperation scheduledOperationStore2 = (StoreOperation) scheduledOperation1;
//                    if (scheduledOperationStore.getLoadUnit().equals(scheduledOperationStore2.getLoadUnit())) {
//                        for (LoadUnitJob stammRelation : originalProblem.getStammRelation()) {
//                            if (stammRelation.getLoadUnit().equals(scheduledOperationStore.getLoadUnit())) {
//                                System.out.println("drin!!!");
//                                break;
//                            }
//                        }
//
//                        System.out.println("Operation1 " + scheduledOperationStore.getResource());
//                        System.out.println("Operation2 " + scheduledOperationStore2.getResource());
//
//                        break;
//                    }
//                }
//            }
//
//            FieldElement pure = alternative.get(other);
//
//            if (pure == null || macroTransformed == null) {
//                System.out.println(scheduledOperation);
//                System.out.println("Von: " + scheduledOperationStore.getLoadUnit().getOrigin());
//                System.out.println("Nach: " + scheduledOperationStore.getLoadUnit().getDestination());
//                System.out.println(">>>>>NULL<<<" + other);
//                System.out.println("trans" + macroTransformed);
//                System.out.println("pure" + pure);
//            } else {
//                if (pure.equals(macroTransformed)) {
//                    System.out.println("<<<<<ok>>>>>");
//                } else {
//                    System.out.println(scheduledOperation);
//                    System.out.println("Von: " + scheduledOperationStore.getLoadUnit().getOrigin());
//                    System.out.println("Nach: " + scheduledOperationStore.getLoadUnit().getDestination());
//                    System.out.println("ungleich");
//                    System.out.println("trans" + macroTransformed);
//                    System.out.println("pure" + pure);
//
//                }
//            }
//            System.out.println("-------------");
//
//        }
        ArrayList<Analysis> analyser = new ArrayList<>();

//        analyser.add(new MaxDurationComparatorAnalysis());
        analyser.add(
                new WorkloadPlotter());
//        analyser.add(new LoadUnitOrientatedScheduleWriter());
        analyser.add(
                new ScheduleWriter());
        analyser.add(
                new CraneAnalysis());
////        analyser.add(new Train2TrainAnalysis());
//
//        analyser.add(new LoadUnitAvailableTimes());
//        analyser.add(new DNF_Occurrence());
//        if (this.scale == Scale.micro) {
//            analyser.add(new CraneView());
//        }
//        analyser.add(new JobAnalysis());

        this.start(macroProblem, 0, 0, 0, true);

        for (Analysis a : analyser) {
            a.analysis(this.getBestSchedule(), macroProblem, this.folder);
        }

        TrainAnalyser trainAnalyser = new TrainAnalyser();
        trainAnalyser.analysis(bestSchedule, macroProblem, folder, opInd);
    }

    public static void main(String[] args) {
        BestMacro b = new BestMacro();
        b.start();
    }

    @Override
    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);

        } catch (IOException ex) {
            Logger.getLogger(BestMacro.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(BestMacro.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        ImplicitSuperIndividual superInd = new ImplicitSuperIndividual(opInd, modImplicit);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(macroSchedule, problem, sgs);
        superInd.setFitness(eval.computeFitness(superInd));

        this.bestSchedule = eval.getSchedule(superInd);
        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(bestSchedule.getDnfJobs().size() + "/" + bestSchedule.getScheduledJobs().size() + "/" + problem.getJobs().size());
        System.out.println(Arrays.toString(superInd.getFitnessVector()));
//        TransshipmentParameter.logger.info(superInd.getFitness() + "\t Fittest Individuum:" + superInd);

    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return this.bestSchedule;
    }

}
