/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.number;

import bijava.math.function.ScalarFunction1d;
import fuzzy.fuzzyset.FuzzySet1d;
import math.FieldElement;

/**
 * Das Interface wurde eingeführt, um zu gewährleisten, dass der Plotter für
 * LR-Fuzzy Zahlen richtig angesprochen wird.
 *
 * Sollte diese Klasse wirklich als Modellklasse genutzt werden, so wäre es
 * sinnvoll, hier noch Methoden für add,sub,div und mult vorzuschreiben.
 *
 * Bei der Implementierung innerhalb der Klasse LRFuzzyNumber müsste dann darauf
 * geachtet werden, dass eine FuzzyNumber1d den Methoden übergeben wird und
 * entweder mit einer instanceof-Abfrage darauf getestet werden, ob es sich um
 * eine LRFuzzyZahl handelt.
 *
 * @author Matthias
 */
public abstract class FuzzyNumber extends FuzzySet1d implements FieldElement<FuzzyNumber> {

    public FuzzyNumber(ScalarFunction1d membership) {
        super(membership);
    }

    /**
     * Nennwert der Fuzzy-Zahl
     *
     * @return
     */
    public abstract double getMean();

    /**
     * Untere Intervallgrenze des Supports
     *
     * @return
     */
    public abstract double getC1();

    /**
     * Obere Intervallgrenze des Supports
     *
     * @return
     */
    public abstract double getC2();

    
    @Override
    public abstract FuzzyNumber clone();

    @Override
    public abstract FuzzyNumber mult(double s);

    @Override
    public abstract FuzzyNumber div(FuzzyNumber b);

    @Override
    public abstract FuzzyNumber sub(FuzzyNumber b);

    @Override
    public abstract FuzzyNumber mult(FuzzyNumber b);

    @Override
    public abstract FuzzyNumber add(FuzzyNumber b);
        

    
}