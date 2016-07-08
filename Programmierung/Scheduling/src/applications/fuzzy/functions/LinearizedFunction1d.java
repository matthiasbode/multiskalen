/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.functions;

import bijava.math.function.ScalarFunction1d;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import math.FieldElement;
import math.function.ExtremumEntry;

/**
 *
 * Diese Funktion beschreibt eine Funktion, die über den kompletten
 * Double-Wertebereich defininiert ist.
 *
 * @author brandt
 */
public class LinearizedFunction1d implements ScalarFunction1d {

    // Stützstellen als TreeMap (x, mue)
    private final TreeMap<Double, Double> values;

    public LinearizedFunction1d(TreeMap<Double, Double> values) {
        this.values = values;
    }

    public LinearizedFunction1d() {
        this.values = new TreeMap<>();
        // negativ und positiv unendlich 0. setzen => leere Menge!
        values.put(Double.MAX_VALUE * (-1.), 0.);
        values.put(Double.MAX_VALUE, 0.);
    }

    public LinearizedFunction1d(Double from, Double to, Double value) {
        this.values = new TreeMap<>();
        this.values.put(Double.NEGATIVE_INFINITY, 0.);
        this.values.put(from, value);
        this.values.put(to, value);
        if (!values.containsKey(Double.POSITIVE_INFINITY)) {
            this.values.put(Double.POSITIVE_INFINITY, 0.);
        }
    }

    public LinearizedFunction1d(LinearizedFunction1d f) {
        this.values = new TreeMap<>(f.values);
    }

    @Override
    public double getValue(double x) {
        // wenn x Stützstelle ist, diese zurückgeben
        if (values.containsKey(x)) {
            return values.get(x);
        }

        // wenn nicht, StreckenSegment suchen und linear interpolieren
        Entry pre = values.lowerEntry(x);
        if (pre == null) {
            return 0;
        }
        Entry suc = values.higherEntry(x);
        if (suc == null) {
            return 0;
        }
        return ((double) pre.getValue()
                + ((double) suc.getValue() - (double) pre.getValue())
                / ((double) suc.getKey() - (double) pre.getKey())
                * (x - (double) pre.getKey()));
    }

    public TreeMap<Double, Double> getValues() {
        return values;
    }

    public void addValue(double x, double mue) {
        values.put(x, mue);
    }

    public void removeValue(double x) {
        values.remove(x);
    }

    /**
     * Gibt den minimalen Verlauf von dieser Funktion sowie der Funktion s
     * zurück.
     *
     * @param s Funktion mit der das Minimum gebildet werden soll.
     * @return Minimaler Funktionsverlauf der beiden Funktionen.
     */
    public LinearizedFunction1d getMin(LinearizedFunction1d s) {

        // Kopie der eigenen Values erstellen
        TreeMap<Double, Double> myValues = (TreeMap<Double, Double>) this.values.clone();
        // Fremde Values bereithalten
        TreeMap<Double, Double> otherValues = (TreeMap<Double, Double>) s.getValues().clone();

        // Beide Funktionen um jeweils andere Stützstellen erweitern
        for (Double x : otherValues.keySet()) {
            if (!myValues.containsKey(x)) {
                myValues.put(x, this.getValue(x));
            }
        }
        for (Double x : myValues.keySet()) {
            if (!otherValues.containsKey(x)) {
                otherValues.put(x, s.getValue(x));
            }
        }

        // MinimalFunktion definieren
        TreeMap<Double, Double> minValues = (TreeMap<Double, Double>) myValues.clone();

        // neue Funktion erstellen
        for (Double x : myValues.keySet()) {
            // alle bis zum letzen auswerten
            if (!(Objects.equals(myValues.lastKey(), x))) {
                // aktuelle Punkte des Intervalls x bis higherKey(x) festlegen
                Double p1x = x;
                Double p2x = myValues.higherKey(x);
                Double q1x = p1x;
                Double q2x = p2x;
                Double p1y = myValues.get(p1x);
                Double p2y = myValues.get(p2x);
                Double q1y = otherValues.get(q1x);
                Double q2y = otherValues.get(q2x);

                if ((p1y <= q1y) && (p2y <= q2y)) {
                    // keine Kreuzung der Geraden im Interval
                    minValues.put(p1x, p1y);
                } else {
                    if ((p1y >= q1y) && (p2y > q2y)) {
                        // keine Kreuzung der Geraden im Intervall
                        minValues.put(p1x, q1y);
                    } else {
                        //  Kreuzung der Geraden im Intervall => Schnittpunkt herausfinden und setzen
                        if (((p1y > q1y) && (p2y < q2y)) || ((p1y < q1y) && (p2y > q2y))) {

                            // Zaehler
                            double zx = (p1x * p2y - p1y * p2x) * (q1x - q2x) - (p1x - p2x) * (q1x * q2y - q1y * q2x);
                            double zy = (p1x * p2y - p1y * p2x) * (q1y - q2y) - (p1y - p2y) * (q1x * q2y - q1y * q2x);

                            // Nenner
                            double n = (p1x - p2x) * (q1y - q2y) - (p1y - p2y) * (q1x - q2x);

                            // Koordinaten des Schnittpunktes
                            double xx = zx / n;
                            double yy = zy / n;

                            // Schnittpunkt hinzufuegen
                            minValues.put(xx, yy);
                        }
                    }
                }
            } else {
                if (myValues.get(x) <= otherValues.get(x)) {
                    minValues.put(x, myValues.get(x));
                } else {
                    minValues.put(x, otherValues.get(x));
                }
            }
        }

        return new LinearizedFunction1d(minValues);
    }

