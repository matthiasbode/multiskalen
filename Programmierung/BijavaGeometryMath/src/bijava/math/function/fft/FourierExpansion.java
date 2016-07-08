package bijava.math.function.fft;

import bijava.math.function.ScalarFunction1d;
import bijava.math.ComplexNumber;

public class FourierExpansion implements ScalarFunction1d
{

	private double[] sinCoefs;
	private double[] cosCoefs;
	private double omega;
	private boolean periodic;

	public FourierExpansion()
	{
		sinCoefs    = new double[1];
		cosCoefs    = new double[1];
		sinCoefs[0] = 0.0;
		cosCoefs[0] = 0.0;
		omega		= 1.0;
		periodic = false;
	}

	public FourierExpansion(ComplexNumber[] c, double t)
	{
		omega	 = 1.0/t;
		int m = (int)(c.length*0.5+1);
		sinCoefs    = new double[m];
		cosCoefs    = new double[m];
		for (int i = 0; i < m; i++)
		{
			sinCoefs[i] = 2.0*c[i].imag;
			cosCoefs[i] = 2.0*c[i].real;		
			System.out.println("Sin"+sinCoefs[i]+" / Cos"+cosCoefs[i]+"OMEGA "+i*omega);
		}
		periodic = true;	

	}


	public double getValue(double x)
	{
		double value = 0.0;
		for(int i = 0; i < sinCoefs.length; i++) 
			value += sinCoefs[i]*Math.sin(x*omega*i)+cosCoefs[i]*Math.cos(x*omega*i);
		return value;
	}

	public void setPeriodic(boolean periodic)
	{
		this.periodic = periodic;
	}

	public boolean isPeriodic()
	{
		return periodic;
	}

}
