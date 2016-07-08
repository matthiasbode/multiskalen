/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;

import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.lattice.CellResource2D;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.lattice.LatticeManager;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.ga.TransshipmentSuperIndividual;
import bijava.math.function.ScalarFunction1d;
import java.awt.GridLayout;
import java.io.File;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import math.FieldElement;
import math.function.StepFunction;
import org.jfree.data.xy.XYSeries;
import util.ColorScale;

/**
 * Deprecated, gibt es jetzt in hübsch als HeatMap
 *
 * @author Philipp
 */
public class CranewayWorkloadPerTimePlotter implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {

        TimeSlot allOverTimeSlot = problem.getTerminal().getTemporalAvailability().getAllOverTimeSlot();
        /**
         * Ausgabe des Cranerunways
         */
        CraneRunway craneRunway = null;
        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (conveyanceSystem instanceof Crane) {
                craneRunway = ((Crane) conveyanceSystem).getCraneRunway();
                break;
            }
        }
        int i = 0;
        HashMap<FieldElement, XYSeries> times;
        times = new HashMap<>();
        FieldElement timeField = null;

        LatticeManager<CraneRunway> manager = (LatticeManager<CraneRunway>) schedule.getHandler().getSharedManager(craneRunway);
        for (CellResource2D cellResource2D : manager.getGrid().getCells()) {

            ScheduleRule scheduleRule = schedule.getHandler().get(cellResource2D);
            if (scheduleRule instanceof ScalarFunctionBasedRule) {
                ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

                ScalarFunction1d f = sfb.getWorkloadFunction();
                if (f == null) {
                    continue;
                }
                if (f instanceof StepFunction) {
                    StepFunction workloadFunction = (StepFunction) f;
                    int j = 0;
                    for (FieldElement time : workloadFunction.getSamplingPoints()) {
                        if (j == 10) {
                            timeField = time;
                            break;
                        }
                        j++;
                    }
                    break;
                }
            }
        }
        JFrame f = new JFrame();
        f.setLayout(new GridLayout(1, 40));
        for (CellResource2D cellResource2D : manager.getGrid().getCells()) {
            ScheduleRule scheduleRule = schedule.getHandler().get(cellResource2D);

            if (scheduleRule instanceof ScalarFunctionBasedRule) {
                ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

                ScalarFunction1d sf = sfb.getWorkloadFunction();
                if (sf == null) {
                    continue;
                }
                if (sf instanceof StepFunction) {
                    StepFunction workloadFunction = (StepFunction) sf;
                    double value = workloadFunction.getValue(timeField).doubleValue();
//                    cellResource2D.setColor(ColorScale.getColorInverted(value));
                    JButton label = new JButton();
                    //System.out.println(ColorScale.getColorInverted(value));
                    label.setBackground(ColorScale.getColorInverted(value));
                    label.setSize(10, 10);
                    f.add(label);
                }
            }
        }
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);

//            File f = new File(folder, "Zelle" + (i) + ".png");
//            JFreeChart createChart = FunctionPlotter.createChart(workloadFuction, allOverTimeSlot, "Für Resource " + cellResource2D.toString());
//            try {
//                ChartUtilities.saveChartAsPNG(f, createChart, 1900, 1000);
//            } catch (IOException ex) {
//                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            f = new File(folder, "Bucketing: Zelle" + (i++) + ".png");
//            JFreeChart bucketChart = FunctionPlotter.createChart(new Bucketing(cellResource2D, workloadFuction, allOverTimeSlot, 10 * 60 * 1000), allOverTimeSlot, "Für Resource " + cellResource2D.toString());
//            try {
//                ChartUtilities.saveChartAsPNG(f, bucketChart, 1900, 1000);
//            } catch (IOException ex) {
//                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//            }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
