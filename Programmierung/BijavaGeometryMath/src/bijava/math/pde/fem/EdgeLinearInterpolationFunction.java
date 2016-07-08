package bijava.math.pde.fem;

import bijava.geometry.dimN.*;
import bijava.math.function.*;

/**
 * Die Klasse EdgeLinearInterpolationFunction stellt Funktionen fuer Lineare Ansatzfunktionen zur Verfuegung.
 *
 * @author  schierbaum
 * @version 1.0
 * @since   1.0
 */

public class EdgeLinearInterpolationFunction extends FEBasisFunction {

	/** Konstruktor einer Linearen Ansatzfunktion
	 * @param l: Lokale Koordinaten
	 * @param d: Freiheitsgrad der Ansatzfunktion 
	 */
	public EdgeLinearInterpolationFunction(NaturalElementCoordinateFunction[] l, FEDOF d) {
		super.setOrder(1);
		super.setLocalCoordinate(l);
		super.setDOF(d);
	}

	/** Wert der Ansatzfunktion ueber dem FE-Element am Punkt p
	 * @param p Punkt, an dem der Wert der Ansatzfunktion berechnet werden soll
	 * @return Wert der Ansatzfunktion am Punkt p  
	 */
	public double getValue(PointNd p) {
		int i = 0;		
		while (i < super.getLocalCoordinate().length) {
			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate().equals(super.getDOF().getPoint()))
				return super.getLocalCoordinate()[i].getValue(p);
			i++;
		}
//		return Double.NaN;
        return 0;
	}

	/** Ableitung der Ansatzfunktion ueber dem FE-Element am Punkt p
	 * @param p Punkt, an dem die Ableitung berechnet werden soll
	 * @return Feld der Ableitungsvektoren  
	 */
	public VectorNd getGradient(PointNd p) {
		int i = 0;
		while (i < super.getLocalCoordinate().length) {
			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate() == super.getDOF().getPoint())
				return super.getLocalCoordinate()[i].getGradient(p);
			i++;
		}
		return null;
	}
}
