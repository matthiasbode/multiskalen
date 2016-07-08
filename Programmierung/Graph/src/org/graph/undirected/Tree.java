package org.graph.undirected;

import org.graph.undirected.SimpleGraph;
import org.util.Pair;


/**
 * Die Klasse Tree beschreibt einen Baum, also einen einfachen azyklischen 
 * Graphen mit einfachem Zusammenhang. Ein Baum mit n Knoten besitzt 
 * genau n-1 ungerichtete Kanten. 
 * 
 * @author bode
 */
public class Tree<V> extends SimpleGraph<V> {
 
    
    public Tree(V start) {
        super();
        super.addVertex(start);
    }

    public boolean addConnection(V vertex, V parent) {
        if (!this.vertices.contains(parent)) {
            System.err.println("Parent nicht im Baum enthalten. "
                    + "Ein Baum muss zusammenhängend sein!");
            return false;
        }
        if (this.vertices.contains(vertex)) {
            System.err.println("Durch hinzufügen dieser Beziehung wäre der "
                    + " Graph nicht mehr azyklisch! " + vertex + " <-> " + parent);
            return false;
        } else {
            super.addVertex(vertex);
            //Da einfacher Graph, wird entgegengesetzte Kante auch hinzugefügt
            super.addEdge(new Pair<V, V>(vertex, parent));
            return true;
        }
    }
    
    
    @Override
    public boolean addEdge(Pair<V, V> edge) {
        throw new UnsupportedOperationException("Innerhalb der Klasse Tree kann eine "
                + " Kante nur über die addConnection-Methode hinzugefügt werden");
    }

    @Override
    public boolean addVertex(V node) {
        throw new UnsupportedOperationException("Innerhalb der Klasse Tree kann ein "
                + " Knoten nur über die addChild-Methode hinzugefügt werden");
    }

    @Override
    public boolean removeVertex(V node) {
        /**
         * Hat ein Knoten mehr als einen Nachbarn und man entfernt ihn, 
         * würde der einfache Zusammenhang nicht mehr bestehen.
         */
        if (getNeighbours(node).size() == 1) {
            return super.removeVertex(node);
        } else {
            System.err.println("Knoten "+node+" konnte nicht entfernt werden, da sonst "
                    + "der einfache Zusammenhang nicht mehr bestehen würde");
            return false;
        }
    }

    @Override
    public String toString() {
        return "Tree{" + super.toString() + '}';
    }
}
