/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.transshipment.analysis.Analysis;
import static applications.transshipment.analysis.Workload.CraneAnalysis.getMeanTransportDuration;
import static applications.transshipment.analysis.Workload.CraneAnalysis.getMittlereRuestZeit;
import static applications.transshipment.analysis.Workload.CraneAnalysis.getNumberOfTansportOperations;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.setup.DefaultIdleSettingUpOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author bode
 */
public class LCSAuswertung implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {

        try {
            File f = new File(folder, "LCSData.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("Krandaten");
            bw.newLine();
            bw.write("#########################");
            bw.newLine();
            List<Resource> resources = new ArrayList<>(schedule.getResources());
            Collections.sort(resources, new Comparator<Resource>() {

                @Override
                public int compare(Resource o1, Resource o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

            for (Resource res : resources) {
                if (res instanceof Agent) {
                    Agent agent = (Agent) res;
                    long busy = 0l;
                    int ops = 0;
                    for (Operation o : schedule.getOperationsForResource(res)) {
                        busy += o.getDuration().longValue();
                        if (!(o instanceof DefaultIdleSettingUpOperation)) {
                            ops++;
                        }
                    }
                    bw.write(agent.toString() + ":");
                    bw.newLine();
                    bw.write(ops + " Operations");
                    bw.newLine();
                    long all = agent.getTemporalAvailability().getAllOverTimeSlot().getDuration().longValue();
                    bw.write(Math.round((double) busy / (double) all * 10000.0) / 100.0 + "% Workload");
                    bw.newLine();
                    bw.write("---------------------------");
                    bw.newLine();
                }
            }
            bw.close();
            fw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

    }

}
