package bijava.math.ode.ivp;

/** The Class solve, starting from a startsolution <I>(t,x[])</I>, 
using a Euler- and a Heun-Method solutions of 1. (<I>bad[]</I>) and 2. Order 
for the System of 1. Order Ordinary Differential Equations <BR> <BR>
	y' = F(t,x[]) <BR> <BR>
at the point <I> t + dt </I>
 * @author Peter Milbradt
*/


public class RK_1_2_TStep implements RKETStep {
	
	public double[] TimeStep(ODESystem sys, double t, double dt,  double x[], double bad[]){
		int resultSize=sys.getResultSize();
		double rhelp[], result[], k1[];
		result = new double[resultSize];
		rhelp = new double[resultSize];
		k1 = null;
		
		k1 = sys.getValue(t, x);
		for(int i=0;i<resultSize;i++){
			rhelp[i] = x[i] + dt/2. * k1[i];
			bad[i] = x[i] + dt * k1[i];
		}
		
		k1 = sys.getValue(t+dt/2., rhelp);
		for(int i=0;i<resultSize;i++){
			result[i] = x[i] + dt * k1[i];
		}
		
		return result;
		
	}
}
