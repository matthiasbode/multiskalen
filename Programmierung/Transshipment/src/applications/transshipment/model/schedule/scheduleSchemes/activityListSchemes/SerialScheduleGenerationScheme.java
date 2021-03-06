/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes;

import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import applications.transshipment.model.schedule.scheduleSchemes.StorageToolMethods;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.EALOSAEBuilder;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.dnf.DNFTreatment;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import static applications.transshipment.model.schedule.scheduleSchemes.StorageToolMethods.getStorageOperationsAtDestination;
import applications.transshipment.model.schedule.scheduleSchemes.Tools;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class SerialScheduleGenerationScheme implements Transshipment_ActivityListScheduleScheme {

    private final DNFTreatment dnfTreatment;

    public SerialScheduleGenerationScheme(DNFTreatment dnfTreatment) {
        this.dnfTreatment = dnfTreatment;

    }

    @Override
    public LoadUnitJobSchedule getSchedule(LoadUnitJobSchedule result, List<RoutingTransportOperation> activityList, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> originalEalosaes, TerminalProblem p, ActivityOnNodeGraph<RoutingTransportOperation> graph, TimeSlot scheudleTimeSlot) {

        if (result == null) {
            throw new NullPointerException("Eingabeschedule ist NULL");
        }

        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = Tools.cloneEalosaes(originalEalosaes);
 

        /**
         * Schleife über alle Operationen
         */
        OperationLoop:
        while (!activityList.isEmpty()) {

            RoutingTransportOperation rtop = activityList.get(0);

            FieldElement tStar;

            /**
             * TODO: Warum kann das passieren?
             */
            if (result.getDnfJobs().contains(rtop.getJob())) {
                throw new UnknownError("Job bereits schon in den DNFs");
            }

            /**
             * Bestimme das minimale T aus den Vorgängerrestriktionen und den
             * EALOSAES.
             */
            tStar = scheudleTimeSlot.getFromWhen();

            /**
             * Finde erste Operation in der Aktivitätsliste, die ausführbar ist.
             */
            ActivityLoop:
            for (RoutingTransportOperation pred : graph.getPredecessors(rtop)) {
                FieldElement Si = null;
                /**
                 * Falls Vorgängeroperation DNF, setze auf spätestes Ende.
                 */
                if (result.getDidNotFinish().contains(pred)) {
                    Si = ealosaes.get(pred).getLatestEnd();

                } else {
                    Si = result.get(pred);
                }
                /**
                 * Vorgängeroperation noch nicht eingeplant.
                 */
                if (Si == null) {
                    throw new UnknownError("Vorgängeroperation nicht eingefügt, sollte nicht vorkommen!");
                }

                /**
                 * Keine Dauer gesetzt.
                 */
                FieldElement pi = pred.getDuration();
                if (pi == null) {
                    throw new IllegalArgumentException("Keine Dauer gesetzt.");
                }

                /**
                 * Aktualisierung des Operationsstarts.
                 */
                FieldElement tCand = Si.add(pi);
                if (tCand.isGreaterThan(tStar)) {
                    tStar = tCand;
                }
            }
            /**
             * aktuelle Operation erfüllt Nebenbediungen, daher wird sie weiter
             * verwendet und aus der Liste entfernt.
             */
            activityList.remove(rtop);

            EarliestAndLatestStartsAndEnds currentEalosae = ealosaes.get(rtop);
            if (currentEalosae == null) {
                throw new NullPointerException("keine EALOSAES hinterlegt!");
            }
            FieldElement earliestOperationStart = currentEalosae.getEarliestStart();
            if (earliestOperationStart.isGreaterThan(tStar)) {
                tStar = earliestOperationStart;
            }

            FieldElement latestOperationStart = currentEalosae.getLatestStart();

            /**
             * t wird unter Umständen verändert, tStar bleibt für diese
             * Operation bestehen als frühstmöglichster Startpunkt.
             */
            FieldElement t = tStar;

            /**
             * Falls t schon größer als latestOperationStart ---> dann DNF...
             */
            if (t.isGreaterThan(latestOperationStart)) {
                t = null;
            }
            /**
             * Ressourcentest für Operation j
             */
            TransportBundle bundle = null;

            MultiScaleTransportOperation j = null;

            /**
             * Diese Schleife setzt unter Umständen das t höher.
             */
            ResourceTestLoop:
            while (true) {

                /**
                 * DNF-Abarbeitung
                 */
                if (t == null) {
//                    System.out.println(rtop.getId());
//                    System.exit(0);
                    /**
                     * Kein DNFJob erzeugt, überspringe die Operation.
                     */
                    if (!dnfTreatment.setDNF(rtop, tStar, activityList, result, graph, ealosaes)) {
                        TransshipmentParameter.logger.finest("Job konnte nicht DNF gesetzt werden:" + rtop.getLoadUnit().getID());
                        continue OperationLoop;
                    } else {
                        TransshipmentParameter.logger.finest("Job DNF gesetzt :" + rtop.getLoadUnit().getID());
                    }
                    continue OperationLoop;
                }

                ConveyanceSystem cs = rtop.getResource();

                /**
                 * #################################################################
                 * START: Bestimmen von genaueren Start/Ziel und entsprechender
                 * Operation
                 * #################################################################
                 */
                ConveyanceSystemRule rule = (ConveyanceSystemRule) result.getHandler().get(cs);
                /**
                 * Hier muss eigentilch noch die Rüstfahrt berücksichtigt
                 * werden.
                 */
                FieldElement startStore = t.add(rtop.getDuration());
                Collection<StoreOperation> storageOperationsAtDestination = getStorageOperationsAtDestination(startStore, p, rtop, ealosaes, result, result.getHandler());
                if (storageOperationsAtDestination.isEmpty()) {
                    t = StorageToolMethods.getNewStorageDependendStartTimeForTransport(startStore, rtop, ealosaes, result, result.getHandler());
                    continue ResourceTestLoop;
                }
                StoreOperation next = storageOperationsAtDestination.iterator().next();

                LoadUnitStorage origin = result.getOrigin(rtop);

                j = rule.getDetailedOperation(rtop, origin, next.getResource());

                /**
                 * #################################################################
                 * Bestimmen von genaueren Start/Ziel ENDE
                 * #################################################################
                 */
                /**
                 * Das t, mit dem angefragt wird, gilt für die Startzeit der
                 * Transportoperation. Als Restriktionen zur Bestimmung des t
                 * dienen lediglich die Vorgängeroperationen der Operation j,
                 * nicht aber die auf der aktuellen Resource.
                 */
                {
                    /**
                     * Kann das Bundle am dem Zeitpunkt t eingeplant werden?
                     */
                    if (!rule.canSchedule(result, j, t)) {
                        if (t.isGreaterThan(cs.getTemporalAvailability().getUntilWhen())) {
                            t = null;
                            continue ResourceTestLoop;
                        }
                        /*
                         * Ressourcenanforderungen zu hoch, Zeitfenster muss
                         * angepasst werden
                         */
                        TimeSlot interval = new TimeSlot(t, cs.getTemporalAvailability().getUntilWhen());
                        FieldElement newStartTime = rule.getNextPossibleBundleStartTime(result, j, interval);

                        /**
                         * DNF-Test
                         */
                        if (newStartTime != null && (newStartTime.equals(t) || newStartTime.isGreaterThan(latestOperationStart) || newStartTime.equals(latestOperationStart))) {
                            t = null;
                        } else {
                            t = newStartTime;
                        }
                        continue ResourceTestLoop;
                    }
                }

                bundle = rule.getBundle(result, j, t);
                FieldElement newStartStore = bundle.getStartTime(bundle.getJ()).add(j.getDuration());
                bundle.setStoreAtDestination(next, new TimeSlot(newStartStore, newStartStore.add(next.getDuration())));

                /**
                 * Keine Anpassungen mehr nötig.
                 */
                break ResourceTestLoop;
            }
            if (bundle == null) {
                throw new UnknownError("Nicht einplanbar!");
            }

            /**
             * Einplanen der Operation.!!!
             */
            result.scheduleBundle(bundle,t);
            EALOSAEBuilder.<RoutingTransportOperation>updateEaloses(j.getRoutingTransportOperation(), j.getDuration(), bundle.getStartTime(j), ealosaes, graph);

        }
        TransshipmentParameter.logger.finer("Anzahl DNF-Jobs:" + result.getDnfJobs().size());
        TransshipmentParameter.logger.finer("Anzahl eingeplanter Jobs:" + result.getScheduledJobs().size());

        return result;
    }



}
