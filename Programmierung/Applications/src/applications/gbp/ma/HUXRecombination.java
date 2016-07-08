/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import ga.individuals.Individual;
import ga.Parameters;
import ga.crossover.Crossover;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author bode
 */
public class HUXRecombination implements Crossover<BiPartitionIndividual> {

    @Override
    public Collection<BiPartitionIndividual> recombine(BiPartitionIndividual p1, BiPartitionIndividual p2, double xOverRate) {
        BiPartitionIndividual c1 = p1;
        BiPartitionIndividual c2 = p2;
        BiPartitionIndividual result = new BiPartitionIndividual(c1.size());
        int count1 = 0;
        int n = c1.size();
        
        ArrayList<Integer> emptyPositions = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            emptyPositions.add(new Integer(i));
        }

        for (int i = 0; i < n; i++) {
            Integer v1 = c1.get(i);
            Integer v2 = c2.get(i);
            /**
             * Beide Eltern haben an der Stelle i den gleichen Wert
             */
            if (v1.equals(v2)) {
                /**
                 * Im Ergebnis an dieser Stelle auch den Wert übernehmen
                 */
                result.set(i, v1);
                /**
                 * Aus den noch zu vergebenen entfernen
                 */
                emptyPositions.remove(new Integer(i));
                /**
                 * Den Zähler für die 1's erhöhen
                 */
                if (v1.equals(new Integer(1))) {
                    count1++;
                }
            }
        }

        /**
         * Leere Positionen mischen
         */
        Collections.shuffle(emptyPositions, Parameters.getRandom());

        /**
         * 1's setzen bis Hälfte erreicht
         */
        while (count1 != n/2) {
            result.set(emptyPositions.remove(0), new Integer(1));
            count1++;
        }

        /**
         * 0's setzen
         */
        while (!emptyPositions.isEmpty()) {
            result.set(emptyPositions.remove(0), new Integer(0));
        }
        ArrayList<BiPartitionIndividual> list = new ArrayList<>();
        list.add(result);
        return list;
    }
}
