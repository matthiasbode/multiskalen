/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.demo;

import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import fuzzy.number.discrete.DiscretizedFuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;
import java.io.IOException;

/**
 *
 * @author brandt
 *
 * Diese Klasse ermittelt für das Anwendungsbeispiel die Nutzleistung eines
 * hydraulischen Baggers
 *
 */
public class Nutzleistung {

    /**
     * Parameter:
     *
     * Bodenart: Sand, Kies, schwach lehmig Tieflöffel: 1 m^3 Nenninhalt (SAE);
     * mittlerer Schneiden- bzw. Zahnzustand Baggerzustand: 1000 – 1500
     * Maschinen-Einsatzstunden (Baggeralter: ca. 2–3 Jahre)
     * Betriebsbedingungen: geübter Baggerfahrer; gute Baustellenbedingungen
     * Tätigkeit: Grabenaushub und Entleeren auf Fahrzeug
     * Leistungsnebenbedingungen: (fast) optimale Grabentiefe; 60° Schwenkwinkel
     * Abtransport: LKW mit 7 m^3 Ladevolumen
     *
     *
     */
    // Breite des oberen Trapezbereichs in den Alpha- und Beta-Bereich angeben
    double FI = 0.15;

    // gibt die Nutzleistung als 
    public DiscretizedFuzzyInterval get_Qn_I(int resolution)  {

        // Fuzzy-Zahlen und Fuzzy-Intervalle erzeugen
        double min = 0.;
        double max = 325.;
        double dx = 0.01;

        // Nenninhalt V_SAE in [m^3]
        // Number
        double mN = 1.000; // Annahme: +-0.005 m^3 für C1 und C2, d.h. es sind +-5L
        double c1N = 0.995;
        double c2N = 1.005;
        // Interval
        double m1I = mN - (mN - c1N) * FI;
        double m2I = mN + (c2N - mN) * FI;
        double c1I = c1N;
        double c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval V_SAE_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Spielzeit ts 
        // Number
        mN = 16.70; // Annahme: +-0.1 Sekunden für C1 und C2, als Ableseungenauigkeit
        c1N = 16.60;
        c2N = 16.80;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval ts_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Lösefaktor Alpha
        // Number
        mN = 0.850;
        c1N = 0.805;
        c2N = 0.895;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval Alpha_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Füllfaktor Psi
        // Number
        c1N = 1.035;
        c2N = 1.140;
        mN = (c1N + c2N) / 2.;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval Psi_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Bedienungsfaktor mue1
        // Number
        mN = 1.000;
        c1N = 0.900;
        c2N = 1.050;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval eta1_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Betriebsbedingungen mue2
        // Number
        mN = 0.950;
        c1N = 0.900;
        c2N = 0.975;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval eta2_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Einfluss der Grabentiefe bzw. Abbautiefe f1
        // Number
        mN = 0.99; // Optimum wird nie richtig erreicht
        c1N = 0.985; // Annahme: +- 0,005 als Ableseungenauigkeit
        c2N = 0.995;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f1_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Schwenkwinkeleinfluss f2
        // Number
        mN = 1.12;
        c1N = 1.05;
        c2N = 1.20;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f2_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Entleerungsgenauigkeit f3
        // Number
        mN = 0.98;
        c1N = 0.97; // Annahme: +-0.01 Ablesegenauigkeit
        c2N = 0.99;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f3_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Schneide-, Zahnzustand f4
        // Number
        mN = 0.90;
        c1N = 0.85;
        c2N = 0.95;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f4_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Verfügbarkeits-, Gerätezustandsfaktor f5
        // Number
        mN = 0.93;
        c1N = 0.89;
        c2N = 0.965;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f5_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Geräteausnutzungsgrad mueG
        // Number
        mN = 0.675;
        c1N = 0.60;
        c2N = 0.75;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval mueG_I = FuzzyFactory.createLinearInterval(m1I, m2I, m1I - c1I, c2I - m2I, resolution);

        // Fuzzy-Faktoren ausrechnen
        // k1 = Alpha * Psi
        DiscretizedFuzzyInterval k1_I = Alpha_I.mult(Psi_I);

        // k2 = f1 * f2 * f3 * f4 * f5
        DiscretizedFuzzyInterval k2_I = f1_I.mult(f2_I.mult(f3_I.mult(f4_I.mult(f5_I))));

        // k3 = mue1 * mue2
        DiscretizedFuzzyInterval k3_I = eta1_I.mult(eta2_I);

        // Nutzleistung ausrechnen
        // Qn = (V_SAE / ts) * 3600 * k1 * k2 * k3 * mueG
        DiscretizedFuzzyInterval Qn_I = (((((V_SAE_I.div(ts_I)).mult(3600.)).mult(k1_I)).mult(k2_I)).mult(k3_I)).mult(mueG_I);

        return Qn_I;
    }

