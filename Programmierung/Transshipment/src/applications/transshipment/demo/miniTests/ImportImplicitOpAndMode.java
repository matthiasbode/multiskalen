/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.miniTests;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.DNF.DNF_Occurrence;
import applications.transshipment.analysis.Problem.LoadUnitAvailableTimes;
import applications.transshipment.analysis.Problem.TrainAnalyser;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Visualization.CraneView;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class ImportImplicitOpAndMode extends Application implements TestClass {

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;
    public File folder = ProjectOutput.create();
    public ImplicitOperationIndividual opInd;
    public ImplicitModeIndividual modImplicit;
    ImplicitSuperIndividual superInd;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        TransshipmentParameter.DEBUG = true;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Auswählen der Operationen-Datei");
        File jsonFile = chooser.showOpenDialog(primaryStage);

        FileChooser chooser2 = new FileChooser();
        chooser2.setTitle("Auswählen der Routen-Datei");
        File jsonFile2 = chooser2.showOpenDialog(primaryStage);

        Type listType = new TypeToken<ArrayList<OperationPriorityRules.Identifier>>() {
        }.getType();
        List<OperationPriorityRules.Identifier> list = JSONSerialisierung.importJSON(new FileInputStream(jsonFile), listType);
        System.out.println(list);
        opInd = new ImplicitOperationIndividual(list);

        Type listType2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
        }.getType();
        List<LoadUnitJobPriorityRules.Identifier> list2 = JSONSerialisierung.importJSON(new FileInputStream(jsonFile2), listType2);
        System.out.println(list2);
        modImplicit = new ImplicitModeIndividual(list2);

        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, this.scale, 4, false);
        ArrayList<Analysis> analyser = new ArrayList<>();

        analyser.add(new WorkloadPlotter());
        analyser.add(new CraneAnalysis());

        analyser.add(new ScheduleWriter());
        analyser.add(new LoadUnitOrientatedScheduleWriter());

        analyser.add(new LoadUnitAvailableTimes());
        analyser.add(new DNF_Occurrence());
        if (this.scale == Scale.micro) {
            analyser.add(new CraneView());
        }
        new TrainAnalyser().analysis(bestSchedule, problem, folder, opInd);

        this.start(problem, 0, 0, 0, true);

        for (Analysis a : analyser) {
            a.analysis(this.getBestSchedule(), problem, this.folder);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINE);

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(ImportImplicitOpAndMode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ImportImplicitOpAndMode.class.getName()).log(Level.SEVERE, null, ex);
        }

        superInd = new ImplicitSuperIndividual(opInd, modImplicit);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(problem, sgs);

        this.bestSchedule = eval.getSchedule(superInd);
        TransshipmentParameter.logger.info("############# Ende #################");
        TransshipmentParameter.logger.info(bestSchedule.getDnfJobs().size() + "/" + bestSchedule.getScheduledJobs().size() + "/" + problem.getJobs().size());

    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return this.bestSchedule;
    }

}
