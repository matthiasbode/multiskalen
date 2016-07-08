/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.rules.SharedResourceManager;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.demo.miniTests.Vergleich_Altes_Transshipment;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.MicroscopicCraneRule;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.File;

/**
 *
 * @author bode
 */
public class Demo2 {

    public static void main(String[] args) {
        DuisburgGenerator g = new DuisburgGenerator();

        Vergleich_Altes_Transshipment ga = new Vergleich_Altes_Transshipment();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 4, false);

        ga.start(problem, 0, 0, 0, true);
        LoadUnitJobSchedule bestSchedule = ga.getBestSchedule();
        File folder = ProjectOutput.create();
        ScheduleWriter sw = new ScheduleWriter();
        sw.analysis(bestSchedule, problem, folder);
        for (Resource resource : bestSchedule.getResources()) {
            if (resource instanceof Crane) {
                MicroscopicCraneRule rule = (MicroscopicCraneRule) bestSchedule.getHandler().get(resource);
                System.out.println(rule.workingArea);
            }
        }

        System.out.println("----------------");
        LoadUnitJobSchedule copy = new LoadUnitJobSchedule(bestSchedule, new InstanceHandler(problem.getScheduleManagerBuilder()));

        for (Resource resource : copy.getResources()) {
            if (resource instanceof Crane) {
                MicroscopicCraneRule rule = (MicroscopicCraneRule) copy.getHandler().get(resource);
                System.out.println(rule.workingArea);
            }
        }

        File subFolder = new File(folder, "Sub");

        subFolder.mkdirs();

        sw.analysis(copy, problem, subFolder);

    }
}
