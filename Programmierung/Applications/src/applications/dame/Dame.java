package applications.dame;

import ga.Parameters;
import ga.individuals.IntegerIndividual;
import java.util.ArrayList;

/**
 *
 * @author hoecker
 */
public class Dame extends IntegerIndividual {

    public Dame(int n) {
        super();
        int num = n;
        int minValue = 0;
        int maxValue = n - 1;

        ArrayList<Integer> res = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            res.add(i, minValue + (int) (Parameters.getRandom().nextDouble() * (maxValue - minValue) + 0.5));  // Pick from range
        }
        this.chromosome = res;
    }
}
