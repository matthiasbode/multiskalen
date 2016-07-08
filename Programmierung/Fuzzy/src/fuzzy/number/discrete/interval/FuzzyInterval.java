/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.number.discrete.interval;

import bijava.math.function.ScalarFunction1d;
import fuzzy.number.FuzzyNumber;

/**
 *
 * @author bode
 */
public abstract class FuzzyInterval extends FuzzyNumber {

    public FuzzyInterval(ScalarFunction1d membership) {
        super(membership);
    }

    public abstract double getM1();

    public abstract double getM2();

    @Override
    public abstract FuzzyInterval clone();

    @Override
    public abstract FuzzyInterval add(FuzzyNumber b);

    @Override
    public abstract FuzzyInterval mult(FuzzyNumber b);

    @Override
    public abstract FuzzyInterval sub(FuzzyNumber b);

    @Override
    public abstract FuzzyInterval div(FuzzyNumber b);

    @Override
    public abstract FuzzyInterval mult(double s);
    
    

    
}
