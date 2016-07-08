/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.scheduling.rules.defaultImplementation;

import applications.fuzzy.functions.FunctionFactory;
import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.operation.FuzzyWorkloadParameters;
import applications.fuzzy.operation.FuzzyWorkloadParameters.Case;
import applications.fuzzy.operation.BetaOperation;
import applications.mmrcsp.model.resources.Resource;
import fuzzy.number.discrete.interval.FuzzyInterval;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author bode
 */
public class FuzzyDemandUtilities {

    /**
     * gibt die Möglichkeit der Operation von -Unendlich bis +Unendlich von
     * Start zurück
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getPossibilityOfStart(FuzzyInterval start) {

        // Interval Values bis Mean erzeugen
        LinearizedFunction1d f = FunctionFactory.createFunction(start, start.getC1(), start.getMean());

        // negativ und positv Unendlich-Werte setzen
        f.addValue(Double.MAX_VALUE * (-1.), 0.);
        f.addValue(Double.MAX_VALUE, 1.);

        // Funktion bereinigen
        f = f.eliminateNotRequiredValues();

        return f;
    }

    /**
     * gibt die Notwendigkeit der Operation von -Unendlich bis +Unendlich von
     * Start zurück
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getNecessityOfStart(FuzzyInterval start) {

        // Interval Values ab Mean erzeugen
        LinearizedFunction1d f = FunctionFactory.createFunction(start, start.getMean(), start.getC2());
        f = f.getComplementMembershipAsFunction();

        // negativ und positv Unendlich-Werte setzen
        f.addValue(Double.MAX_VALUE * (-1.), 0.);
        f.addValue(Double.MAX_VALUE, 1.);

        // Funktion bereinigen
        f = f.eliminateNotRequiredValues();

        return f;
    }

    /**
     * gibt die Möglichkeit der Operation von -Unendlich bis +Unendlich von Ende
     * zurück
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getPossibilityOfEnd(BetaOperation operation, FuzzyInterval start) {

        // Ende ermitteln
        FuzzyInterval ende = start.add((FuzzyInterval) operation.getDuration());

        // Interval Values ab Mean erzeugen
        LinearizedFunction1d f = FunctionFactory.createFunction(ende, ende.getMean(), ende.getC2());

        // negativ und positv Unendlich-Werte setzen
        f.addValue(Double.MAX_VALUE * (-1.), 1.);
        f.addValue(Double.MAX_VALUE, 0.);

        // Funktion bereinigen
        f = f.eliminateNotRequiredValues();

        return f;
    }

    /**
     * gibt die Notwendigkeit der Operation von -Unendlich bis +Unendlich von
     * Ende zurück
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getNecessityOfEnd(BetaOperation operation, FuzzyInterval start) {

        // Ende ermitteln
        FuzzyInterval ende = start.add((FuzzyInterval) operation.getDuration());

        // Interval Values bis Mean erzeugen
        LinearizedFunction1d f = FunctionFactory.createFunction(ende, ende.getC1(), ende.getMean());

        f = f.getComplementMembershipAsFunction();

        // negativ und positv Unendlich-Werte setzen
        f.addValue(Double.MAX_VALUE * (-1.), 1.);
        f.addValue(Double.MAX_VALUE, 0.);

        // Funktion bereinigen
        f = f.eliminateNotRequiredValues();

        return f;
    }

    /**
     * Fügt die Möglichkeitsfunktionen der Start- und Endzeit zu einer Funktion
     * zusammen
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getPossibilityFunction(BetaOperation operation, FuzzyInterval start) {

        // Ende berechnen
        FuzzyInterval end = start.add((FuzzyInterval) operation.getDuration());
        // Possibility von Start und Ende ermitteln (als LinearizedMembershipFunction1d!)
        LinearizedFunction1d PossibilityFunctionOfStart = getPossibilityOfStart(start);
        LinearizedFunction1d PossibilityFunctionOfEnd = getPossibilityOfEnd(operation, start);

        // Vereinigung der beiden Verteilungen ergibt die Möglichkeitsverteilung dieser Operation
        LinearizedFunction1d possibility = PossibilityFunctionOfStart.getMin(PossibilityFunctionOfEnd);
        // Funktion Bereinigen
        possibility = possibility.eliminateNotRequiredValues();

        return possibility;
    }

    /**
     * Fügt die Notwendigkeitsfunktionen der Start- und Endzeit zu einer
     * Funktion zusammen
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getNecessityFunction(BetaOperation operation, FuzzyInterval start) {

        // Necessity von Start und Ende ermitteln (als LinearizedMembershipFunction1d!)
        LinearizedFunction1d NecessityFunctionOfStart = getNecessityOfStart(start);
        LinearizedFunction1d NecessityFunctionOfEnd = getNecessityOfEnd(operation, start);

        // Schnitt der beiden Verteilungen ergibt die Notwendigkeitsverteilung dieser Operation
        LinearizedFunction1d necessity = NecessityFunctionOfStart.getMin(NecessityFunctionOfEnd);
        // Funktion Bereinigen
        necessity = necessity.eliminateNotRequiredValues();

        return necessity;
    }

    /**
     * gibt die Möglichkeit zu einem Zeitpunkt zurück
     *
     * @param t
     * @param start
     * @return
     */
    public static double getPossibility(BetaOperation operation, double t, FuzzyInterval start) {

        // Function abrufen
        LinearizedFunction1d possibility = getPossibilityFunction(operation, start);

        // Wert an der Stelle t zurückgeben
        return possibility.getValue(t);
    }

