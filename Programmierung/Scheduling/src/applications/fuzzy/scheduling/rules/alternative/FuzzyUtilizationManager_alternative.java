///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.fuzzy.scheduling.rules.alternative;
//
//import applications.fuzzy.functions.LinearizedFunction1d;
//import applications.fuzzy.operation.FuzzyWorkloadParameters;
//import applications.fuzzy.operation.BetaOperation;
//import static applications.fuzzy.scheduling.rules.alternative.FuzzyDemandUtilities_alternative.getSizeOfNecessityArea;
//import static applications.fuzzy.scheduling.rules.alternative.FuzzyDemandUtilities_alternative.getSizeOfPossibilityArea;
//import applications.mmrcsp.model.basics.TimeSlot;
//import applications.mmrcsp.model.basics.TimeSlotList;
//import applications.mmrcsp.model.operations.Operation;
//import applications.mmrcsp.model.resources.Resource;
//import applications.mmrcsp.model.schedule.Schedule;
//import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
//import fuzzy.number.FuzzyNumber;
//import fuzzy.number.discrete.FuzzyFactory;
//import fuzzy.number.discrete.interval.FuzzyInterval;
//import java.util.Collection;
//import math.DoubleValue;
//import math.FieldElement;
//
///**
// * WICHTIG: GILT IMMER NUR FÜR EINE RESSOURCE!
// *
// * @author bode
// */
//public class FuzzyUtilizationManager_alternative implements UtilizationManager {
//
//    /**
//     * Fuzzy Auslastungskurve
//     */
//    private LinearizedFunction1d workloadFuction;
//    /**
//     * Resource, für die dieser Manger da ist
//     */
//    final Resource r;
//    /**
//     * Maximale-Kapazität
//     */
//    final DoubleValue capacity;
//    /**
//     * Verfügbarkeit
//     */
//    private TimeSlot t;
//
//    private boolean denyStartAdaption;
//
//    public static double startLamdaL = 1.0;
//
//    public FuzzyUtilizationManager_alternative(Resource r, double capacity, TimeSlot t) {
//        this.capacity = new DoubleValue(capacity);
//        this.r = r;
//        this.t = t;
//        this.workloadFuction = new LinearizedFunction1d();
//    }
//
//    public FuzzyUtilizationManager_alternative(Resource r, double capacity, FieldElement start) {
//        this.capacity = new DoubleValue(capacity);
//        this.r = r;
//        this.t = new TimeSlot(FuzzyFactory.createCrispValue(start.doubleValue()), FuzzyFactory.createCrispValue(r.getTemporalAvailability().getUntilWhen().doubleValue()));
//        this.workloadFuction = new LinearizedFunction1d();
//    }
//
//    public FuzzyUtilizationManager_alternative(Resource r, double capacity, FieldElement start, boolean denyStartAdaption) {
//        this.capacity = new DoubleValue(capacity);
//        this.r = r;
//        this.t = new TimeSlot(FuzzyFactory.createCrispValue(start.doubleValue()), FuzzyFactory.createCrispValue(r.getTemporalAvailability().getUntilWhen().doubleValue()));
//        this.workloadFuction = new LinearizedFunction1d();
//        this.denyStartAdaption = denyStartAdaption;
//    }
//
//    @Override
//    public LinearizedFunction1d getWorkloadFuction() {
//        return workloadFuction;
//    }
//
//    /**
//     *
//     * @return
//     */
//    public DoubleValue getCapacity() {
//        return capacity;
//    }
//
//    private boolean testCapacity(FuzzyWorkloadParameters lambda, FuzzyInterval start, BetaOperation o, LinearizedFunction1d currentWorkloadFunction) {
//        LinearizedFunction1d demand = FuzzyDemandUtilities_alternative.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(o, r, start, lambda);
//        if (demand != null) {
//            LinearizedFunction1d result = currentWorkloadFunction.add(demand);
//            // Kapazität überprüfen
//            Double maxValue = result.getMax().getValue();
//            if (maxValue <= capacity.doubleValue()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean haveEnoughCapacity(Schedule s, Operation op, FieldElement start) {
//        return haveEnoughCapacity(s, op, start, workloadFuction);
//    }
//
//    public boolean haveEnoughCapacity(Schedule s, Operation op, FieldElement st, LinearizedFunction1d currentWorkloadFunction) {
//        BetaOperation operation = (BetaOperation) op;
//        FuzzyInterval start = (FuzzyInterval) st;
//        FuzzyInterval duration = (FuzzyInterval) operation.getDuration();
//        FuzzyInterval end = start.add(duration);
//
//        double DPI = getSizeOfPossibilityArea(operation, start);
//        double DN = getSizeOfNecessityArea(operation, start);
//
//        FuzzyInterval adaptedStart = (FuzzyInterval) start.clone();
//
//        /**
//         * Schleife zum linke Seite Start anpassen
//         */
////        while (adaptedStart != null) {
//        /**
//         * Test, ob zu dem Zeitpunkt überhaupt einplanbar.
//         */
//        FuzzyWorkloadParameters lambdaTest = new FuzzyWorkloadParameters(start, operation, 0, 0, DPI, DN, 0);
//        boolean test = testCapacity(lambdaTest, adaptedStart, operation, currentWorkloadFunction);
//        /**
//         * Falls möglich, dann mit großem Lambda testen.
//         */
//        if (test) {
//            double lambdaMax = FuzzyDemandUtilities_alternative.getLambdaMax(operation, start);
//            double lambdaMin = FuzzyDemandUtilities_alternative.getLambdaMin(operation, start);
//
////            if (lambdaMax > 1) {
////                throw new IllegalArgumentException("Größer 1 nicht erlaubt");
////            }
////            if (lambdaMin < 0) {
////                throw new IllegalArgumentException("kleiner 0 nicht erlaubt");
////            }
//            double wunschLambda = lambdaMin + operation.getBeta() * (lambdaMax - lambdaMin);
//            FuzzyWorkloadParameters lambda = new FuzzyWorkloadParameters(start, operation, wunschLambda, wunschLambda, DPI, DN, 0);
//            LinearizedFunction1d demand = FuzzyDemandUtilities_alternative.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(operation, r, (FuzzyInterval) adaptedStart, lambda);
//            double zielflaeche = demand.getIntegral(start.getC1(), start.getC2() + duration.getC2());
//
//            //Symmetrischer Fall zuerst versuchen einzuplanen.
//            boolean testCapacity = testCapacity(lambda, adaptedStart, operation, currentWorkloadFunction);
//            if (testCapacity) {
//                s.fuzzyWorkloadParameters.put(operation, lambda);
//                return true;
//            }
//
//            //Asymmetrischer Fall
//            lambda = new FuzzyWorkloadParameters(start, operation, startLamdaL, startLamdaL, DPI, DN, zielflaeche);
//            while (lambda.lambdaL > 0) {
//                lambda.lambdaR = FuzzyDemandUtilities_alternative.getMatchingLambdaR(operation, r, start, lambda);
//                if (lambda.lambdaR < 0 || lambda.lambdaR > 1) {
//                    lambda.lambdaL = lambda.lambdaL - 0.1;
//                    continue;
//                }
//                testCapacity = testCapacity(lambda, adaptedStart, operation, currentWorkloadFunction);
//                if (testCapacity) {
//                    s.fuzzyWorkloadParameters.put(operation, lambda);
//                    return true;
//                }
//                /**
//                 * Schleifendurchlauf mit Lambda-Anpassungen nicht erfolgreich,
//                 * schneide vorne was weg, wenn möglich, ansonsten gib false
//                 * zurück.
//                 */
//                if (denyStartAdaption) {
//                    return false;
//                } else {
//                    lambda.lambdaL = lambda.lambdaL - 0.1;
//                }
//            }
//        }
////            adaptedStart = FuzzyFactory.CutLeftSideOfIntervall((DiscretizedFuzzyInterval) adaptedStart, 1000.);
////        }
//        return false;
//    }
//
//    @Override
//    public void scheduleInternal(Operation op, Schedule s, FieldElement start) {
//        BetaOperation o = (BetaOperation) op;
//        FuzzyWorkloadParameters lambda = s.fuzzyWorkloadParameters.get(o);
//        if (start instanceof FuzzyNumber) {
//            LinearizedFunction1d f = FuzzyDemandUtilities_alternative.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(o, r, (FuzzyInterval) start, lambda);
//            if (f == null) {
////                FuzzyDemandUtilities_alternative.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(o, r, (FuzzyInterval) start, lambda);
////                throw new UnknownError("Es konnte keine Einplanung vorgenommen werden für Operation " + op);
//                System.err.println("Es konnte keine Einplanung vorgenommen werden für Operation " + op);
//            } else {
//                this.workloadFuction = workloadFuction.add(f);
//                Double value = this.workloadFuction.getMax().getValue();
//                if (value > this.getCapacity().doubleValue()) {
//                    Collection<Operation> operationsForResource = s.getOperationsForResource(r);
//                    System.err.println("Gewünschter Startzeitpunkt Operation: " + start);
//                    for (Operation operation : operationsForResource) {
//                        System.err.println(s.get(operation) + "\t:" + operation);
//                        System.err.println(s.get(operation).add(operation.getDuration()));
//                    }
//                    throw new UnknownError("Über die Kapazität eingeplant. " + value + "\t" + o);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void unScheduleInternal(Operation op, Schedule s
//    ) {
//        BetaOperation o = (BetaOperation) op;
//        FuzzyWorkloadParameters lambda = s.fuzzyWorkloadParameters.get(o);
//        if (s.getStartTimes().get(o) instanceof FuzzyNumber) {
//            LinearizedFunction1d f = FuzzyDemandUtilities_alternative.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(o, r, (FuzzyInterval) s.getStartTimes().get(o), lambda);
//            this.workloadFuction = workloadFuction.sub(f);
//        }
//    }
//
//    /**
//     * Gibt eine TimeSlot-List zurück von Zeitbereichen, in denen der
//     * Ressourcenbedarf verfügbar ist für die angeforderte Ressource.
//     *
//     * @param k Ressource, die befragt werden soll
//     * @param demand Bedarf
//     * @param interval Zeitfenster, für das gefragt werden soll.
//     * @return
//     */
//    public TimeSlotList getFreeSlotsInternal(Schedule s, BetaOperation o, TimeSlot interval) {
//        throw new UnsupportedOperationException("Klappt noch nicht, da sich immer ein neues LambdaL und LambdaR bestimmt werden muss");
////        // ACHTUNG: dies ist eine sehr einfache Optimierungsmöglichekeit. 
////        // Weitere Möglichkeiten werden in Kapitel 12 der Studienabreit empfohlen.
////        // Schrittweite festlegen
////        TimeSlotList list = new TimeSlotList();
////
////        FieldElement noStart = interval.getFromWhen();
////        // Startzeit immer weiter verschieben, bis einplanen möglich ist
////        boolean noSchedule = true;
////        while (noSchedule) {
////            // nächsten Startpunkt erzeugen
////            noStart = noStart.add(step);
////            noSchedule = !(this.haveEnoughCapacity(o, noStart));
////            System.out.println("Aktueller Versuch: " + noStart.toString());
////        }
////        list.add(new TimeSlot(noStart, noStart.add(o.getDuration())));
////        return list;
//    }
//
//    // ACHTUNG: Fehlerhaft, da bisher nicht angepasst bzw. verwendet
//    public FieldElement getStartTimeInternal(Operation o, TimeSlot interval) {
//        /**
//         * Es wird nur der aktuelle Startpunkt benötigt
//         */
////        FieldElement rjk = o.getDemand(r);
////        FieldElement duration = o.getDuration();
////        /**
////         * t_k_mue bestimmen, ab dem die Operation einplanbar ist.
////         */
////        LinearizedMembershipFunction1d function = this.getLeftOverCapacityFunction().getFunction(interval.getFromWhen(), interval.getUntilWhen());
////
////        for (FieldElement t_new : function.getSamplingPoints()) {
////            Map.Entry<FieldElement, FieldElement> min = function.getMin(t_new, t_new.add(duration));
////            if (min != null && (min.getValue().isGreaterThan(rjk) || min.getValue().equals(rjk))) {
////                return t_new;
////            }
////        }
//
//        return null;
//    }
//
//    public void setTimeSlot(TimeSlot t) {
//        this.t = t;
//    }
//
//    @Override
//    public TimeSlotList getFreeSlotsInternal(Schedule s, FieldElement demand, FieldElement duration, TimeSlot interval) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public boolean isDenyStartAdaption() {
//        return denyStartAdaption;
//    }
//
//    public void setDenyStartAdaption(boolean denyStartAdaption) {
//        this.denyStartAdaption = denyStartAdaption;
//    }
//
//}
