/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.eval.EvalFunction_Slot_ClosestPosition;
import applications.transshipment.model.eval.EvalFunction_StoreOperation;
import applications.transshipment.model.eval.EvalFunction_StoreOperation_ClosestPosition_AvoidSection;
import applications.transshipment.model.eval.SlotComparator;
import applications.transshipment.model.eval.StoreOperationComparator;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.rules.StorageRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs.MinAvailabilityComparator;
import applications.transshipment.model.structs.ReservationForLuOperation;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Train;
import applications.transshipment.model.structs.TrainType;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 *
 * @author bode
 */
public class ScheduleTools {

    @Deprecated
    public static void findExactOriginsANDReserveStamm(TerminalProblem p) {
        findExactOriginsANDReserveStamm(p, null);
    }

    public static void findExactOriginsANDReserveStamm(TerminalProblem p, ScheduleManagerBuilder forOrigins) {
        //Reserviere Stammrelation
        for (LoadUnitJob job : p.getStammRelation()) {
            Slot slot = (Slot) job.getOrigin();
            TimeSlot ts = new TimeSlot(slot.getTemporalAvailability().getFromWhen(), slot.getTemporalAvailability().getUntilWhen());
            ReservationForLuOperation ro = new ReservationForLuOperation(slot, job.getLoadUnit(), ts);
            slot.setFinalStoringReservation(ro);
        }
        InstanceHandler instanceHandler = new InstanceHandler(forOrigins);
        /**
         * Hier wird ein Schedule erzeugt. Dieser dient nur zum Reservieren der
         * Pätze, etc. Nur für die Suche.
         */
        LoadUnitJobSchedule s = new LoadUnitJobSchedule(instanceHandler);

        ArrayList<LoadUnitJob> jobsToScheduleAndersSortiert = new ArrayList<>(p.getJobs());
        MinAvailabilityComparator<LoadUnitJob> maxAvailabilityComparator = new MinAvailabilityComparator<>(jobsToScheduleAndersSortiert, p.getJobTimeWindows());
        Collections.sort(jobsToScheduleAndersSortiert, maxAvailabilityComparator);

        for (LoadUnitJob job : jobsToScheduleAndersSortiert) {
            LoadUnit lu = job.getLoadUnit();
            TimeSlot temporalAvailability = lu.getOrigin().getTemporalAvailability().getAllOverTimeSlot();
            temporalAvailability = new TimeSlot(temporalAvailability.getFromWhen(), temporalAvailability.getFromWhen().add(temporalAvailability.getDuration()));

            //Hole Rule vom Origin und bestimme mögliche Operationen
            StorageRule rule = (StorageRule) s.getHandler().get(lu.getOrigin());
            List possibleStoreOperations = rule.getPossibleStoreOperations(lu, s, temporalAvailability.getFromWhen(), temporalAvailability.getDuration());
            if (possibleStoreOperations.isEmpty()) {
                System.err.println("Kein Platz im Origin gefunden!");
                possibleStoreOperations = rule.getPossibleStoreOperations(lu, s, temporalAvailability.getFromWhen(), temporalAvailability.getDuration());
                throw new UnknownError("Ladeeinheit kann nicht im Origin eingeplant werden! \n" + lu + "\nOrigin: " + lu.getOrigin() + "\nDestination: " + lu.getDestination() + "\nAngefragte Zeitraum: " + temporalAvailability);
            }

            //Falls kein TrainType, dann Lager, dann sortiere.
            if (!(job.getOrigin() instanceof TrainType)) {
                Point2d p2 = lu.getDestination().getCenterOfGeneralOperatingArea();
                Point3d point = new Point3d(p2.getX(), p2.getY(), 0);
                EvalFunction_StoreOperation_ClosestPosition_AvoidSection evalFunction = new EvalFunction_StoreOperation_ClosestPosition_AvoidSection(point, p);
                Collections.sort(possibleStoreOperations, new StoreOperationComparator(evalFunction));
            }

            StoreOperation storeOp = (StoreOperation) possibleStoreOperations.iterator().next();
            /**
             * Das eigentlich wichtige!!!
             */
            lu.setOrigin(storeOp.getResource());
            job.setCurrentOrigin(storeOp.getResource());

            s.schedule(storeOp, temporalAvailability.getFromWhen());
        }
    }

