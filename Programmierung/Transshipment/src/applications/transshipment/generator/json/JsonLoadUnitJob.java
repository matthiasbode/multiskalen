/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.json;

import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.loadunits.Swapbody;
import applications.transshipment.model.loadunits.TwistLockLoadUnit;

/**
 *
 * @author behrensd
 */
public class JsonLoadUnitJob {

    private String typ;
    private double length;
    private JsonStorage origin;
    private JsonStorage destination;
    private boolean hazardous;
    private String id;

    public JsonLoadUnitJob() {
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public JsonStorage getOrigin() {
        return origin;
    }

    public void setOrigin(JsonStorage origin) {
        this.origin = origin;
    }

    public JsonStorage getDestination() {
        return destination;
    }

    public void setDestination(JsonStorage destination) {
        this.destination = destination;
    }

    public boolean isHazardous() {
        return hazardous;
    }

    public void setHazardous(boolean hazardous) {
        this.hazardous = hazardous;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonLoadUnitJob(LoadUnitJob job) {

        LoadUnit lu = job.getLoadUnit();
        this.id = lu.getID();
        if (lu instanceof Swapbody) {
            this.typ = "Swapbody";
        } else if (lu instanceof TwistLockLoadUnit) {
            this.typ = "TwistLockLoadUnit";
        }
        this.hazardous = lu.isHazardous();
        this.length = lu.getLength();

        this.origin = new JsonStorage(lu.getOrigin());
        this.destination = new JsonStorage(lu.getDestination());

    }

    @Override
    public String toString() {
        return "JsonLoadUnitJob{" + "typ=" + typ + ", length=" + length + ", origin=" + origin + ", destination=" + destination + ", hazardous=" + hazardous + ", id=" + id + '}';
    }

    
    
}
