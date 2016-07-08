/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.problem.SchedulingProblem;
import java.util.List;

/**
 * Die statischen Methoden dieser Klasse bieten Möglichkeiten aus Activity-Lists
 * Schedules zu Erzeugen. Ein Schedule sei hierbei durch eine Map zwischen einer
 * Operation und Ihrer Startzeit dargestellt.
 *
 * @author bode
 * @param <E>
 * @param <F>
 */
public interface ScheduleGenerationScheme<F extends Operation, E extends SchedulingProblem> {
    public Schedule getSchedule(List<F> activityList,  E problem,  TimeSlot timeSlot);

}
