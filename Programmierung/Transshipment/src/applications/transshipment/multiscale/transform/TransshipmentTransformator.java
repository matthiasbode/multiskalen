/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.transform;

import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;

/**
 *
 * @author bode
 */
public interface TransshipmentTransformator extends Transformator{

    @Override
    public LoadUnitJobSchedule transform(Schedule schedule, SchedulingProblem problem);
    
}
