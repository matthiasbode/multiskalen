/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.individuals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 * @param <T>
 */
//@XmlSeeAlso({ModeIndividual.class, OperationListIndividual.class, VertexClassIndividual.class})
public abstract class Individual<T> extends Observable implements Cloneable {

    static int counter = 0;
    private final int number;
    protected double[] individualFitness = new double[0];
    private int rank;
    private boolean evaluated = false;
    protected List<T> chromosome;

    public HashMap<String, Object> additionalObjects = new HashMap<String, Object>();

    public Individual(T... gens) {
        this.number = counter++;
        this.chromosome = new ArrayList<>(Arrays.asList(gens));
    }

    public Individual(List<T> chromosome) {
        this.number = counter++;
        this.chromosome = chromosome;
    }

    public Individual() {
        this.number = counter++;
        this.chromosome = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public int size() {
        return chromosome.size();
    }

    public void set(int index, T element) {
        if (index < this.chromosome.size()) {
            this.chromosome.set(index, element);
        } else {
            this.chromosome.add(index, element);
        }
    }

    public T get(int index) {
        return this.chromosome.get(index);
    }

    public int indexOf(T elem) {
        return this.chromosome.indexOf(elem);
    }

    public List<T> getList() {
        return this.chromosome;
    }

    @Override
    public Individual clone() {
        try {
            return (Individual) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < chromosome.size(); i++) {
            if (chromosome.get(i) == null) {
                System.err.println("Null-Gen vorhanden!");
                System.err.println(chromosome);
            }
            hashCode += (i + 1) * chromosome.get(i).hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Individual<?> other = (Individual<?>) obj;
        return Objects.equals(this.chromosome, other.chromosome);
    }

    public List<T> getChromosome() {
        return chromosome;
    }

    public void setChromosome(List<T> chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * The last evaluated fitness for the individual is stored here and is used
     * for the <code>getFitness</code> calls.
     */
    public Double getFitness() {
        return this.individualFitness[0];
    }

    public void setFitness(double f) {
        this.individualFitness[0] = f;
    }

    public double[] getFitnessVector() {
        return this.individualFitness;
    }

    public void setFitness(double[] f) {
        this.individualFitness = f;
    }

    @Override
    public String toString() {
        return "Individual{" + "number=" + number + ", chromosome=" + chromosome + '}';
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    
}