    // gibt den maximalen Verlauf von zwei Funktionen zurück
    public LinearizedFunction1d getMax(LinearizedFunction1d s) {

        // Kopie der eigenen Values erstellen
        TreeMap<Double, Double> myValues = (TreeMap<Double, Double>) values.clone();
        // Fremde Values bereithalten
        TreeMap<Double, Double> otherValues = (TreeMap<Double, Double>) s.getValues().clone();

        // Beide Funktionen um jeweils andere Stützstellen erweitern
        for (Double x : otherValues.keySet()) {
            if (!myValues.containsKey(x)) {
                myValues.put(x, this.getValue(x));
            }
        }
        for (Double x : myValues.keySet()) {
            if (!otherValues.containsKey(x)) {
                otherValues.put(x, s.getValue(x));
            }
        }

        // MinimalFunktion definieren
        TreeMap<Double, Double> maxValues = (TreeMap<Double, Double>) myValues.clone();

        // neue Funktion erstellen
        for (Double x : myValues.keySet()) {
            // alle bis zum letzen auswerten
            if (!(Objects.equals(myValues.lastKey(), x))) {
                // aktuelle Punkte des Intervalls x bis higherKey(x) festlegen
                Double p1x = x;
                Double p2x = myValues.higherKey(x);
                Double q1x = p1x;
                Double q2x = p2x;
                Double p1y = myValues.get(p1x);
                Double p2y = myValues.get(p2x);
                Double q1y = otherValues.get(q1x);
                Double q2y = otherValues.get(q2x);

                if ((p1y <= q1y) && (p2y <= q2y)) {
                    // keine Kreuzung der Geraden im Interval
                    maxValues.put(p1x, q1y);
                } else {
                    if ((p1y >= q1y) && (p2y > q2y)) {
                        // keine Kreuzung der Geraden im Intervall
                        maxValues.put(p1x, p1y);
                    } else {
                        //  Kreuzung der Geraden im Intervall => Schnittpunkt herausfinden und setzen
                        if (((p1y > q1y) && (p2y < q2y)) || ((p1y < q1y) && (p2y > q2y))) {

                            // Zaehler
                            double zx = (p1x * p2y - p1y * p2x) * (q1x - q2x) - (p1x - p2x) * (q1x * q2y - q1y * q2x);
                            double zy = (p1x * p2y - p1y * p2x) * (q1y - q2y) - (p1y - p2y) * (q1x * q2y - q1y * q2x);

                            // Nenner
                            double n = (p1x - p2x) * (q1y - q2y) - (p1y - p2y) * (q1x - q2x);

                            // Koordinaten des Schnittpunktes
                            double xx = zx / n;
                            double yy = zy / n;

                            // Schnittpunkt hinzufuegen
                            maxValues.put(xx, yy);
                        }
                    }
                }
            } else {
                if (myValues.get(x) <= otherValues.get(x)) {
                    maxValues.put(x, otherValues.get(x));
                } else {
                    maxValues.put(x, myValues.get(x));
                }
            }
        }

        return new LinearizedFunction1d(maxValues);
    }

