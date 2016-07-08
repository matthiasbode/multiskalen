/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.routeDetermination;

import applications.transshipment.model.LoadUnitJob;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import math.FieldElement;
import org.graph.algorithms.AcyclicTest;
import org.graph.algorithms.ConnectionComponentGenerator;
import org.graph.algorithms.ConnectionComponentGenerator.ConnectionComponent;
import org.graph.directed.DefaultDirectedGraph;

/**
 * Entfernt Zyklen aus einem JobOnNodeDiagramm, in dem innerhalb eines Zyklus
 * der Job, der zuerst ausgeführt werden kann, gesplittet wird.
 *
 * @author bode
 */
public class CycleRemover {

    /**
     * Für Direkte Codierung: Hier werden Jobs aufgeschnitten. 
     * @param problem
     * @return 
     */
    public static List<LoadUnitJob> removeCycles(MultiJobTerminalProblem problem) {

        /**
         * Vorverarbeitung. Aufsplitten von Zyklen, etc.
         */
        ArrayList<LoadUnitJob> firstJobInSplittedCycles = new ArrayList<>();

        /**
         * JobOnNodeGraph.
         */
        JobOnNodeDiagramm jobOnNodeGraph = problem.getJobOnNodeDiagramm();

        /**
         * Generierung der Zusammenhangskomponenten.
         */
        ConnectionComponentGenerator<LoadUnitJob> ccg = new ConnectionComponentGenerator<>(new DefaultDirectedGraph<LoadUnitJob>(jobOnNodeGraph));
        Collection<ConnectionComponentGenerator.ConnectionComponent<LoadUnitJob>> connectionComponents = ccg.calculateComponents();
        ArrayList<ConnectionComponentGenerator.ConnectionComponent<LoadUnitJob>> cyclicComponents = new ArrayList<ConnectionComponentGenerator.ConnectionComponent<LoadUnitJob>>();
        /**
         * Schleife über die Zusammenhangskomponenten.
         */
        for (ConnectionComponentGenerator.ConnectionComponent<LoadUnitJob> connectionComponent : connectionComponents) {
            DefaultDirectedGraph<LoadUnitJob> componentGraph = new DefaultDirectedGraph<>(connectionComponent.getNodes(), connectionComponent.getEdges());
            AcyclicTest test = new AcyclicTest(componentGraph);
            /**
             * Zusammenhangskomponente ist nicht azyklisch. Suche den Job der
             * als erstes ausführbar ist und setze diesen als erstes unter der
             * Berücksichtigung, dass dieser immer aufgesplittet wird
             * (Zwischenabstellung). Bestimme für diesen Job k+1 Routings und
             * lösche das erste Routing.
             */
            LoadUnitJob firstJob = null;
            if (!test.isAcyclic()) {
                cyclicComponents.add(connectionComponent);
                /**
                 * Ersten Job bestimmen.
                 */
                firstJob = componentGraph.vertexSet().iterator().next();
                FieldElement firstPossibleStart = firstJob.getOrigin().getTemporalAvailability().getFromWhen();

                for (LoadUnitJob loadUnitJob : componentGraph.vertexSet()) {
                    FieldElement candidateFirstStart = loadUnitJob.getOrigin().getTemporalAvailability().getFromWhen();
                    if (candidateFirstStart.isLowerThan(firstPossibleStart)) {
                        firstPossibleStart = candidateFirstStart;
                        firstJob = loadUnitJob;
                    }
                }

                /**
                 * Markierung, welcher Job als erstes gestartet werden muss.
                 */
                firstJobInSplittedCycles.add(firstJob);

                TransshipmentParameter.logger.finer("Angepasste Zusammenhangskomponente, da Zyklus enthalten: ");
                for (LoadUnitJob loadUnitJob : componentGraph.vertexSet()) {
                    TransshipmentParameter.logger.finer(loadUnitJob.toString());
                }
                TransshipmentParameter.logger.log(Level.FINER, "Erste Job, der abgehandelt werden muss: " + firstJob);
                TransshipmentParameter.logger.finer("---------------");

            }

        }
//        System.out.println("Anzahl von aufgelösten Zyklen: " + firstJobInSplittedCycles.size());
//        System.out.println("Zyklen:");
        for (ConnectionComponent<LoadUnitJob> connectionComponent : cyclicComponents) {
            String min = "Z";
            for (LoadUnitJob loadUnitJob : connectionComponent.getNodes()) {
                if (loadUnitJob.getLoadUnit().getID().compareTo(min) < 0) {
                    min = loadUnitJob.getLoadUnit().getID();
                }
            }
//            System.out.println(min + ":\t" + connectionComponent);
        }

        return firstJobInSplittedCycles;
    }
}
