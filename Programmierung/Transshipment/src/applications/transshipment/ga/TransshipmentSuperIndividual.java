/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga;

import applications.mmrcsp.ga.ScheduleIndividual;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public class TransshipmentSuperIndividual<O extends Individual, M extends Individual> extends SuperIndividual   {

    private int DNF;
    private double idleCraneDistance;
    private double craneLongitudinalDistance;
   

    public TransshipmentSuperIndividual() {
    }

    public TransshipmentSuperIndividual(O operationIndividual, M modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

    public O getOperationIndividual() {
        return (O) this.get(0);
    }

    public M getModeIndividual() {
        return (M) this.get(1);
    }

    public void setModeIndividual(M ind) {
        this.set(1, ind);
    }

    public void setOperationIndividual(O ind) {
        this.set(0, ind);
    }

    public int getDNF() {
        return DNF;
    }

    public double getIdleCraneDistance() {
        return idleCraneDistance;
    }

    public void setDNF(int DNF) {
        this.DNF = DNF;
    }

    public void setIdleCraneDistance(double idleCraneDistance) {
        this.idleCraneDistance = idleCraneDistance;
    }

    @Override
    public TransshipmentSuperIndividual<O, M> clone() {
        return new TransshipmentSuperIndividual((O) getOperationIndividual().clone(), (M) getModeIndividual().clone());
    }

    @Override
    public String toString() {
        String res = "TransshipmentSuperIndividual_";
        res += getNumber() + "{";
        res += getOperationIndividual() + ",";
        res += getModeIndividual() + "}";
        res += "(" + DNF + "/" + TransshipmentParameter.doubleFormat.format(idleCraneDistance) + "/" + TransshipmentParameter.doubleFormat.format(craneLongitudinalDistance) + ")";
        return res;
    }

    public double getCraneLongitudinalDistance() {
        return craneLongitudinalDistance;
    }

    public void setCraneLongitudinalDistance(double craneLongitudinalDistance) {
        this.craneLongitudinalDistance = craneLongitudinalDistance;
    }

//    @Override
//    public LoadUnitJobSchedule getSchedule() {
//        return (LoadUnitJobSchedule) additionalObjects.get(Schedule.KEY_SCHEDULE);
//    }
//
//    @Override
//    public void setSchedule(LoadUnitJobSchedule s) {
//        additionalObjects.put(Schedule.KEY_SCHEDULE, s);
//
//    }

}
