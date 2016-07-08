/*
 * Created on 14.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bijava.math.function;




import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;

/**
 * @author pick
 * @version 14.02.2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DerivationNd implements VectorFunctionNd{
	
	DifferentialScalarFunctionNd dsf=null;
	int pos;
	
	public DerivationNd(DifferentialScalarFunctionNd f)
	{
		dsf=f;

	}
	
	public VectorNd getValue(PointNd p)
	{
		return new VectorNd(dsf.getGradient(p));
	}
	
	
	public AbstractScalarFunctionNd[] getDirectionalDerivation()
	{
		int grad=dsf.getDim();
		AbstractScalarFunctionNd scf[]=new AbstractScalarFunctionNd[grad];
		for(int i=0;i<grad;i++)
			scf[i]=new TempClass(i);
		
		return scf;
	}

    public int getDim() {
        return dsf.getDim();
    }
	
	class TempClass extends AbstractScalarFunctionNd
	{
		int pos;
		public TempClass(int pos)
		{
			this.pos=pos;
		}
		
		public double getValue(PointNd p)
		{
			return dsf.getGradient(p).getCoord(pos);
		}
		
		public int getDim()
		{
			return dsf.getDim();
		}
	}

	

}
