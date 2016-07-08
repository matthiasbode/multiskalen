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
public class FEBasisFunctionOrder1 extends FEBasisFunction {

    public FEBasisFunctionOrder1(NaturalElementCoordinateFunction l, FEDOF d) {
        if (!l.getCoordinate().equals(d.getPoint()))
            throw new IllegalArgumentException("Uebergebene Elementkoordinatenfunktion gehoert nicht zu Freiheitsgrad");
        super.setLocalCoordinate(new NaturalElementCoordinateFunction[]{l});
        super.setDOF(d);
    }

    @Override
    public double getValue(PointNd p) {
        return super.getLocalCoordinate()[0].getValue(p);
//        int i = 0;
//		while (i < super.getLocalCoordinate().length) {
//			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate().equals(super.getDOF().getPoint()))
//				return super.getLocalCoordinate()[i].getValue(p);
//			i++;
//		}
//        return 0;
    }

    @Override
    public VectorNd getGradient(PointNd x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
