/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import ga.Parameters;
import ga.individuals.IntegerIndividual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author bode
 */
public class BiPartitionIndividual extends IntegerIndividual {

    public BiPartitionIndividual(Integer[] valList) {
        super(valList);
        test();
    }

    public BiPartitionIndividual(List<Integer> valList) {
        super(valList);
    }
    
    

    public BiPartitionIndividual(int num) {
        super(num);

        ArrayList<Integer> positions = new ArrayList<Integer>();
        for (int i = 0; i < num / 2; i++) {
            positions.add(0);
        }
        for (int i = num / 2; i < num; i++) {
            positions.add(1);
        }
        Collections.shuffle(positions, Parameters.getRandom());
        for (int i = 0; i < positions.size(); i++) {
            this.set(i, positions.get(i));
        }
        test();

    }

    private void test() {
        int counter0 = 0;
        int counter1 = 0;
        for (int index = 0; index < this.size(); index++) {

            int i = get(index);
            if (i == 0) {
                counter0++;
            } else if (i == 1) {
                counter1++;
            } else {
                throw new IllegalArgumentException("Nur für Bipartitionsprobleme "
                        + "und zwei Mengen geeignet");
            }
        }
        if (counter0 != counter1) {
            throw new IllegalArgumentException("Keine gleichgroßen Mengen");
        }
    }

    @Override
    public BiPartitionIndividual clone() {
        return new BiPartitionIndividual(new ArrayList(chromosome));
    }
    
}
