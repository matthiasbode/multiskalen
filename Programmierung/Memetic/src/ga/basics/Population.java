package ga.basics;

import ga.Parameters;
import ga.individuals.Individual;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import util.RandomUtilities;

/**
 * The Population is the encapsulation of the individual solutions (  <code>
 * Individual</code>s that are used to solve the problem.
 * <code>Individual</code>s may be added and deleted from this collection. This
 * also implements the following functions, which are passed on to the
 * appropriate targets: <ul> <li>Mature (or age) <li>Evaluate <li>Combine (or
 * reproduce) <li>Mutate </ul>
 *
 * @author Peter Milbradt, Mario Hoecker, Felix Hofmann
 */
public class Population<I extends Individual> implements Cloneable {

    private final Class cls;
    public String name;
    public int numberOfGenerations;
    private Collection<I> individuals = new HashSet<I>();
    public boolean reseted = false;
    
    public Comparator<I> comparator = new IndividualComparator<I>();

    /**
     * Creates a population using a given
     * <code>Collection of Individuals</code>.
     *
     * @param initialCollection a <code>Collection<Individual></code> to
     * initialize with.
     */
//    public Population(ArrayList<I> initialCollection) {
//        this.addAll(initialCollection);
//    }
//
//    public Population(int numberOfGeneration) {
//        this(new ArrayList<I>());
//        this.numberOfGenerations = numberOfGeneration;
//    }

    public Population(Class cls, int numberOfGeneration) {
        this(cls, new ArrayList<I>());
        this.numberOfGenerations = numberOfGeneration;
    }

    public Population(Class cls, Collection<I> initialCollection) {
        this.addAll(initialCollection);
        this.numberOfGenerations = 0;
        this.cls = cls;
    }

    /**
     * Removes a <code>Individual</code> from this <code>Population</code>.
     * Returns whether the removal was successful or not. Does nothing when
     * given a <code>null</code>.
     *
     * @param toRemove	the <code>Individual</code> to remove.
     * @return <code>true</code> on success; <code>false</code> otherwise;
     */
    public boolean removeIndividual(I toRemove) {
        return (toRemove == null) ? true : individuals.remove(toRemove);
    }

    /**
     * Returns a random <code>Individual</code> from this
     * <code>Population</code>.
     *
     * @return a random <code>Individual</code>.
     */
    public I getRandomIndividual() {
        int size = individuals.size();
        int randomValue = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
        return Iterables.get(individuals, randomValue);
    }

    public int size() {
        return individuals.size();
    }

    public void addAll(Collection<I> individuals) {
        for (I i : individuals) {
            if (i == null) {
                throw new IllegalArgumentException("hinzufügen von null-Individuum nicht möglich!");
            }
            boolean add = this.add(i);
//            if (!add) {
//                throw new IllegalArgumentException("hinzufügen nicht möglich!");
//            }
        }

    }

    public synchronized boolean add(I e) {
        if (e == null) {
            throw new IllegalArgumentException("hinzufügen von null-Individuum nicht möglich!");
        }
//        if (this.individuals.contains(e)) {
//            throw new IllegalArgumentException("Individuum bereits in der Population vorhanden.  " + this.cls);
//        }
        boolean add = individuals.add(e);
//        if (!add) {
//            throw new IllegalArgumentException("hinzufügen nicht möglich!");
//        }
//        return add;
        return add;
    }

    public boolean remove(I e) {
        return individuals.remove(e);
    }

    public Class getIndividualType() {
        return cls;
    }

    public double getMeanFitness() {
        double f = 0;
        for (I i : this.individuals) {
            f += i.getFitness();
        }
        return f / this.size();
    }

    /**
     * Returns the fittest <code>Individual</code> from this
     * <code>Population</code>.
     *
     * @return fittest <code>Individual</code>.
     */
    public I getFittestIndividual() {
        ArrayList<I> individualsSortedList = getIndividualsSortedList();
        return individualsSortedList.get(individualsSortedList.size() - 1);
    }

    /**
     * Sortiert die Individuen der Population so, dass das Individuum mit der
     * höchsten Fitness als letztes in der Liste zu finden ist.
     *
     * @return
     */
    public ArrayList<I> getIndividualsSortedList() {
        ArrayList<I> arrayList = new ArrayList<>(individuals);
        Collections.sort(arrayList, comparator);
        return arrayList;
    }

    public I last() {
        ArrayList<I> individualsSortedList = getIndividualsSortedList();
        return individualsSortedList.get(0);
    }

    public Collection<I> individuals() {
        return individuals;
    }

}
