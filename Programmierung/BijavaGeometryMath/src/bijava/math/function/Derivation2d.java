/*
 * Created on 14.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bijava.math.function;

import bijava.geometry.dim2.Point2d;

import bijava.geometry.dimN.VectorNd;

/**
 * @author pick
 * @version 14.02.2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Derivation2d implements VectorFunction2d{
	
	DifferentialScalarFunction2d dsf=null;
	boolean periodic=false;
	int pos;
	
	public Derivation2d(DifferentialScalarFunction2d f)
	{
		dsf=f;

	}
	
	public VectorNd getValue(Point2d p)
	{
		return new VectorNd(dsf.getGradient(p));
	}
	
	
	public ScalarFunction2d[] getDirectionalDerivation()
	{
		ScalarFunction2d scf[]=new ScalarFunction2d[2];
		scf[0]=new TempClass(0);
		scf[1]=new TempClass(1);
		
		return scf;
	}
	
	class TempClass implements ScalarFunction2d
	{
		int pos;
		public TempClass(int pos)
		{
			this.pos=pos;
		}
		
		public double getValue(Point2d p)
		{
			return dsf.getGradient(p).getCoord(pos);
		}
	}

	

}
