/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.localSearch;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.ExtendedActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.ga.direct.individuals.DirectSuperIndividual;

import ga.individuals.subList.SubListIndividual;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.mmrcsp.ga.localsearch.CriticalPath;
import applications.transshipment.model.basics.util.LoadUnitJobActivityOnNodeBuilder;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import com.google.common.collect.TreeMultimap;
import ga.basics.FitnessEvalationFunction;
import ga.localSearch.LocalSearch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author bode
 */
public class CriticalBlockBasedLocalSearch implements LocalSearch<DirectSuperIndividual> {

    public int maxOperations = 5;
    Collection<ConveyanceSystem> css;
    MultiJobTerminalProblem problem;
    FitnessEvalationFunction<DirectSuperIndividual> eval;

    public CriticalBlockBasedLocalSearch(Collection<ConveyanceSystem> css, MultiJobTerminalProblem problem, FitnessEvalationFunction<DirectSuperIndividual> eval) {
        this.css = css;
        this.problem = problem;
        this.eval = eval;
    }

    @Override
    public DirectSuperIndividual localSearch(DirectSuperIndividual startIndividual) {
//        Double startFitness = startIndividual.getFitness();
//        if (startIndividual == null) {
//            throw new NullPointerException("Kein StartIndividuum für lokale Suche");
//        }
//        /**
//         * Lokale Suche auf Operationenreihenfolge-Individuum.
//         */
//        ActivityOnNodeGraph<RoutingTransportOperation>  activityOnNodeDiagramm = (ActivityOnNodeGraph<RoutingTransportOperation>) startIndividual.additionalObjects.get(Schedule.KEY_AON);
//        ExtendedActivityOnNodeGraph<RoutingTransportOperation> aon = LoadUnitJobActivityOnNodeBuilder.getExtendedActivityOnNodeGraph(css, startIndividual.getSchedule(), activityOnNodeDiagramm);
//        Map<RoutingTransportOperation, CriticalPath<RoutingTransportOperation>> criticalPaths = TransshipmentCriticalPathAnalyser.getCriticalPaths(startIndividual.getSchedule(), aon);
//
//        ArrayList<CriticalPath<RoutingTransportOperation>> paths = new ArrayList<>(criticalPaths.values());
////        System.out.println("Kritische Pfade: " + paths.size());
//        /**
//         * Schleife über alle kritischen Pfade. Bestimme welche aus anderen DNF
//         * entstehen und bestimme prev und suc Operationen.
//         */
//        for (Iterator<CriticalPath<RoutingTransportOperation>> it = paths.iterator(); it.hasNext();) {
//            CriticalPath<RoutingTransportOperation> criticalPath = it.next();
//            if (criticalPath.getAffectedBy() != null) {
//                it.remove();
//            }
//            findPreAndSucForPath(criticalPath, startIndividual.getSchedule());
//        }
//
////        System.out.println("Anzahl Pfade nach Filterung: " + paths.size());
//        HashMap<CriticalPath, ArrayList<RoutingTransportOperation>> newSubPaths = new HashMap<>();
//        LinkedHashMap<RoutingTransportOperation, RoutingTransportOperation> switchLog = new LinkedHashMap<>();
//        HashMap<CriticalPath, List<CriticalPath<RoutingTransportOperation>>> blocks = new HashMap<>();
//
//        /**
//         * Vorbereiten der kritischen Blöcke.
//         */
//        for (CriticalPath<RoutingTransportOperation> criticalPath : paths) {
//            List<CriticalPath<RoutingTransportOperation>> criticalBlocks = TransshipmentCriticalPathAnalyser.getCriticalBlocks(criticalPath);
//
//            sortBlocksByAverageSetupTime(criticalBlocks);
//            blocks.put(criticalPath, criticalBlocks);
//            /**
//             * Bestimme für jede Block die Rüstfahrt nach vorne und nach hinten.
//             */
//            for (int i = 0; i < criticalBlocks.size(); i++) {
//                CriticalPath<RoutingTransportOperation> block = criticalBlocks.get(i);
//                if (i == 0) {
//                    block.setPre(criticalPath.getPre());
//                } else {
//                    block.setPre(criticalBlocks.get(i - 1).getLastVertex());
//                }
//                if (i == criticalBlocks.size() - 1) {
//                    block.setSuc(criticalPath.getSuc());
//                } else {
//                    block.setSuc(criticalBlocks.get(i + 1).getStartVertex());
//                }
//            }
//        }
//
//        for (CriticalPath<RoutingTransportOperation> criticalPath : blocks.keySet()) {
//            List<CriticalPath<RoutingTransportOperation>> criticalBlocks = blocks.get(criticalPath);
//
//            for (int i = 0; i < criticalBlocks.size(); i++) {
//
//                CriticalPath<RoutingTransportOperation> block = criticalBlocks.get(i);
//                if (block.getNumberOfVertices() < 2) {
//                    continue;
//                }
//
//                /**
//                 * Ausgangszeit bestimmen.
//                 */
//                ArrayList<RoutingTransportOperation> ops = new ArrayList<>(block.getPathVertices());
//                if (block.getPre() != null) {
//                    ops.add(0, block.getPre());
//                }
//                if (block.getSuc() != null) {
//                    ops.add(block.getSuc());
//                }
//                double estimatedSetUpDistance = getEstimatedSetupTime(ops);
//
//                /**
//                 * Sortiere Operationen.
//                 */
//                List<RoutingTransportOperation> candidates = sortOperationsBySetupSum(block);
//
//                ArrayList<RoutingTransportOperation> newOrder = null;
//
//                /**
//                 * Eigentliche lokale Suche, verschiebe zunächst die Operation
//                 * mit der größten Setup-Summe.
//                 */
//                for (RoutingTransportOperation routingTransportOperation : candidates) {
//
//                    /**
//                     * Verschiebe routingTransportOperation.
//                     */
//                    for (RoutingTransportOperation toSwitch : block.getPathVertices()) {
//                        if (routingTransportOperation.equals(toSwitch)) {
//                            continue;
//                        }
//                        ArrayList<RoutingTransportOperation> switchOps = switchOps(ops, routingTransportOperation, toSwitch);
//                        double newEstimatedSetUpDistance = getEstimatedSetupTime(switchOps);
//                        if (newEstimatedSetUpDistance < estimatedSetUpDistance) {
//                            newOrder = switchOps;
//                            switchLog.put(routingTransportOperation, toSwitch);
//                            estimatedSetUpDistance = newEstimatedSetUpDistance;
//                        }
//                    }
//                }
//                if (newOrder != null) {
//                    newSubPaths.put(block, newOrder);
//                }
//            }
//
//        }
//
//        /**
//         * Erzeuge neues Individuum, deren gesamten Unterinformationen verändert
//         * werden können
//         */
//        DirectSuperIndividual newOne = (DirectSuperIndividual) startIndividual.clone();
//
//        for (RoutingTransportOperation routingTransportOperation : switchLog.keySet()) {
//            Integer vertexClass = problem.getActivityOnNodeDiagramm().getOperation2NodeClasses().get(routingTransportOperation);
//            RoutingTransportOperation switchPartner = switchLog.get(routingTransportOperation);
//            if (switchPartner == null || routingTransportOperation == null) {
//                continue;
//            }
//            Integer vertexClassPartner = problem.getActivityOnNodeDiagramm().getOperation2NodeClasses().get(switchPartner);
//
//            if (vertexClass == vertexClassPartner) {
//                SubListIndividual vertexClassList = newOne.getOperationIndividual().getList().get(vertexClass);
//                Collections.swap(vertexClassList.getList(), vertexClassList.indexOf(routingTransportOperation), vertexClassList.indexOf(switchPartner));
//            }
//        }
//        /**
//         * Fitnessbestimmung des neuerzeugten Individuums
//         */
//        double fitness = eval.computeFitness(newOne);
//        newOne.setFitness(fitness);
//
//        if (startFitness < newOne.getFitness()) {
//            return newOne;
//        } else {
//            return startIndividual;
//        }
        return null;
    }

