package org.graph.petrinet.anonymous;

import org.graph.petrinet.Token;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.graph.petrinet.Event;
import org.graph.petrinet.PetriNet;
import org.graph.petrinet.Place;
import org.graph.petrinet.Transition;
import org.graph.petrinet.TokenType;

/**
 * A condition/event net (C/E net) is a petri net with the following characteristics:
 * <ul>
 * <li> a CENet net is a bipartite graph, with the two disjoint sets named places and transitions,
 *      where a place represent a condition and a transition represent an event.</li>
 * <li> every edge from a place to transition means, that a token on a
 *      place is required to activate the transition</li>
 * <li> every edge from a transition to a place means, that the firing of this
 *      transition causes, that the the condition which is represented by the 
 *      place is complied.</li>
 * <li> a map M0, which assigns an initial marking to every place, in this case
 *       that the condition, represented by the place is complied.</li>
 * </ul>
 * @author Nils Rinke
 * @version 1.0
 */
public class CENet extends PetriNet<Boolean> {

    protected final Map<Place, Token> tokentoPlaceMap;
    
    public CENet() {
        tokentoPlaceMap = new HashMap<Place, Token>();
    }
    
    
    @Override
    public boolean addToken(Token token, Place place) {
        if(tokentoPlaceMap.containsKey(place))
            return false;
        tokentoPlaceMap.put(place, token);
        checkAllTouchingTransitions(place);
        return true;
    }
    
    
    @Override
    public boolean removeToken(Token token, Place place) {
        if(!tokentoPlaceMap.containsKey(place))
            return false;
        tokentoPlaceMap.remove(place);
        checkAllTouchingTransitions(place);
        return true;
    }
    
    
    public boolean containsToken(Place place) {
        if(tokentoPlaceMap.containsKey(place))
            return true;
        return false;
    }
    
    @Override
    public boolean isActive(Transition trans) {
        for (Place p : getPreset(trans))
            if (!tokentoPlaceMap.containsKey(p))
                return false;
        for (Place p : getPostset(trans))
            if (tokentoPlaceMap.containsKey(p)) 
                return false;
        return true;
    }

    
    /**
     * Schaltet die Transition, wenn moeglich. Es werden Marken von den
     * Vorgaenger-Stellen zu den Nachfolgern transportiert.
     * @param transition Transition, die geschaltet werden soll
     * @return <tt>true</tt> wenn die Transiton geschaltet wurde
     */
    @Override
    public synchronized Event fireTransition(Transition transition) {
        if (!isActive(transition)) {
            return null;
        }

        HashMap<TokenType, LinkedList<Token>> token =
                                    new HashMap<TokenType, LinkedList<Token>>();
        token.put(TokenType.standardType, new LinkedList<Token>()); 
        /*
         * Zunaechst werden im Vorbereich der Transition alle Marken entfernt.
         */
        for (Place p : getPreset(transition)) {
            token.get(TokenType.standardType).add(tokentoPlaceMap.remove(p));
        }
        transition.setState(Transition.State.ISFIRING);
        Event event = new Event(transition, token);
        currentEvents.add(event);
        return event;
    }

    @Override
    protected void placeAdded(Place place) {
        
    }

    @Override
    public void releaseEvent(Event event) {
        for (Place place : getPostset(event.getTransition())) {
            tokentoPlaceMap.put(place, new Token());
        }
        for (Place p : getPostset(event.getTransition())) {
            checkAllTouchingTransitions(p);
        }
    }
}
