package bijava.math.pde.fem;

import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;


/**
 * Die Klasse HierarchicalTrialFunction stellt Funktionen fuer Hierarchische Ansatzfunktionen zur Verfuegung.
 *
 * @author  schierbaum
 * @version 1.0
 * @since   1.0
 */

public class HierarchicInterpolationFunction extends FEBasisFunction {

	private FEDOF DOF_i;
	private FEDOF DOF_j;
	private double[] NullPoints = null;

	/** Konstruktor einer Hierarchischen Ansatzfunktion
	 * @param l: Lokale Koordinaten
	 * @param d_i: 1. Freiheitsgrad der Kante, auf dem die Ansatzfunktion gebildet werden soll
	 * @param d_j: 2. Freiheitsgrad der Kante, auf dem die Ansatzfunktion gebildet werden soll
	 * @param n: Ordnung der Ansatzfunktion */
	public HierarchicInterpolationFunction(NaturalElementCoordinateFunction[] l, FEDOF d, FEDOF d_i, FEDOF d_j, int n) {
		super.setLocalCoordinate(l);
		super.setDOF(d);
		super.setOrder(n);
		this.DOF_i = d_i;
		this.DOF_j = d_j;
		this.NullPoints = getNullPoints(n);
	}

	/** Nullstellen der Hierarchischen Ansatzfunktion vom Grad n
	 * @param n Ordnung der Hierarchischen Ansatzfunktion
	 * @return Feld der Nullstellen  */
	private double[] getNullPoints(int n) {
		switch (n) {
			case 2 :
				return new double[] { 0., 1. };
			case 3 :
				return new double[] { 0., 1. / 2., 1. };
			case 4 :
				return new double[] { 0., 1. / 3., 2. / 3., 1. };
			case 5 :
				return new double[] { 0., 1. / 4., 1. / 2., 3. / 4., 1. };
			case 6 :
				return new double[] { 0., 1. / 5., 2. / 5., 3. / 5., 4. / 5., 1. };
			case 7 :
				return new double[] { 0., 1. / 6., 2. / 6., 3. / 6., 4. / 6., 5. / 6., 1. };
			case 8 :
				return new double[] { 0., 1. / 7., 2. / 7., 3. / 7., 4. / 7., 5. / 7., 6. / 7., 1. };
			case 9 :
				return new double[] { 0., 1. / 8., 2. / 8., 3. / 8., 4. / 8., 5. / 8., 6. / 8., 7. / 8., 1. };
			case 10 :
				return new double[] { 0., 1. / 9., 2. / 9., 3. / 9., 4. / 9., 5. / 9., 6. / 9., 7. / 9., 8. / 9., 1. };
		}
		return null;
	}

	/** Wert der Ansatzfunktion ueber dem FE-Element am Punkt p
	 * @param p Punkt, an dem der Wert der Ansatzfunktion berechnet werden soll
	 * @return Wert der Ansatzfunktion am Punkt p  */
	public double getValue(PointNd p) {
		double lambdadof1 = Double.NaN, lambdadof2 = Double.NaN;
		double erg = 1.0;
		int i = 0;
		while (i < super.getLocalCoordinate().length) {
			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate() == this.DOF_i.getPoint())
				lambdadof1 = super.getLocalCoordinate()[i].getValue(p);
			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate() == this.DOF_j.getPoint())
				lambdadof2 = super.getLocalCoordinate()[i].getValue(p);
			i++;
		}
		for (int k = 1; k <= super.getOrder(); k++) {
			double alpha_k = this.NullPoints[k - 1];
			//double alpha_k=((double) k-1.0)/((double)n-1.0);
			erg = erg * (lambdadof1 - alpha_k * lambdadof1 - alpha_k * lambdadof2);
		}
		//if (n==2) return erg*4.;
		//if (n==3) return erg*21.;
		return erg;
	}

	/** Ableitung der Ansatzfunktion ueber dem FE-Element am Punkt p
	 * @param p Punkt, an dem die Ableitung berechnet werden soll
	 * @return Feld der Ableitungsvektoren  */
	public VectorNd getGradient(PointNd p) {
		double lambda_i = 0.0;
		double lambda_j = 0.0;
		double[] grad_lambda_i = new double[2];
		double[] grad_lambda_j = new double[2];
		double Result = 0.0;
		double[] grad_Result = new double[2];
		int i = 0;
		while (i < super.getLocalCoordinate().length) {
			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate() == this.DOF_i.getPoint()) {
				lambda_i = super.getLocalCoordinate()[i].getValue(p);
				grad_lambda_i = super.getLocalCoordinate()[i].getGradient(p).getCoords();
			}
			if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate() == this.DOF_j.getPoint()) {
				lambda_j = super.getLocalCoordinate()[i].getValue(p);
				grad_lambda_j = super.getLocalCoordinate()[i].getGradient(p).getCoords();
			}
			i++;
		}
		grad_Result[0] = 0.0;
		grad_Result[1] = 0.0;
		Result = 1.0;
		for (int k = 1; k <= super.getOrder(); k++) {
			double alpha_k = this.NullPoints[k - 1];
			//double alpha_k=((double) k-1.0)/((double)n-1.0);
			grad_Result[0] = grad_Result[0] * (lambda_i - alpha_k * lambda_i - alpha_k * lambda_j) + Result * (grad_lambda_i[0] - alpha_k * grad_lambda_i[0] - alpha_k * grad_lambda_j[0]);
			grad_Result[1] = grad_Result[1] * (lambda_i - alpha_k * lambda_i - alpha_k * lambda_j) + Result * (grad_lambda_i[1] - alpha_k * grad_lambda_i[1] - alpha_k * grad_lambda_j[1]);
			Result = Result * (lambda_i - alpha_k * lambda_i - alpha_k * lambda_j);
		}
		//if (n==2){ grad_Result[0] = grad_Result[0]*4.; grad_Result[1] = grad_Result[1]*4.;}
		//if (n==3){ grad_Result[0] = grad_Result[0]*21.; grad_Result[1] = grad_Result[1]*21.;}
		return new VectorNd(grad_Result);
	}
}
