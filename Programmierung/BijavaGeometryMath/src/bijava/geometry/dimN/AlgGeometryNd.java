package bijava.geometry.dimN;


import bijava.vecmath.DMatrix;
import bijava.vecmath.LGS;
import java.util.ArrayList;
import javax.vecmath.GMatrix;
import javax.vecmath.GVector;



/**  Die Klasse AlgGeometryNd stellt Funktionen aus der Algorithmischen 
 * Geometrie fuer PointNd bereit.
 *
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 * @version 1.0
 */

public abstract class AlgGeometryNd {

	public static boolean EPSILONQUAD = false;
        public static double EPSILON=1.E-7;
	/** 
	 * Berechnet die Dimension der linearen Huelle der gegebenen Punktmenge p
	 *
	 * @param p ein Feld von Punkten, deren Dimension der linearen Huelle berechnet 
	 * werden soll
	 * @return Dimension der linearen Huelle der Punktmenge p
	 */
	public static int getLinearHullDimension(PointNd[] p) {
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

	/** 
	 * Berechnet das Zentrum der Umkugel, das durch N+1 geordneten-Tupel gebildet wird.
	 * Das Zentrum einer Umkugel laesst sich aus N+1 Punkten bestimmen und entpsricht im zweidimensionalen
	 * Raum dem Mittelpunkt des Kreises, auf dessen Rand die Punkte (Tupel) liegen.
	 *
	 * @param ts N+1 geordnete n-dimensionale Tupel
	 * @return Zentrum der Umkugel, null, wenn kein Zentrum exisitiert 
	 */
	public static PointNd centerNd(PointNd[] ts) {

		int n = ts.length - 1;
		if (n != ts[ts.length - 1].dim())
			System.out.println("Fehler in VecMath.center: Dimension und Anzahl der Tupel stimmen nicht.");

		GMatrix A = new GMatrix(n, n);
		GVector b = new GVector(n);

		// Aufbau des Gleichungssystems
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				A.setElement(i, j, 2.0 * (ts[i].getCoord(j) - ts[i + 1].getCoord(j)));
				b.setElement(i, b.getElement(i) + (ts[i].getCoord(j) * ts[i].getCoord(j) - ts[i + 1].getCoord(j) * ts[i + 1].getCoord(j)));
			}
		}
		GVector  x = new GVector (n);
		int k = A.LUD(A, x);
		x.LUDBackSolve(A, b, x);

		double[] d = new double[n];
		for (int i = 0; i < n; i++) {
			d[i] = x.getElement(i);
		}

