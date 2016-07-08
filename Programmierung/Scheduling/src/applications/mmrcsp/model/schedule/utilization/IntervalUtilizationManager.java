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
import bijava.math.function.ScalarFunction1d;
import java.util.TreeSet;
import math.DoubleValue;
import math.FieldElement;
import math.function.StepFunction;

/**
 *
 * @author Bode
 */
public class IntervalUtilizationManager implements UtilizationManager {

    TreeSet<Bin> bins = new TreeSet<>();
    DoubleValue capacity;
    Resource r;
    TimeSlot ts;

    public IntervalUtilizationManager(Resource r, double capacity, TimeSlot ts, long dt) {
        this.r = r;
        this.capacity = new DoubleValue(capacity*0.8);
        this.ts = ts;

        for (long t = ts.getFromWhen().longValue(); t <= ts.getUntilWhen().longValue(); t += dt) {
            bins.add(new Bin(TimeSlot.create(t, t + dt), r, this.capacity.doubleValue()));
        }
    }

    public IntervalUtilizationManager(Resource r, double capacity, FieldElement start, long dt) {
        this.r = r;
        this.capacity = new DoubleValue(capacity*0.7);
        this.ts = new TimeSlot(start, r.getTemporalAvailability().getUntilWhen());

        for (long t = ts.getFromWhen().longValue(); t <= ts.getUntilWhen().longValue(); t += dt) {
            bins.add(new Bin(TimeSlot.create(t, t + dt), r, this.capacity.doubleValue()));
        }
    }

    @Override
    public DoubleValue getCapacity() {
        return capacity;
    }

    @Override
    public boolean haveEnoughCapacity(Schedule s, Operation o, FieldElement start) {
        Bin binToSchedule = bins.floor(new Bin(start));
        return binToSchedule.canAdd(o);
    }

    @Override
    public void scheduleInternal(Operation o, Schedule s, FieldElement start) {
        Bin binToSchedule = bins.floor(new Bin(start));
        binToSchedule.add(o);
    }

    @Override
    public void unScheduleInternal(Operation o, Schedule s) {
        Bin binToSchedule = bins.floor(new Bin(s.get(o)));
        binToSchedule.remove(o);
    }

    @Override
    public void setTimeSlot(TimeSlot t) {
        this.ts = t;
    }

    @Override
    public ScalarFunction1d getWorkloadFuction() {
        StepFunction sf = new StepFunction(ts.getFromWhen(), ts.getUntilWhen(), new DoubleValue(0));
        for (Bin bin : bins) {
            StepFunction binFunction = new StepFunction(bin.getSlot().getFromWhen(), bin.getSlot().getUntilWhen(), bin.getWorkload());
            sf = sf.add(binFunction);
        }
        return sf;
    }

    @Override
    public TimeSlotList getFreeSlotsInternal(Schedule s, FieldElement demand, FieldElement duration, TimeSlot interval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