    /**
     * gibt die Notwendigkeit zu einem Zeitpunkt zurück
     *
     * @param t
     * @param start
     * @return
     */
    public static double getNecessity(BetaOperation operation, double t, FuzzyInterval start) {

        // Function abrufen
        LinearizedFunction1d necessity = getNecessityFunction(operation, start);

        // Wert an der Stelle t zurückgeben
        return necessity.getValue(t);
    }

    /**
     * *
     * gibt die Fläche unter der Möglichkeitsverteilung zurück
     *
     * @param start
     * @return
     */
    public static double getSizeOfPossibilityArea(BetaOperation operation, FuzzyInterval start) {

        // Funktion erzeugen
        LinearizedFunction1d possibilityFunction = getPossibilityFunction(operation, start);
        return possibilityFunction.getIntegral(Double.MAX_VALUE * (-1.), Double.MAX_VALUE);
    }

    /**
     * gibt die Fläche unter der Notwendigkeitsverteilung zurück
     *
     * @param start
     * @return
     */
    public static double getSizeOfNecessityArea(BetaOperation operation, FuzzyInterval start) {

        // Funktion erzeugen
        LinearizedFunction1d necessityFunction = getNecessityFunction(operation, start);

        return necessityFunction.getIntegral(Double.MAX_VALUE * (-1.), Double.MAX_VALUE);
    }

    /**
     * gibt den optimistischsten Aufwand einer Ressource zurück
     *
     * @param r
     * @return
     */
    public static double getOptimisticWorkloadOfResourceR(BetaOperation operation, Resource r) {

        return ((FuzzyInterval) operation.getDuration()).getC1() * operation.getDemand(r).doubleValue();
    }

    /**
     * gibt den pessimisstischsten Aufwand einer Ressource zurück
     *
     * @param r
     * @return
     */
    public static double getPessimisticWorkloadOfResourceR(BetaOperation operation, Resource r) {

        return ((FuzzyInterval) operation.getDuration()).getC2() * operation.getDemand(r).doubleValue();
    }

    /**
     * *
     * gibt Lamda-Min zurück
     *
     * @param start
     * @return
     */
    public static double getLambdaMin(BetaOperation operation, FuzzyInterval start) {

        double durationMin = ((FuzzyInterval) operation.getDuration()).getC1();
        double durationNecessity = getSizeOfNecessityArea(operation, start);
        double durationPossibility = getSizeOfPossibilityArea(operation, start);

        if ((durationPossibility - durationNecessity) == 0.) {
            return Double.NaN;
        }

        return ((durationMin - durationNecessity) / (durationPossibility - durationNecessity));
    }

