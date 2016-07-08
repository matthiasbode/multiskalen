//package applications.fuzzy.scheduling.rules.alternative;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//
//import applications.fuzzy.functions.FunctionFactory;
//import applications.fuzzy.functions.LinearizedFunction1d;
//import applications.fuzzy.operation.FuzzyWorkloadParameters;
//import applications.fuzzy.operation.BetaOperation;
//import applications.mmrcsp.model.resources.Resource;
//import fuzzy.number.discrete.interval.FuzzyInterval;
//import java.util.TreeMap;
//
///**
// *
// * @author bode
// */
//public class FuzzyDemandUtilities_alternative {
//
//    /**
//     * gibt die Möglichkeit der Operation von -Unendlich bis +Unendlich von
//     * Start zurück
//     *
//     * @param start
//     * @return
//     */
//    public static LinearizedFunction1d getPossibilityOfStart(FuzzyInterval start) {
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
//    public static LinearizedFunction1d getNecessityOfStart(FuzzyInterval start) {
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
//    public static LinearizedFunction1d getPossibilityOfEnd(BetaOperation operation, FuzzyInterval start) {
//
//        // Ende ermitteln
//        FuzzyInterval ende = start.add((FuzzyInterval) operation.getDuration());
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
//    public static LinearizedFunction1d getNecessityOfEnd(BetaOperation operation, FuzzyInterval start) {
//
//        // Ende ermitteln
//        FuzzyInterval ende = start.add((FuzzyInterval) operation.getDuration());
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
//    public static LinearizedFunction1d getPossibilityFunction(BetaOperation operation, FuzzyInterval start) {
//
//        // Ende berechnen
//        FuzzyInterval end = start.add((FuzzyInterval) operation.getDuration());
//        // Possibility von Start und Ende ermitteln (als LinearizedMembershipFunction1d!)
//        LinearizedFunction1d PossibilityFunctionOfStart = getPossibilityOfStart(start);
//        LinearizedFunction1d PossibilityFunctionOfEnd = getPossibilityOfEnd(operation, start);
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
//    public static LinearizedFunction1d getNecessityFunction(BetaOperation operation, FuzzyInterval start) {
//
//        // Necessity von Start und Ende ermitteln (als LinearizedMembershipFunction1d!)
//        LinearizedFunction1d NecessityFunctionOfStart = getNecessityOfStart(start);
//        LinearizedFunction1d NecessityFunctionOfEnd = getNecessityOfEnd(operation, start);
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
//    public static double getPossibility(BetaOperation operation, double t, FuzzyInterval start) {
//
//        // Function abrufen
//        LinearizedFunction1d possibility = getPossibilityFunction(operation, start);
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
//    public static double getNecessity(BetaOperation operation, double t, FuzzyInterval start) {
//
//        // Function abrufen
//        LinearizedFunction1d necessity = getNecessityFunction(operation, start);
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
//    public static double getSizeOfPossibilityArea(BetaOperation operation, FuzzyInterval start) {
//
//        // Funktion erzeugen
//        LinearizedFunction1d possibilityFunction = getPossibilityFunction(operation, start);
//        return possibilityFunction.getIntegral(Double.MAX_VALUE * (-1.), Double.MAX_VALUE);
//    }
//
//    /**
//     * gibt die Fläche unter der Notwendigkeitsverteilung zurück
//     *
//     * @param start
//     * @return
//     */
//    public static double getSizeOfNecessityArea(BetaOperation operation, FuzzyInterval start) {
//
//        // Funktion erzeugen
//        LinearizedFunction1d necessityFunction = getNecessityFunction(operation, start);
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
//    public static double getOptimisticWorkloadOfResourceR(BetaOperation operation, Resource r) {
//
//        return ((FuzzyInterval) operation.getDuration()).getC1() * operation.getDemand(r).doubleValue();
//    }
//
//    /**
//     * gibt den pessimisstischsten Aufwand einer Ressource zurück
//     *
//     * @param r
//     * @return
//     */
//    public static double getPessimisticWorkloadOfResourceR(BetaOperation operation, Resource r) {
//
//        return ((FuzzyInterval) operation.getDuration()).getC2() * operation.getDemand(r).doubleValue();
//    }
//
//    /**
//     * *
//     * gibt Lamda-Min zurück
//     *
//     * @param start
//     * @return
//     */
//    public static double getLambdaMin(BetaOperation operation, FuzzyInterval start) {
//
//        double durationMin = ((FuzzyInterval) operation.getDuration()).getC1();
//        double durationNecessity = getSizeOfNecessityArea(operation, start);
//        double durationPossibility = getSizeOfPossibilityArea(operation, start);
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
//    public static double getLambdaMax(BetaOperation operation, FuzzyInterval start) {
//
//        double durationMax = ((FuzzyInterval) operation.getDuration()).getC2();
//        double durationNecessity = getSizeOfNecessityArea(operation, start);
//        double durationPossibility = getSizeOfPossibilityArea(operation, start);
//
//        return ((durationMax - durationNecessity) / (durationPossibility - durationNecessity));
//    }
//
//    public static double getMatchingLambdaR(BetaOperation operation, Resource r, FuzzyInterval start, FuzzyWorkloadParameters lambda) {
//        double minimalLambdaRight = 0.;
//        double maximalLambdaRight = 1.;
//        double aktuellLambdaRight = (minimalLambdaRight + maximalLambdaRight) / 2.;
//        double error = Double.MAX_VALUE;
//        while (error > 1) {
//            lambda.lambdaR = aktuellLambdaRight;
//            LinearizedFunction1d demandFunction = getPresenceFunctionAtPessimisticLevelWithLambda(operation, start, lambda);
//            double istFlaeche = demandFunction.getScaleOfFunction(operation.getDemand(r).doubleValue()).getIntegral(Double.MIN_VALUE, Double.MAX_VALUE);
//            error = lambda.zielFlaeche - istFlaeche;
//            if (error > 0.) {
//                minimalLambdaRight = aktuellLambdaRight;
//                aktuellLambdaRight = (minimalLambdaRight + maximalLambdaRight) / 2.;
//            }
//            if (error < 0.) {
//                maximalLambdaRight = aktuellLambdaRight;
//                aktuellLambdaRight = (minimalLambdaRight + maximalLambdaRight) / 2.;
//            }
//        }
//        return lambda.lambdaR;
//    }
//
//    public static LinearizedFunction1d getDemandFunctionAtPessimisticLevelOfResourceWithLambda(BetaOperation operation, Resource r, FuzzyInterval start, FuzzyWorkloadParameters lambda) {
//
//        LinearizedFunction1d demandFunction = getPresenceFunctionAtPessimisticLevelWithLambda(operation, start, lambda);
//        if (demandFunction == null) {
//            return null;
//        }
//        demandFunction = demandFunction.getScaleOfFunction(operation.getDemand(r).doubleValue());
//        return demandFunction;
//
//    }
//
//    public static LinearizedFunction1d getPresenceFunctionAtPessimisticLevelWithLambda(BetaOperation operation, FuzzyInterval start, FuzzyWorkloadParameters lambda) {
//
//        TreeMap<Double, Double> values = new TreeMap<>();
//        //Without Overlap
//        if (lambda.overlapCase.equals(FuzzyWorkloadParameters.Case.noOverlap)) {
//            values.put(lambda.block.as, 0.);
//            values.put(lambda.block.bs, lambda.lambdaL);
//            values.put(lambda.block.cs, lambda.lambdaL);
//            values.put(lambda.block.ds, 1.0);
//            values.put(lambda.block.af, 1.0);
//            values.put(lambda.block.bf, lambda.lambdaR);
//            values.put(lambda.block.cf, lambda.lambdaR);
//            values.put(lambda.block.df, 0.);
//        } //small overlap
//        else if (lambda.overlapCase.equals(FuzzyWorkloadParameters.Case.smallOverlap)) {
//            double alpha = ((lambda.block.bf - lambda.block.af) * (lambda.lambdaL * lambda.block.ds - lambda.block.cs) + (lambda.block.ds - lambda.block.cs) * (lambda.lambdaR * lambda.block.af - lambda.block.bf))
//                    / ((lambda.block.bf - lambda.block.af) * (lambda.lambdaL - 1) + (lambda.block.ds - lambda.block.cs) * (lambda.lambdaR - 1));
//            double beta = ((lambda.block.bf - lambda.lambdaR * lambda.block.af) * (lambda.lambdaL - 1) + (lambda.lambdaL * lambda.block.ds - lambda.block.cs) * (lambda.lambdaR - 1))
//                    / ((lambda.block.bf - lambda.block.af) * (lambda.lambdaL - 1) + (lambda.block.ds - lambda.block.cs) * (lambda.lambdaR - 1));
//            values.put(lambda.block.as, 0.);
//            values.put(lambda.block.bs, lambda.lambdaL);
//            values.put(lambda.block.cs, lambda.lambdaL);
//            values.put(alpha, beta);
//            values.put(lambda.block.bf, lambda.lambdaR);
//            values.put(lambda.block.cf, lambda.lambdaR);
//            values.put(lambda.block.df, 0.);
//        } //Large Overlap
//        else {
//            values.put(lambda.block.as, 0.);
//            values.put(lambda.block.bs, lambda.lambdaL);
//            values.put(lambda.block.bf, lambda.lambdaL);
//            values.put(lambda.block.cs, lambda.lambdaR);
//            values.put(lambda.block.cf, lambda.lambdaR);
//            values.put(lambda.block.df, 0.);
//        }
//        return new LinearizedFunction1d(values);
//    }
//
//    
//}
