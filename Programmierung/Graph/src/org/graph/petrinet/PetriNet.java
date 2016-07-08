package org.graph.petrinet;

import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import org.graph.weighted.WeightedDirectedBipartiteGraph;
import org.util.Pair;

/**
 *
 * @author Nils Rinke
 */
public abstract class PetriNet<W> extends WeightedDirectedBipartiteGraph<Object, W> {
    /**
     * 
     */
    protected ArrayList<Event> currentEvents;
    
    /**
     * 
     */
    protected PriorityQueue<Transition> activeTransitions;
    
    /**
     * 
     */
    public PetriNet() {
        super(Predicates.instanceOf(Place.class),
              Predicates.instanceOf(Transition.class));
        currentEvents = new ArrayList<Event>();
        activeTransitions = new PriorityQueue<Transition>();
    }
    
    
    /**
     * @inheritDoc 
     */
    @Override
    public boolean addVertex(Object vertex) {
        if(vertex instanceof Place)
            placeAdded((Place)vertex);
        return super.addVertex(vertex);
    }
    
    
    /**
     * Methode, in der jedes Petri-Netz festlegen muss, was beim
     * Hinzufuegen einer Stelle erfolgen muss.
     * Beispiel: Ein P/T-Netz muss fuer jede Stelle eine Kapazitaet und Liste
     * zum Abspeichern der Token bereitstellen. Diese Methode wird in der
     * addVertex-Methode aufgerufen.
     * 
     * @param place 
     */
    protected abstract void placeAdded(Place place);
    
        
    
    /**
     * Abstrakte Methode zum Hinzufuegen einer Marke zu einer Stelle.
     * 
     * @param token
     * @param place
     * @return 
     */
    public abstract boolean addToken(Token token, Place place);
    
    
    /**
     * Abstrakte Methode zum Entfernen einer Marke von einer Stelle.
     * 
     * @param token
     * @param p
     * @return 
     */
    public abstract boolean removeToken(Token token, Place p);
    
    
    /**
     * Abstrakte Methode, welche ein Event beim Schalten einer Transition
     * erzeugt.
     * <p>
     * Wichtig: Ein Event muss am Ende der Methode der Liste
     * currentEvents hinzugefuegt werden.
     * 
     * @param trans
     * @return 
     */
    public abstract Event fireTransition(Transition trans);
    
    
    /**
     * 
     * @param event 
     */
    public abstract void releaseEvent(Event event);
    
    
    /**
     * Abstrakte Methode, welche ueberprueft, ob eine Transition
     * aktiv ist oder nicht.
     * 
     * @param trans
     * @return 
     */
    protected abstract boolean isActive(Transition trans);
    
    
    /**
     * Diese Methode ueberprueft den Status aller Transitionen, welche eine
     * Verbindung mit der uebergebenen Stelle haben. Diese Methode soll dann
     * genutzt werden, wenn eine Marke hinzugefuegt/entfernt wird, wenn
     * Marken zum Schalten einer Transition entnommen oder beim Loesen eines
     * Events hinzugefuegt werden.
     * 
     * @param place 
     */
    protected void checkAllTouchingTransitions(Place place) {
        for (Object object : getSuccessors(place)) {
            Transition trans = (Transition) object;
            if(trans.getName().equals("n4"))
                System.out.println("stop");
            if(trans.getState().equals(Transition.State.ISFIRING))
            {}
            else if (isActive(trans)) {
                if(!activeTransitions.contains(trans)) {
                    trans.setState(Transition.State.ACTIVE);
                    activeTransitions.offer(trans);
                    System.out.println(trans + " auf active gesetzt");
                }
            }       
            else if(!trans.getState().equals(Transition.State.INACTIVE)){
                trans.setState(Transition.State.INACTIVE);
                if(activeTransitions.contains(trans)) {
                    activeTransitions.remove(trans);
                }
            }
        }
        for (Object object : getPredecessors(place)) {
            Transition trans = (Transition) object;
            if(trans.getState().equals(Transition.State.ISFIRING)){}
            else if (isActive(trans)) {
                if(!activeTransitions.contains(trans)) {
                    trans.setState(Transition.State.ACTIVE);
                    activeTransitions.offer(trans);
                }
            }
            
            else if(!trans.getState().equals(Transition.State.INACTIVE)){
                trans.setState(Transition.State.INACTIVE);
                if(activeTransitions.contains(trans)) {
                    activeTransitions.remove(trans);
                }
            }
        }
    }
    
    
    /**
     * Methode, welche den Status aller Transitionen ueberprueft.
     */
    public void checkAllTransitionsIfActive() {
        for (Object object : getVertices(v2_predicate)) {
            Transition trans = (Transition) object;
            if (isActive(trans)) {
                trans.setState(Transition.State.ACTIVE);
                activeTransitions.offer(trans);
            } else {
                trans.setState(Transition.State.INACTIVE);
                activeTransitions.remove(trans);
            }
        }
    }
    
    
    /**
     * Diese Methode ueberpreuft alle aktuellen Events, ob diese beendet sind.
     * Wenn ein Event beendet ist, werden die Marken wieder ins Petri-Netz
     * gesetzt {@code releaseEvent(Event event)} und aus der
     * Liste der aktuellen Events entfernt. 
     */
    public void releaseCompletedEvents() {
        ArrayList<Event> completedEvents = new ArrayList<Event>();
        for (Event event : currentEvents) {
            if(event.isCompleted()) {
                releaseEvent(event);
                completedEvents.add(event);
            }
        }
        currentEvents.removeAll(completedEvents);
    }
    
    
    /**
     * 
     * @return 
     */
    public PriorityQueue<Transition> getActiveTransitions() {
        return activeTransitions;
    }
    
    
    /**
     * 
     * @return 
     */
    public boolean hasActiveTransitions() {
        return !activeTransitions.isEmpty();
    }
    
    
    /**
     * Gibt den Vorbereich der uebergebenen Transiton zurueck.
     * 
     * @param transition
     * @return 
     */
    public Collection<Place> getPreset(Transition transition) {
        Collection<Place> preset = new HashSet<Place>();
        for (Object object : getPredecessors(transition))
            preset.add((Place)object);
        return preset;
    }
    
    
    /**
     * Gibt den Nachbereich der uebergebenen Transiton zurueck.
     * 
     * @param transition
     * @return 
     */
    public Collection<Place> getPostset(Transition transition) {
        Collection<Place> postset = new HashSet<Place>();
        for (Object object : getSuccessors(transition))
            postset.add((Place)object);
        return postset;
    }
    
