package bijava.math.function.fft;

import bijava.math.function.interpolation.LinearizedScalarFunction1d;
import bijava.math.ComplexNumber;
import bijava.math.function.AbstractScalarFunction1d;
import bijava.math.function.interpolation.DiscretizedScalarFunction1d;
import bijava.math.function.interpolation.RasterScalarFunction2d;

// Klasse zum Durchfuehren der DFT-Analyse und der DFT-
// Synthese
public abstract class DFT {

    // Funktion fuer die Analyse: Aus Funktionswerten
    // werden die Fourierkoeffizienten berechnet
    public static ComplexNumber[] analysis(DiscretizedScalarFunction1d f) {
        int number = f.getSizeOfValues();
        ComplexNumber[] c = new ComplexNumber[number];
        for (int k = 0; k < number; k++) {
            c[k] = new ComplexNumber();
            for (int j = 0; j < number; j++) {
                double abs = f.getValueAt(j)[1];
                double arg = Math.PI * 2 * j * k / (number * 1.0);
                ComplexNumber cTemp = new ComplexNumber(abs * Math.cos(arg), abs * Math.sin(arg));
                cTemp.conjugate();
                c[k] = c[k].add(cTemp);

            }
            c[k] = c[k].div(number * 1.0);
            c[k].conjugate();
        }
        return c;
    }

    // Funktion fuer die Analyse: Aus Funktionswerten
    // werden n Fourierkoeffizienten berechnet
    public static ComplexNumber[] analysis(DiscretizedScalarFunction1d f, int n) {
        int number = f.getSizeOfValues();
        ComplexNumber[] c = new ComplexNumber[n];
        for (int k = 0; k < n; k++) {
            c[k] = new ComplexNumber();
            for (int j = 0; j < number; j++) {
                double abs = f.getValueAt(j)[1];
                double arg = Math.PI * 2 * j * k / (number * 1.0);
                ComplexNumber cTemp = new ComplexNumber(abs * Math.cos(arg), abs * Math.sin(arg));
                cTemp.conjugate();
                c[k] = c[k].add(cTemp);
            }
            c[k] = c[k].div(number * 1.0);
            c[k].conjugate();
        }
        return c;
    }

    // Funktion fuer die Synthese: Aus den Fourierkoeffizienten
    // werden die Funktionswerte berechnet
    public static LinearizedScalarFunction1d synthesis(ComplexNumber[] c1, double time) {
        int number = c1.length;
        ComplexNumber[] c = new ComplexNumber[number];
        for (int k = 0; k < number; k++) {
            c1[k].conjugate();
        }
        for (int k = 0; k < number; k++) {
            c[k] = new ComplexNumber();
            for (int j = 0; j < number; j++) {
                ComplexNumber cTemp = c1[j];
                double abs = cTemp.norm();
                double arg = Math.PI * 2 * j * k / (number * 1.0) + cTemp.argument();
                cTemp = new ComplexNumber(abs * Math.cos(arg), abs * Math.sin(arg));
                c[k] = c[k].add(cTemp);

            }
        }
        double[][] d = new double[2][number];
        for (int i = 0; i < number; i++) {
            double x = i * time / c.length;
            d[0][i] = x;
            d[1][i] = c[i].real;
        }
        return new LinearizedScalarFunction1d(d);
    }


    // Funktion fuer die Analyse: Aus Funktionswerten
    // werden die Fourierkoeffizienten berechnet
    public static ComplexNumber[][] analysis(RasterScalarFunction2d f) {
        return analysis(f, -1, -1);
    }

    // bandbegrenzt auf die ersten M,N Koeffizienten
    public static ComplexNumber[][] analysis(RasterScalarFunction2d f, int bandM, int bandN) {
        final int M = f.getRowSize();
        final int N = f.getColumnSize();
        final double scaleSqrtMN = Math.sqrt(M * N);

        if (bandM < 0) {
            bandM = M;
        }
        if (bandN < 0) {
            bandN = N;
        }

        ComplexNumber[][] c = new ComplexNumber[M][N];

        for (int k = 0; k < bandM; k++) {
            for (int l = 0; l < bandN; l++) {

                c[k][l] = new ComplexNumber();
                for (int m = 0; m < M; m++) {
                    for (int n = 0; n < N; n++) {

                        double abs = f.getSamplingValueAt(m, n);
                        double arg = Math.PI * 2.0 * (m * k / (1.0 * M) + n * l / (1.0 * N));

                        ComplexNumber cTemp = new ComplexNumber(abs * Math.cos(arg), abs * Math.sin(arg));
                        cTemp.conjugate();
                        c[k][l] = c[k][l].add(cTemp);

                    }
                }
                c[k][l] = c[k][l].div(scaleSqrtMN);
                c[k][l].conjugate();

            }
        }
        for (int k = bandM; k < M; k++) {
            for (int l = bandN; l < N; l++) {
                c[k][l] = new ComplexNumber(0.0);
            }
        }
        return c;
    }


