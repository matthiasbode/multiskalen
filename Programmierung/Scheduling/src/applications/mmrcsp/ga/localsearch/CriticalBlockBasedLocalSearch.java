///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.mmrcsp.ga.localsearch;
//
//import applications.PSPLib.demo.MinMakeSpanEval;
//import applications.mmrcsp.ga.priority.PriorityDeterminator;
//import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
//import applications.mmrcsp.model.basics.ExtendedActivityOnNodeGraph;
//import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
//import applications.mmrcsp.model.operations.Operation;
//import applications.mmrcsp.model.problem.SchedulingProblem;
//import applications.mmrcsp.model.resources.Resource;
//import applications.mmrcsp.model.schedule.Schedule;
//import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;
//
//import ga.basics.FitnessEvalationFunction;
//import ga.individuals.subList.ListIndividual;
//import ga.localSearch.LocalSearch;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//import math.Field;
//
///**
// *
// * @author bode
// */
//public class CriticalBlockBasedLocalSearch implements LocalSearch<ListIndividual<Operation>> {
//
//    public int maxOperations = 5;
//    Collection<Resource> css;
//    SchedulingProblem<Operation> problem;
//    MinMakeSpanEval eval;
//
//    PriorityDeterminator determinator;
//    Operation first;
//    Operation last;
//
//    public CriticalBlockBasedLocalSearch(Collection<Resource> css, SchedulingProblem<Operation> problem, MinMakeSpanEval eval) {
//        this.css = css;
//        this.problem = problem;
//        this.eval = eval;
//
//        for (Operation operation : problem.getOperations()) {
//            if (operation.getDuration().equals(Field.getNullElement(operation.getDuration().getClass()))) {
//                if (problem.getActivityOnNodeDiagramm().getPredecessors(operation).isEmpty()) {
//                    first = operation;
//                } else if (problem.getActivityOnNodeDiagramm().getSuccessors(operation).isEmpty()) {
//                    last = operation;
//                }
//            }
//        }
//    }
//
//    @Override
//    public ListIndividual<Operation> localSearch(ListIndividual<Operation> startIndividual) {
//
//        Double startFitness = startIndividual.getFitness();
//        List<Operation> activityList = eval.determinator.getPriorites(problem.getActivityOnNodeDiagramm(), startIndividual);
//
//        if (startIndividual == null) {
//            throw new NullPointerException("Kein StartIndividuum für lokale Suche");
//        }
//        /**
//         * Lokale Suche auf Operationenreihenfolge-Individuum.
//         */
//        Schedule schedule = (Schedule) startIndividual.additionalObjects.get(Schedule.KEY_SCHEDULE);
//        ActivityOnNodeGraph<Operation> aon = problem.getActivityOnNodeDiagramm();
//
//        ExtendedActivityOnNodeGraph<Operation> extendedActivityOnNodeGraph = ActivityOnNodeBuilder.getExtendedActivityOnNodeGraph(css, schedule, aon);
//        Set<CriticalPath<Operation>> criticalPaths = CriticalPathAnalyser.getCriticalPaths(schedule, extendedActivityOnNodeGraph, first, last);
//        for (CriticalPath<Operation> criticalPath : criticalPaths) {
//            ArrayList<CriticalPath<Operation>> criticalBlocks = CriticalPathAnalyser.getCriticalBlocks(criticalPath, extendedActivityOnNodeGraph);
//            for (CriticalPath<Operation> criticalBlock : criticalBlocks) {
//                for (int i = 0; i < criticalBlock.getNumberOfVertices() - 1; i++) {
//                    Operation o1 = criticalBlock.getVertexAt(i);
//                    Operation o2 = criticalBlock.getVertexAt(i + 1);
//                    
//                    if(aon.getSuccessors(o1).contains(o2)){
//                        continue;
//                    }
//                    
//                    List<Operation> newActivityList = new ArrayList<>(activityList);
//                    int indexOfo1 = newActivityList.indexOf(o1);
//                    int indexOfo2 = newActivityList.indexOf(o2);
//                    newActivityList.set(indexOfo1, o2);
//                    newActivityList.set(indexOfo2, o1);
//                    try{
//                        Schedule newSchedule = eval.sgs.getSchedule(newActivityList, problem, eval.slot);
//                    double newFitness = eval.getFitness(newSchedule);
//                    if (newFitness > startFitness) {
//                        System.out.println(startFitness + " ---> " + newFitness);
//                    }
//                    }catch(Exception e){
//                       
//                    }
//                    
//                }
//            }
//
//        }
//
////        ArrayList<CriticalPath<Operation>> paths = new ArrayList<>(criticalPaths.values());
////        
////        HashMap<CriticalPath, ArrayList<Operation>> newSubPaths = new HashMap<>();
////        LinkedHashMap<Operation, Operation> switchLog = new LinkedHashMap<>();
////        HashMap<CriticalPath, List<CriticalPath>> blocks = new HashMap<>();
////
////        /**
////         * Vorbereiten der kritischen Blöcke.
////         */
////        for (CriticalPath criticalPath : paths) {
////            List<CriticalPath> criticalBlocks =  CriticalPathAnalyser.getCriticalBlocks(criticalPath);
////
////            sortBlocksByAverageSetupTime(criticalBlocks);
////            blocks.put(criticalPath, criticalBlocks);
////            /**
////             * Bestimme für jede Block die Rüstfahrt nach vorne und nach hinten.
////             */
////            for (int i = 0; i < criticalBlocks.size(); i++) {
////                CriticalPath block = criticalBlocks.get(i);
////                if (i == 0) {
////                    block.setPre(criticalPath.getPre());
////                } else {
////                    block.setPre(criticalBlocks.get(i - 1).getLastVertex());
////                }
////                if (i == criticalBlocks.size() - 1) {
////                    block.setSuc(criticalPath.getSuc());
////                } else {
////                    block.setSuc(criticalBlocks.get(i + 1).getStartVertex());
////                }
////            }
////        }
////
////        for (CriticalPath criticalPath : blocks.keySet()) {
////            List<CriticalPath> criticalBlocks = blocks.get(criticalPath);
////
////            for (int i = 0; i < criticalBlocks.size(); i++) {
////
////                CriticalPath block = criticalBlocks.get(i);
////                if (block.getNumberOfVertices() < 2) {
////                    continue;
////                }
////
////                /**
////                 * Ausgangszeit bestimmen.
////                 */
////                ArrayList<Operation> ops = new ArrayList<>(block.getPathVertices());
////                if (block.getPre() != null) {
////                    ops.add(0, block.getPre());
////                }
////                if (block.getSuc() != null) {
////                    ops.add(block.getSuc());
////                }
////                double estimatedSetUpDistance = getEstimatedSetupTime(ops);
////
////                /**
////                 * Sortiere Operationen.
////                 */
////                List<Operation> candidates = sortOperationsBySetupSum(block);
////
////                ArrayList<Operation> newOrder = null;
////
////                /**
////                 * Eigentliche lokale Suche, verschiebe zunächst die Operation
////                 * mit der größten Setup-Summe.
////                 */
////                for (Operation routingTransportOperation : candidates) {
////
////                    /**
////                     * Verschiebe routingTransportOperation.
////                     */
////                    for (Operation toSwitch : block.getPathVertices()) {
////                        if (routingTransportOperation.equals(toSwitch)) {
////                            continue;
////                        }
////                        ArrayList<Operation> switchOps = switchOps(ops, routingTransportOperation, toSwitch);
////                        double newEstimatedSetUpDistance = getEstimatedSetupTime(switchOps);
////                        if (newEstimatedSetUpDistance < estimatedSetUpDistance) {
////                            newOrder = switchOps;
////                            switchLog.put(routingTransportOperation, toSwitch);
////                            estimatedSetUpDistance = newEstimatedSetUpDistance;
////                        }
////                    }
////                }
////                if (newOrder != null) {
////                    newSubPaths.put(block, newOrder);
////                }
////            }
////
////        }
////
////        /**
////         * Erzeuge neues Individuum, deren gesamten Unterinformationen verändert
////         * werden können
////         */
////        I newOne = (I) startIndividual.clone();
////
////        for (Operation routingTransportOperation : switchLog.keySet()) {
////            Integer vertexClass = problem.getActivityOnNodeDiagramm().getOperation2NodeClasses().get(routingTransportOperation);
////            Operation switchPartner = switchLog.get(routingTransportOperation);
////            if (switchPartner == null || routingTransportOperation == null) {
////                continue;
////            }
////            Integer vertexClassPartner = problem.getActivityOnNodeDiagramm().getOperation2NodeClasses().get(switchPartner);
////
////            if (vertexClass == vertexClassPartner) {
////                SubListIndividual vertexClassList = newOne.getOperationIndividual().getList().get(vertexClass);
////                Collections.swap(vertexClassList.getList(), vertexClassList.indexOf(routingTransportOperation), vertexClassList.indexOf(switchPartner));
////            }
////        }
////        /**
////         * Fitnessbestimmung des neuerzeugten Individuums
////         */
////        double fitness = eval.computeFitness(newOne);
////        newOne.setFitness(fitness);
////
////        if (startFitness < newOne.getFitness()) {
////            return newOne;
////        } else {
////            return startIndividual;
////        }
//        return null;
//    }
//
//}
