package bijava.math.ode.ivp;

/** Die Routine a_b_m fuehrt einen Schritt mit dem Adams-Bashforth-Moulton Verfahren der Ordnung 3 
durch. */
public class ABMTStep {
	
	public double[] TimeStep(ODESystem sys, double t, double dt,  double[] x, double[] x_bad, DynNewtonPolynomOrder3[] f_help){
		int resultSize=sys.getResultSize();
		double result[], sysValue[];
		result = new double[resultSize];
		sysValue = new double[resultSize];
		
		DynNewtonPolynomOrder3[] f_tmp = new DynNewtonPolynomOrder3[resultSize];

 		// Praediktor
 		for(int i=0;i<resultSize;i++){
			 x_bad[i] = x[i] + f_help[i].getIntegral(t,t+dt);
			 f_tmp[i]= (DynNewtonPolynomOrder3) f_help[i].clone();
		}
		sysValue = sys.getValue(t+dt, x_bad);
		for(int i=0;i<resultSize;i++)
			f_tmp[i].addValue(t+dt,sysValue[i]);
		
		
		// Korrektor Step
		for(int i=0;i<resultSize;i++){
			 result[i] = 0.5*( x_bad[i] + (x[i] + f_tmp[i].getIntegral(t,t+dt)));
		}
		sysValue = sys.getValue(t+dt, result);
		for(int i=0;i<resultSize;i++)
			f_help[i].addValue(t+dt,sysValue[i]);
				
		return result;
	}
}