    /**
     * gibt Lamda-Max zurück
     *
     * @param start
     * @return
     */
    public static double getLambdaMax(BetaOperation operation, FuzzyInterval start) {

        double durationMax = ((FuzzyInterval) operation.getDuration()).getC2();
        double durationNecessity = getSizeOfNecessityArea(operation, start);
        double durationPossibility = getSizeOfPossibilityArea(operation, start);

        return ((durationMax - durationNecessity) / (durationPossibility - durationNecessity));
    }

    public static LinearizedFunction1d getDemandFunctionAtPessimisticLevelOfResourceWithLambda(BetaOperation operation, Resource r, FuzzyInterval start, FuzzyWorkloadParameters lambda) {

        LinearizedFunction1d demandFunction = getPresenceFunctionAtPessimisticLevelWithLambda(operation, start, lambda);
        if (demandFunction == null) {
            return null;
        }
        demandFunction = demandFunction.getScaleOfFunction(operation.getDemand(r).doubleValue());
        return demandFunction;

    }

    public static LinearizedFunction1d getDemandFunctionAtPessimisticLevel(BetaOperation operation, Resource r, FuzzyInterval start, double level) {

        LinearizedFunction1d demandFunction = getPresenceFunctionAtPessimisticLevel(operation, start, level);
        if (demandFunction == null) {
            return null;
        }
        demandFunction = demandFunction.getScaleOfFunction(operation.getDemand(r).doubleValue());
        return demandFunction;

    }

    public static LinearizedFunction1d getPseudoNecessity(BetaOperation a, FuzzyInterval startA, BetaOperation b, FuzzyInterval startB) {
        FuzzyInterval endB = startB.add((FuzzyInterval) b.getDuration());

        LinearizedFunction1d NA = FunctionFactory.createFunction(startA, startA.getMean(), startA.getC2());
        NA = NA.getComplementMembershipAsFunction();

        // Interval Values bis Mean erzeugen
        LinearizedFunction1d NB = FunctionFactory.createFunction(endB, endB.getC1(), endB.getMean());
        NB = NB.getComplementMembershipAsFunction();

        // negativ und positv Unendlich-Werte setzen
        NB.addValue(Double.MAX_VALUE * (-1.), 0);
        NB.addValue(Double.MAX_VALUE, 0.);

        TreeMap<Double, Double> values = new TreeMap<>();
        values.putAll(NA.getValues());
        values.put(startB.getM2(), 1.0);
        values.putAll(NB.getValues());
        LinearizedFunction1d linearizedFunction1d = new LinearizedFunction1d(values);
        linearizedFunction1d.eliminateNotRequiredValues();
        return linearizedFunction1d;

    }

    /**
     * gibt die optimistischste Workload-Funktion zurück
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getLambdaMinFunction(BetaOperation op, FuzzyInterval start) {

        double lambdaMin = 0.0;//getLambdaMin(op, start);
        if (lambdaMin == Double.NaN) {
            return null;
        }

        // Necessity- und Possibility-Funktion bereitstellen
        LinearizedFunction1d possibility = getPossibilityFunction(op, start);
        LinearizedFunction1d necessity = getNecessityFunction(op, start);

        // wenn Lambda-Min kleiner 0, dann passe den Necessity-Verlauf an
        if (lambdaMin < 0.) {
            /**
             * Was tun, wenn C1 == 0 ist?
             */
            if (((FuzzyInterval) (op.getDuration())).getC1() == 0) {
                throw new UnknownError("Dauer nahe Null kann nicht verarbeitet werden.");
            }
            LinearizedFunction1d newNecessityFunction = necessity.transformHorizontalDownTo(((FuzzyInterval) (op.getDuration())).getC1());

