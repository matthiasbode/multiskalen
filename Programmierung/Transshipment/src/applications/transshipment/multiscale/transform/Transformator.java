/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.transform;

import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.schedule.Schedule;

/**
 *
 * @author bode
 */
public interface Transformator {
    public Schedule transform(Schedule schedule, SchedulingProblem problem);
}
