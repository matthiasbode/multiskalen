/*
 * Created on 14.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bijava.math.function;

/**
 * @author pick
 * @version 14.02.2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Derivation1d implements ScalarFunction1d{
	
	private DifferentialScalarFunction1d dsf=null;
	private boolean periodic=false;
	
	public Derivation1d(DifferentialScalarFunction1d f)
	{
		dsf=f;
	}
	
	public double getValue(double x)
	{
		return dsf.getGradient(x);
	}
	
	public boolean isPeriodic()
	{
		return periodic;
	}
	
	public void setPeriodic(boolean is)
	{
		periodic=is;
	}
	
	

}