    // gibt das Komplement zu einer Zugehörigkeitsfunktion zurück (1. - Mue)
    public LinearizedFunction1d getComplementMembershipAsFunction() {

        // Kopie der eigenen Values erstellen
        TreeMap<Double, Double> complementValues = (TreeMap<Double, Double>) values.clone();

        // Komplement von allen Values bilden (1. - Mue) 
        for (Double x : complementValues.keySet()) {
            complementValues.replace(x, (1. - (complementValues.get(x))));
        }

        return new LinearizedFunction1d(complementValues);
    }

    // eliminiert überflüssige Stützstellen (die beispielsweise auf einer Geraden liegen)
    public LinearizedFunction1d eliminateNotRequiredValues() {

        // Kopie von Values erstellen
        TreeMap<Double, Double> copy = (TreeMap<Double, Double>) values.clone();

        // Überflüßige Stützstellen ausfindig machen und entfernen
        for (Double x : values.keySet()) {
            // bis zum VORVORletzen Key durchlaufen
            if (x < values.lowerKey(values.lastKey())) {
                // Werte bestimmen
                double xi = x;
                double xi1 = values.higherKey(xi);
                double xi2 = values.higherKey(xi1);
                double yi = values.get(xi);
                double yi1 = values.get(xi1);
                double yi2 = values.get(xi2);

                // Fallunterscheidung, falls xi = xi1 oder xi1 = xi2
                if ((xi != xi1) && (xi1 != xi2)) {
                    // Winkel (Tangens) von diesem und nächstem Segment aufstellen
                    double tangensOfI1 = (yi1 - yi) / (xi1 - xi);
                    double tangensOfI2 = (yi2 - yi1) / (xi2 - xi1);

                    // Auf 6 Stelle Runden
//                    tangensOfI1 = ((Math.round(tangensOfI1 * 1000000.0)) / 1000000.0);
//                    tangensOfI2 = ((Math.round(tangensOfI2 * 1000000.0)) / 1000000.0);
                    if (tangensOfI1 == tangensOfI2) {
                        // Stützstelle eliminieren
                        copy.remove(xi1);
                    }
                } else {
                    if (xi == xi1) {
                        copy.remove(xi);
                    }
                }
            }
        }

        return new LinearizedFunction1d(copy);
    }

    // gibt die Fläche unterhalb eines Funktionsverlaufes zurück
    public double getIntegral(double x_min, double x_max) {

        double xHighMin = values.higherKey(x_min);
        double xLowMax = values.lowerKey(x_max);

        // Teilfläche vom ersten StreckenSegment bestimmen
        double summe = 0.5 * (xHighMin - x_min) * (values.get(xHighMin) + this.getValue(x_min));
        // ZwischenStreckenSegmente aufaddieren
        for (Double x : this.values.keySet()) {
            if (x >= xHighMin && x < xLowMax) {
                summe += 0.5 * (values.higherKey(x) - x) * (values.get(values.higherKey(x)) + values.get(x));
            }
        }
        // Teilfläche vom letzten StreckenSegment bestimmen
        summe += 0.5 * (x_max - xLowMax) * (values.get(xLowMax) + this.getValue(x_max));

        // Auf 6 Stellen Runden
        summe = ((Math.round(summe * 1000000.0)) / 1000000.0);

        return summe;
    }

