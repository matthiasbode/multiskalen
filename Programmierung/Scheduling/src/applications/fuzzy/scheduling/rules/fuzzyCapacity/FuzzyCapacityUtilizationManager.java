/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.scheduling.rules.fuzzyCapacity;

import applications.fuzzy.scheduling.rules.defaultImplementation.*;
import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.operation.FuzzyWorkloadParameters;
import applications.fuzzy.operation.BetaOperation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.FuzzyInterval;
import java.util.Collection;
import math.FieldElement;

/**
 * WICHTIG: GILT IMMER NUR FÜR EINE RESSOURCE!
 *
 * @author bode
 */
public class FuzzyCapacityUtilizationManager implements UtilizationManager {

    /**
     * Fuzzy Auslastungskurve
     */
    private LinearizedFunction1d workloadFuction;
    /**
     * Resource, für die dieser Manger da ist
     */
    final Resource r;
    /**
     * Maximale-Kapazität
     */
    final FuzzyInterval capacity;
    /**
     * Verfügbarkeit
     */
    private TimeSlot t;

    private boolean denyStartAdaption;

    public static double startLamdaL = 0.5;

    public FuzzyCapacityUtilizationManager(Resource r, FuzzyInterval capacity, TimeSlot t) {
        this.capacity = capacity;
        this.r = r;
        this.t = t;
        this.workloadFuction = new LinearizedFunction1d();
    }

    public FuzzyCapacityUtilizationManager(Resource r, FuzzyInterval capacity, FieldElement start) {
        this.capacity = capacity;
        this.r = r;
        this.t = new TimeSlot(FuzzyFactory.createCrispValue(start.doubleValue()), FuzzyFactory.createCrispValue(r.getTemporalAvailability().getUntilWhen().doubleValue()));
        this.workloadFuction = new LinearizedFunction1d();
    }

    public FuzzyCapacityUtilizationManager(Resource r, FuzzyInterval capacity, FieldElement start, boolean denyStartAdaption) {
        this.capacity = capacity;
        this.r = r;
        this.t = new TimeSlot(FuzzyFactory.createCrispValue(start.doubleValue()), FuzzyFactory.createCrispValue(r.getTemporalAvailability().getUntilWhen().doubleValue()));
        this.workloadFuction = new LinearizedFunction1d();
        this.denyStartAdaption = denyStartAdaption;
    }

    @Override
    public LinearizedFunction1d getWorkloadFuction() {
        return workloadFuction;
    }

    /**
     *
     * @return
     */
    @Override
    public FuzzyInterval getCapacity() {
        return capacity;
    }

    private boolean isScheduableInBestCase(FuzzyInterval adaptedStart, BetaOperation o, LinearizedFunction1d currentWorkloadFunction) {
        LinearizedFunction1d demand = FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevel(o, r, adaptedStart, 0);
        if (demand != null) {
            LinearizedFunction1d result = currentWorkloadFunction.add(demand);
            // Kapazität überprüfen
            Double maxValue = result.getMax().getValue();
            /**
             * Darf nicht komplett raus sein.
             */
            if (maxValue <= capacity.getC2()) {
                return true;
            }
        }
        return false;
    }

    private boolean testCapacity(FuzzyWorkloadParameters lambda, FuzzyInterval adaptedStart, BetaOperation o, LinearizedFunction1d currentWorkloadFunction) {
        LinearizedFunction1d demand = FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(o, r, (FuzzyInterval) adaptedStart, lambda);
        if (demand != null) {
            LinearizedFunction1d result = currentWorkloadFunction.add(demand);
            // Kapazität überprüfen
            Double maxValue = result.getMax().getValue();
            if (maxValue <= capacity.getC2()) {
                return true;
            }
        }
        return false;
    }

    public boolean haveEnoughCapacity(Schedule s, BetaOperation setup, BetaOperation op, FuzzyInterval startSetup, FuzzyInterval startOp) {
        FuzzyWorkloadParameters lambda = s.fuzzyWorkloadParameters.get(setup);
        LinearizedFunction1d demand = FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(setup, r, startSetup, lambda);
        LinearizedFunction1d function = this.workloadFuction.add(demand);
        return haveEnoughCapacity(s, op, startOp, function);
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation op, FieldElement start) {
        return haveEnoughCapacity(s, op, start, workloadFuction);
    }

    public boolean haveEnoughCapacity(Schedule s, Operation op, FieldElement start, LinearizedFunction1d currentWorkloadFunction) {
        BetaOperation o = (BetaOperation) op;

        FuzzyInterval adaptedStart = (FuzzyInterval) start.clone();

        /**
         * Schleife zum linke Seite Start anpassen
         */
//        while (adaptedStart != null) {
        /**
         * Test, ob zu dem Zeitpunkt überhaupt einplanbar.
         */
        boolean test = isScheduableInBestCase(adaptedStart, o, currentWorkloadFunction);
        /**
         * Falls möglich, dann mit großem Lambda testen.
         */
        if (test) {
            FuzzyWorkloadParameters lambda = new FuzzyWorkloadParameters(startLamdaL);
            while (lambda.lambdaL > 0) {
                boolean testCapacity = testCapacity(lambda, adaptedStart, o, currentWorkloadFunction);
                if (testCapacity) {
                    s.fuzzyWorkloadParameters.put(o, lambda);
                    return true;
                }
                /**
                 * Schleifendurchlauf mit Lambda-Anpassungen nicht erfolgreich,
                 * schneide vorne was weg, wenn möglich, ansonsten gib false
                 * zurück.
                 */
                if (denyStartAdaption) {
                    return false;
                } else {
                    lambda.lambdaL = lambda.lambdaL - 0.1;
                }
            }
        }
        return false;
    }

