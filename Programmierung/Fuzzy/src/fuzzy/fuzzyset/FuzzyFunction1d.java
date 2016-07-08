/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.fuzzyset;
 
import bijava.math.function.ScalarFunction1d;

/**
 * FuzzyFunction1d.java stellt abstrakte Methoden
 * fuer skalare Zugehoerigkeitsfunktionen zur Verfuegung.
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik
 */
public interface FuzzyFunction1d extends ScalarFunction1d {

    /**
     * Liefert das Komplement dieser Funktion.
     * @return Komplement dieser Funktion.
     */
    public FuzzyFunction1d complement();

    /**
     * Liefert das Minimum aus dieser und einer anderen Funktion.
     * @param f andere Zugehoerigikeitsfunktion.
     * @return Minimum aus dieser und der anderen Funktion.
     */
    public FuzzyFunction1d min(FuzzyFunction1d f);

    /**
     * Liefert das Maximum aus dieser und einer anderen Funktion.
     * @param f andere Zugehoerigikeitsfunktion.
     * @return Maximum aus dieser und der anderen Funktion.
     */
    public FuzzyFunction1d max(FuzzyFunction1d f);

    /**
     * Schneidet diese Funktion bei einem Zugehoerigkeitswert.
     * @param r Zugehoerigkeitswert.
     * @return Funktion unterhalb des Schnittes.
     */
    public FuzzyFunction1d cut(double r);
}
