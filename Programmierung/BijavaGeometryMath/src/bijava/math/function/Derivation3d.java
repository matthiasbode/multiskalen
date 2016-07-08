/*
 * Created on 14.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bijava.math.function;


import bijava.geometry.dim3.Point3d;

import bijava.geometry.dimN.VectorNd;

/**
 * @author pick
 * @version 14.02.2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Derivation3d implements VectorFunction3d{
	
	DifferentialScalarFunction3d dsf=null;
	boolean periodic=false;
	int pos;
	
	public Derivation3d(DifferentialScalarFunction3d f)
	{
		dsf=f;

	}
	
	public VectorNd getValue(Point3d p)
	{
		return new VectorNd(dsf.getGradient(p));
	}
	
	
	public ScalarFunction3d[] getDirectionalDerivation()
	{
		ScalarFunction3d scf[]=new ScalarFunction3d[3];
		scf[0]=new TempClass(0);
		scf[1]=new TempClass(1);
		scf[2]=new TempClass(2);
		
		return scf;
	}
	
	class TempClass implements ScalarFunction3d
	{
		int pos;
		public TempClass(int pos)
		{
			this.pos=pos;
		}
		
		public double getValue(Point3d p)
		{
			return dsf.getGradient(p).getCoord(pos);
		}
	}

	

}
