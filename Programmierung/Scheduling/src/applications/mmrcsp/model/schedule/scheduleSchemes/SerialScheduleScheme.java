/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import java.util.List;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class SerialScheduleScheme implements ScheduleGenerationScheme<Operation, SchedulingProblem<Operation>> {

    public SerialScheduleScheme() {
    }

    /**
     * Aus einer ActivityList wird ein Schedule erzeugt, und zwar nach dem
     * Prinzip, die Aufträge aus der Liste immer möglichst früh einzuplanen.
     *
     * @param list
     * @param p
     * @param timeSlot
     *
     * @return
     */
    @Override
    public Schedule getSchedule(List<Operation> list, SchedulingProblem<Operation> p, TimeSlot timeSlot) {

        InstanceHandler rules = new InstanceHandler(p.getScheduleManagerBuilder());

        Schedule result = new Schedule(rules);
        int n = list.size();
        for (int lambda = 0; lambda < n; lambda++) {
            Operation j = list.remove(0);
            /**
             * max_{i-->j \in C} {S_i + p_i} Maximum aller CompletionTimes der
             * Vorgängerknoten von j
             */

            FieldElement t = timeSlot.getFromWhen();
            for (Operation operation : p.getActivityOnNodeDiagramm().getPredecessors(j)) {
                FieldElement Si = result.get(operation);
                if (Si == null) {
                    throw new IllegalArgumentException("Innerhalb der List wurde i-->j nicht eingehalten");
                }
                FieldElement pi = operation.getDuration();

                FieldElement tCand = Si.add(pi);
                if (tCand.isGreaterThan(t)) {
                    t = tCand;
                }
            }
            /**
             * Ressourcentest für Operation j
             */
            ResourceTestLoop:
            while (true) {
                FieldElement t_k_mue = t;
                for (Resource k : j.getRequieredResources()) {
                    ScheduleRule sr = rules.get(k);
                    /**
                     * Ressourcenanforderungen zu hoch, Zeitfenster muss
                     * angepasst werden
                     */
                    if (!sr.canSchedule(result, j, t)) {
                        TimeSlot interval = new TimeSlot(t, k.getTemporalAvailability().getUntilWhen());
                        t_k_mue = sr.getFreeSlots(result, j, interval).first().getFromWhen();
                        if (t_k_mue == null) {
                            sr.getFreeSlots(result, j, interval).first().getFromWhen();
                            throw new IllegalArgumentException("Keine Startzeit ermittelbar für Operation " + j);
                        }
                        t = t_k_mue;
                        continue ResourceTestLoop;
                    }
                }
                /**
                 * Keine Anpassungen mehr nötig.
                 */
                break ResourceTestLoop;
            }
            /**
             * Einplanen von Operation j in das Interval [t, t+p_j] Einplanen im
             * Schedule, dadurch Update der Ressourcenauslastung
             */
            result.schedule(j, t);
//            System.out.println("Eingeplant: " + t.longValue());
//            System.out.println(j);
        }
        return result;
    }
}
