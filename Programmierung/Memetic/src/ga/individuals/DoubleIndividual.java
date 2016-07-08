package ga.individuals;

import ga.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Peter Milbradt, Mario Hoecker
 */
public class DoubleIndividual extends Individual<Double> {

    /**
     * Creates a new instance of DoubleCoding
     */
    public static DoubleIndividual createIndividual(int num, double minValue, double maxValue) {
        ArrayList<Double> res = new ArrayList<>(num);

        for (int i = 0; i < num; i++) {
            res.add(minValue + Parameters.getRandom().nextDouble() * (maxValue - minValue));  // Pick from range
        }
        return new DoubleIndividual(res);
    }

    public DoubleIndividual(List<Double> chromosome) {
        this.chromosome = chromosome;
    }

    public DoubleIndividual(Double[] chromosome) {
        this.chromosome = Arrays.asList(chromosome);
    }

    public double getValueAt(int i) {
        return chromosome.get(i);
    }

    @Override
    public DoubleIndividual clone() {
        DoubleIndividual d = new DoubleIndividual(new ArrayList(chromosome));
        return d;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.chromosome);
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
        final DoubleIndividual other = (DoubleIndividual) obj;
        if (!Objects.equals(this.chromosome, other.chromosome)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.valueOf(this.chromosome.get(0)));

        for (int i = 1; i < this.chromosome.size(); i++) {
            sb.append(" ").append(this.chromosome.get(i));
        }

        return sb.toString();
    }
}