    public static void scheduleStammLU(LoadUnitJobSchedule s, LoadUnitJob job) {
        Slot slot = (Slot) job.getOrigin();
        TimeSlot ts = new TimeSlot(slot.getTemporalAvailability().getFromWhen(), slot.getTemporalAvailability().getUntilWhen());
        StorageRule rule = (StorageRule) s.getHandler().get(slot);
        List possibleStoreOperations = rule.getPossibleStoreOperations(job.getLoadUnit(), s, ts.getFromWhen(), ts.getDuration());
        if (possibleStoreOperations.isEmpty()) {
            throw new UnknownError("Stamm-Relation- Ladeeinheit kann nicht im Origin eingeplant werden!");
        }
        StoreOperation storeOp = (StoreOperation) possibleStoreOperations.iterator().next();
        s.schedule(storeOp, ts.getFromWhen());
    }

    public static void scheduleStoreOnBeginning(LoadUnitJobSchedule s, TerminalProblem p) {
        /**
         * Stammrelation
         */
        for (LoadUnitJob loadUnitJob : p.getStammRelation()) {
            ScheduleTools.scheduleStammLU(s, loadUnitJob);
        }

        /**
         * Einplanen im Origin
         */
        for (LoadUnitJob loadUnitJob : p.getJobs()) {
            LoadUnit lu = loadUnitJob.getLoadUnit();
            TimeSlot temporalAvailability = lu.getOrigin().getTemporalAvailability().getAllOverTimeSlot();
            temporalAvailability = new TimeSlot(temporalAvailability.getFromWhen(), temporalAvailability.getFromWhen().add(temporalAvailability.getDuration()));
            StorageRule rule = (StorageRule) s.getHandler().get(lu.getOrigin());
            List possibleStoreOperations = rule.getPossibleStoreOperations(lu, s, temporalAvailability.getFromWhen(), temporalAvailability.getDuration());
            if (possibleStoreOperations.isEmpty()) {
                throw new UnknownError("Ladeeinheit kann nicht im Origin eingeplant werden!");
            }
            if (!(loadUnitJob.getOrigin() instanceof TrainType)) {
                Point2d p2 = lu.getDestination().getCenterOfGeneralOperatingArea();
                Point3d point = new Point3d(p2.getX(), p2.getY(), 0);
                EvalFunction_StoreOperation evalFunction = new EvalFunction_StoreOperation_ClosestPosition_AvoidSection(point, p);
                Collections.sort(possibleStoreOperations, new StoreOperationComparator(evalFunction));
            }

            StoreOperation storeOp = (StoreOperation) possibleStoreOperations.iterator().next();
            if (!lu.getOrigin().equals(storeOp.getResource())) {
                throw new UnknownError("Konnte nicht eingeplant werden!");
            }
            lu.setOrigin(storeOp.getResource());
            loadUnitJob.setCurrentOrigin(lu.getOrigin());
            s.schedule(storeOp, temporalAvailability.getFromWhen());

        }

    }