    public Set<Transition> getTransitions() {
        Set<Transition> ret = new HashSet<Transition>();
        for (Object object : getVertices(Predicates.instanceOf(Transition.class))) {
            Transition trans = (Transition) object;
            ret.add(trans);
        }
        return ret;
    }
    
    
    

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(this.getClass().getName() + ": ({");
        buf.append("(Places:{");
        Iterator<Object> v_it = getVertices(v1_predicate).iterator();

        if (v_it.hasNext()) {
            buf.append(v_it.next());
        }
        for (; v_it.hasNext();) {
            buf.append(", ").append(v_it.next());
        }
        buf.append("}, Transitions:{");
        v_it = getVertices(v2_predicate).iterator();

        if (v_it.hasNext()) {
            buf.append(v_it.next());
        }
        for (; v_it.hasNext();) {
            buf.append(", ").append(v_it.next());
        }
        buf.append("}; E1={");
        Iterator<Pair<Object,Object>> e_it = getEdges(v1_predicate, v2_predicate).iterator();
        if (e_it.hasNext()) {
            Pair edge = e_it.next();
            buf.append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        for (; e_it.hasNext();) {
            Pair edge = e_it.next();
            buf.append(", ").append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        buf.append("}, E2={");
        e_it = getEdges(v2_predicate, v1_predicate).iterator();
        if (e_it.hasNext()) {
            Pair edge = e_it.next();
            buf.append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        for (; e_it.hasNext();) {
            Pair edge = e_it.next();
            buf.append(", ").append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        buf.append("})");
        return buf.toString();
    }
}