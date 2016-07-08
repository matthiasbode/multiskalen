/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.EALOSAE;

import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author bode
 */
public class EALOSAEWriter {
    public static void writeEalosaes(Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, File folder){
         /**
         * Ausgabe in Datei
         */
        try {
            File f = new File(folder, "ealosae.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("EALOSAE");
            bw.newLine();
             
            for (RoutingTransportOperation routingTransportOperation : ealosaes.keySet()) {
                bw.write(TimeSlot.longToFormattedDateString(ealosaes.get(routingTransportOperation).getLatestEnd().longValue()) + routingTransportOperation);
                bw.newLine();
            }

            bw.write("#########################");
            bw.close();
            fw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }
}
