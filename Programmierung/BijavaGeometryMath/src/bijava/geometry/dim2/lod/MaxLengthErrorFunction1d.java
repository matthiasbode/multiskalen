package bijava.geometry.dim2.lod;

/**
 * MaxLengthErrorFunction1d.java dient zur Speicherung der funktionalen Beziehung
 * zwischen dem Skalierungsfaktor einer Koordinatentransformation k und dem prozentualen
 * Maximum eines globalen Laengenfehlers e: e = f(k).
 * @author Leibniz Universitaet Hannover<br>
 *  Institut fuer Bauinformatik<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 2.0, Oktober 2006
 */
public class MaxLengthErrorFunction1d {
    // Feld mit Paaren (k, e) bezogen auf eine Polygonreduktion nach Minimum
    // Zwischenwerte werden linear interpoliert.
    private double[][] PolygonReductByMinFctn;
    
    /**
     * Erzeugt eine funktionale Beziehung zwischen dem
     * Skalierungsfaktor einer Koordinatentransformation und dem
     * prozentualen Maximum eines globalen Laengenfehlers.
     */
    public MaxLengthErrorFunction1d() {
        this.init_PolygonReductByMinFctn();
    }
    
    private void init_PolygonReductByMinFctn() {
        PolygonReductByMinFctn = new double[][] {
            {0.,2.},{0.01,0.1},{0.018,0.001},{1.,0.}};
    }
    
    /**
     * Liefert fuer den Skalierungsfaktor einer Koordinatentransformation
     * ein prozentuales Maximum eines globalen Laengenfehlers bezogen auf
     * eine Polygonreduktion nach Minimum.
     * @param k Skalierungsfaktor einer Koordinatentransformation.
     * @return prozentuales Maximum eines globalen Laengenfehlers.
     */
    public double getValue_PolygonReductByMin(double k) {
        return this.interpolateLinear(PolygonReductByMinFctn, k);
    }
    
    private double interpolateLinear(double[][] values, double k) {
        for (int i = 0; i < values.length - 1; i++)
            if (values[i + 1][0] >= k) {
            double lambda1 = (k - values[i][0]) / (values[i + 1][0] - values[i][0]);
            return values[i][1] * (1 - lambda1) + values[i + 1][1] * lambda1;
            }
        return Double.NaN;
    }
}