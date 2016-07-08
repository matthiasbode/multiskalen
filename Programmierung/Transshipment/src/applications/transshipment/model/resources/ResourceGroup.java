/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.resources.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import math.FieldElement;
import math.LongValue;

/**
 *
 * Klasse fasst mehrere Ressourcen zu einer Gruppe zusammen. Die Methoden werden
 * dann an die einzelnen Ressourcen weitergeleitet.
 *
 * @author hofmann, wolf
 */
public class ResourceGroup<E extends Resource> implements Resource {

    protected List<E> group;
    private final int id;
    private static int idCounter = 0;

    public ResourceGroup() {
        id = idCounter++;
        group = new ArrayList<E>();
    }

    public ResourceGroup(List<? extends E> resources) {
        this();
        group.addAll(resources);
    }

    public boolean contains(E e) {
        return group.contains(e);
    }

    public boolean add(E e) {
        return group.add(e);
    }

    public boolean addAll(Collection<? extends E> e) {
        return group.addAll(e);
    }

    public boolean remove(E e) {
        return group.remove(e);
    }

    public boolean removeAll(Collection<E> e) {
        return group.removeAll(e);
    }

    public List<E> getResources() {
        return group;
    }

    public String getID() {
        return this.getClass().getSimpleName() + "-" + id;
    }

    /**
     * Diese methode uebergibt jeder Ressource der Resourcegroup den Timeslot
     * timeslot.
     *
     * @param timeSlot, gibt die TemporalAvailability an, die in jeder Ressource
     * aus Resourcegroup mit dieser Methode gesetzt wird.
     */
    @Override
    public void setTemporalAvailability(TimeSlot timeSlot) {
        for (Resource r : group) {
            r.setTemporalAvailability(timeSlot);
        }
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        E next = group.iterator().next();
        return next.getTemporalAvailability().clone();
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        for (Resource r : group) {
            r.setTemporalAvailability(tempAvail);
        }
    }

    public List<E> getGroup() {
        return group;
    }

}
