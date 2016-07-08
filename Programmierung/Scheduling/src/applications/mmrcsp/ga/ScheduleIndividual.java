/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.mmrcsp.ga;

import applications.mmrcsp.model.schedule.Schedule;

/**
 *
 * @author bode
 */
public interface ScheduleIndividual<S extends Schedule> {
    public S getSchedule();
    public void setSchedule(S s);
}
