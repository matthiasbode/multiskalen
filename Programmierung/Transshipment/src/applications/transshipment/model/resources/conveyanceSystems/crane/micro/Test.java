///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.transshipment.model.resources.conveyanceSystems.crane.micro;
//
//import applications.mmrcsp.model.basics.TimeSlot;
//import applications.mmrcsp.model.operations.Operation;
//import applications.mmrcsp.model.resources.Resource;
//import applications.mmrcsp.model.resources.sharedResources.SharedResource;
//import applications.mmrcsp.model.schedule.rules.InstanceHandler;
//import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
//import applications.mmrcsp.model.schedule.rules.ScheduleRule;
//import applications.transshipment.generator.projekte.duisburg.TerminalGenerator;
//import applications.transshipment.model.LoadUnitJob;
//import applications.transshipment.model.basics.TransportBundle;
//import applications.transshipment.model.loadunits.TwistLockLoadUnit;
//import applications.transshipment.model.operations.transport.RoutingTransportOperation;
//import applications.transshipment.model.resources.conveyanceSystems.SharedResourceManager;
//import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
//import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
//import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.AdminCraneOperation;
//import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.CraneMicroTransportOperation;
//import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
//import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
//import applications.transshipment.model.resources.conveyanceSystems.lcs.micro.MicroscopicAgentRule;
//import applications.transshipment.model.resources.conveyanceSystems.lcs.micro.MicroscopicLCSystemRule;
//import applications.transshipment.model.resources.lattice.CellResource2D;
//import applications.transshipment.model.resources.lattice.CellResourceRule;
//import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
//import applications.transshipment.model.resources.storage.simpleStorage.rules.MacroscopicLocationBasedStorageRule;
//import applications.transshipment.model.schedule.LoadUnitJobSchedule;
//import applications.transshipment.model.structs.Slot;
//import applications.transshipment.model.structs.Terminal;
//import applications.transshipment.routing.TransferArea;
//import bijava.geometry.dim2.SimplePolygon2d;
//import bijava.geometry.dim3.Point3d;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.geom.Rectangle2D;
//import java.util.GregorianCalendar;
//import java.util.Random;
//import java.util.TreeSet;
//import javax.swing.JFrame;
//import math.LongValue;
//import math.geometry.DynamicPolygonalRegion;
//import util.RandomUtilities;
//import visualization.Canvas2D;
//import visualization.ObjectShape;
//import visualization.Polygon2D;
//
///**
// *
// * @author bode
// */
//public class Test {
//
//    public static void main(String[] args) {
//        System.out.println("huhuu");
//        ScheduleManagerBuilder scheduleManagerBuilder = new ScheduleManagerBuilder() {
//            @Override
//            public ScheduleRule build(Resource resource) {
//                ScheduleRule rule = null;
//
//                if (resource instanceof LCSystem) {
//                    rule = new MicroscopicLCSystemRule((LCSystem) resource);
//                }
//                if (resource instanceof Crane) {
//                    rule = new MicroscopicCraneRule((Crane) resource);
//                }
//                if (resource instanceof LocationBasedStorage) {
//                    rule = new MacroscopicLocationBasedStorageRule((LocationBasedStorage) resource);
//                }
//                if (resource instanceof CellResource2D) {
//                    rule = new CellResourceRule((CellResource2D) resource);
//                }
//                if (resource instanceof Agent) {
//                    rule = new MicroscopicAgentRule((Agent) resource);
//                }
//                if (resource instanceof Slot) {
//                    rule = new MacroscopicLocationBasedStorageRule((LocationBasedStorage) resource);
//                }
//                return rule;
//            }
//
//            @Override
//            public SharedResourceManager build(SharedResource r) {
//                if (r instanceof CraneRunway) {
//                    return new MicroCraneRunwaySharedManager((CraneRunway) r);
//                }
//                return null;
//            }
//
//        };
//
//        LoadUnitJobSchedule schedule = new LoadUnitJobSchedule(new InstanceHandler(scheduleManagerBuilder));
//        TerminalGenerator.start = new GregorianCalendar(2012, 5, 10, 12, 00);
////        TerminalGenerator.start.setTimeInMillis(0);
//        TerminalGenerator.ende = new GregorianCalendar();
//        TerminalGenerator.ende.setTimeInMillis(TerminalGenerator.start.getTimeInMillis() + 90 * 60 * 1000);
//        Terminal terminal = TerminalGenerator.generateTerminal();
//        CraneRunway cr = null;
//        for (SharedResource sharedResource : terminal.getSharedResources()) {
//            if (sharedResource instanceof CraneRunway) {
//                cr = (CraneRunway) sharedResource;
//                break;
//            }
//        }
//
//        int i = 0;
//        Random random = new Random(3L);
//        LongValue t = new LongValue(terminal.getTemporalAvailability().getFromWhen().longValue());
//        while (true) {
//            // Zufaelligen Kran auswaehlen:
//            int index = (int) (random.nextDouble() * cr.getSharingResources().size());
//            if (index == 2) {
//                System.out.println("huhu");
//            }
//            Crane crane = cr.getSharingResources().get(index);
//
//            Rectangle2D bounds = crane.getGeneralOperatingArea().getBounds2D();
//
//            // zufaelligen Transportauftrag erzeugen:
//            double xstart = bounds.getMinX() + crane.width / 2. + random.nextDouble() * (bounds.getWidth() - crane.width);
//            double ystart = bounds.getMinY() + random.nextDouble() * bounds.getHeight();
//
//            double xend = bounds.getMinX() + crane.width / 2. + random.nextDouble() * (bounds.getWidth() - crane.width);
//            double yend = bounds.getMinY() + random.nextDouble() * bounds.getHeight();
//
//            AdminCraneOperation.StartEndPosition posStart = new AdminCraneOperation.StartEndPosition(new Point3d(xstart, ystart, 0.));
//            AdminCraneOperation.StartEndPosition posZiel = new AdminCraneOperation.StartEndPosition(new Point3d(xend, yend, 0.));
//            TwistLockLoadUnit twistLockLoadUnit = new TwistLockLoadUnit();
//            LoadUnitJob job = new LoadUnitJob(twistLockLoadUnit);
//            RoutingTransportOperation rtop = new RoutingTransportOperation(new TransferArea(posStart), new TransferArea(posZiel), job, crane);
//            ScheduleRule rule = schedule.getRules().get(crane);
//            MicroscopicCraneRule mrule = (MicroscopicCraneRule) rule;
//
//            LongValue start = new LongValue(RandomUtilities.getRandomValue(random, 0, 5 * 60 * 1000));
//
//            start = start.add(t);
//            if (i == 8) {
//                System.out.println("huhu");
//            }
//            CraneMicroTransportOperation detailedOperation = mrule.getDetailedOperation(rtop, posStart, posZiel);
//            System.out.println(TimeSlot.longToFormattedDuration(detailedOperation.getDuration().longValue()));
//            t = t.add(new LongValue(5 * 1000));
////            System.out.println(t.longValue());
//
//            if (!mrule.canSchedule(schedule, detailedOperation, start)) {
//                System.out.println("Passt nicht");
//                continue;
//            } else {
//                TransportBundle bundle = mrule.getBundle(schedule, detailedOperation, start);
//                schedule.schedule(detailedOperation, bundle.getStartTime(detailedOperation));
//            }
//            if (i >= 100) {
//                break;
//            }
//            i++;
//
//        }
//
//        JFrame f = new JFrame();
//        Canvas2D canvas = new Canvas2D();
//        canvas.activateMouseControl(true);
//        f.add(canvas, BorderLayout.CENTER);
//        f.setSize(800, 800);
////        canvas.setSize(1000, 1000);
//        Color[] colors = new Color[10];
//        colors[0] = Color.RED;
//        colors[1] = Color.BLUE;
//        colors[2] = Color.GREEN;
//        colors[3] = Color.ORANGE;
//        colors[4] = Color.MAGENTA;
//        colors[5] = Color.PINK;
//        i = 0;
//        for (Crane crane : cr.getSharingResources()) {
//            MicroscopicCraneRule currentRule = (MicroscopicCraneRule) schedule.getRules().get(crane);
//
////            PolygonalCurve2d leftBound = region.getLeftBound();
////            PolygonalCurve2d rightBound = region.getRightBound();
////            PolygonalCurve2D lB = new PolygonalCurve2D(leftBound);
////            PolygonalCurve2D rB = new PolygonalCurve2D(rightBound);
////            lB.setColor(colors[i]);
////            rB.setColor(colors[i]);
////            canvas.addShape(lB);
////            canvas.addShape(rB);
//            TreeSet<Operation> operationsForResource = schedule.getOperationsForResource(crane);
//            for (Operation operation : operationsForResource) {
//                if (operation instanceof CraneMicroTransportOperation) {
//                    CraneMicroTransportOperation cmt = (CraneMicroTransportOperation) operation;
//                    DynamicPolygonalRegion top = cmt.getWorkingAreaRepresentation();
//                    Polygon2D p2 = new Polygon2D(top.getPolygonalRepresentation(Long.MIN_VALUE, Long.MAX_VALUE));
//                    ObjectShape<CraneMicroTransportOperation> shape = new ObjectShape<>(p2, cmt);
//                    shape.setColor(colors[i]);
//                    shape.setFilled(true);
//                    shape.setFilledColor(colors[i]);
//                    canvas.addShape(shape);
//                }
//            }
//            /**
//             * WorkingArea
//             */
//            DynamicPolygonalRegion region = currentRule.getManager().workingArea;
//            SimplePolygon2d polygonalRepresentation = region.getPolygonalRepresentation(currentRule.getManager().tmin, currentRule.getManager().tmax);
//            Polygon2D polyShape = new Polygon2D(polygonalRepresentation);
//            polyShape.setColor(colors[i]);
//
//            canvas.addShape(polyShape);
//            i++;
//        }
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        canvas.repaint();
//        f.setVisible(true);
//    }
//}
