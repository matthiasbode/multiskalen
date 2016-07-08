/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.bucketing;

import applications.mmrcsp.model.basics.TimeSlot;
import math.LongValue;

/**
 *
 * @author Bode
 */
public class Bucket implements Comparable<Bucket> {

    private TimeSlot<LongValue> timeSlot;
    private long bucketWidth;
    private double load;

    public Bucket(TimeSlot<LongValue> ts, double demand) {
        this.timeSlot = ts;
        this.bucketWidth = ts.getDuration().longValue();
        this.load = demand;
    }

    public double getLoad() {
        return load / bucketWidth;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public double getDemand() {
        return load;
    }

    public void setLoad(double e) {
        this.load = e;
    }

    @Override
    public int compareTo(Bucket o) {
        return this.timeSlot.getFromWhen().compareTo(o.timeSlot.getFromWhen());
    }
}
