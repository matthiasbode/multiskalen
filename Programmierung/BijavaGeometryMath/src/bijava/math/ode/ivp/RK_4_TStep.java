package bijava.math.ode.ivp;

public class RK_4_TStep implements SimpleTStep {
	
	public double[] TimeStep(ODESystem sys, double t, double dt,  double x[]){
		int i;
		int resultSize=sys.getResultSize();
		double result[];
		result = new double[resultSize];

		// temporary
		double a1= 1./6., a2 = 1./3., a3 = 1./3., a4= 1./6.;
		
		double[] k1 = sys.getValue(t, x);
        	for( i=0; i< resultSize; i++) result[i]=x[i] + dt/2. * k1[i];
        	
        	double[] k2 = sys.getValue(t+dt/2., result);
        	for( i=0; i< resultSize; i++) result[i]=x[i] + dt/2. * k2[i];
        	
        	double[] k3 = sys.getValue(t+dt/2., result);
        	for( i=0; i< resultSize; i++) result[i]=x[i] + dt * k3[i];
        	
        	double[] k4 = sys.getValue(t+dt, result);
        	for( i=0; i< resultSize; i++) 
        		result[i]=x[i] + dt/6.*(k1[i]+2.*k2[i]+2.*k3[i]+k4[i]);
        	
		return result;
		
	}
}
