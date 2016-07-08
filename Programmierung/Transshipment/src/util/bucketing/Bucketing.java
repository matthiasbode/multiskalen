/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.bucketing;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import java.util.ArrayList;
import math.FieldElement;
import math.LongValue;
import math.function.StepFunction;

/**
 * Bietet die Möglichkeit, eine Stepfunction in eine Funktion zu überführen, die
 * integrale Größen verwendet.
 *
 * @author bode
 */
public class Bucketing {

    private String r;
    private long dt;
    private ArrayList<Bucket> buckets = new ArrayList<>();

    public Bucketing(Resource r, StepFunction function, TimeSlot ts, long dt) {
        FieldElement start = ts.getFromWhen();
        FieldElement end = ts.getUntilWhen();
        this.r = r.toString();

        for (long t = start.longValue(); t < end.longValue(); t += dt) {
            Bucket bucket = new Bucket(TimeSlot.create(t, t + dt), function.getIntegral(new LongValue(t), new LongValue(t + dt)).doubleValue());
            this.buckets.add(bucket);
        }

        this.dt = dt;
    }

    public Bucketing(String r) {
        this.r = r;
    }
    
    

    public String getResourceName() {
        return r;
    }

    public ArrayList<Bucket> getBuckets() {
        return buckets;
    }

}
