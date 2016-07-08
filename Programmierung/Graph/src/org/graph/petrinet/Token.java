package org.graph.petrinet;

/**
 * Ein Token ist eine Marke, die auf einer Stelle (Place) liegen kann.
 * Marken werden durch das Schalten von Transitionen von Stellen entfernt
 * bzw. auf Stellen gelegt. Jeder Token besitzt eine intere ID, die automatisch
 * vergeben wird. Ein Token kann ein Objekt enthalten.
 * Bei zeitbehafteten Petri-Netzen wird auch noch ein Status fuer jeden Token
 * verwendet: Ein Token kann aktiv oder nicht aktiv sein.
 *  <p><strong>Version: </strong> <br><dd>1.0, November 2006</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Prof. Dr.-Ing. Markus Koenig, Dipl.-Ing. Felix Hofmann</dd></p>   
 * @author  Markus Koenig, Felix Hofmann
 * @version 1.0
 */
public class Token implements Comparable<Token>{
    /**
     * static counter for giving each token an unique id.
     */
    protected static long count = 1;
    
    
    /**
     * unique identifier of this token
     */
    protected long id;
    
    /**
     * state of the token. active or not
     */
    protected boolean active;
    
    
    /**
     * the type of the token is defined by a predicate.
     * Standard is alwaysTrue predicate from the {@link Predicates} class
     */
    protected TokenType tokenType;
    
    
    /**
     * 
     */
    private int priority;
    
    /**
     * erzeugt eine Marke mit dem uebergebenen Zustand und Objekt
     */
    public Token() {  
        this.id = count++;
        this.tokenType = TokenType.standardType;
        this.priority = 0;
        this.active = true;
    }
    
    
    /**
     * erzeugt eine Marke mit dem uebergebenen Zustand und Objekt
     */
    public Token(boolean active) {
        this();
        this.active = active;
    }

    
    public Token(TokenType tokenType, int priority) {
        id = count++;
        this.tokenType = tokenType;
        this.priority = priority;
        this.active = true;
    }

    
    public Token(boolean active, TokenType tokenType, int priority) {
        id = count++;
        this.active = active;
        this.tokenType = tokenType;
        this.priority = priority;
    }

    
    /**
     * Liefert den Zustand des Tokens zurueck.
     * @return Zustand des Tokens
     */
    public boolean isActive() {
        return active;
    }
    
    
    public TokenType getType() {
        return tokenType;
    }

    
    public void setType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    
    public int getPriority() {
        return priority;
    }

    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
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

    
    @Override
    public int compareTo(Token token) {
        if (priority <  token.priority)
            return -1;
        if (priority >  token.priority)
            return  1;
        return 0;
    }

    
    /**
     * Liefert eine Beschreibung dieser Marke in Form einer Zeichenkette zurueck.
     * Die Darstellung entspricht der ID und des Zustandes der Marke.
     * @return Zeichenkettendarstellung der Marke
     */    
    @Override
    public String toString() {
        return "#"+id;
    }
}