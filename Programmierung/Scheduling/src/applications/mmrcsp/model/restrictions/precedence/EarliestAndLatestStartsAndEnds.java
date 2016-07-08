/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.restrictions.precedence;

import applications.mmrcsp.model.basics.TimeSlot;
import math.FieldElement;

/**
 *
 * @author bode
 */
public final class EarliestAndLatestStartsAndEnds implements Cloneable {

    private FieldElement earliestStart,
            latestStart,
            earliestEnd,
            latestEnd;
    private FieldElement transportationTime;

    private boolean scheduled;

    private boolean isDNF;

    public EarliestAndLatestStartsAndEnds(FieldElement transportationTime) {
        this.transportationTime = transportationTime;
    }

    public EarliestAndLatestStartsAndEnds() {
    }

    @Override
    public String toString() {
        return "EarliestAndLatestOperationStartsAndEnds{" + "earliestOperationStart=" + TimeSlot.longToFormattedDateString(earliestStart.longValue()) + ", latestOperationStart=" + TimeSlot.longToFormattedDateString(latestStart.longValue()) + ", earliestOperationEnd=" + TimeSlot.longToFormattedDateString(earliestEnd.longValue()) + ", latestOperationEnd=" + TimeSlot.longToFormattedDateString(latestEnd.longValue()) + '}';
    }

    public void setEarliest(FieldElement earliestOperationStart, FieldElement earliestOperationEnd) {
        if (isScheduled()) {
            throw new UnsupportedOperationException("Operation already scheduled");
        }
        this.earliestStart = earliestOperationStart;
        this.earliestEnd = earliestOperationEnd;
    }

    public void setLatest(FieldElement latestOperationStart, FieldElement latestOperationEnd) {
        if (isScheduled()) {
            throw new UnsupportedOperationException("Operation already scheduled");
        }
        this.latestStart = latestOperationStart;
        this.latestEnd = latestOperationEnd;
    }

    public void setEarliestStart(FieldElement earliestOperationStart) {
        if (isScheduled()) {
            throw new UnsupportedOperationException("Operation already scheduled");
        }
        this.earliestStart = earliestOperationStart;
    }

    public void setLatestStart(FieldElement latestOperationStart) {
        if (isScheduled()) {
            throw new UnsupportedOperationException("Operation already scheduled");
        }
        this.latestStart = latestOperationStart;
    }

    public void setEarliestEnd(FieldElement earliestOperationEnd) {
        if (isScheduled()) {
            throw new UnsupportedOperationException("Operation already scheduled");
        }
        this.earliestEnd = earliestOperationEnd;
    }

    public void setLatestEnd(FieldElement latestOperationEnd) {
        if (isScheduled()) {
            throw new UnsupportedOperationException("Operation already scheduled");
        }
        this.latestEnd = latestOperationEnd;
    }

    public boolean testValues() {
        if (earliestStart != null && latestStart != null && earliestStart.isGreaterThan(latestStart)) {
            return false;// throw new IllegalArgumentException("earliestOperationStart has to be less or equal than latestOperationStart");
        }
        if (earliestEnd != null && earliestStart != null && earliestEnd.isLowerThan(earliestStart)) {
            return false;// throw new IllegalArgumentException("earliestOperationEnd has to be greater  than earliestOperationStart");
        }
        if (earliestEnd != null && latestEnd != null && earliestEnd.isGreaterThan(latestEnd)) {
            return false;// throw new IllegalArgumentException("earliestOperationEnd has to be less or equal than latestOperationEnd");
        }
        if (latestEnd != null && latestStart != null && latestEnd.isLowerThan(latestStart)) {
            return false;// throw new IllegalArgumentException("latestOperationEnd has to be greater  than latestOperationStart");
        }
        if (latestEnd != null && earliestEnd != null && latestEnd.isLowerThan(earliestEnd)) {
            return false;// throw new IllegalArgumentException("latestOperationEnd has to be greater or equal than earliestOperationEnd");
        }
        if (latestStart != null && earliestStart != null && latestStart.isLowerThan(earliestStart)) {
            return false;// throw new IllegalArgumentException("latestOperationStart has to be greater or equal than earliestOperationStart");
        }
        return true;
    }

    public boolean isDNF() {
        return isDNF;
    }

    public void setDNF(boolean isDNF) {
        this.isDNF = isDNF;
    }

    public FieldElement getTransportationTime() {
        return transportationTime;
    }

    public FieldElement getEarliestStart() {
        return earliestStart;
    }

    public FieldElement getEarliestEnd() {
        return earliestEnd;
    }

    public FieldElement getLatestStart() {
        return latestStart;
    }

    public FieldElement getLatestEnd() {
        return latestEnd;
    }

    public TimeSlot getAvailableStartTimeSlot() {
        return new TimeSlot(earliestStart, latestStart);
    }

    @Override
    public EarliestAndLatestStartsAndEnds clone() {
        EarliestAndLatestStartsAndEnds clone = new EarliestAndLatestStartsAndEnds();
        clone.earliestEnd = this.earliestEnd;
        clone.earliestStart = this.earliestStart;
        clone.latestEnd = this.latestEnd;
        clone.latestStart = this.latestStart;
        clone.transportationTime = this.transportationTime;
        return clone;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

 
}
