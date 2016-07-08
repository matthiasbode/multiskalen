package org.graph.petrinet.anonymous;

import org.graph.petrinet.Token;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import org.graph.petrinet.Event;
import org.graph.petrinet.PetriNet;
import org.graph.petrinet.Place;
import org.graph.petrinet.TokenType;
import org.graph.petrinet.Transition;
import org.util.Pair;


/**
 * A place/transition net (P/T net) is a petri net with the following characteristics:
 * <ul>
 *  <li> a P/T net is a bipartite graph, with the two disjoint sets named places and transitions,</li>
 *  <li> a map C, which assigns a capacity (positive integer) to every place,</li>
 *  <li> a map W, which assigns the flow (integer weight) to every edge of this graph,</li>
 *  <li> a map M0, which assigns an initial marking to every place</li>
 * </ul>
 * 
 * @author rinke
 */
public class PTNet extends PetriNet<Integer> {
    /**
     * 
     */
    protected final Map<Place, PTPlaceContainer> placeToContainerMap;


    /**
     * Erzeugt ein zunaechst leeres Petri-Netz
     */
    public PTNet() {
        super();
        placeToContainerMap = new HashMap<Place, PTPlaceContainer>();
    }

    
    /**
     * Zusaetzliche Methode, welche das Hinzufuegen von Stellen mit vorher
     * bekannter Kapazitaet und Anfangsmarkierung erleichtert. Dies ist nur
     * moeglich, da das Netz nur anonyme Marken enthaelt.
     * 
     * @param place Stelle, welche dem Netz hinzugefuegt werden soll
     * @param capacity Kapazitaet dieser Stelle
     * @param amount Anzahl der Token als Anfangsmarkierung M_0.
     * @return true, wenn Stelle noch nicht im Netz enthalten ist,
     *         false, sonst.
     */
    public boolean addPlace(Place place, int capacity, int amount) {
        boolean added = addVertex(place);
        if (added) {
            setCapacity(place, capacity);
            for (int i = 0; i < amount; i++) {
                boolean add = addToken(new Token(), place);
            }
        }
        return added;
    }
    
    
    /**
     * Zusaetzliche Methode, welche das Hinzufuegen von Kanten erleichtert. Die
     * Anzahl von Marken, welche fuer das Schalten der Transition oder das
     * Einfuegen der Marken nach dem Schalten erforderlich sind, werden mit dem
     * Wert cost direkt uebergeben.
     * 
     * @param first
     * @param second
     * @param cost
     * @return true, wenn die Kante noch nicht im Netz enthalten ist,
     *         false, sonst.
     */
    public boolean addEdge(Object first, Object second, int cost) {
        boolean added = addEdge(first, second);
        if (added) {
            setEdgeWeight(new Pair(first, second), cost);
        }
        return added;
    }
    
    
    /**
     * Fuegt eine Marke der uebergebenen Stelle hinzu, wenn es noch moeglich
     * ist. Wenn die Marke hinzugefuegt wurde, wird der Status aller
     * angrenzenden Transition ueberprueft.
     * 
     * @param token
     * @param place
     * @return true, wenn die Marke der Stelle hinzugefuegt wurde. 
     */
    @Override
    public boolean addToken(Token token, Place place) {
        PTPlaceContainer ptc = placeToContainerMap.get(place);
        boolean added = ptc.addToken(token);
        if(added) {
            checkAllTouchingTransitions(place);
        }
        return added;
    }
    
    
     /**
     * Entfernt eine Marke von der uebergebenen Stelle. Wenn die Marke entfernt
     * wurde, wird der Status aller angrenzenden Transition ueberprueft.
     * 
     * @param token
     * @param place
     * @return true, wenn die Marke von der Stelle entfernt wurde. 
     */
    @Override
    public boolean removeToken(Token token, Place p) {
        PTPlaceContainer ptc = placeToContainerMap.get(p);
        boolean removed = ptc.removeToken(token);
        if(removed)
            checkAllTouchingTransitions(p);
        return removed;
    }
    
    
    /**
     * Gibt die Kapazitaet der uebergebenen Stelle zurueck.
     * 
     * @param place
     * @return 
     */
    public int getCapacity(Place place) {
        PTPlaceContainer ptc = placeToContainerMap.get(place);
        return ptc.capacity;
    }
    
    
    /**
     * Setzt die Kapazitaet der uebergebenen Stelle.
     * 
     * @param place
     * @return 
     */
    public void setCapacity(Place place, int capacity) {
        PTPlaceContainer ptc = placeToContainerMap.get(place);
        ptc.capacity = capacity;
    }
    
    
    
