/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.micro;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.AdminCraneOperation;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.AdminCraneOperation.StartEndPosition;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.CraneMicroTransportOperation;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.transshipment.model.structs.SpaceTimeElement;
import bijava.geometry.dim2.PolygonalCurve2d;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.FieldElement;
import math.LongValue;
import math.Tools;
import math.geometry.DynamicPolygonalRegion;

/**
 *
 * @author bode
 */
public class MicroscopicCraneRule implements ConveyanceSystemRule<Crane> {

    public static FieldElement horizon = new LongValue(5 * 60 * 1000);
    /**
     * Sicherheitspuffer fuer Abstand von Operationen (zeitlich)
     */
    private static FieldElement TIME_SECURITY_BUFFER = new LongValue(500L);

    private Crane c;
    private LinkedHashMap<TransportOperation, TransportBundle> bundles = new LinkedHashMap<TransportOperation, TransportBundle>();
    private List<TransportOperation> adminOps;

    public DynamicPolygonalRegion workingArea;
    public long tmin, tmax;

    public MicroscopicCraneRule(Crane c, InstanceHandler handler) {
        this.c = c;
        this.tmin = handler.getStartTimeForResource(c).longValue();
        this.tmax = c.getTemporalAvailability().getUntilWhen().longValue();
        //Anpassen auf letzte Poistion ggf.
        initStartEndPosOperations();
        this.workingArea = new DynamicPolygonalRegion(this.c.smin, this.c.smax, tmin, tmax);
    }

    /**
     * Plant eine TransportOperation fuer den Start- und fuer den Endpunkt ein.
     * Die Transportoperationen werden unter der Referenz {@link #startPos} und
     * {@link #endPos} gespeichert. Diese Operationen dienen nur zur Definition
     * einer Start- und Endposition zu Beginn und Ende der {@link
     * SimpleCrane#getTemporalAvailability() zeitlichen Verfuegbarkeit des Krans}.
     * Diese Operationen werden nicht nach aussen gegeben (auch nicht ueber
     * Methoden wie z.B. {@link #getAllScheduledOperations()}).<br>
     * Die Positionen werden ueber
     * {@link SimpleCrane#getCenterOfGeneralOperatingArea()} ermittelt.
     */
    private void initStartEndPosOperations() {

        adminOps = new ArrayList<>();

        Point2d center = c.getCenterOfGeneralOperatingArea();

        StartEndPosition pos = new StartEndPosition(new Point3d(center.x, center.y, c.getMax_zpos_crab()));
//        DynamicPolygonalRegion workingRep = manager.getWorkingAreaRepresentationWithSettingUpCorridor(pos, pos, null);

        AdminCraneOperation startPos = new AdminCraneOperation(c,
                pos,
                pos
        );
        AdminCraneOperation endPos = new AdminCraneOperation(c,
                pos,
                pos
        );

        adminOps.add(startPos);
        adminOps.add(endPos);
    }

    @Override
    public void initPositions(Schedule s, Collection<SpaceTimeElement> initialPositions) {
        adminOps = new ArrayList<>();
        if (initialPositions != null) {
            SpaceTimeElement posTime = initialPositions.iterator().next();

            StartEndPosition pos = new StartEndPosition(posTime.p);
//        DynamicPolygonalRegion workingRep = manager.getWorkingAreaRepresentationWithSettingUpCorridor(pos, pos, null);

            AdminCraneOperation startPos = new AdminCraneOperation(c,
                    pos,
                    pos
            );
            AdminCraneOperation endPos = new AdminCraneOperation(c,
                    pos,
                    pos
            );

            adminOps.add(startPos);
            adminOps.add(endPos);
            s.schedule(startPos, posTime.time);
        }
    }

    public void initStartEndPosOperations(LoadUnitStorage storage) {

        adminOps = new ArrayList<>();

        Point2d center = storage.getCenterOfGeneralOperatingArea();

        StartEndPosition pos = new StartEndPosition(new Point3d(center.x, center.y, c.getMax_zpos_crab()));
//        DynamicPolygonalRegion workingRep = manager.getWorkingAreaRepresentationWithSettingUpCorridor(pos, pos, null);

        AdminCraneOperation startPos = new AdminCraneOperation(c,
                pos,
                pos
        );
        AdminCraneOperation endPos = new AdminCraneOperation(c,
                pos,
                pos
        );

        adminOps.add(startPos);
        adminOps.add(endPos);
    }

