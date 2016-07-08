
package org.graph.petrinet.coloured;

import java.util.ArrayList;
import org.graph.petrinet.TokenType;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import org.graph.petrinet.Event;
import org.graph.petrinet.Transition;
import org.graph.petrinet.PetriNet;
import org.graph.petrinet.Place;
import org.graph.petrinet.Token;
import org.util.Pair;


/**
 * Petri-Netze eignen sich zur Beschreibung dynamischer Systeme, 
 * die eine feste Grundstruktur besitzen. Ein Petri-Netz ist ein gerichteter
 * bipartiter Graph. Die beiden Knotenmengen heissen Stellen (Places) und 
 * Transitionen (Transitions). Eine Stelle entspricht einer Zwischenablage fuer
 * Daten, eine Transition beschreibt die Verarbeitung von Daten.
 * Diese Klasse "CPetriNet" bildet ein solches Places-Transitions-Petrinet ab.
 * Die Stellen und Transitionen der Klasse enthalten zur Identifizierung Objekte.
 * Diese Objekte muessen eindeutig sein, heisst: jedes Objekt darf nur einmal verwendet werden.
 *                         
 * Definition: Ein Stellen-Transitionen-System besteht aus einem bipartiten Graphen,
 *             einer Abbildung K, die jeder Stelle eine Kapazitaet zuordnet, 
 *             einer Abbildung W, die jeder Kante ein Kantengewicht zuordnet und
 *             einer Abbildung M, die jeder Stelle eine Anfangsmarkierung zurodnet.
 *  <p><strong>Version: </strong> <br><dd>1.0, November 2006</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Prof. Dr.-Ing. Markus Koenig, Dipl.-Ing. Felix Hofmann</dd></p>   
 * @author  Markus Koenig, Felix Hofmann
 * @version 1.0
 */
public class CPetriNet extends PetriNet<HashMap<TokenType,Integer>> {

    /**
     * 
     */
    protected final Map<Place, CPTPlaceContainer> placeToContainerMap;
    
    
    /**
     * 
     */
    public ArrayList<TokenType> types;
    
    
    /**
     * Erzeugt ein leeres Stellen-Transitionen-System (CPetriNet).
     */
    public CPetriNet() {
        types = new ArrayList<TokenType>();
        placeToContainerMap = new HashMap<Place, CPTPlaceContainer>();
        addTokenType(TokenType.standardType);
    } 
 
        
    /**
     * Prueft ob die uebergebene Transition aktiv ist und somit geschaltet werden kann.
     * @param transition Transition
     * @return <tt>true</tt> wenn die Transiton aktiv ist
     */
    @Override
    public boolean isActive(Transition transition) {
        if (transition == null) return false;
        if(transition.getState().equals(Transition.State.ISFIRING))
            return false;
        //man koennte noch die Konsistenz der TokenCPN ueberpruefen
        for (Place p : getPreset(transition)) {
            CPTPlaceContainer container = placeToContainerMap.get(p);
            for (TokenType type : types)
                if(container.activeTokens(type) < getEdgeWeight(p, transition).get(type))
                    return false;
        }
        
        for (Place p : getPostset(transition)) {
            CPTPlaceContainer container = placeToContainerMap.get(p);
            for (TokenType type : types)
                if(container.leftCapacity(type) < getEdgeWeight(transition, p).get(type))
                    return false;
        }
        return true;
    }
    

    protected boolean isConsistent(Transition transition) {
        if (transition == null) return false;
        int in = 0;
        int out = 0;

        for (Place p : getPreset(transition)) {
            for (TokenType type : types)
                in += getEdgeWeight(p, transition).get(type);
        }
        
        for (Place p : getPostset(transition)) {
            for (TokenType type : types)
                out += getEdgeWeight(transition, p).get(type);
        }
        if(in != out)
            return false;
        return true;
    }
    
    
    
