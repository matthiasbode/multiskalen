/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.utilization;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import math.DoubleValue;
import math.FieldElement;
import math.function.StepFunction;

/**
 *
 * @author bode
 */
public class StepFunctionBasedUtilizationManager implements UtilizationManager {

    private StepFunction workloadFuction;
    final Resource r;
    final DoubleValue capacity;
    private TimeSlot t;

    public StepFunctionBasedUtilizationManager(Resource r, double capacity, TimeSlot t) {
        this.capacity = new DoubleValue(capacity);
        this.r = r;
        this.t = t;
        this.workloadFuction = new StepFunction(t.getFromWhen(), t.getUntilWhen(), new DoubleValue(0.0));
    }

    public StepFunctionBasedUtilizationManager(Resource r, double capacity, FieldElement start) {
        this.capacity = new DoubleValue(capacity);
        this.r = r;
        this.t = new TimeSlot(start, r.getTemporalAvailability().getUntilWhen());
        this.workloadFuction = new StepFunction(t.getFromWhen(), t.getUntilWhen(), new DoubleValue(0.0));
    }

    @Override
    public StepFunction getWorkloadFuction() {
        return workloadFuction;
    }

    public DoubleValue getCapacity() {
        return capacity;
    }

    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {

        if (start == null) {
            throw new IllegalArgumentException("Keine Startzeit übergeben!");
        }
        if (o.getDuration() == null) {
            throw new IllegalArgumentException("Keine Dauer gesetzt!");
        }
        FieldElement rjk = o.getDemand(r);
        Map.Entry<FieldElement, FieldElement> min = getLeftOverCapacityFunction().getMin(start, start.add(o.getDuration()));
        if (min == null) {
            return false;
        }

        return !(rjk.isGreaterThan(min.getValue()));
    }

    private StepFunction getLeftOverCapacityFunction() {
        TimeSlotList temporalAvailability = new TimeSlotList(t);
        if (temporalAvailability == null || temporalAvailability.isEmpty()) {
            throw new NoSuchElementException("Keine TemporalAvailability gesetzt" + r);
        }
        StepFunction result = new StepFunction(temporalAvailability.getFromWhen(), temporalAvailability.getUntilWhen(), capacity);
        result = result.sub(workloadFuction);
        return result;
    }

    public void scheduleInternal(Operation o, Schedule s, FieldElement start) {
        if (!haveEnoughCapacity(s, o, start)) {
            haveEnoughCapacity(s, o, start);
            String msg = "Kapazität ausgeschöpft!" + o + "\t" + TimeSlot.longToFormattedDateString(start.longValue()) + "\t" + TimeSlot.longToFormattedDateString(start.longValue() + o.getDuration().longValue());
            Collection<Operation> operationsForResource = s.getOperationsForResource(r);
            msg += "\nOperation auf Ressource\n";
            for (Operation operation : operationsForResource) {
                msg += operation + "\t" + TimeSlot.longToFormattedDateString(s.get(operation).longValue()) + "\t" + TimeSlot.longToFormattedDateString(s.get(operation).add(operation.getDuration()).longValue()) + "\n";
            }
            Map.Entry<FieldElement, FieldElement> min = getLeftOverCapacityFunction().getMin(start, start.add(o.getDuration()));
            TreeMap<FieldElement, FieldElement> values = getLeftOverCapacityFunction().getValues();
            for (FieldElement k : values.keySet()) {
                msg += "\n" + k + ":" + TimeSlot.longToFormattedDateString(k.longValue()) + "-->" + values.get(k);
            }
            msg += "\n Minimaler Wert, Punkt der Überschreitung:\n" + TimeSlot.longToFormattedDateString(min.getKey().longValue()) + "--->" + min.getValue();
            throw new IllegalArgumentException(msg);
        }
        StepFunction f = new StepFunction(start, start.add(o.getDuration()), o.getDemand(r));
        this.workloadFuction = workloadFuction.add(f);
    }

    public void unScheduleInternal(Operation o, Schedule s) {
        FieldElement startTime = s.getStartTimes().get(o);
        if (o == null) {
            throw new NullPointerException("Keine Operation hinterlegt");
        }
        if (startTime == null) {
            throw new NullPointerException("Keine Zeit gesetzt");
        }
        StepFunction f = new StepFunction(startTime, startTime.add(o.getDuration()), o.getDemand(r));
        this.workloadFuction = workloadFuction.sub(f);
    }

    /**
     * Gibt eine TimeSlot-List zurück von Zeitbereichen, in denen der
     * Ressourcenbedarf verfügbar ist für die angeforderte Ressource.
     *
     * @param s
     * @param k Ressource, die befragt werden soll
     * @param duration
     * @param demand Bedarf
     * @param interval Zeitfenster, für das gefragt werden soll.
     * @return
     */
    public TimeSlotList getFreeSlotsInternal(Schedule s, FieldElement demand, FieldElement duration, TimeSlot interval) {
        TimeSlotList result = new TimeSlotList();
        StepFunction leftOverCapacityFunction = getLeftOverCapacityFunction();
        ArrayList<StepFunction.Interval> slots = leftOverCapacityFunction.getFreeSlots(demand, interval);
        for (StepFunction.Interval slot : slots) {
            if (slot.to.sub(slot.from).isGreaterThan(duration) || slot.to.sub(slot.from).equals(duration)) {
                result.add(new TimeSlot(slot.from, slot.to));
            }
        }
        return result;
    }

    public void setTimeSlot(TimeSlot t) {
        this.t = t;
    }

}
