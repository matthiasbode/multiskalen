/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.mmrcsp.model.schedule.test;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.schedule.Schedule;
import java.util.LinkedHashSet;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class OrderValid {
    public static <E extends Operation> boolean testSchedule(SchedulingProblem<E> problem, Schedule schedule){
        for (E e : problem.getOperations()) {
            if(schedule.get(e) == null){
                throw new IllegalArgumentException("Operation nicht eingeplant");
            }
        }
        for (E e : problem.getActivityOnNodeDiagramm().vertexSet()) {
            FieldElement startTimeE = schedule.get(e);
            FieldElement endTimeE = startTimeE.add(e.getDuration());
            LinkedHashSet<E> successors = problem.getActivityOnNodeDiagramm().getSuccessors(e);
            for (E e1 : successors) {
                FieldElement startTimeE1 = schedule.get(e1);
                
                if(endTimeE.isGreaterThan(startTimeE1)){
                    
                    throw new IllegalArgumentException("Nachfolgeoperation früher eingeplant als Vorgänger!\n Ausgangsknoten: " +e + ": " + endTimeE +"\n Nachfolger: "+ e1 +": "+ startTimeE1);
//                    return false;
                }
            }
        }
        return true;
    }
}
