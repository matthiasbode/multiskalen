/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Visualization;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.basics.util.LoadUnitMovementCalculator;
import applications.transshipment.model.basics.util.LoadUnitPositionAndOrientation3DInTime;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.geometry.PositionAndOrientation3DInTime;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author Matthias
 */
public class KeyFrameWriter implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        HashMap<LoadUnit, List<LoadUnitPositionAndOrientation3DInTime>> keyPoints = LoadUnitMovementCalculator.getKeyPoints(schedule);
        File f = new File(folder, "Positions.txt");
        FileWriter fw;
        try {
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("Schedule");
            bw.newLine();
            bw.write("#########################");
            bw.newLine();
            bw.newLine();
            bw.newLine();
            for (LoadUnit lu : keyPoints.keySet()) {
                bw.write(lu.getID());
                bw.newLine();
                bw.write("-----------------");
                bw.newLine();
                for (LoadUnitPositionAndOrientation3DInTime position : keyPoints.get(lu)) {
                    bw.write(TimeSlot.longToFormattedDateString(position.getTime())+"\t"+position.getOperation().getClass().getSimpleName() + "\t" + position.getOperation().getId() +"\t" + position.getPosition() +"\t"+ position.getInterpolator());
                    bw.newLine();
                }
                bw.newLine();
            }
            
            
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(KeyFrameWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

//        JSONSerialisierung.exportJSON(new File(folder, "LoadUnitPositions.json"), keyPoints, true);
    }

}
