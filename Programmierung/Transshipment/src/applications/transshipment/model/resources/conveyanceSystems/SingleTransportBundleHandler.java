/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.mmrcsp.model.operations.SingleResourceOperation;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import math.FieldElement;

/**
 * Bestimmt das Bundle von Operationen für ein einzelnes ConveyanceSystem.
 *
 * @author bode
 */
public class SingleTransportBundleHandler {

    private ConveyanceSystem cs;
    private ConveyanceSystemRule rule;

    private HashMap<Operation, TransportBundle> bundles = new HashMap<>();

    public SingleTransportBundleHandler(ConveyanceSystem cs, ConveyanceSystemRule rule) {
        this.cs = cs;
        this.rule = rule;
    }

    /**
     * Diese Methode wird später von der ScheduleRule aufgerufen und
     * weitergereicht an das Schedule Generation Scheme
     *
     * @param top
     * @return
     */
    public TransportBundle getBundle(TransportOperation top) {
        return this.bundles.get(top);
    }

    /**
     * Test, ob die Operation zu dem übergebenen Startzeitpunkt einplanbar ist.
     * Dafür werden ebenfalls die Rüstfahrten, etc. berücksichtigt.
     *
     * @param s
     * @param startBundle
     * @param top
     * @return
     */
    public boolean isBundleScheduable(Schedule s, FieldElement startBundle, MultiScaleTransportOperation top) {
        /**
         * Bestimme zunächst das Transportbundle
         */
        TransportBundle bundle = getBundle(s, startBundle, top);
        if (bundle == null) {
            return false;
        }

        /*
         * Test ob Rüstfahrt einplanbar.
         */
        /**
         * Rüstfahrt direkt vor TransportOperation
         */
        FieldElement startSQJ = startBundle;
        if (!canScheduleInternal(s, bundle.getSqj(), startSQJ)) {
            return false;
        }

        s.schedule(bundle.getSqj(), startSQJ);
        FieldElement startTop = startSQJ.add(bundle.getSqj().getDuration());

        /**
         * Test, ob Transport einplanbar ist.
         */
        if (!canScheduleInternal(s, bundle.getJ(), startTop)) {
            s.unschedule(bundle.getSqj());
            return false;
        }
//        if (s.fuzzyWorkloadParameters.get(top) != null) {
//            startTop = s.fuzzyWorkloadParameters.get(top).adaptedStart;
//        }
        s.unschedule(bundle.getSqj());
        FieldElement startNextSetup = startTop.add(bundle.getJ().getDuration());

        /**
         * Test, ob nachfolgende IdleSettingUp einplanbar.
         */
        if (bundle.getSjq1_new() != null) {

            /**
             * Ausplanunung von sj1q_old Versuch der Einplanung - Rüstfahrt sqj
             * - Transportoperation top - IdleSettingUpOperation sjq1_new.
             */
            FieldElement oldStartTimeSJQ1 = null;
            if (bundle.getSqq1_old() != null) {
                oldStartTimeSJQ1 = s.unschedule(bundle.getSqq1_old());
            }

            if (!canScheduleInternal(s, bundle.getSjq1_new(), startNextSetup)) {
                return false;
            }

            /**
             * Hat alles gepasst bis hierher. Alte IdleSettingUp wieder
             * einplanen.
             */
            if (bundle.getSqq1_old() != null) {
                s.schedule(bundle.getSqq1_old(), oldStartTimeSJQ1);
            }
        }
        if (bundle.getSqj() != null) {
            bundle.setStartTime(bundle.getSqj(), startSQJ);
        }
        bundle.setStartTime(bundle.getJ(), startTop);
        if (bundle.getSjq1_new() != null) {
            bundle.setStartTime(bundle.getSjq1_new(), startNextSetup);
        }
        bundles.put(top, bundle);
        return true;
    }

