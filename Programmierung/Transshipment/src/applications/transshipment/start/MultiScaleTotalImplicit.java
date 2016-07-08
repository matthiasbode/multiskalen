/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.DNF.DNFFromTrainToTrain;
import applications.transshipment.analysis.Problem.LoadUnitAvailableTimes;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.multiscale.MultiScaleScheduler;
import static applications.transshipment.multiscale.MultiScaleScheduler.numberOfRoutes;
import applications.transshipment.multiscale.initializer.MicroInitializerImplicit;
import applications.transshipment.multiscale.model.Scale;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author bode
 */
public class MultiScaleTotalImplicit {

    public static void main(String[] args) {
        File folder = ProjectOutput.create("Implizit_");
        TransshipmentParameter.initializeLogger(Level.FINE, folder);

        MicroInitializerImplicit.GENERATIONSMicro = 15;
        MicroInitializerImplicit.numberOfIndOperationsMicro = 60;
        MultiScaleScheduler scheduler = new MultiScaleScheduler(folder, false);
//        MultiScaleSchedulerImplicitMacroDirectMicro scheduler = new MultiScaleSchedulerImplicitMacroDirectMicro();
//        MultiScaleSchedulerImplicitMacroDirectMicroDebugImport scheduler = new MultiScaleSchedulerImplicitMacroDirectMicroDebugImport();
        /**
         * Erzeuge Problem
         */
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, Scale.micro, numberOfRoutes);
        LoadUnitJobSchedule schedule = scheduler.calc(problem);

        TransshipmentParameter.logger.log(Level.INFO, "Operationen gesamt: " + schedule.getScheduledOperations().size());

        ArrayList<Analysis> analyser = new ArrayList<>();

        analyser.add(new ScheduleWriter());
        analyser.add(new LoadUnitOrientatedScheduleWriter());
        analyser.add(new WorkloadPlotter());
        analyser.add(new CraneAnalysis());
        analyser.add(new LoadUnitAvailableTimes());
        analyser.add(new DNFFromTrainToTrain());

        for (Analysis a : analyser) {
            a.analysis(schedule, problem, folder);
        }

    }
}
