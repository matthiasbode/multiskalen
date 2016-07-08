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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */
public class DistanceAnalysis implements Analysis {

    DecimalFormat f = new DecimalFormat("#0.000", DecimalFormatSymbols.getInstance(Locale.US));

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
        for (LoadUnitJob job : lus) {
            double distance = job.getOrigin().getCenterOfGeneralOperatingArea().distance(job.getDestination().getCenterOfGeneralOperatingArea());
//            double bewertung = Double.NaN;
//            if (schedule.routingsPerJob != null) {
//                bewertung = schedule.routingsPerJob.get(job).getWeight();
//            }
            Point2d centerOfGeneralOperatingAreaO = job.getOrigin().getCenterOfGeneralOperatingArea();
            String ori = "(" + f.format(centerOfGeneralOperatingAreaO.x) + ", " + f.format(centerOfGeneralOperatingAreaO.y) + ")";
            Point2d centerOfGeneralOperatingAreaD = job.getDestination().getCenterOfGeneralOperatingArea();
            String des = "(" + f.format(centerOfGeneralOperatingAreaD.x) + ", " + f.format(centerOfGeneralOperatingAreaD.y) + ")";
 

            sb += (job.getLoadUnit().getID() + "\t" + ori + "\t" + des + "\t" + f.format(distance) + "\n");
        }
        try {
            File f = new File(folder, "JobAnalysis.txt");
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
