package bijava.math.ode.ivp;

/** The Class solve, starting from a startsolution <I>(t,x[])</I>, 
using a Runge-Kutta-embbeding-Method solutions of 2. (<I>bad[]</I>) and 3. Order 
for the System of 1. Order Ordinary Differential Equations <BR> <BR>
	y' = F(t,x[]) <BR> <BR>
at the point <I> t + dt </I>
 * @author Peter Milbradt
*/

public class RK_2_3_TStep implements RKETStep {
	
	public double[] TimeStep(ODESystem sys, double t, double dt,  double x[], double bad[]){
		int resultSize=sys.getResultSize();
		double rhelp[], result[]; 
		double k1[], k2[], k3[];
		result = new double[resultSize];
		rhelp = new double[resultSize];
		k1 = null;
		k2 = null;
		k3 = null;
		
		k1 = sys.getValue(t, x);
		for(int i=0;i<resultSize;i++)
			rhelp[i] = x[i] + dt * k1[i];
		
		
		k2 = sys.getValue(t+dt, rhelp);
		for(int i=0;i<resultSize;i++)
			rhelp[i] = x[i] + dt/4. * (k1[i] + k2[i]);
		
		k3 = sys.getValue(t+dt/2., rhelp);
		for(int i=0;i<resultSize;i++){
			bad[i]   = x[i] + dt/2. * (k1[i] + k2[i]); 
			result[i] = x[i] + dt/6. * (k1[i] + k2[i] + 4. *k3[i]);
		}
		
		return result;
		
	}
}