    // gibt die x-Koordinate des Schwerpunkt unter einem Funktionsverlauf zurück 
    // (über den ganzen Verlauf wird der Schwerpunkt einer Fuzzy-Zahl zurückgegeben)
    public double getCenterOfGravityX(double x_min, double x_max) {

        double xHighMin = values.higherKey(x_min);
        double xLowMax = values.lowerKey(x_max);

        // Teilfläche vom ersten StreckenSegment bestimmen
        double integral_fx_mult_x = 0.5 * (xHighMin - x_min) * (values.get(xHighMin) * xHighMin + this.getValue(x_min) * x_min);
        // ZwischenStreckenSegmente aufaddieren
        for (Double x : this.values.keySet()) {
            if (x >= xHighMin && x < xLowMax) {
                integral_fx_mult_x += 0.5 * (values.higherKey(x) - x) * (values.get(values.higherKey(x)) * values.higherKey(x) + values.get(x) * x);
            }
        }
        // Teilfläche vom letzten StreckenSegment bestimmen
        integral_fx_mult_x += 0.5 * (x_max - xLowMax) * (values.get(xLowMax) * xLowMax + this.getValue(x_max) * x_max);

        double integral = getIntegral(x_min, x_max);

        double xS = integral_fx_mult_x / integral;

        // Auf 6 Stellen Runden
        xS = ((Math.round(xS * 1000000.0)) / 1000000.0);

        return xS;
    }

    // Skaliert die Funktion (wird für die affinie Kombination gebraucht)
    public LinearizedFunction1d getScaleOfFunction(double scale) {

        // Kopie der eigenen Values erstellen
        TreeMap<Double, Double> scaleValues = (TreeMap<Double, Double>) values.clone();

        for (Double x : scaleValues.keySet()) {
            //Werte um Faktor scale verändern
            scaleValues.replace(x, scaleValues.get(x) * scale);
        }

        return new LinearizedFunction1d(scaleValues);
    }

    // gibt die Summe zweier Funktionen zurück
    public LinearizedFunction1d add(LinearizedFunction1d s) {
        if (s == null) {
            throw new NullPointerException("Der Summand ist Null");
        }

        TreeMap<Double, Double> summationFunction = new TreeMap<>();

        // eigene Stüzustellen durchlaufen und Values aufsummieren
        for (Double x : this.values.keySet()) {
            summationFunction.put(x, this.getValue(x) + s.getValue(x));
        }

        // fremde Stüzustellen durchlaufen und Values aufsummieren
        for (Double xx : s.getValues().keySet()) {
            if (!summationFunction.containsKey(xx)) {
                summationFunction.put(xx, this.getValue(xx) + s.getValue(xx));
            }
        }

        return new LinearizedFunction1d(summationFunction);
    }

    // gibt die Subtraktion zweier Funktionen zurück
    public LinearizedFunction1d sub(LinearizedFunction1d s) {

        TreeMap<Double, Double> subtractionFunction = new TreeMap<>();

        // eigene Stüzustellen durchlaufen und Values aufsummieren
        for (Double x : this.values.keySet()) {
            subtractionFunction.put(x, this.getValue(x) - s.getValue(x));
        }

        // fremde Stüzustellen durchlaufen und Values aufsummieren
        for (Double xx : s.getValues().keySet()) {
            if (!subtractionFunction.containsKey(xx)) {
                subtractionFunction.put(xx, this.getValue(xx) - s.getValue(xx));
            }
        }

        return new LinearizedFunction1d(subtractionFunction);
    }

    public Entry<Double, Double> getMin() {
        if (values.isEmpty()) {
            return null;
        }
        Double minKey = values.firstKey();
        Double minValue = values.get(minKey);
        for (Double fieldElement : values.keySet()) {
            if (values.lastKey().equals(fieldElement)) {
                continue;
            }
            Double currentValue = values.get(fieldElement);
            if (values.get(fieldElement) < (minValue)) {
                minKey = fieldElement;
                minValue = currentValue;
            }
        }
        return new ExtremumEntry(minKey, minValue);
    }

    public Entry<Double, Double> getMax() {
        if (values.isEmpty()) {
            return new AbstractMap.SimpleEntry<Double, Double>(0., 0.);
        }

        Double maxValue = values.get(values.firstKey());
        Double maxPos = values.firstKey();

        for (Double key : values.keySet()) {
            Double value = values.get(key);
            if (value > maxValue) {
                maxPos = key;
                maxValue = value;
            }
        }

        return new AbstractMap.SimpleEntry<Double, Double>(maxPos, maxValue);
    }

