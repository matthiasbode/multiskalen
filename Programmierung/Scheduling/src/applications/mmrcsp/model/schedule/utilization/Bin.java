/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.utilization;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import java.util.ArrayList;
import math.DoubleValue;
import math.Field;
import math.FieldElement;

/**
 *
 * @author Bode
 */
public class Bin<O extends Operation> implements Comparable<Bin> {

    private TimeSlot slot;
    private ArrayList<O> operations;
    private FieldElement capacity;
    private Resource r;
    FieldElement resourceDemand = new DoubleValue(0);

    public Bin(TimeSlot slot, Resource r, double capacity) {
        this.slot = slot;
        this.capacity = new DoubleValue(capacity);
        this.r = r;
        this.operations = new ArrayList<>();
    }

    public Bin(FieldElement fromWhen) {
        this.slot = new TimeSlot(fromWhen, fromWhen);
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public ArrayList<O> getOperations() {
        return operations;
    }

    public FieldElement getCapacity() {
        return capacity.mult(new DoubleValue(slot.getDuration().doubleValue()));
    }

    public void add(O operation) {
        this.operations.add(operation);
        this.resourceDemand = this.resourceDemand.add(operation.getDemand(r).mult(new DoubleValue(operation.getDuration().doubleValue())));
    }

    public void remove(O operation) {
        this.operations.remove(operation);
        this.resourceDemand = this.resourceDemand.sub(operation.getDemand(r).mult(new DoubleValue(operation.getDuration().doubleValue())));
    }

    public boolean canAdd(O operation) {
        FieldElement demand = operation.getDemand(r).mult(new DoubleValue(operation.getDuration().doubleValue()));
        FieldElement afterAdd = this.resourceDemand.add(demand);
        if (afterAdd.isGreaterThan(getCapacity())) {
            return false;
        }
        return true;
    }

    public FieldElement getResourceDemand() {
        return resourceDemand;
    }

    public FieldElement getWorkload() {
        return getResourceDemand().div(new DoubleValue(slot.getDuration().doubleValue()).mult(new DoubleValue(this.capacity.doubleValue())));
    }

    @Override
    public int compareTo(Bin o) {
        return this.slot.getFromWhen().compareTo(o.slot.getFromWhen());
    }
}
