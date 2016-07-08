package org.graph.petrinet;

import com.google.common.base.Predicate;
import java.awt.Color;

/**
 *
 * @author Nils Rinke
 */
public class TokenType implements Predicate<Token> {

    private String id;
    
    public final static TokenType standardType = new TokenType("Standard", Color.GRAY);

    private Color color;
    
    
    public TokenType(String id) {
        this.id = id;
    }

    public TokenType(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    
    @Override
    public boolean apply(Token t) {
        if(t.getType().equals(this))
            return true;
        return false;
    }
    

    public void setColor(Color color) {
        this.color = color;
    }


    public void setName(String typeID) {
        this.id = typeID;
    }
    

    public String getName() {
        return id;
    }
    

    public Color getColor() {
        return color;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TokenType other = (TokenType) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    
    @Override
    public String toString() {
        return id;
    }
}