/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start.debug;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Visualization.CraneView;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.multiscale.model.Scale;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author bode
 */
public class DebugTest {

    public static void main(String[] args) {
        File folder = ProjectOutput.create();
        Test scheduler = new Test();
        /**
         * Erzeuge Problem
         */
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, Scale.micro, 4, false);

        LoadUnitJobSchedule schedule = scheduler.calc(problem);

        TransshipmentParameter.logger.log(Level.INFO, "Operationen gesamt: " + schedule.getScheduledOperations().size());

        ArrayList<Analysis> analyser = new ArrayList<>();
//        analyser.add(new DurationList());
        analyser.add(new LoadUnitOrientatedScheduleWriter());
        analyser.add(new ScheduleWriter());
        analyser.add(new CraneAnalysis());
//        analyser.add(new JobEalosaeAnalysis());
        analyser.add(new WorkloadPlotter());
//        analyser.add(new HeatMapPlotter());
//        analyser.add(new Train2TrainAnalysis());
//        analyser.add(new AuswertungGenerator());
        analyser.add(new CraneView());
        for (Analysis a : analyser) {
            a.analysis(schedule, problem, folder);
        }

        System.out.println("Anzahl an DNF-Jobs:" + schedule.getDnfJobs().size());
        System.out.println("Anzahl an eingeplanter Jobs:" + schedule.getScheduledJobs().size());

    }
}