    public static double getEstimatedSetupTime(List<? extends RoutingTransportOperation> operations) {
        double res = 0;
        RoutingTransportOperation first = operations.get(0);
        for (int i = 1; i < operations.size() - 1; i++) {
            RoutingTransportOperation second = operations.get(i);
            res += first.getDestination().getCenterOfGeneralOperatingArea().distance(second.getOrigin().getCenterOfGeneralOperatingArea());
            first = second;
        }
        return res;
    }

    public static ArrayList<RoutingTransportOperation> switchOps(ArrayList<RoutingTransportOperation> ops, RoutingTransportOperation first, RoutingTransportOperation second) {
        ArrayList<RoutingTransportOperation> newOps = new ArrayList<>(ops);
        int indexOfFirst = ops.indexOf(first);
        int indexOfSecond = ops.indexOf(second);
        newOps.set(indexOfFirst, second);
        newOps.set(indexOfSecond, first);
        return newOps;
    }

    public static void sortBlocksByAverageSetupTime(List<CriticalPath<RoutingTransportOperation>> criticalBlocks) {
        final HashMap<CriticalPath<RoutingTransportOperation>, Double> middleSetupTimes = new HashMap<>();

        for (CriticalPath<RoutingTransportOperation> block : criticalBlocks) {
            double averageSetupTime = 0;
            for (int i = 0; i < block.getNumberOfVertices() - 1; i++) {
                RoutingTransportOperation vertex = block.getVertexAt(i);
                RoutingTransportOperation vertexNext = block.getVertexAt(i + 1);
                averageSetupTime += vertex.getDestination().getCenterOfGeneralOperatingArea().distance(vertexNext.getOrigin().getCenterOfGeneralOperatingArea());
            }
            averageSetupTime /= new Double(block.getNumberOfVertices());
            middleSetupTimes.put(block, averageSetupTime);
        }

        Collections.sort(criticalBlocks, new Comparator<CriticalPath>() {
            @Override
            public int compare(CriticalPath o1, CriticalPath o2) {
                return Double.compare(middleSetupTimes.get(o1), middleSetupTimes.get(o2));
            }
        });

    }