    @Override
    public CraneMicroTransportOperation getDetailedOperation(RoutingTransportOperation o, LoadUnitStorage origin, LoadUnitStorage destination) {
        DynamicPolygonalRegion workingAreaRepresentation = CraneMotionCalculator.getWorkingAreaRepresentation(c, origin, destination, o.getLoadUnit());
        DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor = CraneMotionCalculator.getWorkingAreaRepresentationWithSettingUpCorridor(c, workingAreaRepresentation);
        FieldElement transportationTime = new LongValue(workingAreaRepresentation.getTmax() - workingAreaRepresentation.getTmin());
        CraneMicroTransportOperation craneMicroTransportOperation = new CraneMicroTransportOperation(o, origin, destination, workingAreaRepresentationWithSettingUpCorridor, workingAreaRepresentation);
        craneMicroTransportOperation.setDuration(transportationTime);
        return craneMicroTransportOperation;

    }

    @Override
    public FieldElement getNextPossibleBundleStartTime(Schedule s, MultiScaleTransportOperation o, TimeSlot interval) {
        CraneMicroTransportOperation top = (CraneMicroTransportOperation) o;

        TimeSlotList freeSlots = getFreeSlots(s, top, interval);
        for (TimeSlot freeSlot : freeSlots) {
            /**
             * Finde Zeitfenster, in dem ein Transport möglich ist unter
             * Berücksichtigung von Rüstfahrten
             */
            TimeSlot possibleTimeWindow = getTimeWindowForSettingUpAndTransport(s,
                    freeSlot,
                    top.getOrigin(),
                    top.getDestination(),
                    top.getLoadUnit()
            );

            // Wenn die TOP nicht reinpassen wuerde, weiter
            if (possibleTimeWindow == null) {
                continue;
            }

            // zeitlichen Ausschnitt der aktuellen workingArea bestimmen
            DynamicPolygonalRegion combinedBCs = getWorkingArea().getSection(possibleTimeWindow);

            // Polygonale Darstellung fuer Transport (beginnt zeitlich bei 0, da hier noch verschieblich)
            DynamicPolygonalRegion transportRegion = top.getWorkingAreaRepresentationWithSettingUpCorridor();

//            DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor = CraneMotionCalculator.getWorkingAreaRepresentationWithSettingUpCorridor(c, transportRegion);

            FieldElement transportDuration = new LongValue(CraneMotionCalculator.getTransportationTime(c, top.getOrigin(), top.getDestination(), top.getLoadUnit()));
//new LongValue(transportRegion.getTmax()-transportRegion.getTmin());//
            // Bereiche finden, in die die Operation einplanbar waere:
            TimeSlot[] possibleSlots = combinedBCs.getTimeSlots(transportRegion);

            // Falls der TimeSlot keine freien Plaetze laut geometrischen RBs hat, weiter
            if (possibleSlots.length == 0) {
                continue;
            }

            // possible Slots in Operations ueberfuehren
            // TODO: wie SettingUpOperations codieren?
            for (TimeSlot ts : possibleSlots) {
                // Zeitlich verschneiden mit angefragtem Slot und pruefen, ob die Op
                // zeitlich in den zeitlichen Verschnitt passt:
                // +-5 ist eine Art puffer, um uebrschneidungsprobleme zu loesen (nur, wenn es sich nicht um unendlich handelt!)

                FieldElement topPossibleStart = ts.getFromWhen().add(TIME_SECURITY_BUFFER);
                if (interval.getFromWhen().isGreaterThan(topPossibleStart)) {
                    topPossibleStart = interval.getFromWhen();
                }
                FieldElement topPossibleEnd = ts.getUntilWhen().isLowerThan(interval.getUntilWhen()) ? ts.getUntilWhen() : interval.getUntilWhen();

                if (topPossibleStart.add(transportDuration).isLowerThan(topPossibleEnd) || topPossibleStart.add(transportDuration).equals(topPossibleEnd)) {
                    TransportBundle bundle = new TransportBundle(c, top, topPossibleStart);
                    bundles.put(top, bundle);
                    return topPossibleStart;
                }

            }
        }

        return null;

    }

    @Override
    public Crane getResource() {
        return c;
    }

    @Override
    public FieldElement getTransportationTime(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit lu) {
        long transportationTime = CraneMotionCalculator.getTransportationTime(c, origin, destination, lu);
        return new LongValue(transportationTime);
    }

