package bijava.math.nle;

/** System of ordinary differential equations */
public interface NLESystem  {
	/** give the dimension of the System (number of equations) */
	int  getResultSize();
	/** give the values of each equation at the  Point  x[] */
	double[] getValue(double x[]);
	/** give the matrix of derivations at the  Point  x[] */
	double[][] getDerivation(double x[]);
}
