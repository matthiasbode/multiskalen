/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Problem;

import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs.MinAvailabilityComparator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point2d;

/**
 *
 * @author Matthias
 */
public class MaxDurationComparatorAnalysis implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        String sb = "";
        ArrayList<LoadUnitJob> lus = new ArrayList<>(problem.getJobs());
        Collections.sort(lus, new Comparator<LoadUnitJob>() {

            @Override
            public int compare(LoadUnitJob o1, LoadUnitJob o2) {
                return o1.getLoadUnit().getID().compareTo(o2.getLoadUnit().getID());
            }
        }
        );

        MinAvailabilityComparator<LoadUnitJob> comp = new MinAvailabilityComparator(problem.getJobs(), problem.getJobTimeWindows());

        for (LoadUnitJob loadUnitJob : lus) {
            sb += (loadUnitJob.getLoadUnit().getID() + "\t" + comp.maxDurations.get(loadUnitJob).longValue() + "\n");
        }

        try {
            File f = new File(folder, "MaxDuration.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("Jobs");
            bw.newLine();

            bw.write(sb);

            bw.close();
            fw.close();
        } catch (Exception ex) {
            Logger.getLogger(DistanceAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
