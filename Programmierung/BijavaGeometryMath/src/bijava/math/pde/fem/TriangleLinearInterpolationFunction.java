/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bijava.math.pde.fem;

import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;

/**
 *
 * @author schierm
 */
public class TriangleLinearInterpolationFunction extends FEBasisFunction {

    public TriangleLinearInterpolationFunction(NaturalElementCoordinateFunction[] l, FEDOF d) {
        super.setLocalCoordinate(l);
        super.setDOF(d);
    }

    @Override
    public double getValue(PointNd p) {
        int i = 0;
		while (i < super.getLocalCoordinate().length) {
			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate().equals(super.getDOF().getPoint()))
				return super.getLocalCoordinate()[i].getValue(p);
			i++;
		}
        return 0;
    }

    @Override
    public VectorNd getGradient(PointNd x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
