//package applications.transshipment.ga.localSearch;
//
//import applications.transshipment.ga.TransshipmentSuperIndividual;
//import applications.transshipment.model.LoadUnitJob;
//import applications.transshipment.model.dnf.analysis.DNFReason;
//import applications.transshipment.model.operations.transport.RoutingTransportOperation;
//import applications.transshipment.model.problem.TerminalProblem;
//import ga.basics.FitnessEvalationFunction;
//import ga.localSearch.LocalSearch;
//import java.util.LinkedHashSet;
//
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//  
//
///**
// *
// * @author bode
// */
//public class DNFBasedLocalSearch implements LocalSearch<TransshipmentSuperIndividual> {
//
//    public static double prevEALOSAELimit = 0.7;
//    public static double resourceLimit = 0.3;
//    private final FitnessEvalationFunction<TransshipmentSuperIndividual> evalFunction;
//    private final TerminalProblem problem;
//
//    public DNFBasedLocalSearch(TerminalProblem problem, FitnessEvalationFunction<TransshipmentSuperIndividual> evalFunction) {
//        this.evalFunction = evalFunction;
//        this.problem = problem;
//    }
//
//    @Override
//    public TransshipmentSuperIndividual localSearch(TransshipmentSuperIndividual start) {
//        TransshipmentSuperIndividual result = start;
//        LinkedHashSet<LoadUnitJob> dnfJobs = start.schedule.getDnfJobs();
//   
//        DNFJOBLoop:
//        for (LoadUnitJob loadUnitJob : dnfJobs) {
//            DNFReason reason = reasons.get(loadUnitJob);
//            /**
//             * vorherige Operation sehr spät eingeplant
//             */
//            if (reason.indexInRouting > 0 && reason.prevEALOSAEpercent > prevEALOSAELimit) {
//
//                /**
//                 * Anpassen der Operationenreihenfolge
//                 */
//                int vertexClass = (problem.getOperation2NodeClasses().get(reason.operation) - 1);
//                TransshipmentSuperIndividual superNew = getOperationLocal(vertexClass, reason.prevOperation, result);
//
//                if (superNew != null) {
//                    result = superNew;
//                } else {
//                    continue;
//                }
//            }
//
////            /**
////             * Sehr weit hinten für aktuelle Operation
////             */
////            if (reason.percentResource > resourceLimit) {
////
////                /**
////                 * Anpassen der Operationenreihenfolge
////                 */
////                int vertexClass = (problem.getOperation2NodeClasses().get(reason.operation));
////                TransshipmentSuperIndividual superNew = getOperationLocal(vertexClass, reason.operation, result);
////
////                if (superNew != null) {
////                    result = superNew;
////                } else {
////                    continue;
////                }
////            }
//        }
//        return result;
//    }
//
//    private TransshipmentSuperIndividual getOperationLocal(int vertexClass, RoutingTransportOperation o, TransshipmentSuperIndividual start) {
//        VertexClassIndividual clone = start.getOperationIndividual().clone();
//        /**
//         * Anpassen der Operationenreihenfolge
//         */
//        OperationListIndividual opInd = clone.get(vertexClass);
//
//        opInd.getList().remove(o);
//        /**
//         * Finde neue Position für Operation o Verschieben nach vorne in der
//         * Liste Berücksichtigung Rüstfahrten. Wie weit nach vorne schieben?
//         * Ganz nach vorne schwierig EALOSAE-proportional?
//         */
//        opInd.getList().add(0, o);
//
//        TransshipmentSuperIndividual superNew = new TransshipmentSuperIndividual(clone, start.getModeIndividual());
//        superNew.setFitness(evalFunction.computeFitness(superNew));
//        if (superNew.getFitness() > start.getFitness()) {
//            return superNew;
//        } else {
//            return null;
//        }
//    }
//}
