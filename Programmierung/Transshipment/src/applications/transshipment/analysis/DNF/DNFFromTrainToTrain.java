/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.DNF;

import applications.transshipment.analysis.Analysis;
import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.structs.Train;
import applications.transshipment.model.structs.TrainType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author bode
 */
/**
 *
 * @author bode
 */
public class DNFFromTrainToTrain implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        HashMap<Train, Integer> FromTrain = new HashMap<>();
        for (Train train : problem.getTrains()) {
            FromTrain.put(train, 0);
        }

        HashMap<Train, Integer> toTrain = new HashMap<>();
        for (Train train : problem.getTrains()) {
            toTrain.put(train, 0);
        }

        Set<LoadUnitJob> jobsThatCouldNotBeScheduled = schedule.getDnfJobs();

        for (LoadUnitJob job : jobsThatCouldNotBeScheduled) {
            if (job.getLoadUnit().getDestination() instanceof TrainType) {
                Train train = Train.getTrain(job.getLoadUnit().getDestination());
                if (train == null) {
                    throw new UnknownError(job.getLoadUnit().getDestination().toString());
                }
                Integer currentCount = toTrain.get(train);
                toTrain.put(train, currentCount + 1);
            }
        }

        for (LoadUnitJob job : jobsThatCouldNotBeScheduled) {
            if (job.getLoadUnit().getOrigin() instanceof TrainType) {
                Train train = Train.getTrain(job.getLoadUnit().getOrigin());
                if (train == null) {
                    throw new UnknownError(job.getLoadUnit().getDestination().toString());
                }
                Integer currentCount = FromTrain.get(train);
                FromTrain.put(train, currentCount + 1);
            }
        }

        ArrayList<Train> trains = new ArrayList<>(FromTrain.keySet());
        Collections.sort(trains, new Comparator<Train>() {

            @Override
            public int compare(Train o1, Train o2) {
                return Integer.compare(o1.getNumber(), o2.getNumber());
            }
        });

        String str = "DNF vom Zug:\n";
        for (Train train : trains) {
            Integer dnfs = FromTrain.get(train);
            str += train.getNumber() + "\t" + dnfs + "\n";
        }
        str += "DNF zum Zug:\n";
        for (Train train : trains) {
            Integer dnfs = toTrain.get(train);
            str += train.getNumber() + "\t" + dnfs + "\n";
        }

        System.out.println(str);

        try {
            File f = new File(folder, "DNF.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("DNF");
            bw.newLine();
            bw.write("#########################");
            bw.newLine();
            bw.write(str);
            bw.close();
            fw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

}
