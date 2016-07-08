/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import ga.Parameters;
import ga.mutation.Mutation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class BPMutation implements Mutation<BiPartitionIndividual> {

    private int subsetSize = 0;
    private boolean randomSize = false;

    public BPMutation() {
        randomSize = true;
    }

    public BPMutation(int subsetSize) {
        this.subsetSize = subsetSize;
    }

    @Override
    public BiPartitionIndividual mutate(BiPartitionIndividual c, double xMutationRate) {
        BiPartitionIndividual result = ((BiPartitionIndividual) c.clone());
        if (randomSize) {
            this.subsetSize = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, (c.size() - 1) / 2);
        }

        ArrayList<Integer> positionsToSwap = new ArrayList<Integer>();
        for (int i = 0; i < c.size() - 1; i++) {
            positionsToSwap.add(i);
        }
        Collections.shuffle(positionsToSwap, Parameters.getRandom());
        int swapped = 0;
        Iterator<Integer> iterator = positionsToSwap.iterator();
        while (swapped != this.subsetSize) {
            /**
             * Suche alle 1's
             */
            Integer currentPosition = iterator.next();
            if (c.get(currentPosition) == 0) {
                result.set(currentPosition, 1);
                iterator.remove();
                swapped++;
            }
        }
        Collections.shuffle(positionsToSwap, Parameters.getRandom());
        iterator = positionsToSwap.iterator();
        while (swapped != 0) {
            Integer currentPosition = iterator.next();
            if (c.get(currentPosition) == 1) {
                result.set(currentPosition, 0);
                iterator.remove();
                swapped--;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        BiPartitionIndividual bpc = new BiPartitionIndividual(20);
        BPMutation bpm = new BPMutation(4);
        BiPartitionIndividual mutate = bpm.mutate(bpc, 1.);
        System.out.println(bpc);
        System.out.println(mutate);
    }

}
