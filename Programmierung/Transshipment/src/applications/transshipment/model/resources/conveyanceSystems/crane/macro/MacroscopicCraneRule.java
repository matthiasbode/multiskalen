/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.macro;

import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyUtilizationManager;
import applications.transshipment.model.resources.conveyanceSystems.SingleTransportBundleHandler;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.utilization.StepFunctionBasedUtilizationManager;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.setup.DefaultIdleSettingUpOperation;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.AdminCraneOperation;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.utilization.UtilizationManager;
import applications.transshipment.model.structs.SpaceTimeElement;
import bijava.math.function.ScalarFunction1d;
import fuzzy.number.discrete.FuzzyFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.FieldElement;
import math.LongValue;

/**
 * Dies Klasse beschreibt die Einplanvorschriften für einen Kran auf einer
 * Makroskopischen Ebene. Unschärfe wird hier aber zunächst nicht
 * berücksichtigt.
 *
 * @author bode
 */
public class MacroscopicCraneRule implements ConveyanceSystemRule<Crane>, ScalarFunctionBasedRule<Crane> {

    public boolean fuzzy;
    private final Crane c;
    private final SingleTransportBundleHandler bundleHandler;
    private final UtilizationManager manager;
//    private final LatticeManager<CraneRunway> latticeManger;
    private List<TransportOperation> adminOps;

    public MacroscopicCraneRule(Crane c, InstanceHandler handler, boolean fuzzy) {
        this.fuzzy = fuzzy;
        if (fuzzy) {
            this.manager = new FuzzyUtilizationManager(c, 1.0, handler.getStartTimeForResource(c));
        } else {
            this.manager = new StepFunctionBasedUtilizationManager(c, 1.0, handler.getStartTimeForResource(c));
        }
        this.c = c;
        this.bundleHandler = new SingleTransportBundleHandler(c, this);
        initStartEndPosOperations();
//        SharedResourceManager sharedManager = handler.getSharedManager(c.getCraneRunway());
//        this.latticeManger = (LatticeManager<CraneRunway>) sharedManager;
    }

