/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import math.FieldElement;
import math.LongValue;

/**
 * Gibt die Dauern für Transporte und Rüstfahrten pro Ressource in einer
 * Textdatei aus.
 *
 * @author bode
 */
public class DurationPerConveyanceSystem implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        StringBuilder sb = new StringBuilder();

        for (Resource resource : schedule.getResources()) {
            sb.append("####################");
            sb.append(resource).append("\n");
            FieldElement transportDuration = new LongValue(0);
            FieldElement setUpDuration = new LongValue(0);
            for (Operation operation : schedule.getOperationsForResource(resource)) {
                if (operation instanceof TransportOperation) {
                    transportDuration = transportDuration.add(operation.getDuration());
                }
                if (operation instanceof IdleSettingUpOperation) {
                    setUpDuration = setUpDuration.add(operation.getDuration());
                }
            }
            sb.append("Transportdauer: ");
            sb.append(transportDuration).append("\n");
            sb.append("SetUpDauer: ");
            sb.append(setUpDuration).append("\n");
        }


        File file = new File(folder, "/Auslastung.txt");
        try {

            FileWriter writer = new FileWriter(file);
            writer.write(sb.toString());


            writer.flush();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
