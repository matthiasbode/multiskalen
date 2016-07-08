/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import applications.transshipment.*;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.demo.TestGAImplicitOp_PermutationMode;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.multiscale.model.Scale;
import java.io.File;
import java.util.ArrayList;
import javafx.concurrent.Task;

/**
 *
 * @author Matthias
 */
public class MainConsole {

    public static void main(String[] args) {

        WorkingSpace space = new WorkingSpace();

        String scaleS = args[0];
        String numberOfIndOperationsS = args[1];
        String numberOfIndModesS = args[2];
        String GENERATIONSS = args[3];
        String path = args[4];

        if (!"-".equals(path)) {
            space.setPath(path);
        }
        final Scale scale = scaleS.equals("Micro") ? Scale.micro : Scale.macro;
        final File folder = ProjectOutput.create(space.getFolder(), scale.toString());

        final int numberOfIndOperations = Integer.parseInt(numberOfIndOperationsS);
        final int numberOfIndModes = Integer.parseInt(numberOfIndModesS);
        final int GENERATIONS = Integer.parseInt(GENERATIONSS);

        TestGAImplicitOp_PermutationMode ga = new TestGAImplicitOp_PermutationMode(folder);
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, scale, 3, false);
        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);
        ArrayList<Analysis> analyser = new ArrayList<>();
        analyser.add(new WorkloadPlotter());
        analyser.add(new CraneAnalysis());
        analyser.add(new ScheduleWriter());
        for (Analysis a : analyser) {
            a.analysis(ga.bestSchedule, problem, folder);
        }
 
    }

}