    /**
     * Schaltet die Transition, wenn moeglich. Es werden Marken von den
     * Vorgaenger-Stellen zu den Nachfolgern transportiert.
     * @param trans Transition, die geschaltet werden soll
     * @return <tt>true</tt> wenn die Transiton geschaltet wurde
     */
    @Override
    public synchronized Event fireTransition(Transition trans) {
        //bisher nur Konsistente Transitionen, da die neu-erzeugung schwierig ist
        if (!isActive(trans) /*|| !isConsistent(trans)*/)
            return null;
        trans.setState(Transition.State.ISFIRING);
        /*
         * von den Vorgaenger-Stellen werden Token weggenommen
         * und in der Liste tokenQueue gespeichert.
         * Danach werden diese in gleicher Reihenfolge zu
         * den Nachfolgern geschickt.
         *
         * Sind die beiden Mengen inkonsistent, soll heissen,
         * es existieren zu viele oder zu wenige Marken,
         * dann werden entsprechend viele Marken neu
         * erstellt oder vernichtet.
         */
        HashMap<TokenType, LinkedList<Token>> transfer =
                                    new HashMap<TokenType, LinkedList<Token>>();
        for (TokenType type : types)
           transfer.put(type, new LinkedList<Token>());

        for (Place p : getPreset(trans)) {
            CPTPlaceContainer cptc = placeToContainerMap.get(p);
            for (TokenType type : types)
                transfer.get(type).addAll(cptc.getTokens(type, 
                                            getEdgeWeight(p, trans).get(type)));
        }
        for (Place p : getPreset(trans)) {
            checkAllTouchingTransitions(p);
        }
        Event event = new Event(trans, transfer);
        currentEvents.add(event);
        return event;
    }
    
    
    @Override
    public boolean addEdge(Pair<Object, Object> edge) {
        boolean added = super.addEdge(edge);
        if(added) {
            HashMap<TokenType,Integer> weight = new HashMap<TokenType, Integer>();
            for (TokenType tokenType : types) {
                weight.put(tokenType, 0);
            }
            setEdgeWeight(edge, weight);
        }
        return added;
    }
    

    @Override
    public boolean addEdge(Object first, Object second) {
        return this.addEdge(new Pair<Object,Object>(first, second));
    }
    

    public boolean addTokenType(TokenType type) {
        boolean added;
        if(!types.contains(type)) {
            types.add(type);
            added = true;
        } else
            added = false;
        if(added) {
            for (Pair<Object, Object> edge : edgeSet()) {
                getEdgeWeight(edge).put(type, 0);
            }
            for (Object object : getVertices(v1_predicate)) {
                Place p = (Place) object;
                CPTPlaceContainer container = placeToContainerMap.get(p);
                container.capacityPerType.put(type, 0);
                container.tokensPerType.put(type, new HashSet<Token>());
            }
        }
        return added;
    }
    
    
    public boolean removeTokenType(TokenType type) {
        boolean removed = types.remove(type);
        if(removed) {
            for (Pair<Object, Object> edge : edgeSet()) {
                getEdgeWeight(edge).remove(type);
            }
            for (Object object : getVertices(v1_predicate)) {
                Place p = (Place) object;
                CPTPlaceContainer container = placeToContainerMap.get(p);
                container.capacityPerType.remove(type);
                container.tokensPerType.remove(type);
            }
        }
        return removed;
    }
    
    
    /**
     * Gibt die Kapazitaet der uebergebenen Stelle zurueck.
     * 
     * @param place
     * @return 
     */
    public int getCapacity(Place place, TokenType type) {
        CPTPlaceContainer cpc = placeToContainerMap.get(place);
        return cpc.capacityPerType.get(type);
    }
    
    
    /**
     * Setzt die Kapazitaet der uebergebenen Stelle.
     * 
     * @param place
     * @return 
     */
    public void setCapacity(Place place, TokenType type, int capacity) {
        CPTPlaceContainer cpc = placeToContainerMap.get(place);
        cpc.capacityPerType.put(type, capacity);
    }
    
    
    /**
     * Gibt die Anzahl der Token auf der uebergebenen Stelle zurueck.
     * 
     * @param place
     * @return 
     */
    public int getNumberOfTokens(Place place, TokenType type) {
        CPTPlaceContainer cpc = placeToContainerMap.get(place);
        return cpc.tokensPerType.get(type).size();
    }
    
    public Set<Token> getTokens(Place place, TokenType type) {
        CPTPlaceContainer cpc = placeToContainerMap.get(place);
        return cpc.tokensPerType.get(type);
    }
    

    public ArrayList<TokenType> getTokenTypes() {
        return types;
    }
    
    
    @Override
    protected void placeAdded(Place place) {
        if(!containsVertex(place)) {
            CPTPlaceContainer container = new CPTPlaceContainer();
            for (TokenType type : types) {
                container.capacityPerType.put(type, 0);
                container.tokensPerType.put(type, new HashSet<Token>());
            }
            placeToContainerMap.put(place, container);
        }
    }


    @Override
    public void releaseEvent(Event event) {
        Transition trans = event.getTransition();
        for (Place p : getPostset(trans)) {
            CPTPlaceContainer ptc = placeToContainerMap.get(p);
            for (TokenType type : types)
                for (int i = 0; i < getEdgeWeight(trans, p).get(type); i++)
                    ptc.addToken(event.getToken().get(type).pollFirst());
        }
        for (Place p : getPostset(trans)) {
            checkAllTouchingTransitions(p);
        }
        trans.setState(Transition.State.INACTIVE);
    }