    public ArrayList<Double> getSupportValues(double fx) {

        // StÃ¼tzstellenliste anlegen
        ArrayList<Double> supportValues = new ArrayList<>();

        // StÃ¼tzstellen finden
        for (Double key : values.keySet()) {

            // Wenn der Key den gewÃ¼nschten Funktionswert besitzt, diesen Ã¼bernehmen
            if (values.get(key) == fx) {
                supportValues.add(key);
            } else {
                // Ã¼berprÃ¼fen ob Ende erreicht
                if (values.higherKey(key) != null) {
                    Double now_x = key;
                    Double now_y = values.get(key);
                    Double suc_x = values.higherKey(key);
                    Double suc_y = values.get(suc_x);

                    // Falls fx zwischen now_y und suc_y, dann interpolieren und StÃ¼tzstelle hinzufÃ¼gen
                    if (((now_y < fx) && (suc_y > fx)) || ((now_y > fx) && (suc_y < fx))) {
                        // Interpolieren
                        supportValues.add(now_x + (fx - now_y) / (suc_y - now_y) * (suc_x - now_x));
                    }
                }
            }

        }

        return supportValues;
    }

    public LinearizedFunction1d transformHorizontalUpTo(double zielFlaechenInhalt) {

        // nicht benötigte Punkte entfernen
        this.eliminateNotRequiredValues();

        double DPI = this.getIntegral(Double.MAX_VALUE * (-1.), Double.MAX_VALUE);
        double aktuelleFlaeche = DPI;
//        double z = zielFlaechenInhalt;
//
//        if (!(DPI < z)) {
//            return this;
//        }
//
//        double aS = 0.;
//        double bS = 0.;
//        double cF = 0.;
//        double dF = 0.;
//
//        // Erkenne m1 und m2
//        ArrayList<Double> m1m2 = this.getSupportValues(1.);
//        if (m1m2.size() == 1) {
//            bS = m1m2.get(0);
//            cF = m1m2.get(0);
//        }
//        if (m1m2.size() == 2) {
//            bS = m1m2.get(0);
//            cF = m1m2.get(1);
//        }
//        // Mehr als 2 Stützstellen sind nicht definiert! FEHLER!
//        if (m1m2.size() > 2) {
//            return null;
//        }
//
//        // Erkenne c1 und c2
//        ArrayList<Double> c1c2 = this.getSupportValues(0.);
//
//        // Plus- und Minus-Unendlich bei niveau = 0. entfernen
//        if (c1c2.size() == 4) {
//            c1c2.remove(3);
//            c1c2.remove(0);
//        }
//
//        if (c1c2.size() == 2) {
//            aS = c1c2.get(0);
//            dF = c1c2.get(1);
//        }
//        // Mehr oder weniger als 2 Stützstellen sind nicht definiert! FEHLER!
//        if (c1c2.size() != 2) {
//            return null;
//        }
//
//        TreeMap<Double, Double> valuesNEW = new TreeMap<Double, Double>();
//        valuesNEW.put(aS, this.values.get(aS));
//        double bS_ = bS - 2 * (z-DPI) * (bS - aS) / (bS - aS + dF - cF);
//        double cF_ = cF + 2 * (z-DPI) * (dF - cF) / (bS - aS + dF - cF);
//
//        valuesNEW.put(bS_, 1.0);
//        valuesNEW.put(cF_, 1.0);
//        valuesNEW.put(dF, this.values.get(dF));
//
//        LinearizedFunction1d newFunktion = new LinearizedFunction1d(valuesNEW);
//        aktuelleFlaeche = newFunktion.getIntegral(aS, dF);
//
//       
//        return newFunktion;

        // Verlauf zwischen c1 und m1 und m2 und c2 anpassen
        TreeMap<Double, Double> valuesNEW = new TreeMap<Double, Double>();
        TreeMap<Double, Double> valuesOLD = (TreeMap<Double, Double>) this.values.clone();
        LinearizedFunction1d funktionOLD = new LinearizedFunction1d(valuesOLD);

        // iterativ Funktionsverlauf gemäß Masmoudi-Paper Seite 139 verändern
        boolean canTransform = true;
        while (canTransform) {

            double m1x = 0.;
            double m2x = 0.;
            double c1x = 0.;
            double c2x = 0.;

            // Erkenne m1 und m2
            ArrayList<Double> m1m2 = funktionOLD.getSupportValues(1.);
            if (m1m2.size() == 1) {
                m1x = m1m2.get(0);
                m2x = m1m2.get(0);
            }
            if (m1m2.size() == 2) {
                m1x = m1m2.get(0);
                m2x = m1m2.get(1);
            }
            // Mehr als 2 Stützstellen sind nicht definiert! FEHLER!
            if (m1m2.size() > 2) {
                return null;
            }

            // Erkenne c1 und c2
            ArrayList<Double> c1c2 = funktionOLD.getSupportValues(0.);

            // Plus- und Minus-Unendlich bei niveau = 0. entfernen
            if (c1c2.size() == 4) {
                c1c2.remove(3);
                c1c2.remove(0);
            }

            if (c1c2.size() == 2) {
                c1x = c1c2.get(0);
                c2x = c1c2.get(1);
            }
            // Mehr oder weniger als 2 Stützstellen sind nicht definiert! FEHLER!
            if (c1c2.size() != 2) {
                return null;
            }

            // Schrittweiten für Verlaufanpassungen errechnen (1/100)
            // GESCHICKTERE LÖSUNG MIT VERHÄLTNISS AUS FLÄCHEN MÖGLICH!!!
            double rechteckflaecheIST = m2x - m1x;

            double verhaeltnisAktuellZiel = (aktuelleFlaeche - rechteckflaecheIST) / (DPI - rechteckflaecheIST);

            double dxLeft = (m1x - c1x) / (verhaeltnisAktuellZiel * 3.);
            double dxRight = (c2x - m2x) / (verhaeltnisAktuellZiel * 3.);

            for (Double x : valuesOLD.keySet()) {
                // ist x zwischen c1 und m1
                boolean treffer = false;
                if ((x > c1x) && (x <= m1x)) {
                    double newX = x - valuesOLD.get(x) * dxLeft;
                    if (newX < c1x) {
                        newX = c1x;
                    }
                    valuesNEW.put(newX, valuesOLD.get(x));
                    treffer = true;
                }
                if ((x >= m2x) && (x < c2x)) {
                    double newX = x + valuesOLD.get(x) * dxRight;
                    if (newX > c2x) {
                        newX = c2x;
                    }
                    valuesNEW.put(newX, valuesOLD.get(x));
                    treffer = true;
                }
                if (!treffer) {
                    valuesNEW.put(x, valuesOLD.get(x));
                }
            }
            if (valuesNEW.equals(valuesOLD)) {
//                canTransform = false;
//                return this;
                throw new UnsupportedOperationException("Kann nicht so genau aufgelöst werden!");
            }
            // neue Funktion
            LinearizedFunction1d newFunktion = new LinearizedFunction1d(valuesNEW);
            aktuelleFlaeche = newFunktion.getIntegral(c1x, c2x);

            // Flächen-Verhältnis auf 5 Stellen runden
            double verhaeltnis = aktuelleFlaeche / DPI;
            verhaeltnis = ((int) (verhaeltnis * 100000.)) / 100000.;

//            if (verhaeltnis >= 1.) {
            if (Math.abs(verhaeltnis - 1.) < 0.0005) {
                //System.out.println("Verhältnis: " + verhaeltnis);
                return new LinearizedFunction1d(valuesNEW);
            }

            valuesOLD = (TreeMap<Double, Double>) valuesNEW.clone();
            valuesNEW.clear();
            funktionOLD = new LinearizedFunction1d(valuesOLD);
        }

        return null;
    }