		return new PointNd(d);
	}

	/** 
	 * Berechnung Zentrum fuer ein Simplex der Ordnung k im Raum der Dimension n, wobei die Ordnung durch die Anzahl der n-Tupel gegeben ist.
	 * 
	 * @param ts k+1 geordnete n-dimensionale Tupel
	 * @return Zentrum der Spaehre der Ordnung k, null, wenn kein Zentrum exisitiert 
	 */
	public static PointNd center(PointNd[] ts) {
		int n = ts[0].dim();
		int s = ts.length - 1;

		if (n == s) {
                        if ( n == 1 )
                            return ts[0].add(ts[1]).mult(0.5);
			if ( n == 2 )
				return center2D(ts);
			if ( n == 3 )
				return center3D(ts);
			return centerNd(ts);
		}

		double[][] A = new double[n + s][n + s];
		double[] b = new double[n + s];

		// Aufbau des Gleichungssystems
		for (int i = 0; i < s; i++)
			for (int j = 0; j < n; j++) {
				A[i][j] = 2.0 * (ts[i].getCoord(j) - ts[i + 1].getCoord(j));
				b[i] += (ts[i].getCoord(j) * ts[i].getCoord(j) - ts[i + 1].getCoord(j) * ts[i + 1].getCoord(j));
			}

		for (int i = s; i < s + n; i++) {
			b[i] = -2. * ts[0].getCoord(i-s);
			A[i][i - s] = -2.0;
			for (int j = n; j < s + n; j++)
				A[i][j] = 2. * (ts[j - n + 1].getCoord(i-s) - ts[0].getCoord(i-s));
		}
		// Loesen des Gleichungssystems
		DMatrix mA = new DMatrix(A);
		double[] x, erg;
		try {
			erg = mA.solve(b);
			x = new double[n];
			PointNd s5 = new PointNd(new double[] {0.342, -0.940});
			for (int i = 0; i < n; i++)
				x[i] = erg[i];
		} catch (ArithmeticException ae) {
			return null;
		}

		return new PointNd(x);

		//        MatrixNd A = new MatrixNd(n+s,n+s);
		//        VectorNd b = new VectorNd(n+s);

		// Aufbau des Gleichungssystems
		//        for (int i=0; i<s; i++ )
		//            for (int j=0; j<n; j++) {
		//                A.setElement(i,j,2.0*(ts[i].x[j]-ts[i+1].x[j]));
		//                b.setElement(i,b.getElement(i)+(ts[i].x[j]*ts[i].x[j]-ts[i+1].x[j]*ts[i+1].x[j])); 
		//            }

		//        for (int i=s; i<s+n; i++ ) {
		//            b.setElement(i,-2.*ts[0].x[i-s]);
		//            A.setElement(i,i-s,-2.0);
		//            for (int j=n; j<s+n; j++)
		//                A.setElement(i,j,2.*(ts[j-n+1].x[i-s] - ts[0].x[i-s]));
		//        }
		//        VectorNd x=new VectorNd(s+n);
		//        int k=A.LUD(A,x);
		//        x.LUDBackSolve(A,b,x);

		//        double[] d=new double[n];
		//        for (int i=0; i<n; i++){
		//            d[i]=x.getElement(i);
		//        }
		//        return new PointNd(d);
	}

	/** 
	 * Berechnung Zentrum fuer ein Simplex der Dimension 2.
	 *
	 * @param ts 3 geordnete 2-dimensionale Tupel
	 * @return Zentrum der Spaehre, null, wenn kein Zentrum exisitiert 
	 */
	private static PointNd center2D(PointNd[] p) {
		double d = 4.0 * (p[0].getCoord(0) - p[1].getCoord(0)) * (p[1].getCoord(1) - p[2].getCoord(1)) - 4.0 * (p[1].getCoord(0) - p[2].getCoord(0)) * (p[0].getCoord(1) - p[1].getCoord(1));
		double d1 = 2.0 * (p[0].getCoord(0) * p[0].getCoord(0) + p[0].getCoord(1) * p[0].getCoord(1) - p[1].getCoord(0) * p[1].getCoord(0) - p[1].getCoord(1) * p[1].getCoord(1)) * (p[1].getCoord(1) - p[2].getCoord(1)) - 2.0 * (p[1].getCoord(0) * p[1].getCoord(0) + p[1].getCoord(1) * p[1].getCoord(1) - p[2].getCoord(0) * p[2].getCoord(0) - p[2].getCoord(1) * p[2].getCoord(1)) * (p[0].getCoord(1) - p[1].getCoord(1));
		double d2 = 2.0 * (p[1].getCoord(0) * p[1].getCoord(0) + p[1].getCoord(1) * p[1].getCoord(1) - p[2].getCoord(0) * p[2].getCoord(0) - p[2].getCoord(1) * p[2].getCoord(1)) * (p[0].getCoord(0) - p[1].getCoord(0)) - 2.0 * (p[0].getCoord(0) * p[0].getCoord(0) + p[0].getCoord(1) * p[0].getCoord(1) - p[1].getCoord(0) * p[1].getCoord(0) - p[1].getCoord(1) * p[1].getCoord(1)) * (p[1].getCoord(0) - p[2].getCoord(0));

		//System.out.println("d = "+Math.abs(d));
		if (EPSILONQUAD) {
			if (Math.abs(d) < (EPSILON * EPSILON))
				return null;
		} else {
			if (Math.abs(d) < (EPSILON))
				return null;
		}

		return new PointNd(new double[] {d1 / d, d2 / d});
	}

	/** 
	 * Berechnung Zentrum fuer ein Simplex der Dimension 3.
	 *
	 * @param p 4 geordnete 3-dimensionale Tupel
	 * @return Zentrum der Spaehre, null, wenn kein Zentrum exisitiert 
	 */
	private static PointNd center3D(PointNd[] p) {
		double ab1 = p[0].getCoord(0) - p[1].getCoord(0);
		double bc2 = p[1].getCoord(1) - p[2].getCoord(1);
		double cd3 = p[2].getCoord(2) - p[3].getCoord(2);
		double bc1 = p[1].getCoord(0) - p[2].getCoord(0);
		double cd2 = p[2].getCoord(1) - p[3].getCoord(1);
		double ab3 = p[0].getCoord(2) - p[1].getCoord(2);
		double cd1 = p[2].getCoord(0) - p[3].getCoord(0);
		double ab2 = p[0].getCoord(1) - p[1].getCoord(1);
		double bc3 = p[1].getCoord(2) - p[2].getCoord(2);

		double d = ab1 * bc2 * cd3 + bc1 * cd2 * ab3 + cd1 * ab2 * bc3 - ab3 * bc2 * cd1 - bc3 * cd2 * ab1 - cd3 * ab2 * bc1;

		if (Math.abs(d) < EPSILON * 0.00001) {
			//System.out.println(d+" "+PointNd.EPSILON);
			return null;
		}

		double ab = 0.5 * (p[0].getCoord(0) * p[0].getCoord(0) + p[0].getCoord(1) * p[0].getCoord(1) + p[0].getCoord(2) * p[0].getCoord(2) - p[1].getCoord(0) * p[1].getCoord(0) - p[1].getCoord(1) * p[1].getCoord(1) - p[1].getCoord(2) * p[1].getCoord(2));
		double bc = 0.5 * (p[1].getCoord(0) * p[1].getCoord(0) + p[1].getCoord(1) * p[1].getCoord(1) + p[1].getCoord(2) * p[1].getCoord(2) - p[2].getCoord(0) * p[2].getCoord(0) - p[2].getCoord(1) * p[2].getCoord(1) - p[2].getCoord(2) * p[2].getCoord(2));
		double cd = 0.5 * (p[2].getCoord(0) * p[2].getCoord(0) + p[2].getCoord(1) * p[2].getCoord(1) + p[2].getCoord(2) * p[2].getCoord(2) - p[3].getCoord(0) * p[3].getCoord(0) - p[3].getCoord(1) * p[3].getCoord(1) - p[3].getCoord(2) * p[3].getCoord(2));

		double d1 = ab * bc2 * cd3 + bc * cd2 * ab3 + cd * ab2 * bc3 - ab3 * bc2 * cd - bc3 * cd2 * ab - cd3 * ab2 * bc;
		double d2 = ab1 * bc * cd3 + bc1 * cd * ab3 + cd1 * ab * bc3 - ab3 * bc * cd1 - bc3 * cd * ab1 - cd3 * ab * bc1;
		double d3 = ab1 * bc2 * cd + bc1 * cd2 * ab + cd1 * ab2 * bc - ab * bc2 * cd1 - bc * cd2 * ab1 - cd * ab2 * bc1;

		return new PointNd(new double [] {d1 / d, d2 / d, d3 / d});
	}

	public static boolean isInPlane(SimplexNd simplex, PointNd point) {
		if (simplex.getElementDimension() < simplex.getSpaceDimension()) {
			PointNd[] tmp1 = simplex.getNodes();
			PointNd[] tmp2 = new PointNd[tmp1.length + 1];
			for (int i = 0; i < tmp1.length; i++) {
				tmp2[i] = tmp1[i];
			}
			tmp2[tmp1.length] = point;
			if (getLinearHullDimension(tmp1) == getLinearHullDimension(tmp2)) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	/** 
		* Methode zum Erfragen, ob zwei Simplexe in derselben Ebene liegen.
		*
		* @param simplex1, simplex2 Simplexe fuer die ueberprueft werden soll, ob sie 
		* in einer Ebene liegen.
		* @return <code>true</code>, wenn der Punkt in der Ebene des Simplexes 
		* liegt, <code>false</code>, wenn nicht. 
		*/
	public static boolean isInPlane(SimplexNd simplex1, SimplexNd simplex2) {
		PointNd[] tmp = simplex2.getNodes();
		boolean erg = true;
		for (int i = 0; i < tmp.length; i++) {
			erg = erg && isInPlane(simplex1, tmp[i]);
		}
		return erg;
	}
        
        public static boolean isLinearlyIndependent(AbstractVectorNd[] points) {
        //this code is copied from the the class SimplexNd
        if(points.length < 2) throw new IllegalArgumentException("points given als Parameter to " +
                "isLinearlyIndependent Method must contain more than one point to do a linear independence test on");
        if(points.length > points[0].dim()) return false;
        
        DMatrix m = new DMatrix(points[0].dim(), points.length+1);
        
        for (int i = 0; i < points.length+1; i++) {
            for (int j = 0; j < points[0].dim(); j++) {
                if (i == points.length)
                    m.setItem(j, i, 0.);
                else
                    m.setItem(j, i, points[i].getCoord(j));
            }
        }
        
        DMatrix erg = LGS.gauss(m);
        
        // falls erg mehrere Spalten hat, sind Punkte nicht lin Unabhaengig
        
        if (erg.getCols() > 1)
            return false;
        else
            return true;
    }
        
        public static boolean isAffinelyIndependent(PointNd[] points) {
        //this code is copied from the the class SimplexNd
        
        if(points.length < 3) throw new IllegalArgumentException("points given als Parameter to " +
                "isAffinelyIndependent Method must contain more than two point to do an affine independence test on");
        if(points.length-1 > points[0].dim()) return false;
        
        double[] x = new double[points[0].dim()];
        VectorNd[] vectors = new VectorNd[points.length-1];
        for(int i = 0; i < vectors.length; i++) {
            for (int j = 0; j < points[0].dim(); j++) {
                x[j] = points[i+1].getCoord(j) - points[0].getCoord(j);
            }
            vectors[i] = new VectorNd(x);
        }
        
        return AlgGeometryNd.isLinearlyIndependent(vectors);
    }
        
        public static PointNd[] getAffinelyIndependentPointNdSet(PointNd[] pointsSet) {
        if(pointsSet.length < 3) throw new IllegalArgumentException("the pointsSet points number must be " +
                "at least 3");
        
        ArrayList<PointNd> originalSet = new ArrayList<PointNd>();
        for(int i = 0; i < pointsSet.length; i++) {
            originalSet.add(pointsSet[i]);
        }
        
        ArrayList<PointNd> affinSet = new ArrayList<PointNd>();
        for(int i = 0; i < 3; i++) {
            affinSet.add(originalSet.remove(0));
        }
        
        while ( !originalSet.isEmpty() ) {
            if(AlgGeometryNd.isAffinelyIndependent(affinSet.toArray(new PointNd[0]))) {
                if (affinSet.size() == pointsSet[0].dim()+1) {
                    break;
                } else {
                    affinSet.add(originalSet.remove(0));
                }
            } else {
                affinSet.add(affinSet.size()-1,originalSet.remove(0));
            }
        }
        if(!AlgGeometryNd.isAffinelyIndependent(affinSet.toArray(new PointNd[0]))) affinSet.remove(affinSet.size()-1);     
        
        if(affinSet.size() == 2) throw new RuntimeException("No affinely independent PointNd set out of the argument PointNd set");
        return affinSet.toArray(new PointNd[0]);
    }
}
