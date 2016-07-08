/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bijava.geometry.dimN;

import bijava.geometry.VectorPoint;

/**
 *
 * @author thees
 */
public abstract class AbstractVectorNd<T extends AbstractVectorNd> extends TupleNd<T> {

    public AbstractVectorNd(int N) {
        super(N);
    }

    public AbstractVectorNd(double[] x) {
        super(x);
    }

    public AbstractVectorNd(TupleNd t) {
        super(t);
    }
    
    /** Returns the dot product of this vector and vector v1.
     * @param v1 - the other vector
     * @result  the dot product of this and v1
     */
    public double dot(AbstractVectorNd v1) {
        double erg = 0;
        for (int i = 0; i < x.length; i++) {
            erg += x[i] * v1.x[i];
        }
        return erg;
    }

    @Override
    public double[] getCoords() {
        double[] result = new double[dim()];
        System.arraycopy(x, 0, result, 0, dim());
        return result;
    }

    /**
     * @depricated
     */
    public double[] getVector() {
        double[] d = new double[super.getSize()];
        System.arraycopy(super.x, 0, d, 0, super.getSize());
        return d;
    }
}
