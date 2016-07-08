/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.demo.restrictionsTest;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.DummyOperation;
import applications.mmrcsp.model.operations.OperationImplementation;
import applications.mmrcsp.model.resources.ResourceImplementation;
import applications.mmrcsp.model.restrictions.ResourceRestrictions;
import applications.mmrcsp.model.restrictions.TimeRestrictions;
import applications.mmrcsp.model.schedule.rules.DefaultScheduleRules;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import java.util.ArrayList;
import java.util.HashMap;
import math.DoubleValue;

/**
 * Einfaches Beispiel. i | 1 2 3 4 ----------------------- p_i | 4 3 5 8 r_i1 |
 * 2 1 2 2 r_i2 | 3 5 2 4
 *
 * Kapazitäten für R1 = 5, R2 = 7
 *
 * @author bode
 */
public class RestrictionTest {

    public static void main(String[] args) {
        TimeSlot slot = TimeSlot.create(0, 100);
        /**
         * Definieren der Resourcen
         */
        Resource r1 = new ResourceImplementation();
        Resource r2 = new ResourceImplementation();
        r1.setTemporalAvailability(slot);
        r2.setTemporalAvailability(slot);

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
         * Kapazitäten festlegen
         */
        HashMap<Resource, Double> capacity = new HashMap<>();
        capacity.put(r1, 5.);
        capacity.put(r2, 7.);

        /**
         * Alternative Kapazität der Resource r2
         */
//        capacity.put(r2, 5.);

        /**
         * Resource Restrictions
         */
        ResourceRestrictions resourceRestrictions = new ResourceRestrictions(capacity);

        /**
         * Einplanregeln für die Ressourcen festlegen
         */
        DefaultScheduleRules builder = new DefaultScheduleRules(capacity);
        InstanceHandler rules = new InstanceHandler(builder);
        /**
         *
         *
         * /**
         * Beispiel Schedule erzeugt
         */
        Schedule s = new Schedule(rules, r1, r2);
        s.schedule(o1, 0L);
        s.schedule(o2, 4L);
        s.schedule(o3, 7L);
        s.schedule(o4, 7L);
        s.schedule(o5, s.get(o4).add(o4.getDuration()));
        /**
         * Test auf ResourceRestrictions
         */
        System.out.println("Ressource 1: " + resourceRestrictions.complyRestriction(s, r1));
        System.out.println("Ressource 2: " + resourceRestrictions.complyRestriction(s, r2));

        /**
         * Test auf ZeitRestrictions
         */
        tr.complyRestrictions(s, ops);

    }
}
