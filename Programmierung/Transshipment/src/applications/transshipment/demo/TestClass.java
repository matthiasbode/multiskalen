/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.multiscale.model.Scale;

/**
 *
 * @author bode
 */
public interface TestClass {

    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel);

//    public MultiJobTerminalProblem getProblem();
    public LoadUnitJobSchedule getBestSchedule();
 
}