            if (newNecessityFunction != null) {
                // LamdaMin ist jetzt genau 0!
                lambdaMin = 0.;
                necessity = newNecessityFunction;
            } else {
                return null;
            }
        }

        // possibility- und necessity-Funktionen skalieren
        possibility = possibility.getScaleOfFunction(lambdaMin);
        necessity = necessity.getScaleOfFunction(1. - lambdaMin);

        // Affine Kombination der Necessity- und Possibility-Funktion ergibt LamdaMin-Fkt.
        LinearizedFunction1d lamdaMinFunction = possibility.add(necessity);

        return lamdaMinFunction;
    }

    /**
     * gibt die pessimisstiste Workload-Funktion zurück
     *
     * @param start
     * @return
     */
    public static LinearizedFunction1d getLambdaMaxFunction(BetaOperation op, FuzzyInterval start) {

        double lambdaMax = 1.0;// getLambdaMax(op, start);
        if (lambdaMax == Double.NaN) {
            return null;
        }

        // Necessity- und Possibility-Funktion bereitstellen
        LinearizedFunction1d possibility = getPossibilityFunction(op, start);
        LinearizedFunction1d necessity = getNecessityFunction(op, start);

        // wenn Lambda-Max größer 1, dann passe den Possibility-Verlauf an
        if (lambdaMax > 1.) {

            LinearizedFunction1d newPossibilityFunction = possibility.transformHorizontalUpTo(((FuzzyInterval) (op.getDuration())).getC2());

            if (newPossibilityFunction != null) {
                // LamdaMax ist jetzt genau 1!
                lambdaMax = 1.;
                possibility = newPossibilityFunction;
            } else {
                return null;
            }
        }

        // possibility- und necessity-Funktionen skalieren
        possibility = possibility.getScaleOfFunction(lambdaMax);
        necessity = necessity.getScaleOfFunction(1. - lambdaMax);

        // Affine Kombination der Necessity- und Possibility-Funktion ergibt LamdaMin-Fkt.
        LinearizedFunction1d lamdaMaxFunction = possibility.add(necessity);

        return lamdaMaxFunction;
    }

    public static LinearizedFunction1d getPresenceFunctionAtPessimisticLevel(BetaOperation operation, FuzzyInterval start) {
        return getPresenceFunctionAtPessimisticLevel(operation, start, operation.getBeta());
    }

    /**
     * gibt die Präsenzfunktion für ein beta zwischen 0 und 1 zurück
     *
     * @param start
     * @param beta
     * @return
     */
    public static LinearizedFunction1d getPresenceFunctionAtPessimisticLevel(BetaOperation operation, FuzzyInterval start, double level) {
        // Fehlt: überprüfung ob beta zwischen 0  und 1 liegt
        // Anmerkung: wenn beta klein, eher optimistisch; wenn beta groß, eher pessimistisch

        LinearizedFunction1d lamdaMinFunction = getLambdaMinFunction(operation, start);
        LinearizedFunction1d lamdaMaxFunction = getLambdaMaxFunction(operation, start);

        double lambdaMin = getLambdaMin(operation, start);
        double lambdaMax = getLambdaMax(operation, start);
        double lambda = lambdaMin + level * (lambdaMax - lambdaMin);

        // lamdaMin- und lamdaMax-Funktionen skalieren
        lamdaMaxFunction = lamdaMaxFunction.getScaleOfFunction(lambda);
        lamdaMinFunction = lamdaMinFunction.getScaleOfFunction(1. - lambda);

        //Affine Kombination der lamdaMin- und lamdaMax-Funktion ergibt Presence-Fkt. at Pessimistic-Level beta
        LinearizedFunction1d presenceFunction = lamdaMinFunction.add(lamdaMaxFunction);

        return presenceFunction;
    }

    public static LinearizedFunction1d getPresenceFunctionAtPessimisticLevelWithLambda(BetaOperation operation, FuzzyInterval startT, FuzzyWorkloadParameters lambda) {
        // Fehlt: Ã¼berprÃ¼fung ob beta zwischen 0  und 1 liegt
        // Anmerkung: wenn beta klein, eher optimistisch; wenn beta groÃŸ, eher pessimistisch
        FuzzyInterval duration = (FuzzyInterval) operation.getDuration();
        FuzzyInterval ende = (FuzzyInterval) startT.add(duration);

        // ZielflÃ¤cheninhalt bestimmen
        LinearizedFunction1d presenceFunctionAtPessimisticLevel = getPresenceFunctionAtPessimisticLevel(operation, startT);
        double zielFlaeche = presenceFunctionAtPessimisticLevel.getIntegral(startT.getC1(), startT.getC2() + duration.getC2());

        // Linken Verlauf erstellen (c1 bis Mitte zwischen m1 und m2)        
        LinearizedFunction1d lamdaMinFunction = getNecessityFunction(operation, startT); //  getLambdaMinFunction(operation, startT);
        LinearizedFunction1d lamdaMaxFunction = getPossibilityFunction(operation, startT); // getLambdaMaxFunction(operation, startT);

        // lamdaMin- und lamdaMax-Funktionen skalieren
        LinearizedFunction1d lamdaMaxFunctionLeft = lamdaMaxFunction.getScaleOfFunction(lambda.lambdaL);
        LinearizedFunction1d lamdaMinFunctionLeft = lamdaMinFunction.getScaleOfFunction(1. - lambda.lambdaL);

        //Affine Kombination der lamdaMin- und lamdaMax-Funktion ergibt Presence-Fkt. at Pessimistic-Level beta
        LinearizedFunction1d presenceFunctionLeft = lamdaMinFunctionLeft.add(lamdaMaxFunctionLeft);

        presenceFunctionLeft = presenceFunctionLeft.eliminateNotRequiredValues();

        // Teilungspunkt errechnen
        double grenzwertX1 = 0.;
        double grenzwertX2 = 0.;
        // Start und Ende Überschneiden sich nicht (Maximum der Notwendigkeit ist = 1)
        if (startT.getC2() <= ende.getC1()) {
            grenzwertX1 = (startT.getC2() + ende.getC1()) / 2.;
            grenzwertX2 = (startT.getC2() + ende.getC1()) / 2.;
            lambda.overlapCase = FuzzyWorkloadParameters.Case.noOverlap;
        } else {
            // Notwendigkeitsverlauf analysieren
            LinearizedFunction1d notwendigkeit = getNecessityFunction(operation, startT);
            // Anzahl stÃ¼tzstellen bei mue = 0.001
            ArrayList<Double> stuetzstellen = notwendigkeit.getSupportValues(0.0);

            // Maximum der Notwendigkeit ist > 0 und < 1
            if (stuetzstellen.size() == 4) {
                grenzwertX1 = stuetzstellen.get(1);
                grenzwertX2 = stuetzstellen.get(2);
                lambda.overlapCase = FuzzyWorkloadParameters.Case.smallOverlap;
            } // maximum der Notwendigkeit = 0
            else if (stuetzstellen.size() == 2) {
                grenzwertX1 = ende.getM1();
                grenzwertX2 = startT.getM2();
                lambda.overlapCase = FuzzyWorkloadParameters.Case.largeOverlab;
            } else {
                System.out.println("Fehler: Der Verlauf der Notwendigkeit ist unerwartet.");
                return null;
            }
        }

        // rechten und mittleren Bereich entfernen
        TreeMap<Double, Double> valuesOFPresenceFunctionLeft = new TreeMap<>(presenceFunctionLeft.getValues());
        // Grenzwerte hinzufügen
        valuesOFPresenceFunctionLeft.put(grenzwertX1, presenceFunctionLeft.getValue(grenzwertX1));

        for (Iterator<Double> iterator = valuesOFPresenceFunctionLeft.keySet().iterator(); iterator.hasNext();) {
            Double xx = iterator.next();
            if (xx > grenzwertX1) {
                iterator.remove();
            }
        }

        // +-unendlich hinzufügen
        valuesOFPresenceFunctionLeft.put(Double.MAX_VALUE * (-1.), 0.);
//        valuesOFPresenceFunctionLeft.put(Double.MAX_VALUE, 0.);

        presenceFunctionLeft = new LinearizedFunction1d(valuesOFPresenceFunctionLeft);

        // iterativ Rechten und Mittleren Bereich bestimmen bis diese der SollRechtenUndMittlerenFlÃ¤che entspricht
        // lamdaMin- und lamdaMax-Funktionen skalieren
        double minimalLambda = 0.;
        double maximalLambda = 1.;
        double aktuellLambdaRight = (minimalLambda + maximalLambda) / 2.;

        double fehler = Double.MAX_VALUE;
        double fehlerGrenze = 1.;

        double deltaFehler = 100.; // = positiv = Flächeninhalt zu gering
        double oldDelta = Double.MAX_VALUE;

        while (deltaFehler > fehlerGrenze) {

            if (fehler != Double.MAX_VALUE) {
                if (fehler > 0.) {
                    minimalLambda = aktuellLambdaRight;
                    aktuellLambdaRight = (minimalLambda + maximalLambda) / 2.;
                }
                if (fehler < 0.) {
                    maximalLambda = aktuellLambdaRight;
                    aktuellLambdaRight = (minimalLambda + maximalLambda) / 2.;
                }
            }
            if (oldDelta == deltaFehler) {
                return null;
            }
//            if (Math.abs(aktuellLambdaRight) < 0.001 || Math.abs(aktuellLambdaRight) > 0.999 || aktuellLambdaRight == maximalLambda || aktuellLambdaRight == minimalLambda) {
//                return null;
//            }
            // lamdaMin- und lamdaMax-Funktionen mit aktuellem lambdaRight skalieren
            LinearizedFunction1d lamdaMaxFunctionRight = lamdaMaxFunction.getScaleOfFunction(aktuellLambdaRight);
            LinearizedFunction1d lamdaMinFunctionRight = lamdaMinFunction.getScaleOfFunction(1. - aktuellLambdaRight);

            //Affine Kombination der lamdaMin- und lamdaMax-Funktion ergibt Presence-Fkt. at Pessimistic-Level beta
            LinearizedFunction1d presenceFunctionRight = lamdaMinFunctionRight.add(lamdaMaxFunctionRight);
            presenceFunctionRight = presenceFunctionRight.eliminateNotRequiredValues();

            // linken und mittleren Bereich entfernen
            TreeMap<Double, Double> valuesOFPresenceFunctionRight = new TreeMap<>(presenceFunctionRight.getValues());
            // Grenzwerte hinzufügen
//            valuesOFPresenceFunctionRight.put(grenzwertX1, presenceFunctionRight.getValue(grenzwertX1));
            valuesOFPresenceFunctionRight.put(grenzwertX2, presenceFunctionRight.getValue(grenzwertX2));

            for (Iterator<Double> iterator = valuesOFPresenceFunctionRight.keySet().iterator(); iterator.hasNext();) {
                Double xx = iterator.next();
                if (xx < grenzwertX2) {
                    iterator.remove();
                }
            }

            // +-unendlich hinzufÃ¼gen
//            valuesOFPresenceFunctionRight.put(Double.MAX_VALUE * (-1.), 0.);
            valuesOFPresenceFunctionRight.put(Double.MAX_VALUE, 0.);

            presenceFunctionRight = new LinearizedFunction1d(valuesOFPresenceFunctionRight);

            // Mittleren Bereich ermitteln
            LinearizedFunction1d lamdaMinFunctionMiddle = getNecessityFunction(operation, startT);//getLambdaMinFunction(operation, startT);
            LinearizedFunction1d lamdaMaxFunctionMiddle = getPossibilityFunction(operation, startT);//getLambdaMaxFunction(operation, startT);

            // Values bestimmen
            TreeMap<Double, Double> minValuesMiddle = lamdaMinFunctionMiddle.getValues();
            TreeMap<Double, Double> maxValuesMiddle = lamdaMaxFunctionMiddle.getValues();

            // Alle Stützstellen beider Funktionen ermitteln
            ArrayList<Double> allStuetzstellen = new ArrayList<>();
            for (Double xx : minValuesMiddle.keySet()) {
                if (!allStuetzstellen.contains(xx)) {
                    allStuetzstellen.add(xx);
                }
            }
            for (Double xx : maxValuesMiddle.keySet()) {
                if (!allStuetzstellen.contains(xx)) {
                    allStuetzstellen.add(xx);
                }
            }

            // Values der Mittleren Funktion einzelnd hinzufÃ¼gen
            TreeMap<Double, Double> middleValues = new TreeMap<>();

            if (lambda.overlapCase.equals(Case.smallOverlap)) {
                for (Double xStuetz : allStuetzstellen) {
                    if (xStuetz >= grenzwertX1 && xStuetz <= grenzwertX2) {
                        double valueOfMinFunctionAtX = lamdaMinFunctionMiddle.getValue(xStuetz);
                        double valueOfMaxFunctionAtX = lamdaMaxFunctionMiddle.getValue(xStuetz);

                        // aktuelles Lambda zwischen LambdaLeft und LambdaRight für Stelle x interpolieren
                        double LambdaAtX = lambda.lambdaL + (aktuellLambdaRight - lambda.lambdaL) / (grenzwertX2 - grenzwertX1) * (xStuetz - grenzwertX1);

                        // von Hand affine Kombination an der Stelle mit LambdaAtX ausführen
                        middleValues.put(xStuetz, (valueOfMaxFunctionAtX * LambdaAtX) + (valueOfMinFunctionAtX * (1. - LambdaAtX)));
                    }
                }
                // +-unendlich hinzufügen
                middleValues.put(Double.MAX_VALUE * (-1.), 0.);
                middleValues.put(Double.MAX_VALUE, 0.);

            }

            LinearizedFunction1d presenceFunctionMiddle = new LinearizedFunction1d(middleValues);
            presenceFunctionMiddle.eliminateNotRequiredValues();

            // Funktionen zusammenfügen
            TreeMap<Double, Double> presenceFunctionLeftMiddleRightValues = presenceFunctionLeft.getValues();

            if (lambda.overlapCase.equals(Case.smallOverlap)) {
                // Werte fÃ¼r den Mittleren Bereich Ã¼bernehmen
                for (Double xxx : presenceFunctionMiddle.getValues().keySet()) {
                    if ((xxx > grenzwertX1) && (xxx < grenzwertX2)) {
                        presenceFunctionLeftMiddleRightValues.put(xxx, presenceFunctionMiddle.getValue(xxx));
                    }
                }
            }
            // Werte für den Rechten Bereich übernehmen
            for (Double xxx : presenceFunctionRight.getValues().keySet()) {
                if (xxx >= grenzwertX2) {
                    presenceFunctionLeftMiddleRightValues.put(xxx, presenceFunctionRight.getValue(xxx));
                }
            }

            // +-unendlich hinzufügen
            presenceFunctionLeftMiddleRightValues.put(Double.MAX_VALUE * (-1.), 0.);
            presenceFunctionLeftMiddleRightValues.put(Double.MAX_VALUE, 0.);

            LinearizedFunction1d presenceFunctionLeftMiddleRight = new LinearizedFunction1d(presenceFunctionLeftMiddleRightValues);
            presenceFunctionLeftMiddleRight.eliminateNotRequiredValues();

            // kontrolle
            double istInhalt = presenceFunctionLeftMiddleRight.getIntegral(Double.MIN_VALUE, Double.MAX_VALUE);

            // aktuellen fehler bestimmen
            fehler = zielFlaeche - (istInhalt);
            oldDelta = deltaFehler;
            deltaFehler = Math.sqrt(fehler * fehler);

            //Überprüfen, ob der Fehler kleiner als der Grenzfehler ist
            if (deltaFehler < fehlerGrenze) {
                lambda.lambdaR = aktuellLambdaRight;
                return presenceFunctionLeftMiddleRight;
            }

        }

//        System.out.println("Es konnte kein gültiges LambdaRight gefunden werden für Operation ." +operation);
        return null;
    }

}
