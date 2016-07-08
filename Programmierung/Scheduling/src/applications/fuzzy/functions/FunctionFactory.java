/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.functions;

import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.DiscretizedFuzzyNumber;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author bode
 */
public class FunctionFactory {
    // gibt auch +-unendlich mit zurück!

    public static LinearizedFunction1d createFunction(FuzzyNumber f) {
        if (f instanceof DiscretizedFuzzyNumber) {
            DiscretizedFuzzyNumber df = (DiscretizedFuzzyNumber) f;
            // StützstellenFeld erzeugen
            TreeMap<Double, Double> values = new TreeMap<>();

            // negativ Unendlich auf 0 setzen
            values.put(Double.MAX_VALUE * (-1.), 0.);

            // Alpha-Bereich-Stützstellen erzeugen
            for (int i = 0; i < df.getNumberOfAlphaCuts(); i++) {
                values.put(df.getAlphaCutSet(i).getMin(), df.getAlphaCutSet(i).getAlpha());
            }
            for (int i = df.getNumberOfAlphaCuts() - 1; i > -1; i--) {
                values.put(df.getAlphaCutSet(i).getMax(), df.getAlphaCutSet(i).getAlpha());
            }

            // positiv Unendlich auf 0 setzen
            values.put(Double.MAX_VALUE, 0.);
            return new LinearizedFunction1d(values);
        }
        if (f instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval di = (DiscretizedFuzzyInterval) f;

            // StützstellenFeld erzeugen
            TreeMap<Double, Double> values = new TreeMap<>();

            // negativ Unendlich auf 0 setzen
            values.put(Double.MAX_VALUE * (-1.), 0.);

            // Alpha-Bereich-Stützstellen erzeugen
            for (int i = 0; i < di.getNumberOfAlphaCuts(); i++) {
                values.put(di.getAlphaCutSet(i).getMin(), di.getAlphaCutSet(i).getAlpha());
            }
            for (int i = di.getNumberOfAlphaCuts() - 1; i > -1; i--) {
                values.put(di.getAlphaCutSet(i).getMax(), di.getAlphaCutSet(i).getAlpha());
            }

            // positiv Unendlich auf 0 setzen
            values.put(Double.MAX_VALUE, 0.);
            return new LinearizedFunction1d(values);
        }

        throw new UnsupportedOperationException("Nur für diskretisierte Zahlen und Intervalle möglich");
    }

    // gibt nur das Interval zurück (ohne +-Unendlich)
    public static LinearizedFunction1d createFunction(FuzzyNumber f, double x1, double x2) {
        if (f instanceof DiscretizedFuzzyNumber) {
            DiscretizedFuzzyNumber df = (DiscretizedFuzzyNumber) f;
            if (!(x1 <= x2)) {
                double x_temp = x1;
                x1 = x2;
                x2 = x_temp;
            }

            TreeMap<Double, Double> intervalValues = FunctionFactory.createFunction(f).getValues();
            // Falls die GrenzValues noch nicht enthalten sind, diese hinzufügen
            if (!intervalValues.containsKey(x1)) {
                intervalValues.put(x1, df.getMembership(x1));
            }
            if (!intervalValues.containsKey(x2)) {
                intervalValues.put(x2, df.getMembership(x2));
            }

            // SubMap erzeugen (x1 und x2 sind enthalten)
            SortedMap<Double, Double> subMap = intervalValues.subMap(x1, x2);
            intervalValues = new TreeMap<>(subMap);
            if (!intervalValues.containsKey(x2)) {
                intervalValues.put(x2, df.getMembership(x2));
            }

            return new LinearizedFunction1d(intervalValues);
        }
        if (f instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval fi = (DiscretizedFuzzyInterval) f;

            if (!(x1 <= x2)) {
                double x_temp = x1;
                x1 = x2;
                x2 = x_temp;
            }

            TreeMap<Double, Double> intervalValues = FunctionFactory.createFunction(fi).getValues();
            // Falls die GrenzValues noch nicht enthalten sind, diese hinzufügen
            if (!intervalValues.containsKey(x1)) {
                intervalValues.put(x1, fi.getMembership(x1));
            }
            if (!intervalValues.containsKey(x2)) {
                intervalValues.put(x2, fi.getMembership(x2));
            }

            // SubMap erzeugen (x1 und x2 sind enthalten)
            SortedMap<Double, Double> subMap = intervalValues.subMap(x1, x2);
            intervalValues = new TreeMap<>(subMap);
            if (!intervalValues.containsKey(x2)) {
                intervalValues.put(x2, fi.getMembership(x2));
            }

            return new LinearizedFunction1d(intervalValues);
        }
        throw new UnsupportedOperationException("Nur für diskretisierte Zahlen und Intervalle möglich");
    }

}
