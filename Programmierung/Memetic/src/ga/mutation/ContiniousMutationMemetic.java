/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.mutation;

import ga.individuals.Individual;
import ga.individuals.DoubleIndividual;

/**
 *
 * @author bode
 */
public class ContiniousMutationMemetic implements Mutation<DoubleIndividual> {

    DoubleIndividual beforeLocale;

    public ContiniousMutationMemetic() {
    }

    public void setAdditionalInformation(Object[] o) {
        if (o.length > 1 || !(o[0] instanceof Individual)) {
            throw new UnsupportedOperationException("This Mutation only allows the Setting of the Locale Chromosome");
        }
        beforeLocale = (DoubleIndividual) o[0];

    }

    @Override
    public DoubleIndividual mutate(DoubleIndividual ind, double xMutationRate) {
        DoubleIndividual c = ind;

        double[] diff = new double[beforeLocale.size()];
        for (int i = 0; i < diff.length; i++) {
            diff[i] = c.get(i) - beforeLocale.get(i);
        }

        /**
         * Ist der FDC nahe bei 1 wächst die Güte der Lösungen eher in Richtung
         * des globalen Optimums.
         */
//        double FDC = fitnessLandscapeEvaluation.getFDC(new Individual(c));
        Double[] newChrom = new Double[diff.length];
        for (int i = 0; i < diff.length; i++) {
            newChrom[i] = c.get(i) + diff[i];
        }
        return new DoubleIndividual(newChrom);
    }
}