    public DiscretizedFuzzyInterval get_Qn_I_gaussian(int resolution, double varianz) throws IOException {

        // Varianz für Gauß-Funktion festlegen
        double faktor = varianz;

        // Fuzzy-Zahlen und Fuzzy-Intervalle erzeugen
        double min = 0.;
        double max = 325.;
        double dx = 0.01;

        // Nenninhalt V_SAE in [m^3]
        // Number
        double mN = 1.000; // Annahme: +-0.005 m^3 für C1 und C2, d.h. es sind +-5L
        double c1N = 0.995;
        double c2N = 1.005;
        // Interval
        double m1I = mN - (mN - c1N) * FI;
        double m2I = mN + (c2N - mN) * FI;
        double c1I = c1N;
        double c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval V_SAE_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Spielzeit ts 
        // Number
        mN = 16.70; // Annahme: +-0.1 Sekunden für C1 und C2, als Ableseungenauigkeit
        c1N = 16.60;
        c2N = 16.80;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval ts_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Lösefaktor Alpha
        // Number
        mN = 0.850;
        c1N = 0.805;
        c2N = 0.895;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval Alpha_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Füllfaktor Psi
        // Number
        c1N = 1.035;
        c2N = 1.140;
        mN = (c1N + c2N) / 2.;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval Psi_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Bedienungsfaktor mue1
        // Number
        mN = 1.000;
        c1N = 0.900;
        c2N = 1.050;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval eta1_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Betriebsbedingungen mue2
        // Number
        mN = 0.950;
        c1N = 0.900;
        c2N = 0.975;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval eta2_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Einfluss der Grabentiefe bzw. Abbautiefe f1
        // Number
        mN = 0.99; // Optimum wird nie richtig erreicht
        c1N = 0.985; // Annahme: +- 0,005 als Ableseungenauigkeit
        c2N = 0.995;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f1_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Schwenkwinkeleinfluss f2
        // Number
        mN = 1.12;
        c1N = 1.05;
        c2N = 1.20;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f2_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Entleerungsgenauigkeit f3
        // Number
        mN = 0.98;
        c1N = 0.97; // Annahme: +-0.01 Ablesegenauigkeit
        c2N = 0.99;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f3_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Schneide-, Zahnzustand f4
        // Number
        mN = 0.90;
        c1N = 0.85;
        c2N = 0.95;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f4_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Verfügbarkeits-, Gerätezustandsfaktor f5
        // Number
        mN = 0.93;
        c1N = 0.89;
        c2N = 0.965;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval f5_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Geräteausnutzungsgrad mueG
        // Number
        mN = 0.675;
        c1N = 0.60;
        c2N = 0.75;
        // Interval
        m1I = mN - (mN - c1N) * FI;
        m2I = mN + (c2N - mN) * FI;
        c1I = c1N;
        c2I = c2N;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyInterval mueG_I = FuzzyFactory.createFromGauss(m1I, m2I, m1I - c1I, c2I - m2I, resolution, varianz);

        // Fuzzy-Faktoren ausrechnen
        // k1 = Alpha * Psi
        DiscretizedFuzzyInterval k1_I = Alpha_I.mult(Psi_I);

        // k2 = f1 * f2 * f3 * f4 * f5
        DiscretizedFuzzyInterval k2_I = f1_I.mult(f2_I.mult(f3_I.mult(f4_I.mult(f5_I))));

        // k3 = mue1 * mue2
        DiscretizedFuzzyInterval k3_I = eta1_I.mult(eta2_I);

        // Nutzleistung ausrechnen
        // Qn = (V_SAE / ts) * 3600 * k1 * k2 * k3 * mueG
        DiscretizedFuzzyInterval Qn_I = (((((V_SAE_I.div(ts_I)).mult(3600.)).mult(k1_I)).mult(k2_I)).mult(k3_I)).mult(mueG_I);

        return Qn_I;
    }

