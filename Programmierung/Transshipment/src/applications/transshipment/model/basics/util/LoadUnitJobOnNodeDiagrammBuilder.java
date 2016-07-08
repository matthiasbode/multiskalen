/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.transshipment.model.LoadUnitJob;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.transshipment.model.structs.TrainType;
import java.io.File;
import java.util.Collection;
import org.util.ExportToYed;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class LoadUnitJobOnNodeDiagrammBuilder {

    public static JobOnNodeDiagramm<LoadUnitJob> build(Collection<LoadUnitJob> jobs) {
        JobOnNodeDiagramm<LoadUnitJob> graph = new JobOnNodeDiagramm<>();
        for (LoadUnitJob job : jobs) {
            graph.addVertex(job);
        }
        for (LoadUnitJob job1 : jobs) {

            for (LoadUnitJob job2 : jobs) {
                if (job1.getLoadUnit().equals(job2.getLoadUnit())) {
                    continue;
                }

                /**
                 * Wenn das Ziel von Job1 gleich dem Start von Job2
                 * ist, muss Job 2 zuerst abgearbeitet werden.
                 */
                if (job1.getDestination().equals(job2.getOrigin())) {
                    if (job1.getLoadUnit().getDestination() instanceof TrainType) {
                        graph.addEdge(new Pair<>(job2, job1));
                    }
                }

            }
        }
        ExportToYed.exportToGraphML(graph,  "C:\\Users\\Bode\\Documents\\Promo\\jon.graphml");
        return graph;
    }
    
}
