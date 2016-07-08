/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.number.discrete;

import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;

/**
 *
 * @author bode
 */
public class FuzzyFactory {

    public static int DEFAULT_RESOLUTION = 2;

    public static DiscretizedFuzzyInterval createFromGauss(double m1, double m2, double alpha, double beta, int resolution, double varianz) {

        if ((alpha < 0) || (beta < 0)) {
            throw new IllegalArgumentException("alphaL and alphaR must be positive real numbers");
        }

        /**
         * quadratischer ansatz für Verlauf der Kurve
         */
        double sigmaL = alpha * (1. / varianz);
        double sigmaR = beta * (1. / varianz);

        AlphaCutSet[] alphaCutSets = new AlphaCutSet[resolution + 1];
        for (int i = 0; i < resolution + 1; i++) {
            double niveau = (1.0 / resolution) * i;
            // Werte ermitteln
            double min = m1 - alpha;
            double max = m2 + beta;
            if (i != 0.) {
                double delta = Math.sqrt((-2.) * (Math.log(niveau)));
                min = m1 - sigmaL * delta;
                max = m2 + sigmaR * delta;
            }

            //System.out.println(niveau + ":" + "(" + min + "," + max + ")");
            alphaCutSets[i] = new AlphaCutSet(niveau, min, max);
        }

        DiscretizedFuzzyInterval interval = new DiscretizedFuzzyInterval(alphaCutSets);
        return interval;

    }

    public static DiscretizedFuzzyInterval createLinearInterval(double mean, double gamma) {
        return createLinearInterval(mean - gamma, mean + gamma, gamma, gamma, DEFAULT_RESOLUTION);
    }

    public static DiscretizedFuzzyInterval createLinearInterval(double m1, double m2, double alpha, double beta) {
        return createLinearInterval(m1, m2, alpha, beta, DEFAULT_RESOLUTION);
    }

    public static DiscretizedFuzzyInterval createLinearIntervalFromPoints(double c1, double m1, double m2, double c2) {
        return createLinearInterval(m1, m2, m1 - c1, c2 - m2);
    }

    public static DiscretizedFuzzyInterval createLinearInterval(double mean, double gamma, double distanceToZero) {
        double c1 = mean - gamma - gamma;
        double m1 = mean - gamma;
        double m2 = mean + gamma;
        double c2 = mean + gamma + gamma;

        if (c1 < distanceToZero) {
            c1 = distanceToZero;
        }
        if (c1 > m1) {
            m1 = mean;
        }
        return createLinearIntervalFromPoints(c1, m1, m2, c2);
    }

    
    public static DiscretizedFuzzyInterval createLinearInterval(double mean, double plateaulength, double alpha, double beta, double distanceToZero) {
        double c1 = mean -  plateaulength / 2. - alpha;
        double m1 = mean - plateaulength / 2.;
        double m2 = mean +  plateaulength / 2.;
        double c2 = mean +  plateaulength / 2. + beta;

        if (c1 < distanceToZero) {
            c1 = distanceToZero;
        }
        if (c1 > m1) {
            m1 = mean;
        }
        return createLinearIntervalFromPoints(c1, m1, m2, c2);
    }
    
    public static DiscretizedFuzzyInterval createLinearInterval(double m1, double m2, double alpha, double beta, int resolution) {

        if ((alpha < 0) || (beta < 0)) {
            throw new IllegalArgumentException("alphaL and alphaR must be positive real numbers" + m1 + "," + m2 + "," + alpha + "," + beta);
        }

        /**
         * Lineares Interpolieren für linken Ast
         */
        double xL = m1 - alpha;
        double xR = m2 + beta;
        double dxL = alpha / resolution;
        double dxR = beta / resolution;

        AlphaCutSet[] alphaCutSets = new AlphaCutSet[resolution + 1];
        for (int i = 0; i < resolution + 1; i++) {
            double niveau = (1.0 / resolution) * i;
            double min = xL + dxL * i;
            double max = xR - dxR * i;
            alphaCutSets[i] = new AlphaCutSet(niveau, min, max);
        }
        return new DiscretizedFuzzyInterval(alphaCutSets);

    }

    public static DiscretizedFuzzyInterval createCrispValue(double m1, int resolution) {
        return createLinearInterval(m1, m1, 0, 0, resolution);
    }

    public static DiscretizedFuzzyInterval createCrispValue(double m1) {
        return createLinearInterval(m1, m1, 0, 0, DEFAULT_RESOLUTION);
    }

    public static DiscretizedFuzzyInterval CutLeftSideOfIntervall(DiscretizedFuzzyInterval fInterval, double dx) {

        if(fInterval.getC1()+dx > fInterval.getC2()){
            return null;
        }
        DiscretizedFuzzyInterval createCrispValue = FuzzyFactory.createCrispValue(fInterval.getC1()+dx);
        DiscretizedFuzzyInterval max = DiscretizedFuzzyInterval.max(fInterval, createCrispValue);
        return max;
        
//        double m1 = fInterval.getAlphaCutSet(fInterval.getNumberOfAlphaCuts() - 1).getMin();
//        double m2 = fInterval.getAlphaCutSet(fInterval.getNumberOfAlphaCuts() - 1).getMax();
//        double diff = (m2 - m1);
//
//        // verschieben nicht möglich, null zurückgeben
//        if (diff == 0.) {
//            return null;
//        }
//
//        DiscretizedFuzzyInterval dfi = fInterval.clone();
//
//        // Linken Bereich voll verkürzen
//        if (diff >= dx) {
//            for (int z = 0; z < fInterval.getNumberOfAlphaCuts(); z++) {
//                dfi.getAlphaCutSet(z).setMin(fInterval.getAlphaCutSet(z).getMin() + dx);
//            }
//        }
//
//        // Linken Bereich so weit wie möglich kürzen
//        if ((diff < dx) && (diff > 0.)) {
//            for (int z = 0; z < fInterval.getNumberOfAlphaCuts(); z++) {
//                dfi.getAlphaCutSet(z).setMin(fInterval.getAlphaCutSet(z).getMin() + (m2 - m1));
//            }
//        }
//
//        return dfi;

    }
}
