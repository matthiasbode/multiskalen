/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.operation;

import applications.mmrcsp.model.resources.Resource;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.interval.FuzzyInterval;
import java.util.LinkedHashMap;
import java.util.Set;
import math.DoubleValue;
import math.FieldElement;

/**
 *
 * @author brandt, bode
 *
 *
 * Diese Klasse stellt die Methoden bereit, die für die Umsetzung des von
 * Masmoudi und Hauit (2013) erörterten Weges der Ablaufplanung unter Unschärfe
 * notwendig sind.
 *
 */
public class FuzzyOperation<E extends FuzzyInterval> implements BetaOperation {

    private E duration;
    private LinkedHashMap<Resource, DoubleValue> demand = new LinkedHashMap<Resource, DoubleValue>();
    static int counter = 0;
    private final int number;
    private double lambda;
//    private double lambdaL;
//    private double lambdaR;

    public FuzzyOperation(E duration, double beta) {
        this.duration = duration;
        this.number = counter++;
        this.lambda = beta;
//        this.lambdaL = beta;
//        this.lambdaR = beta;
    }

    public FuzzyOperation(int number, E duration, double beta) {
        this.duration = duration;
        this.number = number;
        this.lambda = beta;
//        this.lambdaL = beta;
//        this.lambdaR = beta;
        counter = number + 1;
    }

    @Override
    public Set<Resource> getRequieredResources() {
        return demand.keySet();
    }

    @Override
    public void setDemand(Resource r, FieldElement rik) {
        if (rik instanceof DoubleValue) {
            demand.put(r, (DoubleValue) (rik));
        }
        return;
    }

    @Override
    public E getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     */
    @Override
    public void setDuration(FieldElement duration) {
        if (duration instanceof FuzzyNumber) {
        } else {
            this.duration = (E) duration;
        }
    }

    @Override
    public int getId() {
        return number;
    }

