/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics;

import applications.mmrcsp.model.MultiModeJob;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.util.Pair;

/**
 *
 * @author Matthias
 */
public class JoNComponent<E extends MultiModeJob> extends JobOnNodeDiagramm<E> implements Cloneable {

    private static int counter = 0;
    private final JobOnNodeDiagramm<E> superGraph;
    private final int number;

    public JoNComponent(JobOnNodeDiagramm<E> superGraph) {
        this.superGraph = superGraph;
        this.number = counter++;
    }

    public JoNComponent(JobOnNodeDiagramm<E> superGraph, Collection<E> verticies, Collection<Pair<E, E>> edges) {
        super(verticies, edges);
        this.superGraph = superGraph;
        this.number = counter++;
    }

    public JobOnNodeDiagramm<E> getSuperGraph() {
        return superGraph;
    }

    @Override
    public JoNComponent<E> clone() {
        try {
            return (JoNComponent<E>) super.clone(); //To change body of generated methods, choose Tools | Templates.
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(JoNComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.number;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JoNComponent<?> other = (JoNComponent<?>) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JoNComponent{" + "number=" + number + "\t :" + this.vertices + " }";
    }

}
