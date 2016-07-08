/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.multiscale.evaluation;

import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class ScheduleListIndividual extends ListIndividual<RoutingTransportOperation> {
    public LoadUnitJobSchedule schedule;
    public ScheduleListIndividual(ListIndividual<RoutingTransportOperation> sup) {
        super(sup.getChromosome());
    }

    public ScheduleListIndividual(List<SubListIndividual<RoutingTransportOperation>> chromosome) {
        super(chromosome);
    }

    public ScheduleListIndividual() {
    }
    
    
    @Override
    public ScheduleListIndividual clone() {
       return new ScheduleListIndividual(super.clone());
    }
    
    
}