    /**
     * falls eine LoadUnit noch kein festes Ziel hat, wird jetzt eins gefunden.
     * fuer das Lager soll das nicht geschehen, weil die Reservierungen sonst
     * zuviel blockieren
     *
     * @param p
     */
    public static void specifyDestinations(TerminalProblem p) {
//        if (TransshipmentParameter.DEBUG) {
//            for (Train train : p.getTrains()) {
//                System.out.println("Train: " + train.getNumber());
//                int Gross = 0;
//                int klein = 0;
//                for (Slot slot : train.getStorageLocations()) {
//                    if (slot.getFinalStoringReservations() == null) {
//                        if (slot.getLength() > 7.0) {
//                            Gross++;
//                        } else {
//                            klein++;
//                        }
//                    }
//                }
//                System.out.println(Gross + "/" + klein + "/" + train.getNumberOfStorageLocations());
//            }
//
//            System.out.println("Muss drauf!!!!+++++++++++++++++++++++++++");
//            for (Train train : p.getTrains()) {
//                System.out.println("Train: " + train.getNumber());
//                int mussDraufGross = 0;
//                int mussDraufKlein = 0;
//                for (LoadUnitJob j : p.getJobs()) {
//                    LoadUnitStorage destination = j.getLoadUnit().getDestination();
//                    if ((destination instanceof Train)) {
//                        Train t = (Train) destination;
//                        if (t.equals(train)) {
//                            if (j.getLoadUnit().getLength() > 7.0) {
//                                mussDraufGross++;
//                            } else {
//                                mussDraufKlein++;
//                            }
//                        }
//                    }
//                }
//                System.out.println(mussDraufGross + "/" + mussDraufKlein + "/" + train.getNumberOfStorageLocations());
//            }
//        }
        ArrayList<LoadUnitJob> jobs = new ArrayList<>(p.getJobs());
        MinAvailabilityComparator comparator = new MinAvailabilityComparator(jobs, p.getJobTimeWindows());
        Collections.sort(jobs, comparator);

        for (LoadUnitJob j : jobs) {
            LoadUnitStorage destination = j.getDestination();
            if ((destination instanceof Train)) {
                boolean findAndReserveDestinationForLoadUnit = findAndReserveDestinationForLoadUnit(j);
                if (!findAndReserveDestinationForLoadUnit) {
                    throw new NoSuchElementException("Es kann kein exaktes Ziel gefunden werden!");
                }
            }
            if (destination instanceof Slot) {
                Slot slot = (Slot) destination;
                TimeSlot ts = new TimeSlot(j.getOrigin().getTemporalAvailability().getFromWhen(), slot.getTemporalAvailability().getUntilWhen());
                ReservationForLuOperation ro = new ReservationForLuOperation(slot, j.getLoadUnit(), ts);
                if (!slot.setFinalStoringReservation(ro)) {
                    throw new UnsupportedOperationException("Destination konnte nicht reserviert werden.");
                }
            }

        }
    }

    /**
     * Methode findet, setzt und reserviert ein exaktes Ziel
     * (ExactLoadUnitPosition) fuer die LoadUnit. Das Ziel kann auch belegt
     * sein, nur nicht reserviert.
     *
     * @param job
     * @return
     */
    public static boolean findAndReserveDestinationForLoadUnit(LoadUnitJob job) {
        Train destinationTrain = (Train) job.getDestination();

        ArrayList<Slot> slots = new ArrayList<>();
        for (Slot slot : destinationTrain.getStorageLocations()) {
            if (slot.getFinalStoringReservations() == null) {
                //Keine großen Slots vergeuden.
                if (slot.getLength() > 2 * job.getLoadUnit().getLength()) {
                    continue;
                }
                //Nur passende
                if (slot.getLength() < job.getLoadUnit().getLength()) {
                    continue;
                }
                slots.add(slot);
            }
        }

        // possible StoreOperations sortieren
        Rectangle2D rect = job.getOrigin().getGeneralOperatingArea().getBounds2D();
        Point3d originPoint = new Point3d(rect.getCenterX(), rect.getCenterY(), 0);
        EvalFunction_Slot_ClosestPosition evalFunction = new EvalFunction_Slot_ClosestPosition(originPoint);
        Collections.sort(slots, new SlotComparator(evalFunction));

        if (slots.isEmpty()) {
            for (Slot slot : destinationTrain.getStorageLocations()) {
                if (slot.getFinalStoringReservations() == null) {
                    slots.add(slot);
                }
            }
            throw new UnknownError(job.getLoadUnit().getLength() + " keine Reservierungsmoeglichkeit gefunden fuer " + job.getLoadUnit() + " auf " + job.getDestination());
        }

        Slot sto = slots.get(0);
        TimeSlot ts = new TimeSlot(job.getOrigin().getTemporalAvailability().getFromWhen(), sto.getTemporalAvailability().getUntilWhen());
        ReservationForLuOperation ro = new ReservationForLuOperation(sto, job.getLoadUnit(), ts);
        if (!sto.setFinalStoringReservation(ro)) {
            throw new UnknownError("FinalStoringReservation kann nicht gesetzt werden.");
        }
        job.getLoadUnit().setDestination(sto);
        return true;

    }
}