    @Override
    public boolean addToken(Token token, Place place) {
        CPTPlaceContainer cptc = placeToContainerMap.get(place);
        boolean added = cptc.addToken(token);
        if(added) {
            checkAllTouchingTransitions(place);
        }
        return added;
    }

    @Override
    public boolean removeToken(Token token, Place p) {
        CPTPlaceContainer cptc = placeToContainerMap.get(p);
        boolean removed = cptc.removeToken(token);
        if(removed)
            checkAllTouchingTransitions(p);
        return removed;
    }

    public void setNumberOfToken(Place place, TokenType type, int numberOfToken) {
        CPTPlaceContainer cptc = placeToContainerMap.get(place);
        cptc.tokensPerType.get(type).clear();
        for (int i = 0; i < numberOfToken; i++)
            addToken(new Token(type, 0), place);
    }

    
    protected class CPTPlaceContainer {

        /**
         * Liste der Tokens auf dieser Stelle
         */
        protected final HashMap<TokenType, HashSet<Token>> tokensPerType;
        
        /**
         * Kapazitaet dieser Stelle (max. Anzahl Tokens)
         */
        protected final HashMap<TokenType, Integer> capacityPerType;


        /**
         * Erzeugt eine Stelle mit dem uebergebenen Objekt und der
         * angegebenen Kapazitaet
         */
        CPTPlaceContainer() {
            tokensPerType   = new HashMap<TokenType, HashSet<Token>>();
            capacityPerType = new HashMap<TokenType, Integer>();
        }

        /**
         * fuegt dieser Stelle den uebergebenen Token hinzu, wenn die
         * Restkapazitaet das zulaesst.
         * @return true, falls der Token hinzugefuegt werden konnte, sonst false
         */
        boolean addToken(Token token) {
            if (leftCapacity(token.getType()) < 1) {
                return false; //inaktive Token werden nicht gezaehlt
            }
            return tokensPerType.get(token.getType()).add(token);
        }

        /**
         * enfernt den uebergebenen Token, falls er auf dieser Stelle liegt. 
         * @return true, falls der Token entfernt werden konnte, sonst false
         */
        boolean removeToken(Token token) {
            return tokensPerType.get(token.getType()).remove(token);
        }

        /**
         * prueft, ob der uebergebene Token auf dieser Stelle liegt.
         */
        boolean containsToken(Token token) {
            return tokensPerType.get(token.getType()).contains(token);
        }

        
        /**
         * liefert die Anzahl der aktiven Token dieser Stelle.
         *@return Anzahl der aktiven Token dieser Stelle
         */
        int activeTokens(TokenType type) {
            int i = 0;
            for (Token t : tokensPerType.get(type)) {
                if (t.isActive()) {
                    i++;
                }
            }
            return i;
        }
        

        /** liefert die Restkapazitaet dieser Stelle. Inaktive Token benoetigen keine Kapazitaet.
         * Ergo: Restkapazitaet = Gesamtkapazitaet - Anzahl aktive Tokens.
         *@return Restkapazitaet dieser Stelle
         */
        int leftCapacity(TokenType type) // inaktive Token werden nicht beruecksichtigt
        {
            return capacityPerType.get(type) - activeTokens(type);
        }

        /**
         * Diese Methode liefert eine Menge mit der angegebenen Anzahl an aktiven Tokens.
         *Diese Tokens werden von der Stelle entfernt und muessen somit vorher ausreichend vorhanden sein.
         *Das sollte ueberprueft werden, da sonst <code>null</code> zurueckgegeben wird.*/
        public PriorityQueue<Token> getTokens(TokenType type, int amount) {
            if (amount == 0) {
                return null;
            }
            if (amount > activeTokens(type)) {
                System.out.println("Not enough tokens");
                // dieser Fehler sollte nicht auftreten koennen
                return null;
            }

            PriorityQueue<Token> resultTokens = new PriorityQueue<Token>();
            int i = 0;

            for (Token t : tokensPerType.get(type)) {
                if (t.isActive()) {
                    resultTokens.offer(t);
                    i++;
                }
                if (i == amount) {
                    break;
                }
            }

            tokensPerType.get(type).removeAll(resultTokens);
            return resultTokens;
        }
        

        /**
         * Gibt die Menge der aktiven Tokens zurueck
         * ohne sie von der Stelle zu entfernen.
         */
        TreeSet<Token> getActiveTokens(TokenType type) {
            TreeSet<Token> res = new TreeSet<Token>();
            for (Token token : tokensPerType.get(type)) {
                if (token.isActive()) {
                    res.add(token);
                }
            }
            return res;
        }
    }
}
