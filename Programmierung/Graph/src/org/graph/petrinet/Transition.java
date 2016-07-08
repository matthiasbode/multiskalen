package org.graph.petrinet;
 
/**
 * Eine Transition repraesentiert ein Ereignis oder eine Informationswandlung
 * eines Petrinetzes.
 *  <p><strong>Version: </strong> <br><dd>1.0, November 2006</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Prof. Dr.-Ing. Markus Koenig, Dipl.-Ing. Felix Hofmann</dd></p>   
 * @author  Markus Koenig, Felix Hofmann
 * @version 1.0 
 */
public class Transition implements Comparable<Transition> {
    /**
     * static counter for giving each transition an unique id.
     */
    protected static int counter = 1;
    
    
    /**
     * unique identifier of this transition
     */
    protected final int id;
    
    
    /**
     * 
     */
    protected String name;
    
    /**
     * 
     */
    private int priority;
    

    /**
     * State of the transition.
     * <ul>
     *  <li> ENABLED: A transition is enabled, if the preset of this
     * transition contains at least as many tokens as required by the
     * weights of the incoming edges.
     * <li> ACTIVE: A transition is active, if it was enabled and was fired by
     * the petri net. This state is only reached in timed petri nets.
     * <li> COMPLETED: If the firing ended.
     * <li> DISABLED: When not enough token are in the preset
     * </ul>
     * 
     */
    public enum State {
        ACTIVE, ISFIRING, COMPLETED, INACTIVE
    }
    
    
    /**
     * 
     */
    private State state = State.INACTIVE;
    
    /**
     * Erzeugt eine Transition mit dem uebergebenen Objekt.
     * @param obj Objekt der Transition
     */
    public Transition() {
        this.id = counter++;
        this.priority = 0;
    }

    
    public Transition(String name) {
        this.id = counter++;
        this.priority = 1;
        this.name = name;
    }
    
    
    public Transition(int priority) {
        this.id = counter++;
        this.priority = priority;
    }

    
    public Transition(String name, int priority) {
        this.id = counter++;
        this.name = name;
        this.priority = priority;
    }
    
    
    public int getPriority() {
        return priority;
    }

    
    public void setPriority(int priority) {
        this.priority = priority;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public State getState() {
        return state;
    }

    
    public void setState(State state) {
        this.state = state;
    }
    
    
    public boolean isCompleted() {
        return state.equals(State.COMPLETED);
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transition other = (Transition) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
    

    /**
     * Vergleicht diese Transition mit einer anderen Transition anhand ihrer
     * Priorität.
     *
     * @return  0 - wenn die beiden Transitionen die gleiche Priorität besitzen
     *         -1 - wenn diese Transition eine kleinere Priorität 
     *              als die uebergebene Transition besitzt
     *         +1 - wenn diese Transition eine grössere Priorität 
     *              als die uebergebene Transition besitzt
     */
    @Override
    public int compareTo(Transition trans) {
        if(this.priority > trans.priority)
            return -1;
        if(this.priority < trans.priority)
            return +1;
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }  
}