    @Override
    public double getBeta() {
        return lambda;
    }

//    /**
//     * gibt die Möglichkeit der Operation von -Unendlich bis +Unendlich von
//     * Start zurück
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getPossibilityOfStart(E start) {
//
//        // Interval Values bis Mean erzeugen
//        LinearizedFunction1d f = FunctionFactory.createFunction(start, start.getC1(), start.getMean());
//
//        // negativ und positv Unendlich-Werte setzen
//        f.addValue(Double.MAX_VALUE * (-1.), 0.);
//        f.addValue(Double.MAX_VALUE, 1.);
//
//        // Funktion bereinigen
//        f = f.eliminateNotRequiredValues();
//
//        return f;
//    }
//
//    /**
//     * gibt die Notwendigkeit der Operation von -Unendlich bis +Unendlich von
//     * Start zurück
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getNecessityOfStart(E start) {
//
//        // Interval Values ab Mean erzeugen
//        LinearizedFunction1d f = FunctionFactory.createFunction(start, start.getMean(), start.getC2());
//        f = f.getComplementMembershipAsFunction();
//
//        // negativ und positv Unendlich-Werte setzen
//        f.addValue(Double.MAX_VALUE * (-1.), 0.);
//        f.addValue(Double.MAX_VALUE, 1.);
//
//        // Funktion bereinigen
//        f = f.eliminateNotRequiredValues();
//
//        return f;
//    }
//
//    /**
//     * gibt die Möglichkeit der Operation von -Unendlich bis +Unendlich von Ende
//     * zurück
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getPossibilityOfEnd(E start) {
//
//        // Ende ermitteln
//        E ende = (E) start.add(duration);
//
//        // Interval Values ab Mean erzeugen
//        LinearizedFunction1d f = FunctionFactory.createFunction(ende, ende.getMean(), ende.getC2());
//
//        // negativ und positv Unendlich-Werte setzen
//        f.addValue(Double.MAX_VALUE * (-1.), 1.);
//        f.addValue(Double.MAX_VALUE, 0.);
//
//        // Funktion bereinigen
//        f = f.eliminateNotRequiredValues();
//
//        return f;
//    }
//
//    /**
//     * gibt die Notwendigkeit der Operation von -Unendlich bis +Unendlich von
//     * Ende zurück
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getNecessityOfEnd(E start) {
//
//        // Ende ermitteln
//        E ende = (E) start.add(duration);
//
//        // Interval Values bis Mean erzeugen
//        LinearizedFunction1d f = FunctionFactory.createFunction(ende, ende.getC1(), ende.getMean());
//
//        f = f.getComplementMembershipAsFunction();
//
//        // negativ und positv Unendlich-Werte setzen
//        f.addValue(Double.MAX_VALUE * (-1.), 1.);
//        f.addValue(Double.MAX_VALUE, 0.);
//
//        // Funktion bereinigen
//        f = f.eliminateNotRequiredValues();
//
//        return f;
//    }
//
//    /**
//     * Fügt die Möglichkeitsfunktionen der Start- und Endzeit zu einer Funktion
//     * zusammen
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getPossibilityFunction(E start) {
//
//        // Ende berechnen
//        E end = (E) start.add(duration);
//        // Possibility von Start und Ende ermitteln (als LinearizedMembershipFunction1d!)
//        LinearizedFunction1d PossibilityFunctionOfStart = getPossibilityOfStart(start);
//        LinearizedFunction1d PossibilityFunctionOfEnd = getPossibilityOfEnd(start);
//
//        // Vereinigung der beiden Verteilungen ergibt die Möglichkeitsverteilung dieser Operation
//        LinearizedFunction1d possibility = PossibilityFunctionOfStart.getMin(PossibilityFunctionOfEnd);
//        // Funktion Bereinigen
//        possibility = possibility.eliminateNotRequiredValues();
//
//        return possibility;
//    }
//
//    /**
//     * Fügt die Notwendigkeitsfunktionen der Start- und Endzeit zu einer
//     * Funktion zusammen
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getNecessityFunction(E start) {
//
//        // Ende berechnen
//        E end = (E) start.add(duration);
//        // Necessity von Start und Ende ermitteln (als LinearizedMembershipFunction1d!)
//        LinearizedFunction1d NecessityFunctionOfStart = getNecessityOfStart(start);
//        LinearizedFunction1d NecessityFunctionOfEnd = getNecessityOfEnd(start);
//
//        // Schnitt der beiden Verteilungen ergibt die Notwendigkeitsverteilung dieser Operation
//        LinearizedFunction1d necessity = NecessityFunctionOfStart.getMin(NecessityFunctionOfEnd);
//        // Funktion Bereinigen
//        necessity = necessity.eliminateNotRequiredValues();
//
//        return necessity;
//    }
//
//    /**
//     * gibt die Möglichkeit zu einem Zeitpunkt zurück
//     *
//     * @param t
//     * @param start
//     * @return
//     */
//    public double getPossibility(double t, E start) {
//
//        // Function abrufen
//        LinearizedFunction1d possibility = getPossibilityFunction(start);
//
//        // Wert an der Stelle t zurückgeben
//        return possibility.getValue(t);
//    }
//
//    /**
//     * gibt die Notwendigkeit zu einem Zeitpunkt zurück
//     *
//     * @param t
//     * @param start
//     * @return
//     */
//    public double getNecessity(double t, E start) {
//
//        // Function abrufen
//        LinearizedFunction1d necessity = getNecessityFunction(start);
//
//        // Wert an der Stelle t zurückgeben
//        return necessity.getValue(t);
//    }
//
//    /**
//     * *
//     * gibt die Fläche unter der Möglichkeitsverteilung zurück
//     *
//     * @param start
//     * @return
//     */
//    public double getSizeOfPossibilityArea(E start) {
//
//        // Funktion erzeugen
//        LinearizedFunction1d possibilityFunction = getPossibilityFunction(start);
//        return possibilityFunction.getIntegral(Double.MAX_VALUE * (-1.), Double.MAX_VALUE);
//    }
//
//    /**
//     * gibt die Fläche unter der Notwendigkeitsverteilung zurück
//     *
//     * @param start
//     * @return
//     */
//    public double getSizeOfNecessityArea(E start) {
//
//        // Funktion erzeugen
//        LinearizedFunction1d necessityFunction = getNecessityFunction(start);
//
//        return necessityFunction.getIntegral(Double.MAX_VALUE * (-1.), Double.MAX_VALUE);
//    }
//
//    /**
//     * gibt den optimistischsten Aufwand einer Ressource zurück
//     *
//     * @param r
//     * @return
//     */
//    public double getOptimisticWorkloadOfResourceR(Resource r) {
//
//        return duration.getC1() * getDemand(r).doubleValue();
//    }
//
//    /**
//     * gibt den pessimisstischsten Aufwand einer Ressource zurück
//     *
//     * @param r
//     * @return
//     */
//    public double getPessimisticWorkloadOfResourceR(Resource r) {
//
//        return duration.getC2() * getDemand(r).doubleValue();
//    }
//
//    /**
//     * *
//     * gibt Lamda-Min zurück
//     *
//     * @param start
//     * @return
//     */
//    public double getLamdaMin(E start) {
//
//        double durationMin = getDuration().getC1();
//        double durationNecessity = getSizeOfNecessityArea(start);
//        double durationPossibility = getSizeOfPossibilityArea(start);
//
//        if ((durationPossibility - durationNecessity) == 0.) {
//            return Double.NaN;
//        }
//
//        return ((durationMin - durationNecessity) / (durationPossibility - durationNecessity));
//    }
//
//    /**
//     * gibt Lamda-Max zurück
//     *
//     * @param start
//     * @return
//     */
//    public double getLamdaMax(E start) {
//
//        double durationMax = getDuration().getC2();
//        double durationNecessity = getSizeOfNecessityArea(start);
//        double durationPossibility = getSizeOfPossibilityArea(start);
//
//        return ((durationMax - durationNecessity) / (durationPossibility - durationNecessity));
//    }
//
//    /**
//     * gibt die optimistischste Workload-Funktion zurück
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getLamdaMinFunction(E start) {
//
//        double lamdaMin = getLamdaMin(start);
//        if (lamdaMin == Double.NaN) {
//            return null;
//        }
//
//        // Necessity- und Possibility-Funktion bereitstellen
//        LinearizedFunction1d possibility = getPossibilityFunction(start);
//        LinearizedFunction1d necessity = getNecessityFunction(start);
//
//        // possibility- und necessity-Funktionen skalieren
//        possibility = possibility.getScaleOfFunction(lamdaMin);
//        necessity = necessity.getScaleOfFunction(1. - lamdaMin);
//
//        // Affine Kombination der Necessity- und Possibility-Funktion ergibt LamdaMin-Fkt.
//        LinearizedFunction1d lamdaMinFunction = possibility.add(necessity);
//
//        return lamdaMinFunction;
//    }
//
//    /**
//     * gibt die pessimisstiste Workload-Funktion zurück
//     *
//     * @param start
//     * @return
//     */
//    public LinearizedFunction1d getLamdaMaxFunction(E start) {
//
//        double lamdaMax = getLamdaMax(start);
//        if (lamdaMax == Double.NaN) {
//            return null;
//        }
//
//        // Necessity- und Possibility-Funktion bereitstellen
//        LinearizedFunction1d possibility = getPossibilityFunction(start);
//        LinearizedFunction1d necessity = getNecessityFunction(start);
//
//        // possibility- und necessity-Funktionen skalieren
//        possibility = possibility.getScaleOfFunction(lamdaMax);
//        necessity = necessity.getScaleOfFunction(1. - lamdaMax);
//
//        // Affine Kombination der Necessity- und Possibility-Funktion ergibt LamdaMin-Fkt.
//        LinearizedFunction1d lamdaMaxFunction = possibility.add(necessity);
//
//        return lamdaMaxFunction;
//    }
//
//    /**
//     * gibt die Präsenzfunktion für ein beta zwischen 0 und 1 zurück
//     *
//     * @param start
//     * @param beta
//     * @return
//     */
//    @Deprecated
//    public LinearizedFunction1d getPresenceFunctionAtBeta(E start, double beta) {
//        // Fehlt: überprüfung ob beta zwischen 0  und 1 liegt
//        // Anmerkung: wenn beta klein, eher optimistisch; wenn beta groß, eher pessimistisch
//
//        LinearizedFunction1d lamdaMinFunction = getLamdaMinFunction(start); // //
//        LinearizedFunction1d lamdaMaxFunction = getLamdaMaxFunction(start); ////
//
//        // lamdaMin- und lamdaMax-Funktionen skalieren
//        lamdaMaxFunction = lamdaMaxFunction.getScaleOfFunction(beta);
//        lamdaMinFunction = lamdaMinFunction.getScaleOfFunction(1. - beta);
//
//        //Affine Kombination der lamdaMin- und lamdaMax-Funktion ergibt Presence-Fkt. at Pessimistic-Level beta
//        LinearizedFunction1d presenceFunction = lamdaMinFunction.add(lamdaMaxFunction);
//
//        return presenceFunction;
//    }
//
//    public LinearizedFunction1d getPresenceFunction(E start, AdaptedLambda lambda) {
//        // Fehlt: überprüfung ob beta zwischen 0  und 1 liegt
//        // Anmerkung: wenn beta klein, eher optimistisch; wenn beta groß, eher pessimistisch
////        double lamdaMax = getLamdaMax(start);
////        if (lamdaMax > 1) {
////            System.err.println("Zu großes LambdaMAX");
////        }
////        double lamdaMin = getLamdaMin(start);
////        if (lamdaMin < 0) {
////            System.err.println("Zu kleines LambdaMIN");
////        }
//
//        double lambdaL = lambda.lambdaL;
//        double lambdaR = lambda.lambdaR;
//
////        if (lambdaL < lamdaMin || lambdaR < lamdaMin) {
////            System.err.println("Lambda zu klein gewählt");
////            throw new IllegalArgumentException("Lambda zu klein gewählt");
////        }
////        if (lambdaL > lamdaMax || lambdaR > lamdaMax) {
////            System.err.println("Lambda zu groß gewählt");
////            throw new IllegalArgumentException("Lambda zu groß gewählt");
////        }
//        FuzzyInterval end = start.add(duration);
//
//        double as = start.getC1();
//        double bs = start.getM1();
//        double cs = start.getM2();
//        double ds = start.getC2();
//
//        double af = end.getC1();
//        double bf = end.getM1();
//        double cf = end.getM2();
//        double df = end.getC2();
//
//        TreeMap<Double, Double> values = new TreeMap<>();
//        //Without Overlap
//        if (af > ds) {
//            values.put(as, 0.);
//            values.put(bs, lambdaL);
//            values.put(cs, lambdaL);
//            values.put(ds, 1.0);
//            values.put(af, 1.0);
//            values.put(bf, lambdaR);
//            values.put(cf, lambdaR);
//            values.put(df, 0.);
//        }
//
//        //Small Overlap
//        if (af < ds && bf > cs) {
//            double alpha = ((bf - af) * (lambdaL * ds - cs) + (ds - cs) * (lambdaR * af - bf)) / ((bf - af) * (lambdaL - 1) + (ds - cs) * (lambdaR - 1));
//            double beta = ((bf - lambdaR * af) * (lambdaL - 1) + (lambdaL * ds - cs) * (lambdaR - 1)) / ((bf - af) * (lambdaL - 1) + (ds - cs) * (lambdaR - 1));
//            values.put(as, 0.);
//            values.put(bs, lambdaL);
//            values.put(cs, lambdaL);
//            values.put(alpha, beta);
//            values.put(bf, lambdaR);
//            values.put(cf, lambdaR);
//            values.put(df, 0.);
//        } //Large Overlap
//        else {
//            values.put(as, 0.);
//            values.put(bs, lambdaL);
//            values.put(bf, lambdaL);
//            values.put(cs, lambdaR);
//            values.put(cf, lambdaR);
//            values.put(df, 0.);
//        }
//
//        LinearizedFunction1d presenceFunction = new LinearizedFunction1d(values);
//
//        return presenceFunction;
//    }
//
//    /**
//     * Gibt die Aufwandsfunktion dieses Vorgangs für eine bestimmte Ressource
//     * zurück
//     *
//     * @param r
//     * @param start
//     * @return
//     */
//    @Deprecated
//    public LinearizedFunction1d getDemandFunctionAtPessimisticLevelOfResource(Resource r, E start) {
//        LinearizedFunction1d demandFunction = getPresenceFunctionAtBeta(start, this.lambda);
//        demandFunction = demandFunction.getScaleOfFunction(this.getDemand(r).doubleValue());
//        return demandFunction;
//    }
//
//    public LinearizedFunction1d getDemandFunctionAtLambdaLevel(Resource r, E start, AdaptedLambda lambda) {
//        LinearizedFunction1d demandFunction = getPresenceFunction(start, lambda);
//        demandFunction = demandFunction.getScaleOfFunction(this.getDemand(r).doubleValue());
//        return demandFunction;
//    }
    /**
     * gibt das Pessimissmus-Level dieser Operation zurück
     *
     * @return
     */
    @Deprecated
    public double getPessimisticLevelOfOperation() {
        return lambda;
    }

    @Override
    public FieldElement getDemand(Resource r) {
        return demand.get(r);
    }

    @Override
    public String toString() {
        return "FuzzyOperation{" + number + " crispDuration: " + duration.getMean() + '}';
    }

    @Override
    public FuzzyOperation clone() {
        FuzzyOperation fuzzyOperation = new FuzzyOperation(this.number, duration.clone(), lambda);
        for (Resource r : demand.keySet()) {
            DoubleValue dem = demand.get(r);
            fuzzyOperation.setDemand(r, dem.clone());
        }
        return fuzzyOperation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.number;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FuzzyOperation<?> other = (FuzzyOperation<?>) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public void setBeta(double lambda) {
        this.lambda = lambda;
    }

}
