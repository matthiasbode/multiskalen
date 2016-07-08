/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.individuals.subList;

import ga.individuals.Individual;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Die Codierung eines TopoSortTransshipmentIndividual erfolgt über
 * Knotenklassen und die Reihenfolge der Operationen innerhalb dieser
 * Knotenklassen.
 *
 * @author bode
 * @param <E>
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ListIndividual<E> extends Individual<SubListIndividual<E>> {
     
    public ListIndividual(List<SubListIndividual<E>> chromosome) {
        this.chromosome = chromosome;
    }

    public ListIndividual() {
        this.chromosome = new ArrayList<>();
    }

    @Override
    public ListIndividual<E> clone() {
        ArrayList<SubListIndividual<E>> res = new ArrayList<>();
        for (SubListIndividual<E> operationListIndividual : chromosome) {
            res.add(operationListIndividual.clone());
        }
        return new ListIndividual(res);
    }

    @Override
    public String toString() {
        String res = "ListIndividual{" + getNumber() + '}';
        return res;
    }

    @XmlElement(name="list")
    @Override
    public List<SubListIndividual<E>> getChromosome() {
        return this.chromosome;
    }

    @Override
    public void setChromosome(List<SubListIndividual<E>> chromosome) {
        this.chromosome = chromosome;
    }

    public Collection<E> getAllElements() {
        LinkedHashSet<E> res = new LinkedHashSet<>();
        for (SubListIndividual<E> operationListIndividual : chromosome) {
            res.addAll(operationListIndividual.getList());
        }
        return res;
    }
}
