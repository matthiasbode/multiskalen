/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.demo;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
import applications.mmrcsp.model.operations.DummyOperation;
import applications.mmrcsp.model.operations.OperationImplementation;
import applications.mmrcsp.model.problem.DefaultSchedulingProblem;
import applications.mmrcsp.model.resources.ResourceImplementation;
import applications.mmrcsp.model.restrictions.TimeRestrictions;
import applications.mmrcsp.model.schedule.rules.DefaultScheduleRules;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.scheduleSchemes.ParallelScheduleScheme;
import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;


import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import math.DoubleValue;
import math.function.StepFunction;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import util.chart.FunctionPlotter;

/**
 * Einfaches Beispiel. i | 1 2 3 4 ----------------------- p_i | 4 3 5 8 r_i1 |
 * 2 1 2 2 r_i2 | 3 5 2 4
 *
 * Kapazitäten für R1 = 5, R2 = 7
 *
 * @author bode
 */
public class ScheduleTest {

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
        DummyOperation o0 = new DummyOperation(true);
        Operation o1 = new OperationImplementation(4L);
        o1.setDemand(r1, new DoubleValue(2));
        o1.setDemand(r2, new DoubleValue(3));
        Operation o2 = new OperationImplementation(3L);
        o2.setDemand(r1, new DoubleValue(1));
        o2.setDemand(r2, new DoubleValue(5));
        Operation o3 = new OperationImplementation(5L);
        o3.setDemand(r1, new DoubleValue(2));
        o3.setDemand(r2, new DoubleValue(2));
        Operation o4 = new OperationImplementation(8L);
        o4.setDemand(r1, new DoubleValue(2));
        o4.setDemand(r2, new DoubleValue(4));
        Operation o5 = new DummyOperation(false);

        /**
         * Hinzufügen der Operationen
         */
        ops.add(o0);
        ops.add(o1);
        ops.add(o2);
        ops.add(o3);
        ops.add(o4);
        ops.add(o5);

        /**
         * Zeitrestriktionen zwischen den Operationen
         */
        TimeRestrictions tr = new TimeRestrictions();
        tr.putMinRestriction(o2, o3, o2.getDuration());
        tr.putMinRestriction(o1, o5, o1.getDuration());
        tr.putMinRestriction(o3, o5, o3.getDuration());
        tr.putMinRestriction(o4, o5, o4.getDuration());

        /**
         * Einplanregeln für die Ressourcen festlegen
         */
        DefaultScheduleRules builder = new DefaultScheduleRules();
        builder.put(r1, 5.);
        builder.put(r2, 7.);

        /**
         * Erstelle ActivityOnNodeGraphen
         */
        ActivityOnNodeGraph graph = ActivityOnNodeBuilder.build(ops, tr);

        /* *
         * Komplettes Problem
         * ops, resourceRestrictions.getResources(), builder
         */
        DefaultSchedulingProblem<Operation> problem = new DefaultSchedulingProblem<Operation>(null,ops, resources, builder, graph);

        
        /**
         * Erzeugt einen Ablaufplan
         */
        ScheduleGenerationScheme scheme = new ParallelScheduleScheme();
        
        /**
         * Reihenfolge des Einplanens.
         */
        List<Operation> activityListToTest = new LinkedList<>();
        activityListToTest.add(o1);
        activityListToTest.add(o2);
        activityListToTest.add(o3);
        activityListToTest.add(o4);
        activityListToTest.add(o5);
        /**
         * Eigentliches Erzeuge.
         */
        Schedule result = scheme.getSchedule(activityListToTest, problem, slot);
        
        /**
         * Ausgabe der Startzeit nach Ressource.
         */
        System.out.println("##########################");
        for (Resource resource : problem.getResources()) {
            System.out.println("Resource: " + resource);
            for (Operation operation : result.getOperationsForResource(resource)) {
                System.out.println(operation + " ---> " + result.get(operation));
            }
        }

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

    }
}