    private void initStartEndPosOperations() {

        adminOps = new ArrayList<>();

        Point2d center = c.getCenterOfGeneralOperatingArea();

        AdminCraneOperation.StartEndPosition pos = new AdminCraneOperation.StartEndPosition(new Point3d(center.x, center.y, c.getMax_zpos_crab()));
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
    public boolean canSchedule(Schedule s, Operation o, FieldElement startBundle) {
        MultiScaleTransportOperation top = (MultiScaleTransportOperation) o;
        if (startBundle == null) {
            throw new IllegalArgumentException("Keine Startzeit definiert!");
        }
        /**
         * Bestimme für die Operation das TransportBundle
         */
        return bundleHandler.isBundleScheduable(s, startBundle, top);
    }

    @Override
    public FieldElement getNextPossibleBundleStartTime(Schedule s, MultiScaleTransportOperation o, TimeSlot interval) {
        MultiScaleTransportOperation top = (MultiScaleTransportOperation) o;

        /**
         * sollte Zeitfenster null sein, betrachte alle Zeitfenster, bis eines
         * gefunden wurde, in das eingeplant werden kann. Möglichst früh
         * einplanen!
         */
        TimeSlotList freeSlots = manager.getFreeSlotsInternal(s, o.getDemand(c), o.getDuration(), interval);
        for (TimeSlot freeSlot : freeSlots) {
            FieldElement startTime = bundleHandler.getBundleStartTime(s, top, freeSlot);
            if (startTime != null) {
                return startTime;
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
        if (!fuzzy) {
            return new LongValue(CraneMotionCalculator.getTransportationTime(c, origin, destination, lu));
        } else {
            return CraneFuzzyCalculatorExtended.getFuzzyTransportationTime(c, origin, origin, lu);
        }
    }

    @Override
    public IdleSettingUpOperation findIdleSettingUpOperation(Operation predessor, MultiScaleTransportOperation transOp) {

        /**
         * Bei Kranen Annahme: es kann nur einen Vorgänger geben, dieser muss
         * eine TransportOperation sein.
         */
        if (predessor == null) {
            DefaultIdleSettingUpOperation defaultIdleSettingUpOperation = new DefaultIdleSettingUpOperation(c, transOp.getOrigin(), transOp.getOrigin());
            defaultIdleSettingUpOperation.setDuration(new LongValue(1));
            if (fuzzy) {
                defaultIdleSettingUpOperation.setDuration(FuzzyFactory.createCrispValue(1));
            }
            return defaultIdleSettingUpOperation;
        }

        Operation preOp = predessor;
        if (preOp instanceof TransportOperation) {
            TransportOperation preTop = (TransportOperation) preOp;
            LoadUnitStorage startRuest = preTop.getDestination();
            LoadUnitStorage endRuest = transOp.getOrigin();

            DefaultIdleSettingUpOperation settingUp = new DefaultIdleSettingUpOperation(c, startRuest, endRuest);
            FieldElement transportationTime = new LongValue(CraneMotionCalculator.getSettingUpTime(c, startRuest, endRuest));
            /**
             * *
             * TODO: muss angepasst werden!
             */
            if (fuzzy) {
                transportationTime = FuzzyFactory.createCrispValue(transportationTime.longValue());
            }
            settingUp.setDuration(transportationTime);
            return settingUp;
        } else {
            throw new UnknownError("Vorgängeroperation keine Transportopertion. Darf nicht vorkommen: " + preOp);
        }

    }

    @Override
    public TransportBundle getBundle(Schedule s, MultiScaleTransportOperation top, FieldElement startTimeTransport) {
        /**
         * Anfrage wird an den BundleHandler weitergeleitet.
         */
        return this.bundleHandler.getBundle(top);
    }

    @Override
    public MultiScaleTransportOperation getDetailedOperation(RoutingTransportOperation o, LoadUnitStorage origin, LoadUnitStorage destination) {
        MultiScaleTransportOperation operation = new MultiScaleTransportOperation(o, origin, destination);
        FieldElement transportationTime = getTransportationTime(operation.getOrigin(), operation.getDestination(), operation.getLoadUnit());
        operation.setDuration(transportationTime);

        /**
         * Bestimmung des Zusätzlichen Demands für die Kranbahn
         */
//        SubOperations subDemand = new SubOperations(operation);
//        /**
//         * Zellen bestimmen
//         */
//        ArrayList<CellResource2D> cells = latticeManger.getCells(operation.getOrigin().getCenterOfGeneralOperatingArea(), operation.getDestination().getCenterOfGeneralOperatingArea());
//        FieldElement currentOffset = new LongValue(0);
//        for (CellResource2D cellResource2D : cells) {
//            FieldElement timeForCell = getMovingTime(cellResource2D);
//            subDemand.put(new CellOperation(cellResource2D, timeForCell), currentOffset.clone());
//            currentOffset = currentOffset.add(timeForCell);
//        }
//        operation.setSubOperations(subDemand);
        return operation;
    }

    @Override
    public void schedule(Operation o, Schedule s, FieldElement start) {
        manager.scheduleInternal(o, s, start);
    }

    @Override
    public void unSchedule(Operation o, Schedule s) {
        manager.unScheduleInternal(o, s);
    }

    @Override
    public TimeSlotList getFreeSlots(Schedule s, Operation o, TimeSlot interval) {
        return manager.getFreeSlotsInternal(s, o.getDemand(c), o.getDuration(), interval);
    }

    @Override
    public ScalarFunction1d getWorkloadFunction() {
        return manager.getWorkloadFuction();
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        return manager.haveEnoughCapacity(s, o, start);
    }

    @Override
    public double getMax() {
        return manager.getCapacity().doubleValue();
    }
    
    
    @Override
    public void initPositions(Schedule s, Collection<SpaceTimeElement> initialPositions) {
    }
}
