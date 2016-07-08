/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import applications.mmrcsp.model.schedule.Schedule;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public interface ScheduleFitnessEvalationFunction<I extends Individual> extends FitnessEvalationFunction<I> {

    public Schedule getSchedule(I ind);
}