    /**
     * Such für den angefragt Zeitpunkt ein TransportBundle aus eventueller
     * Rüstfahrt zum Auftrag, den Auftrag und einer eventuell anschließenden
     * Rüstfahrt.
     *
     * @param s
     * @param insertTimePoint zu diesem Zeitpunkt soll die Rüstfahrt eingeplant
     * werden, darauffolgend der Transport.
     * @param top
     * @return
     */
    private TransportBundle getBundle(Schedule s, FieldElement insertTimePoint, MultiScaleTransportOperation top) {
        if (insertTimePoint == null) {
            throw new IllegalArgumentException("Keine Startzeit definiert");
        }

        TreeMultimap<FieldElement, Operation> timeToOperationMap = s.getTimeToOperationMap(cs);
        /**
         * Vorangegangene TransportOperation.
         */
        TransportOperation q = null;
        /**
         * Bestimme vorangegangen TransportOperation
         */
        Map.Entry<FieldElement, Collection<Operation>> lowerEntry = timeToOperationMap.asMap().lowerEntry(insertTimePoint);

        Collection<Operation> operationsBefore = new LinkedHashSet<>();

        if (lowerEntry != null) {
            operationsBefore = lowerEntry.getValue();
            if (operationsBefore.size() != 1) {
                for (Operation operation : operationsBefore) {
                    if (operation instanceof TransportOperation) {
                        q = (TransportOperation) operation;
                        break;
                    }
                }
            } else {
                Operation qop = operationsBefore.iterator().next();

                /**
                 * Nur Transportoperationen können untersucht werden.
                 */
                if (qop instanceof IdleSettingUpOperation) {
                    return null;
                }

                q = (TransportOperation) qop;
            }
        }

        /**
         * Suche SetupOperation von vorangegangener zur neu einzuplanender
         * Operation.
         */
        IdleSettingUpOperation sqj = rule.findIdleSettingUpOperation(q, top);

        /**
         * Hole zunächst die Setup-Operation zur nächsten TransportOperation.
         */
        IdleSettingUpOperation sqq1_old = null;
        /**
         * Gibt es eine vorangegangene TransportOperation q., dann bestimme die
         * Rüstfahrt als nachfolgende Operation von q.
         */
        if (q != null) {
            FieldElement startQ = s.get(q);
            Map.Entry<FieldElement, Collection<Operation>> higherEntry = timeToOperationMap.asMap().higherEntry(startQ);
            if (higherEntry != null) {
                Collection<Operation> candidatesSqq1_old = timeToOperationMap.asMap().higherEntry(startQ).getValue();
                for (Operation candidate : candidatesSqq1_old) {
                    if (candidate instanceof IdleSettingUpOperation) {
                        sqq1_old = (IdleSettingUpOperation) candidate;
                    }
                }
            }
        }

        /**
         * Dann die darauffolgende bereits eingeplante Transportoperation
         */
        MultiScaleTransportOperation q1Top = null;
        /**
         * Gibt es eine Rüstfahrt, bestimme die nachfolgende Transportoperation
         * als Operation nach der Rüstfahrt
         */
        if (sqq1_old != null) {
            try {
                FieldElement startQQ1_old = s.get(sqq1_old);
                if (startQQ1_old == null) {
                    throw new UnknownError("keine Startzeit hinterlegt.");
                }
                FieldElement higher = timeToOperationMap.asMap().higherKey(startQQ1_old);
                if (higher == null) {
                    throw new UnknownError("Nachfolgende Rüstfahrt gefunden, aber keine Einträge");
                }
                Collection<Operation> candidatesq1Top = timeToOperationMap.asMap().get(higher);
                for (Operation candidate : candidatesq1Top) {
                    if (candidate instanceof IdleSettingUpOperation) {
                        q1Top = (MultiScaleTransportOperation) candidate;
                    }
                }
                if (sqq1_old == null) {
                    throw new NullPointerException("Keine vorherige Rüstfahrt gefunden");
                }
            } catch (Exception e) {
                String message = "Falsche Reihenfolge, Operation nicht auf Transportopertaion castbar\n";
                message += ("sqq1_old\t" + sqq1_old + "\n");
//                for (Operation operation : operationsForResource) {
//                    message += (s.get(operation) + "\t" + operation + "\n");
//                }
                message += ("-----------------------");
                e.printStackTrace();
                throw new IllegalArgumentException(message);
            }
        } /**
         * Sonst bestimme die nächste Operation nach startSlot.
         */
        else {
            Map.Entry<FieldElement, Collection<Operation>> higherEntry = timeToOperationMap.asMap().higherEntry(insertTimePoint);

            if (higherEntry != null) {
                Collection<Operation> operationsAbove = higherEntry.getValue();
                if (operationsAbove.size() != 1) {
                    if (operationsAbove.size() == 2) {
                        operationsAbove = new ArrayList<>(operationsAbove);
                        for (Iterator<Operation> iterator = operationsAbove.iterator(); iterator.hasNext();) {
                            Operation next = iterator.next();
                            if (next instanceof IdleSettingUpOperation) {
                                iterator.remove();
                            }
                        }
                    } else {
                        throw new UnsupportedOperationException("Noch nicht implementiert");
                    }
                }
                Operation qop = operationsAbove.iterator().next();

                /**
                 * Nur Transportoperationen können untersucht werden.
                 */
                if (qop instanceof IdleSettingUpOperation) {
                    return null;
                }

                q1Top = (MultiScaleTransportOperation) qop;
            }

        }

        /**
         * Bestimme neue Rüstfahrt
         */
        IdleSettingUpOperation sjq1_new = null;
        if (q1Top != null) {
            sjq1_new = rule.findIdleSettingUpOperation(top, q1Top);
        }
        TransportBundle bundle = new TransportBundle(cs, q, operationsBefore, sqj, top, sqq1_old, sjq1_new, q1Top);
        return bundle;
    }