    // Funktion fuer die 2D-Synthese: Aus den Fourierkoeffizienten u
    // werden die komplexen Funktionswerte c rekonstruiert
    // @TODO testen
    public static ComplexNumber[][] synthesis(ComplexNumber[][] u) {
        final int M = u.length;
        final int N = u[0].length;
        final double scaleSqrtMN = Math.sqrt(M * N);

        ComplexNumber[][] c = new ComplexNumber[M][N];
        for (int k = 0; k < M; k++) {
            for (int l = 0; l < N; l++) {
                u[k][l].conjugate();
            }
        }

        for (int k = 0; k < M; k++) {
            for (int l = 0; l < N; l++) {
                c[k][l] = new ComplexNumber();
                for (int m = 0; m < M; m++) {
                    for (int n = 0; n < N; n++) {
                        ComplexNumber cTemp = c[m][n];
                        double abs = cTemp.norm();
                        double arg = Math.PI * 2.0 * (m * k / (1.0 * M) + n * l / (1.0 * N)) + cTemp.argument();
                        cTemp = new ComplexNumber(abs * Math.cos(arg), abs * Math.sin(arg));
                        c[k][l] = c[k][l].add(cTemp);
                    }
                }
                c[k][l] = c[k][l].div(scaleSqrtMN);
            }
        }

        return c;
    }

    /**
     * Funktion fuer die gefensterte Fourier-Analyse: Aus Funktionswerten werden die Fourierkoeffizienten berechnet.
     * Hierfuer wird eine Fensterfunktion definiert, die ausserhalb des Wertebereichs 0 ist (s.u.)
     * Der Bereich der Ausgabespektren ist justierbar, so dass ein Frequenzband von ... bis ... errechnet werden kann
     *
     * @param f             zu analysierende Funktion
     * @param window        Fenster-Funktion (zB Gauss, Hann, oder Rechteck (s.u.) )
     * @param minFreq       Anfangsfrequenz
     * @param maxFreq       Endfrequenz
     * @return              2D-Array mit [Bandgroesse][Spektrumgroesse]
     */
    public static double[][] windowAnalysis(DiscretizedScalarFunction1d f, AbstractScalarFunction1d window, int minFreq, int maxFreq) {
        int number = f.getSizeOfValues();
        int diffFreq = maxFreq - minFreq + 1;
        ComplexNumber[][] c = new ComplexNumber[diffFreq][number];

        for (int freq = minFreq; freq <= maxFreq; freq++) {

            for (int m = 0; m < number; m++) {
                c[freq - minFreq][m] = new ComplexNumber();

                for (int t = 0; t < number; t++) {
                    double abs = f.getValueAt(t)[1] * window.getValue(t-m);
                    double arg = t * m * freq;
                    ComplexNumber cTemp = new ComplexNumber(abs * Math.cos(arg), abs * Math.sin(arg));
                    cTemp.conjugate();
                    c[freq - minFreq][m] = c[freq - minFreq][m].add(cTemp);

                }
                c[freq - minFreq][m] = c[freq - minFreq][m].div(number * 1.0);
                c[freq - minFreq][m].conjugate();
            }
        }

        double[][] ret = new double[diffFreq][number];
        for (int i = 0; i < diffFreq; i++) {
            for (int j = 0; j < number; j++) {
                ret[i][j] = ComplexNumber.abs(c[i][j].div(c[i][0]));
            }
        }
        return ret;
    }

    /**
     * Fensterfunktion fuer ein Rechteck
     * @param x0    Beginn des Definitionsbereichs
     * @param x1    Ende des Definitionsbereichs
     * @return      Funktionswert
     */
     public static AbstractScalarFunction1d getRectangleWindow(final double x0, final double x1) {
        return new AbstractScalarFunction1d() {
            @Override
            public double getValue(double x) {
                if (x >= x0 && x <= x1) {
                    return 1.0;
                } else {
                    return 0.0;
                }
            }
        };
    }

    /**
     * Fensterfunktion fuer eine Gaussfunktion
     * @param sigma     Sigma
     * @param mu        Mue
     * @param alpha     plus/minus-Intervall, ab dem die Gaussfunktion auf Null gesetzt wird
     * @return          Funktionswert
     */
    public static AbstractScalarFunction1d getGaussianWindow(final double sigma, final double mu, final double alpha) {
        return new AbstractScalarFunction1d() {
            @Override
            public double getValue(double x) {
                if (Math.abs(x) > alpha) {
                    return 0.0;
                } else {
                    return (Math.exp(Math.pow(-0.5 * ((x - mu) /sigma), 2.0)) /(sigma * Math.sqrt(2*Math.PI)));
                }
            }
        };
    }

    /**
     * Fensterfunktion fuer eine Hann-Funktion
     * @param n         N
     * @param alpha     plus/minus-Intervall, ab dem die Gaussfunktion auf Null gesetzt wird
     * @return          Funktionswert
     */

    public static AbstractScalarFunction1d getHannWindow(final double n, final double alpha) {
        return new AbstractScalarFunction1d() {
            @Override
            public double getValue(double x) {
                if (Math.abs(x) > alpha) {
                    return 0.0;
                } else {
                    return 0.5 * (1.0 - Math.cos((2*Math.PI * x) / (n - 1)));
                }
            }
        };
    }
}