    @Override
    public void scheduleInternal(Operation op, Schedule s, FieldElement start) {
        BetaOperation o = (BetaOperation) op;
        FuzzyWorkloadParameters lambda = s.fuzzyWorkloadParameters.get(o);
        if (start instanceof FuzzyNumber) {
            LinearizedFunction1d f = FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(o, r, (FuzzyInterval) start, lambda);
            if (f == null) {
                System.err.println("Es konnte keine Einplanung vorgenommen werden für Operation " + op);
            } else {
                this.workloadFuction = workloadFuction.add(f);
                Double value = this.workloadFuction.getMax().getValue();
                if (value > this.getCapacity().getC2()) {
                    Collection<Operation> operationsForResource = s.getOperationsForResource(r);
                    System.err.println("Gewünschter Startzeitpunkt Operation: " + start);
                    for (Operation operation : operationsForResource) {
                        System.err.println(s.get(operation) + "\t:" + operation);
                        System.err.println(s.get(operation).add(operation.getDuration()));
                    }
                    throw new UnknownError("Über die Kapazität eingeplant. " + value + "\t" + o);
                }
            }
        }
    }

    @Override
    public void unScheduleInternal(Operation op, Schedule s) {
        BetaOperation o = (BetaOperation) op;
        FuzzyWorkloadParameters lambda = s.fuzzyWorkloadParameters.get(o);
        if (s.getStartTimes().get(o) instanceof FuzzyNumber) {
            LinearizedFunction1d f = FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(o, r, (FuzzyInterval) s.getStartTimes().get(o), lambda);
            this.workloadFuction = workloadFuction.sub(f);
        }
    }

    /**
     * Gibt eine TimeSlot-List zurück von Zeitbereichen, in denen der
     * Ressourcenbedarf verfügbar ist für die angeforderte Ressource.
     *
     * @param k Ressource, die befragt werden soll
     * @param demand Bedarf
     * @param interval Zeitfenster, für das gefragt werden soll.
     * @return
     */
    public TimeSlotList getFreeSlotsInternal(Schedule s, BetaOperation o, TimeSlot interval) {
        throw new UnsupportedOperationException("Klappt noch nicht, da sich immer ein neues LambdaL und LambdaR bestimmt werden muss");
//        // ACHTUNG: dies ist eine sehr einfache Optimierungsmöglichekeit. 
//        // Weitere Möglichkeiten werden in Kapitel 12 der Studienabreit empfohlen.
//        // Schrittweite festlegen
//        TimeSlotList list = new TimeSlotList();
//
//        FieldElement noStart = interval.getFromWhen();
//        // Startzeit immer weiter verschieben, bis einplanen möglich ist
//        boolean noSchedule = true;
//        while (noSchedule) {
//            // nächsten Startpunkt erzeugen
//            noStart = noStart.add(step);
//            noSchedule = !(this.haveEnoughCapacity(o, noStart));
//            System.out.println("Aktueller Versuch: " + noStart.toString());
//        }
//        list.add(new TimeSlot(noStart, noStart.add(o.getDuration())));
//        return list;
    }

    // ACHTUNG: Fehlerhaft, da bisher nicht angepasst bzw. verwendet
    public FieldElement getStartTimeInternal(Operation o, TimeSlot interval) {
        /**
         * Es wird nur der aktuelle Startpunkt benötigt
         */
//        FieldElement rjk = o.getDemand(r);
//        FieldElement duration = o.getDuration();
//        /**
//         * t_k_mue bestimmen, ab dem die Operation einplanbar ist.
//         */
//        LinearizedMembershipFunction1d function = this.getLeftOverCapacityFunction().getFunction(interval.getFromWhen(), interval.getUntilWhen());
//
//        for (FieldElement t_new : function.getSamplingPoints()) {
//            Map.Entry<FieldElement, FieldElement> min = function.getMin(t_new, t_new.add(duration));
//            if (min != null && (min.getValue().isGreaterThan(rjk) || min.getValue().equals(rjk))) {
//                return t_new;
//            }
//        }

        return null;
    }

    @Override
    public void setTimeSlot(TimeSlot t) {
        this.t = t;
    }

    @Override
    public TimeSlotList getFreeSlotsInternal(Schedule s, FieldElement demand, FieldElement duration, TimeSlot interval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isDenyStartAdaption() {
        return denyStartAdaption;
    }

    public void setDenyStartAdaption(boolean denyStartAdaption) {
        this.denyStartAdaption = denyStartAdaption;
    }

}
