/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.storage.DefaultStoreOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.eval.EvalFunction_StoreOperation_ClosestPosition_AvoidSection;
import applications.transshipment.model.eval.StoreOperationComparator;
import applications.transshipment.model.operations.storage.SuperStoreOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.schedule.rules.StorageRule;
import fuzzy.number.discrete.AlphaCutSet;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class StorageToolMethods {

    private static FieldElement storageReservationTime = new LongValue(5 * 60 * 1000L);

    /**
     * Generiert Lageroperationen an der Destination einer TransportOperation.
     * Die Lageroperation muss von beginStoreOperation bis zum LatestStart der
     * nachfolgenden Operation reichen.
     *
     * @param beginStoreOperation
     * @param routingTransportOperation
     * @param ealosaes
     * @param schedule
     * @param currentTransport
     * @param rules
     * @return
     */
    public static List<StoreOperation> getStorageOperationsAtDestination(FieldElement beginStoreOperation, TerminalProblem problem, RoutingTransportOperation routingTransportOperation, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, LoadUnitJobSchedule schedule, InstanceHandler rules) {
        LoadUnitStorage destination = routingTransportOperation.getDestination();

        StorageRule sr = (StorageRule) rules.get(destination);
        if (sr == null) {
            throw new NoSuchElementException("Keine ScheduleRegel hinterlegt für Resource: " + destination);
        }

        FieldElement duration = getStorageDuration(beginStoreOperation, routingTransportOperation, ealosaes);
        if (duration == null) {
            return new ArrayList<>();
        }
        List<StoreOperation> possibleStoreOperations = sr.getPossibleStoreOperations(routingTransportOperation.getLoadUnit(), schedule, beginStoreOperation, duration);

        /**
         * Bestimme die nächste Transportoperation und deren Ziel, um
         * auszuwählen, welche StoreOperation genommen werden soll, um eine
         * exakte Transportoperation zu erzeugen.
         */
        JobOperationList<RoutingTransportOperation> sequence = routingTransportOperation.getRouting();
        int indexOfCurrentTransport = sequence.indexOf(routingTransportOperation);
        RoutingTransportOperation nextTransport = null;
        if (indexOfCurrentTransport < (sequence.size() - 1)) {
            nextTransport = sequence.get(indexOfCurrentTransport + 1);
        }

        boolean toAGVTransport = false;

        if (routingTransportOperation.getResource() instanceof Crane && nextTransport != null && nextTransport.getResource() instanceof LCSystem) {
            toAGVTransport = true;
        }

        if (toAGVTransport) {
            final Point2d centerOrigin = routingTransportOperation.getOrigin().getCenterOfGeneralOperatingArea();
            Collections.sort(possibleStoreOperations, new Comparator<StoreOperation>() {
                @Override
                public int compare(StoreOperation o1, StoreOperation o2) {
                    if (o1.getResource().getCenterOfGeneralOperatingArea().distance(centerOrigin) < o2.getResource().getCenterOfGeneralOperatingArea().distance(centerOrigin)) {
                        return -1;
                    }
                    if (o1.getResource().getCenterOfGeneralOperatingArea().distance(centerOrigin) > o2.getResource().getCenterOfGeneralOperatingArea().distance(centerOrigin)) {
                        return +1;
                    }
                    return o1.toString().compareTo(o2.toString());
                }
            });
        } else if (nextTransport != null) {
            LoadUnitStorage nextStore = nextTransport.getDestination();
            final Point2d centerNextStore = nextStore.getCenterOfGeneralOperatingArea();
            Collections.sort(possibleStoreOperations, new Comparator<StoreOperation>() {
                @Override
                public int compare(StoreOperation o1, StoreOperation o2) {
                    if (o1.getResource().getCenterOfGeneralOperatingArea().distance(centerNextStore) < o2.getResource().getCenterOfGeneralOperatingArea().distance(centerNextStore)) {
                        return -1;
                    }
                    if (o1.getResource().getCenterOfGeneralOperatingArea().distance(centerNextStore) > o2.getResource().getCenterOfGeneralOperatingArea().distance(centerNextStore)) {
                        return +1;
                    }
                    return o1.toString().compareTo(o2.toString());
                }
            });
        } else {
            Point2d p2 = routingTransportOperation.getDestination().getCenterOfGeneralOperatingArea();
            Point3d point = new Point3d(p2.getX(), p2.getY(), 0);
            EvalFunction_StoreOperation_ClosestPosition_AvoidSection sort = new EvalFunction_StoreOperation_ClosestPosition_AvoidSection(point, problem);
            Collections.sort(possibleStoreOperations, new StoreOperationComparator(sort));
        }

//        Rectangle2D rect = routingTransportOperation.getOrigin().getGeneralOperatingArea().getBounds2D();
//        Point3d originPoint = new Point3d(rect.getCenterX(), rect.getCenterY(), 0);
//        EvalFunction_StoreOperation_ClosestPosition evalFunction = new EvalFunction_StoreOperation_ClosestPosition(originPoint);
//        Collections.sort(possibleStoreOperations, new StoreOperationComparator(evalFunction));
        return possibleStoreOperations;
    }

    /**
     * Falls Lager nicht verfügbar, muss der gesamte Auftrag nach hinten
     * geschoben werden. Hierfür wird eine neue Startzeit der ursprünglichen
     * Transportoperation benötigt.
     *
     * @param beginStoreOperation
     * @param currentTransport
     * @param ealosaes
     * @param schedule
     * @param rules
     * @return
     */
    public static FieldElement getNewStorageDependendStartTimeForTransport(FieldElement beginStoreOperation, RoutingTransportOperation routingTransportOperation, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, LoadUnitJobSchedule schedule, InstanceHandler rules) {
        LoadUnitStorage destination = routingTransportOperation.getDestination();
        StorageRule sr = (StorageRule) rules.get(destination);

        FieldElement duration = getStorageDuration(beginStoreOperation, routingTransportOperation, ealosaes);
        if (duration == null) {
            return null;
        }

        /**
         * Wenn es die letzte Operation ist, einplanen bis Ende möglich
         */
        TimeSlot ts = new TimeSlot(beginStoreOperation, destination.getTemporalAvailability().getUntilWhen());
        if (!routingTransportOperation.getRouting().isLast(routingTransportOperation)) {
            int indexOfRtop = routingTransportOperation.getRouting().indexOf(routingTransportOperation);
            RoutingTransportOperation rTopNext = routingTransportOperation.getRouting().get(indexOfRtop + 1);
            FieldElement maxSlotEnd = ealosaes.get(rTopNext).getLatestStart();
            if (maxSlotEnd.isLowerThan(ts.getUntilWhen())) {
                ts.setUntilWhen(maxSlotEnd);
            }
        }

        DefaultStoreOperation storeOp = new DefaultStoreOperation(routingTransportOperation.getLoadUnit(), destination, duration);
        TimeSlotList freeSlots = sr.getFreeSlots(schedule, storeOp, ts);
        if (freeSlots.isEmpty()) {
            return null;
        }
        FieldElement startTime = freeSlots.first().getFromWhen(); //sr.getNextPossibleBundleStartTime(schedule, storeOp, ts);
        if (startTime == null) {
            return null;
        }
        startTime = startTime.sub(routingTransportOperation.getDuration());

        if (startTime instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval newDurationFuzzy = (DiscretizedFuzzyInterval) routingTransportOperation.getDuration();
            DiscretizedFuzzyInterval startTOPFuzzy = (DiscretizedFuzzyInterval) startTime;
            startTime = startTOPFuzzy.interactiveSub(newDurationFuzzy);
        }
        return startTime;

    }

    public static FieldElement getStorageDuration(FieldElement beginStoreOperation, RoutingTransportOperation routingTransportOperation, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes) {
        LoadUnitStorage destination = routingTransportOperation.getDestination();
        FieldElement duration = null;

        if (beginStoreOperation.isGreaterThan(destination.getTemporalAvailability().getUntilWhen())) {
            return null;
        }
        duration = destination.getTemporalAvailability().getUntilWhen().sub(beginStoreOperation);

        /**
         * Sonderbehandlungen im unscharfen Fall.
         */
        if (duration instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval beginStoreOperationFuzzy = (DiscretizedFuzzyInterval) beginStoreOperation;
            DiscretizedFuzzyInterval end = (DiscretizedFuzzyInterval) destination.getTemporalAvailability().getUntilWhen();
            duration = end.interactiveSub(beginStoreOperationFuzzy);
        }

//        if (duration instanceof DiscretizedFuzzyInterval) {
//            DiscretizedFuzzyInterval durationF = (DiscretizedFuzzyInterval) duration;
//            for (AlphaCutSet alphaCutSet : durationF.getAlphaCutSets()) {
//                if (alphaCutSet.getMin() < 0.0) {
//                    //IST DAS JETZT EIN DNF oder nicht?
////                     throw new IllegalArgumentException("Dauer kleiner 0");
//                    return null;
//                }
//            }
//        }
        return duration;
    }

    private static void determineLambdaForAdaption(LoadUnitJobSchedule schedule, StoreOperation lastStoreOperation, FieldElement start) {
        ScheduleRule rule = schedule.getHandler().get(lastStoreOperation.getResource());

        rule.haveEnoughCapacity(schedule, lastStoreOperation, start);
    }

    public static void adaptPreviousStorage(LoadUnitJobSchedule schedule, TransportBundle bundle, MultiScaleTransportOperation j) {

        /**
         * Verkürzen der vorangegangen Lageroperation.
         */
        LoadUnitPositions operationsForLoadUnit = schedule.getOperationsForLoadUnit(j.getLoadUnit());
        if (operationsForLoadUnit != null && operationsForLoadUnit.size() > 0) {
            LoadUnitOperation operation = operationsForLoadUnit.get(operationsForLoadUnit.size() - 1);
            if (!(operation instanceof StoreOperation)) {
                System.err.println("Keine StoreOperation vorangegangen, sondern Transportoperation");
            }
            StoreOperation lastStoreOperation = (StoreOperation) operation;

            //Etwaige Übergabezeiten beachten!
            FieldElement startTOP = bundle.getStartTime(j);
            FieldElement startTimeLastStore = schedule.get(lastStoreOperation);
            if (startTimeLastStore == null) {
                throw new NoSuchElementException("Operation nicht im Schedule eingeplant");
            }

            FieldElement newDuration = startTOP.sub(startTimeLastStore);
            /**
             * Sonderbehandlung bei Fuzzy-Zahlen
             */
            if (startTOP instanceof DiscretizedFuzzyInterval) {
                DiscretizedFuzzyInterval startTimeLastStoreFuzzy = (DiscretizedFuzzyInterval) startTimeLastStore;
                DiscretizedFuzzyInterval startTOPFuzzy = (DiscretizedFuzzyInterval) startTOP;
                newDuration = startTOPFuzzy.interactiveSub(startTimeLastStoreFuzzy);
            }

            /**
             * Sicherheitstest, dass keine Dauer kleiner als 0 ist.
             */
            if (newDuration instanceof DiscretizedFuzzyInterval) {
                DiscretizedFuzzyInterval durationF = (DiscretizedFuzzyInterval) newDuration;
                for (AlphaCutSet alphaCutSet : durationF.getAlphaCutSets()) {
                    if (alphaCutSet.getMin() < 0.0) {
                        alphaCutSet.setMin(0.0);
//                        throw new IllegalArgumentException("Dauer kleiner 0");
                    }
                }
            }

            try {
                FieldElement startLastStore = schedule.get(lastStoreOperation);
                schedule.unschedule(lastStoreOperation);
                lastStoreOperation.setDuration(newDuration);
                if (lastStoreOperation instanceof SuperStoreOperation) {
                    SuperStoreOperation supOps = (SuperStoreOperation) lastStoreOperation;
                    for (Operation subOp : supOps.getSubOperations().getSubOperations()) {
                        subOp.setDuration(newDuration);
                    }
                }

                schedule.schedule(lastStoreOperation, startLastStore);
                /**
                 * Anpassen der Lagerung für alle Manager
                 */

            } catch (Exception e) {
                e.printStackTrace();
                String fehlermeldung = "";
                for (LoadUnitOperation loadUnitOperation : operationsForLoadUnit) {
                    fehlermeldung += TimeSlot.longToFormattedDateString(schedule.get(loadUnitOperation).longValue()) + "\t" + TimeSlot.longToFormattedDateString(schedule.get(loadUnitOperation).add(loadUnitOperation.getDuration()).longValue()) + "\t" + loadUnitOperation + "\n";
                }
                fehlermeldung += "Versuchter Einplanzeitpunkt: " + TimeSlot.longToFormattedDateString(startTOP.longValue());
                System.err.println(fehlermeldung);
                throw new RuntimeException();
            }
        }

    }

}
