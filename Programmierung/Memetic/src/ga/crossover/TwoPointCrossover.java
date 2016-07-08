/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.individuals.Individual;
import ga.Parameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class TwoPointCrossover<I extends Individual> implements Crossover<I> {

    @Override
    public List<? extends I> recombine(I c1, I c2, double xOverRate) {
        if (Parameters.getRandom().nextDouble() > xOverRate) {
            ArrayList<I> result = new ArrayList<>();
            result.add(c1);
            result.add(c2);
            return result;
        }

        int size = c1.size();

        // choose two random numbers for the start and end indices of the slice
        // (one can be at index "size")
        int number1 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
        int number2 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size);

        // make the smaller the start and the larger the end
        int start = Math.min(number1, number2);
        int end = Math.max(number1, number2);

        final I cNeu1 = (I) c1.clone();
        final I cNeu2 = (I) c2.clone();

        List a0 = new ArrayList(c1.getList().subList(0, start));
        List a1 = new ArrayList(c1.getList().subList(start, end));
        List a2 = new ArrayList(c1.getList().subList(end, c1.getList().size()));
        
        List b0 = new ArrayList(c2.getList().subList(0, start));
        List b1 = new ArrayList(c2.getList().subList(start, end));
        List b2 = new ArrayList(c2.getList().subList(end, c2.getList().size()));

        cNeu1.getList().clear();
        cNeu2.getList().clear();

        cNeu1.getList().addAll(a0);
        cNeu1.getList().addAll(b1);
        cNeu1.getList().addAll(a2);
        
        cNeu2.getList().addAll(b0);
        cNeu2.getList().addAll(a1);
        cNeu2.getList().addAll(b2);

        ArrayList<I> result = new ArrayList<>();
        result.add(cNeu1);
        result.add(cNeu2);
        
    
//        if(!cNeu1.getChromosome().containsAll(cNeu2.getChromosome())){
//            throw new IllegalArgumentException("nicht alles drin im neuen");
//        }
//        if(!cNeu2.getChromosome().containsAll(cNeu1.getChromosome())){
//            throw new IllegalArgumentException("nicht alles drin im neuen");
//        }
//        
        return result;
    }

    @Override
    public String toString() {
        return "TwoPointCrossover{" + '}';
    }

   
}
