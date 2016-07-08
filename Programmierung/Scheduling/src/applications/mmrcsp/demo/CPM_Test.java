/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.demo;


import applications.mmrcsp.ga.localsearch.CriticalPath;
import applications.mmrcsp.ga.localsearch.CriticalPathAnalyser;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.ExtendedActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
import applications.mmrcsp.model.operations.DummyOperation;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.operations.OperationImplementation;
import applications.mmrcsp.model.problem.DefaultSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.ResourceImplementation;
import applications.mmrcsp.model.restrictions.TimeRestrictions;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.DefaultScheduleRules;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JFrame;
import math.DoubleValue;
import math.LongValue;
import math.function.StepFunction;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import util.chart.FunctionPlotter;

/**
 *
 * @author bode
 */
public class CPM_Test {

    public static void main(String[] args) {
        TimeSlot slot = TimeSlot.create(0, 100);
        /**
         * Definieren der Resourcen
         */
        ArrayList<Resource> resources = new ArrayList<>();
        Resource r1 = new ResourceImplementation();
        Resource r2 = new ResourceImplementation();
        r1.setTemporalAvailability(slot);
        r2.setTemporalAvailability(slot);
        resources.add(r1);
        resources.add(r2);

        /**
         * Definieren der Operationen
         */
        ArrayList<Operation> ops = new ArrayList<>();

        DummyOperation opStart = new DummyOperation(0, true);
        Operation o1 = new OperationImplementation(4L, 1);
        o1.setDemand(r1, new DoubleValue(2));

        Operation o2 = new OperationImplementation(4L, 2);
        o2.setDemand(r2, new DoubleValue(2));

        Operation o3 = new OperationImplementation(4L, 3);
        o3.setDemand(r1, new DoubleValue(2));
        o3.setDemand(r2, new DoubleValue(2));

        DummyOperation o4 = new DummyOperation(4, true);

        /**
         * Hinzufügen der Operationen
         */
        ops.add(opStart);
        ops.add(o1);
        ops.add(o2);
        ops.add(o3);
        ops.add(o4);

        /**
         * Zeitrestriktionen zwischen den Operationen
         */
        TimeRestrictions tr = new TimeRestrictions();
        tr.putMinRestriction(opStart, o1, opStart.getDuration());
//        tr.putMinRestriction(o0, o2, o0.getDuration());
        tr.putMinRestriction(opStart, o2, opStart.getDuration());

//        tr.putMinRestriction(o2, o3, o2.getDuration());
        tr.putMinRestriction(o1, o3, o1.getDuration());
//        tr.putMinRestriction(o3, o5, o3.getDuration());
        tr.putMinRestriction(o2, o3, o2.getDuration());

        tr.putMinRestriction(o3, o4, o3.getDuration());

        /**
         * Einplanregeln für die Ressourcen festlegen
         */
        DefaultScheduleRules builder = new DefaultScheduleRules();
        builder.put(r1, 12.);
        builder.put(r2, 12.);

        /**
         * Erstelle ActivityOnNodeGraphen
         */
        ActivityOnNodeGraph graph = ActivityOnNodeBuilder.build(ops, tr);

        /* *
         * Komplettes Problem
         * ops, resourceRestrictions.getResources(), builder
         */
        DefaultSchedulingProblem<Operation> problem = new DefaultSchedulingProblem<>(null,ops, resources, builder, graph);

        /**
         * Verwaltet die Einplanungen für die einzelnen Ressourcen.
         */
        InstanceHandler handler = new InstanceHandler(builder);

        /**
         * Eigentliches Erzeuge.
         */
        Schedule result = new Schedule(handler);

        result.schedule(opStart, new LongValue(0));
        result.schedule(o1, new LongValue(0));
        result.schedule(o2, new LongValue(0));
        result.schedule(o3, new LongValue(4));
        result.schedule(o4, new LongValue(8));

        for (Resource resource : problem.getResources()) {
            ScheduleRule scheduleRule = result.getHandler().get(resource);
            if (scheduleRule instanceof ScalarFunctionBasedRule) {
                ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;
                JFreeChart createChart = FunctionPlotter.createAreaDateChart((StepFunction)sfb.getWorkloadFunction(), slot, "Für Resource " + resource.toString());
                JFrame f = new JFrame();
                f.setSize(800, 600);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(new ChartPanel(createChart), BorderLayout.CENTER);
                f.setVisible(true);
            }
        }

        ExtendedActivityOnNodeGraph extendedActivityOnNodeGraph = ActivityOnNodeBuilder.getExtendedActivityOnNodeGraph(resources, result, graph);
        Set<CriticalPath<Operation>> criticalPaths = CriticalPathAnalyser.getCriticalPaths(result, extendedActivityOnNodeGraph, opStart, o4);
        for (CriticalPath<Operation> criticalPath : criticalPaths) {
            System.out.println(criticalPath);
            ArrayList<CriticalPath<Operation>> criticalBlocks = CriticalPathAnalyser.<Operation>getCriticalBlocks(criticalPath, extendedActivityOnNodeGraph);
            for (CriticalPath<Operation> block : criticalBlocks) {
                System.out.println(block);
            }
            System.out.println("-------------");
        }

    }
}
