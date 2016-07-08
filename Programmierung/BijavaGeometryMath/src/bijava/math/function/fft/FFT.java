package bijava.math.function.fft;

import bijava.math.function.interpolation.LinearizedScalarFunction1d;
import bijava.math.ComplexNumber;

// Klasse zum Durchf�hren der FFT-Analyse und der FFT-
// Synthese
public abstract class FFT
{
	
	// Funktion f�r die Analyse: Aus Funktionswerten 
	// werden die Fourierkoeffizienten berechnet
	public static ComplexNumber[] analysis(LinearizedScalarFunction1d f)
	{
            int n = f.getSizeOfValues();
            // Testen ob die Anzahl der Messwerte 2^n ist, wobei n eine Ganzzahl gr��er Null ist
            double d = Math.log(n) / Math.log(2);
            int id = (int) d;
            if(d - id == 0.0 && d >= 1.0){
		ComplexNumber[] c = calcAnalysis(f);
		for(int i = 0; i < c.length; i++)
                    c[i] = c[i].div(c.length);
		return c;
            }else{
                System.out.println("can't use FFT");
                /* f.addNull();
			Complex[] c = calcAnalysis(f);
			Complex.div(c, c.length);			
			return c; 
                */
                return DFT.analysis(f);
            }
	}
	   
	public static ComplexNumber[] calcAnalysis(LinearizedScalarFunction1d f)
	{
		int n = f.getSizeOfValues();
		ComplexNumber[] c = new ComplexNumber[n];
		if(n == 2)
		{				
			c[0] = new ComplexNumber((f.getValueAt(0)[1] + f.getValueAt(1)[1]), 0.0);
			c[1] = new ComplexNumber((f.getValueAt(0)[1] - f.getValueAt(1)[1]), 0.0);
		}
		else
		{
			int m = (int)(n * 0.5);
			ComplexNumber[] cEven   = calcAnalysis(FFT.getEvenFunc(f));
			ComplexNumber[] cOdd    = calcAnalysis(FFT.getOddFunc(f));
			for(int k = 0; k < m; k++)
			{	
				ComplexNumber c1	  = cEven[k];
				ComplexNumber c2    = FFT.changePhi(cOdd[k], 2 * Math.PI * k / n);
				c[k]          = c1.add(c2);
				c[k+m]        = c1.sub(c2);
			}
		}
		return c;
	}	

	public static ComplexNumber changePhi(ComplexNumber c, double d)
	{		
		double abs = c.norm();
		double arg = c.argument() + d;			
		while(arg > 2*Math.PI) arg -= 2*Math.PI;	
		while(arg < 2*Math.PI) arg += 2*Math.PI;	
		return new ComplexNumber(abs*Math.cos(arg), abs*Math.sin(arg));
	}							

	public static LinearizedScalarFunction1d getEvenFunc(LinearizedScalarFunction1d f)
	{
		int n = f.getSizeOfValues();
		int m = (int)(n*0.5);
		double[][] values = new double[2][m];
		for (int i = 0; i < m; i ++)
		{
			double[] d = f.getValueAt((int)(2*i));
			values[0][i] = d[0];
			values[1][i] = d[1];
		}
		return new LinearizedScalarFunction1d(values);
	}


	public static LinearizedScalarFunction1d getOddFunc(LinearizedScalarFunction1d f)
	{
		int n = f.getSizeOfValues();
		int m = (int)(n*0.5);
		double[][] values = new double[2][m];
		for (int i = 0; i < m; i ++)
		{
			double[] d = f.getValueAt((int)(2*i+1));
			values[0][i] = d[0];
			values[1][i] = d[1];
		}
		return new LinearizedScalarFunction1d(values);
	}

	// Funktion fuer die Synthese: Aus Fourierkoeffizienten
	// werden die Funktionswerte berechnet
	public static LinearizedScalarFunction1d synthesis(ComplexNumber[] c, double time)
	{
		ComplexNumber[] c2 = calcSynthesis(c);
		double[][] values = new double[2][c2.length]; 
		for(int i = 0; i < c2.length; i++)
		{
			double x = i * time / c2.length;
			values[0][i] = x;
			values[1][i] = c2[i].real;
		}	
		LinearizedScalarFunction1d f = new LinearizedScalarFunction1d(values);	
		return f;
	}
	
	public static ComplexNumber[] calcSynthesis(ComplexNumber[] c)
	{
		int n = c.length;
		ComplexNumber[] c2 = new ComplexNumber[n];
		if(n == 2)
		{				
			c2[0] = c[0].add(c[1]);
			c2[1] = c[0].sub(c[1]);
		}
		else
		{
			int m = (int)(n * 0.5);
			ComplexNumber[] cEven = new ComplexNumber[m];
			ComplexNumber[] cOdd  = new ComplexNumber[m];
			for(int i = 0; i < m; i++)
			{
				cEven[i] = c[(2*i)];
				cOdd[i]  = c[(2*i)+1];
			}
			cEven = calcSynthesis(cEven);
			cOdd  = calcSynthesis(cOdd);
			for(int k = 0; k < m; k++)
			{	
				ComplexNumber co1	  = cEven[k];
				ComplexNumber co2   = FFT.changePhi(cOdd[k], -2 * Math.PI * k / n);
				c2[k]         = co1.add(co2);
				c2[k+m]       = co1.sub(co2);
			}
		}
		return c2;
	}
	
}