    /**
     * Gibt die Anzahl der Token auf der uebergebenen Stelle zurueck.
     * 
     * @param place
     * @return 
     */
    public int getNumberOfTokens(Place place) {
        PTPlaceContainer ptc = placeToContainerMap.get(place);
        return ptc.tokens.size();
    }
    
    /**
     * Gibt die Anzahl der Token auf der uebergebenen Stelle zurueck.
     * 
     * @param place
     * @return 
     */
    public void setNumberOfToken(Place place, int numberOfTokens) {
        PTPlaceContainer ptc = placeToContainerMap.get(place);
        if(numberOfTokens > ptc.capacity)
            throw new IllegalArgumentException("number of token must be less or"
                                          + "equal the capacity of this place");
        ptc.tokens.clear();
        for (int i = 0; i < numberOfTokens; i++) {
            ptc.addToken(new Token());
        }
        checkAllTouchingTransitions(place);        
    }
    

    /**
     * Ueberprueft, ob die Transition aktiv ist. Wenn die Transition
     * gerade feuert oder nicht entsprechend viele Marken / genug Kapazitaet auf
     * den Vorgaenger-/Nachfolger-Stellen vorhanden sind/ist, wird false
     * zurueck gegeben.
     * 
     * @param transition
     * @return 
     */
    @Override
    protected boolean isActive(Transition transition) {
        if(transition.getState().equals(Transition.State.ISFIRING))
            return false;
        for (Place p : getPreset(transition)) {
            PTPlaceContainer ptc = placeToContainerMap.get(p);
            if (ptc.numberOfTokens() < getEdgeWeight(p, transition)) {
                return false;
            }
        }
        for (Place p : getPostset(transition)) {
            PTPlaceContainer ptc = placeToContainerMap.get(p);
            if (ptc.leftCapacity() < getEdgeWeight(transition, p)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Schaltet die Transition, wenn moeglich. Es werden Marken von den
     * Vorgaenger-Stellen entnommen und in ein Event gespeichert.
     * 
     * @param transition Transition, die geschaltet werden soll
     * @return Das Event, welches die Transition sowie die zu transportierenden
     *         Marken enthält.
     */
    @Override
    public synchronized Event fireTransition(Transition transition) {
        if (!isActive(transition)) {
            System.out.println(transition + " ist nicht aktiv");
            return null;
        }
        //Zunaechst wird der Zustand der Transition gesetzt
        transition.setState(Transition.State.ISFIRING);
        
        /*
         * Anschließend werden von den Vorgaenger-Stellen Token weggenommen
         * und in der Liste tokens gespeichert.
         */
        HashMap<TokenType, LinkedList<Token>> tokens =
                                    new HashMap<TokenType, LinkedList<Token>>();
        
        //Es reicht nur der Type TokenType.standardType, da anonym
        tokens.put(TokenType.standardType, new LinkedList<Token>());
        
        for (Place p : getPreset(transition)) {
            PTPlaceContainer ptc = placeToContainerMap.get(p);
            if(getEdgeWeight(p, transition) > 0)
                tokens.get(TokenType.standardType).
                            addAll(ptc.getTokens(getEdgeWeight(p, transition)));
        }
        //checke alle betroffenen, ob sich ihr Zustand durch das Schalten aendert.
        for (Place p : getPreset(transition)) {
            checkAllTouchingTransitions(p);
        }
        //Erzeuge Event und fuege es denn currentEvents zu. 
        Event event = new Event(transition, tokens);
        currentEvents.add(event);
        return event;
    }
    

    @Override
    protected void placeAdded(Place place) {
        if(!containsVertex(place))
            placeToContainerMap.put(place, new PTPlaceContainer());
    }
    

    @Override
    public void releaseEvent(Event event) {
        Transition transition = event.getTransition();
        int counter = 0;
        for (Place p : getPostset(transition)) {
            PTPlaceContainer ptc = placeToContainerMap.get(p);
            for (int i = 0; i < getEdgeWeight(transition, p); i++) {
                Token t;
                if (event.getToken().get(TokenType.standardType).size() <= counter) {
                    t = new Token();
                    counter++;
                } else
                    t = event.getToken().get(TokenType.standardType).get(counter++);
                
                ptc.addToken(t);
            }
        }
        for (Place p : getPostset(transition)) {
            checkAllTouchingTransitions(p);
        }
        transition.setState(Transition.State.INACTIVE);
    }

    
    protected class PTPlaceContainer {

        /** Liste der Tokens auf dieser Stelle */
        protected final HashSet<Token> tokens;
        /** Kapazitaet dieser Stelle (max. Anzahl Tokens)*/
        protected int capacity;

        /**
         * Erzeugt eine Stelle mit dem uebergebenen Objekt.
         * @param obj Objekt der Stelle
         */
        PTPlaceContainer(int capacity) {
            this.capacity = capacity;
            tokens = new HashSet<Token>();
        }

        /**
         * Erzeugt eine Stelle mit dem uebergebenen Objekt und der angegebenen Kapazitaet */
        PTPlaceContainer() {
            tokens = new HashSet<Token>();
            this.capacity = Integer.MAX_VALUE;
        }

        /**fuegt dieser Stelle den uebergebenen Token hinzu, wenn die Restkapazitaet das zulaesst.
         * @return true, falls der Token hinzugefuegt werden konnte, sonst false */
        boolean addToken(Token token) {
            if (tokens.contains(token)) {
                return false;
            }
            if (leftCapacity() < 1) {
                return false; //inaktive Token werden nicht gezaehlt
            }
            return tokens.add(token);
        }

        /**
         * enfernt den uebergebenen Token, falls er auf dieser Stelle liegt. 
         * @return true, falls der Token entfernt werden konnte, sonst false
         */
        boolean removeToken(Token token) {
            return tokens.remove(token);
        }

        /**
         * prueft, ob der uebergebene Token auf dieser Stelle liegt.
         */
        boolean containsToken(Token token) {
            return tokens.contains(token);
        }

        /**
         * liefert die Anzahl der aktiven Token dieser Stelle.
         *@return Anzahl der aktiven Token dieser Stelle
         */
        public int numberOfTokens() {
            return tokens.size();
        }


        /** liefert die Restkapazitaet dieser Stelle. Inaktive Token benoetigen keine Kapazitaet.
         * Ergo: Restkapazitaet = Gesamtkapazitaet - Anzahl aktive Tokens.
         *@return Restkapazitaet dieser Stelle
         */
        int leftCapacity() // inaktive Token werden nicht beruecksichtigt
        {
            return capacity - numberOfTokens();
        }

        /**
         * Diese Methode liefert eine Menge mit der angegebenen Anzahl an aktiven Tokens.
         *Diese Tokens werden von der Stelle entfernt und muessen somit vorher ausreichend vorhanden sein.
         *Das sollte ueberprueft werden, da sonst <code>null</code> zurueckgegeben wird.*/
        public Collection<Token> getTokens(int amount) {
            if (amount == 0) {
                return null;
            }
            if (amount > numberOfTokens()) {
                System.out.println("Not enough tokens");
                // dieser Fehler sollte nicht auftreten koennen
                return null;
            }

            Collection<Token> resultTokens = new HashSet<Token>();
            int i = 0;

            for (Token t : tokens) {
                resultTokens.add(t);
                i++;
                if (i == amount) {
                    break;
                }
            }

            tokens.removeAll(resultTokens);
            return resultTokens;
        }
    }
}
