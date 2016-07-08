/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Problem;

import applications.transshipment.analysis.Problem.GeneratorAuswertung;
import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.problem.multiMode.MultiModeJobProblem;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Analysis;
import static applications.transshipment.analysis.Problem.GeneratorAuswertung.generatePath;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.structs.Train;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author bode
 */
public class Train2TrainAnalysis implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        if (!(problem instanceof MultiJobTerminalProblem)) {
            throw new UnsupportedOperationException("Kann nur auf Terminalprobleme angewandt werden");
        }
        MultiJobTerminalProblem tproblem = (MultiJobTerminalProblem) problem;

        File subfolderEntladungen = new File(folder, "/entladungen");
        File subfolderBeladungen = new File(folder, "/beladungen");

        subfolderEntladungen.mkdirs();
        subfolderBeladungen.mkdirs();

        for (Train t : tproblem.getTrains()) {
            ArrayList<Train> trainsToVis = new ArrayList<Train>();
            trainsToVis.add(t);
            System.out.println(t);
            GeneratorAuswertung generatorA = new GeneratorAuswertung(trainsToVis, new ArrayList<>(tproblem.getTrains()), tproblem.getLoadUnits(), new ArrayList<>(tproblem.getTerminal().getGleise()));
            try {
                ImageIO.write(generatorA.getPlot(GeneratorAuswertung.Modus.entladung), "png", new File(subfolderEntladungen, t.getNumber() + ".png"));
                ImageIO.write(generatorA.getPlot(GeneratorAuswertung.Modus.beladung), "png", new File(subfolderBeladungen, t.getNumber() + ".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}