    /**
     * Methode für den Gebrauch innerhalb dieser Klasse, die einen
     * canScheduleInternal-Test für die einzelnen Operationen, also sowohl für
     * die Rüstfahrten als auch für die Transportoperationen, macht.
     *
     * @param s
     * @param o
     * @param start
     * @return
     */
    private boolean canScheduleInternal(Schedule s, Operation o, FieldElement start) {
        if (start == null) {
            throw new IllegalArgumentException("Eine Startzeit muss definiert sein!");
        }
        if (!rule.haveEnoughCapacity(s, o, start)) {
            return false;
        } else {
            /**
             * Test bei allen SubResources.
             */
            if (o instanceof SingleResourceOperation) {
                SingleResourceOperation so = (SingleResourceOperation) o;
                SubOperations subResourceDemand = so.getSubOperations();
                if (subResourceDemand == null) {
                    return true;
                }
                for (Operation subOperation : subResourceDemand.getSubOperations()) {
                    for (Resource subResource : subOperation.getRequieredResources()) {
                        FieldElement startSubResource = start.add(subResourceDemand.getTimeOffset().get(subOperation));
                        if (!s.getHandler().get(subResource).canSchedule(s, subOperation, startSubResource)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Bestimmmt die neue Transportzeit für die Operation unter Berücksichtigung
     * aller für die Transportoperation benötigten Rüstfahrten.
     *
     * @param result
     * @param o
     * @param intervalForTransport
     * @return
     */
    public FieldElement getBundleStartTime(Schedule result, MultiScaleTransportOperation o, TimeSlot intervalForTransport) {
        /**
         * Die Startzeit ist schon über getFreeSlots gegen den
         * StepFunctionBasedUtilizationManager für die Transportoperation
         * getestet.
         */
        FieldElement currentBundleStart = intervalForTransport.getFromWhen().clone();

        while (currentBundleStart.isLowerThan(intervalForTransport.getUntilWhen())) {
            TimeSlot currentSlot = new TimeSlot(currentBundleStart, intervalForTransport.getUntilWhen());

            TransportBundle bundle = getBundle(result, currentBundleStart, o);
            if (bundle == null) {
                return null;
            }

            FieldElement currentStartTop = currentBundleStart;

            /**
             * Ist eine Rüstfahrt erforderlich, muss diese zunächst eingeplant
             * werden.
             */
            if (bundle.getSqj() != null) {
                /**
                 * Zunächst wird versucht die Rüstfahrt zu Beginn des Intervalls
                 * einzuplanen.
                 */
                FieldElement startSQJ = currentBundleStart;
                /*
                 * Test ob Rüstfahrt einplanbar.
                 */
                if (!canScheduleInternal(result, bundle.getSqj(), startSQJ)) {
                    /**
                     * Wenn nicht, bestimme neuen Zeitraum mit
                     * IntervalForTransport
                     */
                    currentBundleStart = getStartTimeInternal(result, bundle.getSqj(), currentSlot);
                    /**
                     * Nicht einplanbar!
                     */
                    if (currentBundleStart == null) {
                        return null;
                    } else {
                        continue;
                    }
                }

                bundle.setStartTime(bundle.getSqj(), startSQJ);

                /**
                 * Aktualisierung Startzeit Transport.
                 */
                FieldElement durationRuestFahrt = bundle.getSqj().getDuration();
                currentStartTop = startSQJ.add(durationRuestFahrt);
                /*
                 * Test, ob Transport noch ins Zeitfenster passt.
                 */
                if (currentStartTop.isGreaterThan(intervalForTransport.getUntilWhen())) {
                    return null;
                }
            }

            /**
             * Einplanen der Transportoperation, dessen Startzeit bestimmen.
             */
            if (!canScheduleInternal(result, bundle.getJ(), currentStartTop)) {
                currentSlot = new TimeSlot(currentStartTop, currentSlot.getUntilWhen());
                currentBundleStart = getStartTimeInternal(result, bundle.getJ(), currentSlot);
                if (currentBundleStart == null) {
                    return null;
                }

//                if (bundle.getSqj() != null) {
//                    currentBundleStart = currentBundleStart.sub(bundle.getJ().getDuration());
//                }
                continue;
            }

            bundle.setStartTime(bundle.getJ(), currentStartTop);

            /**
             * Test, ob Ende der TransportOperation noch passt.
             */
            FieldElement endTransport = currentStartTop.add(bundle.getJ().getDuration());
            if (endTransport.isGreaterThan(intervalForTransport.getUntilWhen())) {
                return null;
            }

            FieldElement startNextSetup = endTransport;
            /**
             * Test, ob nachfolgende IdleSettingUp einplanbar.
             */
            if (bundle.getSjq1_new() != null) {
                /**
                 * Ausplanunung von sj1q_old Versuch der Einplanung - Rüstfahrt
                 * sqj - Transportoperation top - IdleSettingUpOperation
                 * sjq1_new.
                 */
                FieldElement oldStartTimeSJQ1 = null;
                if (bundle.getSqq1_old() != null) {
                    oldStartTimeSJQ1 = result.unschedule(bundle.getSqq1_old());
                }
                if (!canScheduleInternal(result, bundle.getSjq1_new(), startNextSetup)) {
                    TimeSlot interval = new TimeSlot(currentStartTop, intervalForTransport.getUntilWhen());
                    startNextSetup = getStartTimeInternal(result, bundle.getSjq1_new(), interval);
                }
                FieldElement endNextSetUp = startNextSetup.add(bundle.getSjq1_new().getDuration());
                if (endNextSetUp.isGreaterThan(intervalForTransport.getUntilWhen())) {
                    return null;
                }
                bundle.setStartTime(bundle.getSjq1_new(), startNextSetup);
                /**
                 * Hat alles gepasst bis hierher. Alte IdleSettingUp wieder
                 * einplanen.
                 */
                result.schedule(bundle.getSqq1_old(), oldStartTimeSJQ1);
            }
            this.bundles.put(o, bundle);
            return currentBundleStart;
        }
        return null;
    }

    private FieldElement getStartTimeInternal(Schedule s, Operation o, TimeSlot interval) {
        TimeSlotList freeSlots = rule.getFreeSlots(s, o, interval);
        if (freeSlots.isEmpty()) {
            return null;
        }
        FieldElement startTimeInternal = freeSlots.iterator().next().getFromWhen();

        if (startTimeInternal == null) {
            return null;
        }
        /**
         * Test bei allen SubResources.
         */
        if (o instanceof SingleResourceOperation) {
            SingleResourceOperation so = (SingleResourceOperation) o;
            SubOperations subResourceDemand = so.getSubOperations();
            if (subResourceDemand == null) {
                return startTimeInternal;
            }
            for (Operation subOperation : subResourceDemand.getSubOperations()) {
                for (Resource subResource : subOperation.getRequieredResources()) {
                    FieldElement startSubResource = startTimeInternal.add(subResourceDemand.getTimeOffset().get(subOperation));
                    ScheduleRule subRule = s.getHandler().get(subResource);
                    if (!subRule.canSchedule(s, subOperation, startSubResource)) {
                        if (!startSubResource.isLowerThan(interval.getUntilWhen())) {
                            return null;
                        }
                        TimeSlot requestSlot = new TimeSlot(startSubResource, interval.getUntilWhen());
                        FieldElement nextPossibleBundleStartTime = subRule.getFreeSlots(s, subOperation, requestSlot).first().getFromWhen();
                        if (nextPossibleBundleStartTime == null) {
                            return null;
                        }
                        TimeSlot newInterval = new TimeSlot(nextPossibleBundleStartTime, interval.getUntilWhen());
                        return getStartTimeInternal(s, o, newInterval);
                    }
                }
            }
        }
        return startTimeInternal;
    }

}