    public LinearizedFunction1d transformHorizontalDownTo(double zielFlaechenInhalt) {

        // nicht benötigte Punkte entfernen
        this.eliminateNotRequiredValues();

        double urspruenglicheFlaeche = this.getIntegral(Double.MAX_VALUE * (-1.), Double.MAX_VALUE);
        double aktuelleFlaeche = urspruenglicheFlaeche;
        double zielFlaeche = zielFlaechenInhalt;

        // Verlauf zwischen c1 und m1 und m2 und c2 anpassen
        TreeMap<Double, Double> valuesNEW = new TreeMap<Double, Double>();
        TreeMap<Double, Double> valuesOLD = (TreeMap<Double, Double>) this.values.clone();
        LinearizedFunction1d funktionOLD = new LinearizedFunction1d(valuesOLD);

        // iterativ Funktionsverlauf gemäß Masmoudi-Paper Seite 139 verändern
        boolean canTransform = true;
        while (canTransform) {

            double m1x = 0.;
            double m2x = 0.;
            double c1x = 0.;
            double c2x = 0.;

            // Erkenne m1 und m2
            ArrayList<Double> m1m2 = funktionOLD.getSupportValues(1.);
            if (m1m2.size() == 1) {
                m1x = m1m2.get(0);
                m2x = m1m2.get(0);
            }
            if (m1m2.size() == 2) {
                m1x = m1m2.get(0);
                m2x = m1m2.get(1);
            }
            // Mehr als 2 Stützstellen sind nicht definiert! FEHLER!
            if (m1m2.size() > 2) {
                return null;
            }

            // Erkenne c1 und c2
            ArrayList<Double> c1c2 = funktionOLD.getSupportValues(0.);

            // Plus- und Minus-Unendlich bei niveau = 0. entfernen
            if (c1c2.size() == 4) {
                c1c2.remove(3);
                c1c2.remove(0);
            }

            if (c1c2.size() == 2) {
                c1x = c1c2.get(0);
                c2x = c1c2.get(1);
            }
            // Mehr oder weniger als 2 Stützstellen sind nicht definiert! FEHLER!
            if (c1c2.size() != 2) {
                return null;
            }

            // Schrittweiten für Verlaufanpassungen errechnen
            double rechteckflaecheIST = m2x - m1x;

            double verhaeltnisAktuellZiel = (aktuelleFlaeche - rechteckflaecheIST) / (zielFlaeche - rechteckflaecheIST);

            double dxLeft = (m1x - c1x) / (verhaeltnisAktuellZiel * 3.);
            double dxRight = (c2x - m2x) / (verhaeltnisAktuellZiel * 3.);

            for (Double x : valuesOLD.keySet()) {
                // ist x zwischen c1 und m1
                boolean treffer = false;
                if ((x >= c1x) && (x < m1x)) {
                    double newX = x + (1. - valuesOLD.get(x)) * dxLeft;
                    if (newX > m1x) {
                        newX = m1x;
                    }
                    valuesNEW.put(newX, valuesOLD.get(x));
                    treffer = true;
                }
                if ((x > m2x) && (x <= c2x)) {
                    double newX = x - (1. - valuesOLD.get(x)) * dxRight;
                    if (newX < m2x) {
                        newX = m2x;
                    }
                    valuesNEW.put(newX, valuesOLD.get(x));
                    treffer = true;
                }
                if (!treffer) {
                    valuesNEW.put(x, valuesOLD.get(x));
                }
            }

            // neue Funktion
            LinearizedFunction1d newFunktion = new LinearizedFunction1d(valuesNEW);
            aktuelleFlaeche = newFunktion.getIntegral(c1x, c2x);

            // Flächen-Verhältnis auf 5 Stellen runden
            double verhaeltnis = aktuelleFlaeche / zielFlaeche;
            verhaeltnis = ((int) (verhaeltnis * 100000.)) / 100000.;

            if (verhaeltnis <= 1.) {
                //System.out.println("Verhältnis: " + verhaeltnis);
                return new LinearizedFunction1d(valuesNEW);
            }

            valuesOLD = (TreeMap<Double, Double>) valuesNEW.clone();
            valuesNEW.clear();
            funktionOLD = new LinearizedFunction1d(valuesOLD);
        }

        return null;
    }

}
