package bijava.geometry;


import bijava.vecmath.DMatrix;
import bijava.vecmath.LGS;



/**
 * Die Klasse AlgGeom stellt Funktionen aus der Algorithmischen 
 * Geometrie fuer allgemeine Point zur Verfuegung
 *
 * @author  Schierbaum, Milbadt
 * @version 1.0
 */

public class AlgGeometrie {

	public static boolean EPSILONQUAD = false;
        public static double EPSILON=1.E-7;
        
        private AlgGeometrie(){}
        
	/** 
	 * Berechnet die Dimension der linearen Huelle der gegebenen Punktmenge p
	 *
	 * @param p ein Feld von Punkten, deren Dimension der linearen Huelle berechnet 
	 * werden soll
	 * @return Dimension der linearen Huelle der Punktmenge p
	 */
	public static int getLinearHullDimension(VectorPoint[] p) {
		if (p.length == 0)
			return -1;
		if (p.length == 1)
			return 0;
		int m = p.length - 1;
		int n = p[0].dim();
		double[][] mat = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				mat[i][j] = p[i + 1].getCoord(j) - p[0].getCoord(j);
			}
		}
		int erg = LGS.rangMat(new DMatrix(mat));
		return erg;
	}
}
