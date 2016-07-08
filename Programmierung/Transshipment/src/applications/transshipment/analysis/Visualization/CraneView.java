/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Visualization;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.MicroscopicCraneRule;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.CraneMicroTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import bijava.geometry.dim2.SimplePolygon2d;
import canvas2D.Canvas2D;
import canvas2D.ObjectShape;
import canvas2D.Polygon2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Collection;
import javax.swing.JFrame;
import math.geometry.DynamicPolygonalRegion;

/**
 *
 * @author bode
 */
public class CraneView implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {
        JFrame f = new JFrame();
        final Canvas2D canvas = new Canvas2D();

        canvas.activateMouseControl(true);

        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Point2D.Double screen2world = canvas.screen2world(e.getPoint());
                long time = DynamicPolygonalRegion.doubleToLong(screen2world.y);
                System.out.println(screen2world.x + "\t" + TimeSlot.longToFormattedDateString(time));
            }

        });
        f.add(canvas, BorderLayout.CENTER);
        f.setSize(800, 800);

        Color[] colors = new Color[10];
        colors[0] = Color.RED;
        colors[1] = Color.BLUE;
        colors[2] = Color.GREEN;
        colors[3] = Color.ORANGE;
        colors[4] = Color.MAGENTA;
        colors[5] = Color.PINK;

        CraneRunway cr = null;
        for (SharedResource sharedResource : problem.getTerminal().getSharedResources()) {
            if (sharedResource instanceof CraneRunway) {
                cr = (CraneRunway) sharedResource;
                break;
            }
        }

        int i = 0;
        for (Crane crane : cr.getSharingResources()) {
            MicroscopicCraneRule currentRule = (MicroscopicCraneRule) schedule.getHandler().get(crane);

            Collection<Operation> operationsForResource = schedule.getOperationsForResource(crane);
            for (Operation operation : operationsForResource) {
                if (operation instanceof CraneMicroTransportOperation) {
                    CraneMicroTransportOperation cmt = (CraneMicroTransportOperation) operation;
                    DynamicPolygonalRegion top = cmt.getWorkingAreaRepresentation();

                    Polygon2D p2 = new Polygon2D(top.getPolygonalRepresentation(Long.MIN_VALUE, Long.MAX_VALUE));
                    long time = DynamicPolygonalRegion.doubleToLong(p2.getBounds2D().getMinY());
//                        System.out.println(TimeSlot.longToFormattedDateString(this.bestSchedule.get(operation).longValue()) + "\t:" + TimeSlot.longToFormattedDateString(time));
                    ObjectShape<CraneMicroTransportOperation> shape = new ObjectShape<>(p2, cmt);
                    shape.setColor(colors[i]);
                    shape.setFilled(true);
                    shape.setFilledColor(colors[i]);
                    canvas.addShape(shape);
                }
            }
            /**
             * WorkingArea
             */
            DynamicPolygonalRegion region = currentRule.getWorkingArea();
            SimplePolygon2d polygonalRepresentation = region.getPolygonalRepresentation(currentRule.getWorkingArea().getTmin(), currentRule.getWorkingArea().getTmax());
            Polygon2D polyShape = new Polygon2D(polygonalRepresentation);
            polyShape.setColor(colors[i]);

            canvas.addShape(polyShape);
            i++;
        }
        
//            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.repaint();
        f.setVisible(true);
    }

}