    public DiscretizedFuzzyNumber get_Qn_N(int resolution) {

        // Fuzzy-Zahlen und Fuzzy-Intervalle erzeugen
        // Nenninhalt V_SAE in [m^3]
        // Number
        double mN = 1.000; // Annahme: +-0.005 m^3 für C1 und C2, d.h. es sind +-5L
        double c1N = 0.995;
        double c2N = 1.005;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber V_SAE_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Spielzeit ts 
        // Number
        mN = 16.70; // Annahme: +-0.1 Sekunden für C1 und C2, als Ableseungenauigkeit
        c1N = 16.60;
        c2N = 16.80;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber ts_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Lösefaktor Alpha
        // Number
        mN = 0.850;
        c1N = 0.805;
        c2N = 0.895;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber Alpha_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Füllfaktor Psi
        // Number
        c1N = 1.035;
        c2N = 1.140;
        mN = (c1N + c2N) / 2.;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber Psi_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Bedienungsfaktor mue1
        // Number
        mN = 1.000;
        c1N = 0.900;
        c2N = 1.050;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber mue1_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Betriebsbedingungen mue2
        // Number
        mN = 0.950;
        c1N = 0.900;
        c2N = 0.975;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber mue2_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Einfluss der Grabentiefe bzw. Abbautiefe f1
        // Number
        mN = 0.99; // Optimum wird nie richtig erreicht
        c1N = 0.985; // Annahme: +- 0,005 als Ableseungenauigkeit
        c2N = 0.995;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber f1_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Schwenkwinkeleinfluss f2
        // Number
        mN = 1.12;
        c1N = 1.05;
        c2N = 1.20;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber f2_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Entleerungsgenauigkeit f3
        // Number
        mN = 0.98;
        c1N = 0.97; // Annahme: +-0.01 Ablesegenauigkeit
        c2N = 0.99;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber f3_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Schneide-, Zahnzustand f4
        // Number
        mN = 0.90;
        c1N = 0.85;
        c2N = 0.95;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber f4_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Verfügbarkeits-, Gerätezustandsfaktor f5
        // Number
        mN = 0.93;
        c1N = 0.89;
        c2N = 0.965;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber f5_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Geräteausnutzungsgrad mueG
        // Number
        mN = 0.775;
        c1N = 0.75;
        c2N = 0.80;
        // Fuzzy-Zahlen definieren
        DiscretizedFuzzyNumber mueG_N = new DiscretizedFuzzyNumber(mN, mN - c1N, c2N - mN, resolution);

        // Fuzzy-Faktoren ausrechnen
        // k1 = Alpha * Psi
        DiscretizedFuzzyNumber k1_N = Alpha_N.mult(Psi_N);

        // k2 = f1 * f2 * f3 * f4 * f5
        DiscretizedFuzzyNumber k2_N = f1_N.mult(f2_N.mult(f3_N.mult(f4_N.mult(f5_N))));

        // k3 = mue1 * mue2
        DiscretizedFuzzyNumber k3_N = mue1_N.mult(mue2_N);

        // Nutzleistung ausrechnen
        // Qn = (V_SAE / ts) * 3600 * k1 * k2 * k3 * mueG
        DiscretizedFuzzyNumber Qn_N = (((((V_SAE_N.div(ts_N)).mult(3600.)).mult(k1_N)).mult(k2_N)).mult(k3_N)).mult(mueG_N);

        return Qn_N;
    }

}
