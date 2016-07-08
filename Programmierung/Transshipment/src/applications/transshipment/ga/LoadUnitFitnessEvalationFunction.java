/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga;

import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import ga.individuals.Individual;
import util.ScheduleFitnessEvalationFunction;

/**
 *
 * @author bode
 */
public interface LoadUnitFitnessEvalationFunction<I extends Individual> extends  ScheduleFitnessEvalationFunction<I> {
    @Override
    public LoadUnitJobSchedule getSchedule(I ind);
}
