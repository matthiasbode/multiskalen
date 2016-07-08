package org.util;

import java.util.LinkedHashSet;

/**
 * Die Klasse "VertexContainer" beschreibt einen Knoten in einem Graphen. Ein
 * Knoten hat eine Liste von direkten Vorgängern und eine Liste von direkten
 * Nachfolgern. Des Weiteren können Knoten eine Bezeichnung haben.
 */
public class VertexContainer<E> implements Cloneable {

    private static int counter;
    private final int id;
    private LinkedHashSet<E> successors, predecessors;

    public VertexContainer() {
        id = counter++;
        successors = new LinkedHashSet<E>();
        predecessors = new LinkedHashSet<E>();
    }

    public boolean addPredecessor(E node) {
        return predecessors.add(node);
    }

    public LinkedHashSet<E> getPredecessors() {
        return predecessors;
    }

    public boolean addSuccessor(E node) {
        return successors.add(node);
    }

    public LinkedHashSet<E> getSuccessors() {
        return successors;
    }

    @Override
    public VertexContainer<E> clone() {
        VertexContainer<E> res = new VertexContainer();
        res.successors = new LinkedHashSet(this.successors);
        res.predecessors = new LinkedHashSet(this.predecessors);
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        VertexContainer container = (VertexContainer) obj;
        if (id == container.id) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        return hash;
    }
}