    @Override
    public IdleSettingUpOperation findIdleSettingUpOperation(Operation predessor, MultiScaleTransportOperation transOp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TransportBundle getBundle(Schedule s, MultiScaleTransportOperation top, FieldElement startTimeTransport) {
        return bundles.get(top);
    }

    @Override
    public boolean canSchedule(Schedule s, Operation o, FieldElement startBundle) {
        CraneMicroTransportOperation top = (CraneMicroTransportOperation) o;
        TimeSlot interval = new TimeSlot(startBundle, startBundle.add(horizon));
        TimeSlotList freeSlots = getFreeSlots(s, top, interval);
        if (freeSlots.isEmpty() || freeSlots.first() == null || !freeSlots.first().contains(startBundle.longValue())) {
            return false;
        }

         
        /**
         * Finde Zeitfenster, in dem ein Transport möglich ist unter
         * Berücksichtigung von Rüstfahrten
         */
        TimeSlot possibleTimeWindow = getTimeWindowForSettingUpAndTransport(s,
                freeSlots.first(),
                top.getOrigin(),
                top.getDestination(),
                top.getLoadUnit()
        );

        // Wenn die TOP nicht reinpassen wuerde, weiter
        if (possibleTimeWindow == null) {
            return false;
        }

        // zeitlichen Ausschnitt der aktuellen workingArea bestimmen
        DynamicPolygonalRegion combinedBCs = getWorkingArea().getSection(possibleTimeWindow);

        // Polygonale Darstellung fuer Transport (beginnt zeitlich bei 0, da hier noch verschieblich)
        DynamicPolygonalRegion transportRegion = top.getWorkingAreaRepresentationWithSettingUpCorridor();

        FieldElement transportDuration = new LongValue(CraneMotionCalculator.getTransportationTime(c, top.getOrigin(), top.getDestination(), top.getLoadUnit()));
        // Bereiche finden, in die die Operation einplanbar waere:
        TimeSlot[] possibleSlots = combinedBCs.getTimeSlots(transportRegion);

        // Falls der TimeSlot keine freien Plaetze laut geometrischen RBs hat, weiter
        if (possibleSlots.length == 0) {
            return false;
        }

        // possible Slots in Operations ueberfuehren
        // TODO: wie SettingUpOperations codieren?
        for (TimeSlot ts : possibleSlots) {
            // Zeitlich verschneiden mit angefragtem Slot und pruefen, ob die Op
            // zeitlich in den zeitlichen Verschnitt passt:
            // +-5 ist eine Art puffer, um uebrschneidungsprobleme zu loesen (nur, wenn es sich nicht um unendlich handelt!)

            FieldElement topPossibleStart = ts.getFromWhen().add(TIME_SECURITY_BUFFER);
            if (startBundle.isGreaterThan(topPossibleStart)) {
                topPossibleStart = startBundle;
            }
            FieldElement topPossibleEnd = ts.getUntilWhen();
            if (!TransshipmentParameter.allowInsert) {
                if (s.getScheduleEventTimes(c).higher(topPossibleStart) != null) {
                    return false;
                }
            }

            if (topPossibleStart.add(transportDuration).isLowerThan(topPossibleEnd) || topPossibleStart.add(transportDuration).equals(topPossibleEnd)) {
                TransportBundle bundle = new TransportBundle(c, top, topPossibleStart);
                bundles.put(top, bundle);
                return true;
            }
        }
        return false;
    }

    @Override
    public void unSchedule(Operation o, Schedule s) {
        MicroCraneRunwaySharedManager manager = (MicroCraneRunwaySharedManager) s.getHandler().getSharedManager(c.getCraneRunway());
        manager.unconstrainNeighbours(s, c, o);
    }

    /**
     * Liefert die Dauer, die fuer die Longitudinalbewegung (reine Kranfahrt) um
     * s Meter benoetigt wird, ausgehend von einer Startgeschwindigkeit v_0 und
     * amax_crane.
     *
     * @param v_0 Startgeschwindigkeit in [m/sec]
     * @param s zurueckzulegender Weg in [m]‚
     * @return benoetigte Zeit in [sec]
     */
    public double getLongitudinalMovingTime(double v_0, double s) {
        return CraneMotionCalculator.getMovingTimeRegardingAcc(v_0, s, c.amax_crane);
    }

    /**
     * Zurueckgegeben wird eine Zeitspanne als TimeSlot, in der der Transport
     * von A nach B durchgefuehrt werden koennte
     *
     * @param slot
     * @param posFrom
     * @param posTo
     * @return
     */
    protected TimeSlot getTimeWindowForSettingUpAndTransport(Schedule schedule, TimeSlot slot, LoadUnitStorage from, LoadUnitStorage to, LoadUnit lu) {
        CraneMicroTransportOperation topA = null;
        CraneMicroTransportOperation topB = null;

        /**
         * Bestimme vorangegangen TransportOperation
         */
        TreeMultimap<FieldElement, Operation> timeToOperationMap = schedule.getTimeToOperationMap(c);

        Collection<Operation> lower = null;
        Map.Entry<FieldElement, Collection<Operation>> lowerEntry = timeToOperationMap.asMap().lowerEntry(slot.getFromWhen());
        if (lowerEntry != null) {
            lower = lowerEntry.getValue();
        }

        Collection<Operation> higher = timeToOperationMap.asMap().get(slot.getUntilWhen());
        if (higher == null) {
            Map.Entry<FieldElement, Collection<Operation>> higherEntry = timeToOperationMap.asMap().higherEntry(slot.getUntilWhen());
            if (higherEntry != null) {
                higher = higherEntry.getValue();
            }
        }

        if (lower != null) {
            Collection<Operation> operationsBefore = lower;
            if (operationsBefore.size() != 1) {
                for (Operation operation : operationsBefore) {
                    if (operation instanceof TransportOperation) {
                        topA = (CraneMicroTransportOperation) operation;
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

                topA = (CraneMicroTransportOperation) qop;
            }
        }
        if (higher != null) {
            Collection<Operation> operationsAfter = higher;
            if (operationsAfter.size() != 1) {
                for (Operation operation : operationsAfter) {
                    if (operation instanceof TransportOperation) {
                        topB = (CraneMicroTransportOperation) operation;
                        break;
                    }
                }
            } else {
                Operation qop = operationsAfter.iterator().next();

                /**
                 * Nur Transportoperationen können untersucht werden.
                 */
                if (qop instanceof IdleSettingUpOperation) {
                    return null;
                }

                topB = (CraneMicroTransportOperation) qop;
            }
        }

        // Ende von topA (falls topA nicht existiert Anfang des angefragten TimeSlots, bzw. Verfuegbarkeit)
        FieldElement t_end_A = Tools.max(c.getTemporalAvailability().getFromWhen(), slot.getFromWhen());
        // Anfang von topB (falls topB nicht existiert Ende des angefragten TimeSlots, bzw. Verfuegbarkeit)
        FieldElement t_start_B = Tools.min(c.getTemporalAvailability().getUntilWhen(), slot.getUntilWhen());

        // Ruestzeit fuer einzuplanende Operation P (falls keine Vorgaengeroperation
        // existiert, muss auch nicht geruestet werden)
        FieldElement t_ruest_AP = new LongValue(0L);

        // Ruestzeit fuer Fokge-Operation B (falls keine Nachfolgeroperation
        // existiert, muss auch nicht geruestet werden)
        FieldElement t_ruest_PB = new LongValue(0L);

        if (topA != null) {
            // Fahrstrategie durch Kran (getSettingUpTime) vorgegeben
            t_ruest_AP = new LongValue(CraneMotionCalculator.getSettingUpTime(c, topA.getDestination(), from));
            t_end_A = schedule.get(topA).add(topA.getDuration());
        }
        if (topB != null) {
            // Fahrstrategie durch Kran (getSettingUpTime) vorgegeben
            t_ruest_PB = new LongValue(CraneMotionCalculator.getSettingUpTime(c, to, topB.getOrigin()));
            t_start_B = schedule.get(topB);
        }

        // Transportdauer des Transports
        FieldElement t_P_duration = new LongValue(CraneMotionCalculator.getTransportationTime(c, from, to, lu));

        // Fruehstmoeglicher Startzeitpunkt von P, bedingt durch Ruestung
        FieldElement t_earliestStart_P = t_end_A.add(t_ruest_AP); // +1 bzw +2?

        // Spaetmoeglichster Endzeitpunkt von P, bedingt durch Ruestung
        FieldElement t_latestEnd_P = t_start_B.sub(t_ruest_PB); // -1 bzw. -2?

        // Falls Verbleibender slot zu klein, return null
        if (t_latestEnd_P.sub(t_earliestStart_P).isLowerThan(t_P_duration)) {
            return null;
        } else {
            return new TimeSlot(t_earliestStart_P, t_latestEnd_P);
        }

    }

    public TransportOperation getTransportOperation(Schedule s, FieldElement timeStamp) {
        TransportOperation result = null;
        Map.Entry<FieldElement, Collection<Operation>> lowerEntry = s.getTimeToOperationMap(c).asMap().lowerEntry(timeStamp);

        Collection<Operation> operationsBefore = new LinkedHashSet<>();

        if (lowerEntry != null) {
            operationsBefore = lowerEntry.getValue();
            if (operationsBefore.size() != 1) {
                for (Operation operation : operationsBefore) {
                    if (operation instanceof TransportOperation) {
                        result = (TransportOperation) operation;
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

                result = (TransportOperation) qop;
            }
        }
        return result;
    }

    @Override
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval) {

        FieldElement demand = o.getDemand(c);
        FieldElement duration = o.getDuration();
        long from = interval.getFromWhen().longValue();
        long to = interval.getUntilWhen().longValue();
        long mindt = duration.longValue();

        if (mindt < 1) {
            throw new IllegalArgumentException("mindt should be greater than 0 (here: " + mindt + ")");
        }

        TimeSlotList freeSlots = new TimeSlotList();
        Iterator<Operation> it = s.getOperationsForResource(c).iterator();

        if (from >= to) {
            throw new IllegalArgumentException("from must be smaller than to. from: " + from + "   to: " + to);
        }

        // keine Verfuegbarkeit im gefragten Bereich
        if (from > tmax || to < tmin) {
            return null;
        }

        // Anpassen der Bereiche
        from = Math.max(tmin, from);
        to = Math.min(tmax, to);

        // nichts reserviert, also den maximalen TimeSlot in [from,to]
        if (!it.hasNext()) {
            freeSlots.add(TimeSlot.create(from, to));
            return freeSlots;
        }

        // TODO: Sonderfall abfangen: es gibt nur eine eingeplante Operation
        Operation o1 = it.next();
        long o1Start = s.get(o1).longValue();

        // Alle Operations groesser als gefragter Bereich
        if (o1Start > to) {
            freeSlots.add(TimeSlot.create(from, to));
            return freeSlots;
        }

        // nur eine Operation:
        if (!it.hasNext()) {
//            output.CTSO_Logger.println("Nur eine Operation...");
            // dann kann es maximal 2 Free Timeslots geben (davor und danach)
            if (o1Start > from + mindt) {
                freeSlots.add(TimeSlot.create(from, Math.min(to, o1Start)));
            }
            long o1End = o1Start + o1.getDuration().longValue();
            if (o1End + mindt < to) {
                freeSlots.add(TimeSlot.create(Math.max(from, o1End), to));
            }
        }

        // mehr als 1 Operation
        if (it.hasNext()) {
            Operation o2 = it.next();

            // TimeSlot vor erster Operation:
            if (o1Start > from + mindt) {
                freeSlots.add(TimeSlot.create(from, o1Start)); //Min (to) wird schon oben abgefangen
            }
            while (true) {
                // Grenzen des moeglichen TimeSlots: [tsStart, tsEnd]
                long tsStart = s.get(o1).longValue() + o1.getDuration().longValue();
                long tsEnd = s.get(o2).longValue();

                // vorige TimeSlots ueberspringen
                if (tsEnd < from) {
                    if (!it.hasNext()) {
                        if (s.get(o2).add(o2.getDuration()).longValue() + mindt < to) {
                            freeSlots.add(TimeSlot.create(Math.max(from, s.get(o2).add(o2.getDuration()).longValue()), to));
                        }

                        break;
                    }
                    // Referenzen umhaengen
                    o1 = o2;
                    o2 = it.next();
                    continue;
                }
                // spaetere sind auch nicht interessant
                if (tsStart > to) {
                    break;
                }
                // Hier angekommen bedeutet eine zeitliche Ueberschneidung
                long potStart = Math.max(tsStart, from);
                long potEnd = Math.min(tsEnd, to);
                // Falls TimeSlotgroesse > mindt, dann handelt es sich um einen gueltigen TS
                if (potStart + mindt <= potEnd) {
                    freeSlots.add(TimeSlot.create(potStart, potEnd));
                }

                // Sonderbehandlung fuer letztes Element, das noch innerhalb des gefragten
                // Bereichs liegt. Faengt gleichzeitig den Fall ab, wenn alle
                // Operations kleiner als der gefragte Bereich sind
                if (!it.hasNext()) {
                    long o2End = tsEnd + o2.getDuration().longValue();
                    if (o2End + mindt <= to) {
                        freeSlots.add(TimeSlot.create(Math.max(from, o2End), to));
                    }
                    break;
                }

                // Referenzen umhaengen
                o1 = o2;
                o2 = it.next();
            }
        }
        return freeSlots;
    }

    public void schedule(Operation o, Schedule s, FieldElement start) {
        if (o instanceof CraneMicroTransportOperation) {
            CraneMicroTransportOperation top = (CraneMicroTransportOperation) o;
            DynamicPolygonalRegion topBound = top.getWorkingAreaRepresentation();
            /**
             * Verschiebe die Operation zeitlich
             */
            topBound.moveInTime(start.longValue());

            try {
                MicroCraneRunwaySharedManager manager = (MicroCraneRunwaySharedManager) s.getHandler().getSharedManager(c.getCraneRunway());
                manager.constrainNeighbours(s, this.c, topBound);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public DynamicPolygonalRegion getWorkingArea() {
        return workingArea;
    }

    public PolygonalCurve2d[] getSettingUpBCsForDeletedTransport(Schedule schedule, TimeSlot slot) {

        // Erzeuge temporaere Region, die dem aktuellen Arbeitsbereich
        // entspricht, eingeschraenkt durch Ruestoperationen
        // dazu vorige und folgende TransportOperation holen
        TransportOperation topBefore = getTransportOperation(schedule, slot.getFromWhen());
        TransportOperation topAfter = getTransportOperation(schedule, slot.getUntilWhen());

        // Zeit, die benoetigt wird, um v=vmax von v=0 zu erreichen
        double t_acc = CraneMotionCalculator.getLongitudinalAccelarationDuration(c, 0., this.c.getVmax_crane());

        // Weg, der zurueckgelegt wird waehrend Beschleunigungsphase
        double s_acc = CraneMotionCalculator.getLongitudinalDistance(c, 0., this.c.getVmax_crane());

        // Zeitdifferenz bis zum Schnittpunkt
        double t_p1 = t_acc - s_acc / this.c.getVmax_crane();

        // Die geometrische Form in s,t von der VorgaengerTOP, falls existent
        DynamicPolygonalRegion wABefore = null;
        PolygonalCurve2d rightBoundTOPBefore = null;
        PolygonalCurve2d leftBoundTOPBefore = null;

        // Die geometrische Form in s,t von der NachfolgerTOP, falls existent
        DynamicPolygonalRegion wAAfter = null;
        PolygonalCurve2d rightBoundTOPAfter = null;
        PolygonalCurve2d leftBoundTOPAfter = null;

        // tmin, tmax gibt den Bereich zwischen topBefore_Ende und topAfter_Start an
        double tmin = workingArea.getTminInSeconds();
        double tmax = workingArea.getTmaxInSeconds();

        if (topBefore != null) {
            wABefore = CraneMotionCalculator.getWorkingAreaRepresentation(topBefore, schedule);
            rightBoundTOPBefore = wABefore.getRightBound();
            leftBoundTOPBefore = wABefore.getLeftBound();
            tmin = wABefore.getTmaxInSeconds();
        }

        if (topAfter != null) {
            wAAfter = CraneMotionCalculator.getWorkingAreaRepresentation(topAfter, schedule);
            rightBoundTOPAfter = wAAfter.getRightBound();
            leftBoundTOPAfter = wAAfter.getLeftBound();
            tmax = wAAfter.getTminInSeconds();
        }

        // Die durch die Vorgaengeroperation entstehende Randbedingung fuer den rechten Kran
        PolygonalCurve2d bc_before_r = null;
        bijava.geometry.dim2.Point2d[] ptsFor_bc_before_r = null;    // Punkte dazu
        // Die durch die Vorgaengeroperation entstehende Randbedingung fuer den linken Kran
        PolygonalCurve2d bc_before_l = null;
        bijava.geometry.dim2.Point2d[] ptsFor_bc_before_l = null;    // Punkte dazu

        if (topBefore != null) {

            // Ausfahrweg nach der Operation (im s,t Diagramm nach oben)
            // Beschraenkung fuer rechten Nachbarn:
            bijava.geometry.dim2.Point2d p0_r = (bijava.geometry.dim2.Point2d) rightBoundTOPBefore.getPointAt(rightBoundTOPBefore.size() - 1).clone();

            bijava.geometry.dim2.Point2d p1_r = new bijava.geometry.dim2.Point2d(
                    rightBoundTOPBefore.getPointAt(rightBoundTOPBefore.size() - 1).x,
                    rightBoundTOPBefore.getPointAt(rightBoundTOPBefore.size() - 1).y + t_p1
            );

            bijava.geometry.dim2.Point2d p2_r = new bijava.geometry.dim2.Point2d(
                    c.getSmin(),
                    p1_r.y + (p1_r.x - c.getSmin()) / c.vmax_crane
            );

            // Die bc_before_r ergeben sich entweder aus p1,p2,p3, wenn tmax noch nicht erreicht...
            if (p2_r.y < tmax) {
                bijava.geometry.dim2.Point2d p3_r = new bijava.geometry.dim2.Point2d(
                        c.getSmin(),
                        tmax
                );
                ptsFor_bc_before_r = new bijava.geometry.dim2.Point2d[]{
                    p0_r,
                    p1_r,
                    p2_r,
                    p3_r
                };
            } // oder aus dem zeitlichen Ausschnitt von p1,p2 im Bereich tmin,tmax
            else {
                ptsFor_bc_before_r = DynamicPolygonalRegion.getPointsInRange(new bijava.geometry.dim2.Point2d[]{p0_r, p1_r, p2_r}, tmin, tmax);
            }

            // Beschraenkung fuer linken Kran
            bijava.geometry.dim2.Point2d p0_l = (bijava.geometry.dim2.Point2d) leftBoundTOPBefore.getPointAt(leftBoundTOPBefore.size() - 1).clone();

            bijava.geometry.dim2.Point2d p1_l = new bijava.geometry.dim2.Point2d(
                    leftBoundTOPBefore.getPointAt(leftBoundTOPBefore.size() - 1).x,
                    leftBoundTOPBefore.getPointAt(leftBoundTOPBefore.size() - 1).y + t_p1
            );

            bijava.geometry.dim2.Point2d p2_l = new bijava.geometry.dim2.Point2d(
                    c.getSmax(),
                    p1_l.y + (c.getSmax() - p1_l.x) / c.vmax_crane
            );

            // Die bc_before_l ergeben sich entweder aus p1_l, p2_l, p3_l, wenn tmax noch nicht erreicht...
            if (p2_l.y < tmax) {
                bijava.geometry.dim2.Point2d p3_l = new bijava.geometry.dim2.Point2d(
                        c.getSmax(),
                        tmax
                );
                ptsFor_bc_before_l = new bijava.geometry.dim2.Point2d[]{
                    p0_l,
                    p1_l,
                    p2_l,
                    p3_l
                };
            } // oder aus dem zeitlichen Ausschnitt von p1_l, p2_l im bereich tmin, tmax
            else {
                ptsFor_bc_before_l = DynamicPolygonalRegion.getPointsInRange(new bijava.geometry.dim2.Point2d[]{p0_l, p1_l, p2_l}, tmin, tmax);
            }
        } else {
            ptsFor_bc_before_r = new bijava.geometry.dim2.Point2d[]{
                new bijava.geometry.dim2.Point2d(c.getSmin(), tmin),
                new bijava.geometry.dim2.Point2d(c.getSmin(), tmax)
            };
            ptsFor_bc_before_l = new bijava.geometry.dim2.Point2d[]{
                new bijava.geometry.dim2.Point2d(c.getSmax(), tmin),
                new bijava.geometry.dim2.Point2d(c.getSmax(), tmax)
            };
        }

        bc_before_r = new PolygonalCurve2d(ptsFor_bc_before_r);
        bc_before_l = new PolygonalCurve2d(ptsFor_bc_before_l);

        // Die durch die Nachfolgeroperation entstehende Randbedingung fuer den rechten Kran
        PolygonalCurve2d bc_after_r = null;
        bijava.geometry.dim2.Point2d[] ptsFor_bc_after_r = null;    // Punkte dazu
        // Die durch die Nachfolgeroperation entstehende Randbedingung fuer den linken Kran
        PolygonalCurve2d bc_after_l = null;
        bijava.geometry.dim2.Point2d[] ptsFor_bc_after_l = null;    // Punkte dazu

        if (topAfter != null) {

            // Einfahrweg der naechsten Operation (im s,t Diagramm nach oben)
            // Beschraenkung fuer rechten Nachbarn:
            bijava.geometry.dim2.Point2d q0_r = (bijava.geometry.dim2.Point2d) rightBoundTOPAfter.getPointAt(0).clone();

            bijava.geometry.dim2.Point2d q1_r = new bijava.geometry.dim2.Point2d(
                    rightBoundTOPAfter.getPointAt(0).x,
                    rightBoundTOPAfter.getPointAt(0).y - t_p1
            );

            bijava.geometry.dim2.Point2d q2_r = new bijava.geometry.dim2.Point2d(
                    c.getSmin(),
                    q1_r.y - (q1_r.x - c.getSmin()) / c.vmax_crane
            );

            // Die bc_after_r ergeben sich entweder aus q3_r,q2_r,q1_r, wenn tmin mit q2_r noch nicht erreicht...
            if (q2_r.y > tmin) {
                bijava.geometry.dim2.Point2d q3_r = new bijava.geometry.dim2.Point2d(
                        c.getSmin(),
                        tmin
                );
                ptsFor_bc_after_r = new bijava.geometry.dim2.Point2d[]{
                    q3_r,
                    q2_r,
                    q1_r,
                    q0_r
                };
            } // oder aus dem Teilbereich (tmin,tmax) von q2_r,q1_r
            else {
                try {
                    ptsFor_bc_after_r = DynamicPolygonalRegion.getPointsInRange(new bijava.geometry.dim2.Point2d[]{q2_r, q1_r, q0_r}, tmin, tmax);
                } catch (IllegalArgumentException e) {
                    String message = ("topBefore: " + topBefore);
                    message += ("\ntopAfter: " + topAfter);
                    e.printStackTrace();
                    throw new IllegalArgumentException(message);
                }
            }

            // Beschraenkung fuer linken Nachbarn:
            bijava.geometry.dim2.Point2d q0_l = (bijava.geometry.dim2.Point2d) leftBoundTOPAfter.getPointAt(0).clone();

            bijava.geometry.dim2.Point2d q1_l = new bijava.geometry.dim2.Point2d(
                    leftBoundTOPAfter.getPointAt(0).x,
                    leftBoundTOPAfter.getPointAt(0).y - t_p1
            );

            bijava.geometry.dim2.Point2d q2_l = new bijava.geometry.dim2.Point2d(
                    this.c.getSmax(),
                    q1_l.y - (this.c.getSmax() - q1_l.x) / this.c.vmax_crane
            );

            // Die bc_after_l ergeben sich entweder aus q3_l,q2_l,q1_l, wenn tmin mit q2_l noch nicht erreicht...
            if (q2_l.y > tmin) {
                bijava.geometry.dim2.Point2d q3_l = new bijava.geometry.dim2.Point2d(
                        this.c.getSmax(),
                        tmin
                );
                ptsFor_bc_after_l = new bijava.geometry.dim2.Point2d[]{
                    q3_l,
                    q2_l,
                    q1_l,
                    q0_l
                };
            } // oder aus dem zeitlichen Ausschnitt (tmin,tmax) von q2_l, q1_l
            else {
                ptsFor_bc_after_l = DynamicPolygonalRegion.getPointsInRange(new bijava.geometry.dim2.Point2d[]{q2_l, q1_l, q0_l}, tmin, tmax);
            }
        } else {
            ptsFor_bc_after_r = new bijava.geometry.dim2.Point2d[]{
                new bijava.geometry.dim2.Point2d(this.c.getSmin(), tmin),
                new bijava.geometry.dim2.Point2d(this.c.getSmin(), tmax)
            };
            ptsFor_bc_after_l = new bijava.geometry.dim2.Point2d[]{
                new bijava.geometry.dim2.Point2d(this.c.getSmax(), tmin),
                new bijava.geometry.dim2.Point2d(this.c.getSmax(), tmax)
            };
        }

        bc_after_r = new PolygonalCurve2d(ptsFor_bc_after_r);
        bc_after_l = new PolygonalCurve2d(ptsFor_bc_after_l);

        // Die Randbedinungen fuer den rechten Nachbarn ergeben sich aus dem Maximum
        // von bc_before_r und bc_after_r
        ArrayList<bijava.geometry.dim2.Point2d> leftBound = DynamicPolygonalRegion.getSMaxPts(bc_before_r, bc_after_r);

        // Die Randbedingungen fuer den linken Nachbarn ergeben sich aus dem Minimum
        // von bc_before_l und bc_after_l
        ArrayList<bijava.geometry.dim2.Point2d> rightBound = DynamicPolygonalRegion.getSMinPts(bc_before_l, bc_after_l);

        // neue Randbedingungen erzeugen durch verschneiden:
        return new PolygonalCurve2d[]{
            new PolygonalCurve2d(rightBound.toArray(new bijava.geometry.dim2.Point2d[]{})),
            new PolygonalCurve2d(leftBound.toArray(new bijava.geometry.dim2.Point2d[]{}))
        };

    }

    public Crane getC() {
        return c;
    }

    public void setTimeSlot(TimeSlot t) {
        this.tmin = t.getFromWhen().longValue();
        this.tmax = t.getUntilWhen().longValue();
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
