/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.individuals.subList;

import ga.individuals.Individual;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;

/**
 *
 * @author bode
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SubListIndividual<E> extends Individual<E> {

    public SubListIndividual(List<E> list) {
        super();
        for (E e : list) {
            if (e == null) {
                throw new UnknownError("Null-Element im Konstruktor");
            }
        }
        this.chromosome = list;
    }

    public SubListIndividual() {
        super();
    }

    @Override
    public SubListIndividual<E> clone() {
        return new SubListIndividual<E>(new ArrayList<>(this.chromosome));
    }

    @Override
    public String toString() {
        return "SubListIndividual{" + this.getNumber() + '}';
    }

    @XmlElement
    @XmlList
    @Override
    public List<E> getChromosome() {
        return chromosome;
    }

    @Override
    public void setChromosome(List<E> chromosome) {
        this.chromosome = chromosome;
    }
}
