package bijava.math.function.interpolation;

import bijava.geometry.dimN.VectorNd;
import bijava.math.function.VectorFunction1d;

/**
 *
 * @author milbradt
 */
public class LinearizedVectorFunction1d implements VectorFunction1d {

    double pkt[];
    double werte[][];
    int anz;

    public LinearizedVectorFunction1d(double[] p, double[][] w) {
        pkt = p;
        werte = w;
        anz = w[0].length;
    }

    public void initial(double[] p, double w[][]) {
        pkt = p;
        werte = w;
        anz = w[0].length;
    }

    public double[][] getmin() {
        double y[][] = new double[werte.length][1];
        for (int i = 0; i < werte.length; i++) {
            y[i][0] = werte[0][0];
        }
        for (int i = 1; i < anz; i++) {
            for (int j = 0; j < werte.length; j++) {
                if (werte[1][i] < y[1][0]) {
                    y[j][0] = werte[j][i];
                }
            }
        }
        return y;
    }

    public double[][] getmax() {
        double y[][] = new double[werte.length][1];
        for (int i = 0; i < werte.length; i++) {
            y[i][0] = werte[0][0];
        }
        for (int i = 1; i < anz; i++) {
            for (int j = 0; j < werte.length; j++) {
                if (werte[1][i] > y[1][0]) {
                    y[j][0] = werte[j][i];
                }
            }
        }
        return y;
    }

    @Override
    public VectorNd getValue(double t) {
        double y[] = new double[werte.length];
        double t1[] = new double[werte.length];
        double t2[] = new double[werte.length];
        double y1[] = new double[werte.length];
        double y2[] = new double[werte.length];
        for (int i = 0; i < y.length; i++) {
            y[i] = Double.NaN;
            t1[i] = 0.;
            t2[i] = 0.;
            y1[i] = 0.;
            y2[i] = 0.;
        }

        int i, j, pos;
        pos = anz;
        for (i = 0; i < anz; i++) {
            if (pkt[i] > t) {
                pos = i - 1;
                i = anz;
            }

        }
        if (pos < anz) {
            for (i = 0; i < y.length; i++) {
                t1[i] = werte[i][pos];
                y1[i] = werte[i][pos];
            }
            for (j = pos; j < anz; j++) {
                if (pkt[j] > t) {
                    for (i = 0; i < y.length; i++) {
                        t2[i] = werte[i][j];
                        y2[i] = werte[i][j];
                    }
                    j = anz;
                }

            }
            for (i = 0; i < y.length; i++) {
                if (t1[i] != t2[i]) {
                    y[i] = y1[i] + ((t - t1[i]) / (t2[i] - t1[i])) * (y2[i] - y1[i]);
                } else {
                    y[i] = y1[i];
                }
            }
        }
        return new VectorNd(y);
    }

    public String toString() {
        String s = "";

        for (int i = 0; i < anz; i++) {
            s += " " + werte[0][i] + " " + werte[1][i];
        }
        return s;
    }
}