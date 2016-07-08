/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import ga.basics.FitnessEvalationFunction;

/**
 *
 * @author bode
 */
public class TimeTableEvaluationFunction implements FitnessEvalationFunction<TimeTableMatrix> {

    /**
     * Berücksichtigt werden muss:
     * Möglichst wenig Leerstunden für eine Klasse zwischen den Kursen
     * Möglichst gute Raumauslastung --> Erwartete Anzahl an Studenten / Größe Raum
     * Speziell: einige Kurse sollten nacheinander stattfinden
     * Sonst: möglichst gut verteilt unter der Woche
     * 
     * @param c
     * @return 
     */
    @Override
    public double[] computeFitness(TimeTableMatrix c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