    public static void findPreAndSucForPath(CriticalPath<RoutingTransportOperation> criticalPath, LoadUnitJobSchedule schedule) {
        /**
         * Informationen für Rüstfahrten für die Randoperationen.
         */
        RoutingTransportOperation startVertex = criticalPath.getStartVertex();
        MultiScaleTransportOperation scheduledStartOperation = schedule.getScheduledOperation(startVertex);
        NavigableSet<Operation> operationsForResourceStart = schedule.getOperationsForResourceAsNavigableSet(scheduledStartOperation.getResource());
        Operation prevSetup = operationsForResourceStart.lower(scheduledStartOperation);
        if (prevSetup != null) {
            MultiScaleTransportOperation lowerOp = (MultiScaleTransportOperation) operationsForResourceStart.lower(operationsForResourceStart.lower(prevSetup));
            if (lowerOp != null) {
                criticalPath.setPre(lowerOp.getRoutingTransportOperation());
            }
        }

        RoutingTransportOperation endVertex = criticalPath.getStartVertex();
        MultiScaleTransportOperation scheduledEndOperation = schedule.getScheduledOperation(endVertex);
        /**
         * Falls letzte Operation, die DNF ist, dann nicht weiter betrachten.
         */
        if (schedule.isScheduled(scheduledEndOperation)) {
            NavigableSet<Operation> operationsForResourceEnd = schedule.getOperationsForResourceAsNavigableSet(scheduledEndOperation.getResource());
            Operation higherSetup = operationsForResourceEnd.higher(scheduledEndOperation);
            if (higherSetup != null) {
                MultiScaleTransportOperation higherOp = (MultiScaleTransportOperation) operationsForResourceEnd.higher(higherSetup);
                if (higherOp != null) {
                    criticalPath.setSuc(higherOp.getRoutingTransportOperation());
                }
            }
        }
    }

    public static List<RoutingTransportOperation> sortOperationsBySetupSum(CriticalPath<RoutingTransportOperation> block) {
        List<RoutingTransportOperation> candidates = new ArrayList<>();
        final HashMap<RoutingTransportOperation, Double> setupDistances = new HashMap<>();
        for (int j = 0; j < block.getNumberOfVertices(); j++) {
            RoutingTransportOperation top = block.getVertexAt(j);
            double setupSum = 0;
            if (block.getPre() == null || block.getSuc() == null) {
                continue;
            }
            if (j == 0) {
                setupSum += block.getPre().getDestination().getCenterOfGeneralOperatingArea().distance(top.getOrigin().getCenterOfGeneralOperatingArea());
            } else {
                setupSum += block.getVertexAt(j - 1).getDestination().getCenterOfGeneralOperatingArea().distance(top.getOrigin().getCenterOfGeneralOperatingArea());
            }
            if (j == block.getNumberOfVertices() - 1) {
                setupSum += block.getSuc().getOrigin().getCenterOfGeneralOperatingArea().distance(top.getDestination().getCenterOfGeneralOperatingArea());
            } else {
                setupSum += block.getVertexAt(j + 1).getOrigin().getCenterOfGeneralOperatingArea().distance(top.getDestination().getCenterOfGeneralOperatingArea());
            }
            candidates.add(top);
            setupDistances.put(top, setupSum);
        }
        /**
         * Sortieren der Auswahl der zu verschiebenen Operationen anhand
         * SetupSumme.
         */
        Collections.sort(candidates, new Comparator<RoutingTransportOperation>() {
            @Override
            public int compare(RoutingTransportOperation o1, RoutingTransportOperation o2) {
                return -Double.compare(setupDistances.get(o1), setupDistances.get(o2));
            }
        });
        return candidates;
    }
}
