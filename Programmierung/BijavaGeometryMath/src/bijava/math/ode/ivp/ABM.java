package bijava.math.ode.ivp;

/** The Class solve, starting from a startsolution <I>(ta,xa[])</I> of the System of 
1. Order Ordinary Differential Equations <BR> <BR>
	y' = F(t,x[]) <BR> <BR>
at the point <I> te </I>. The Integration have a intern time step control depending
from a absolut and relativ errorestimation (<I> eps_abs, eps_rel </I>).
*/
public class ABM {

	static final double MACH_EPS = 2.220446049250313e-016;
	static final double MACH_2   = 100.*MACH_EPS;

	private double norm( double a[], double b[]) {
		double diff=0., hilf;
		int n=a.length;
		for(int i=0;i<n;i++){
			hilf = Math.abs(a[i]-b[i]);
			diff = Math.max(diff,hilf);
		}
		return diff;
	}
/** The Method Solve compute the solution of the 1. Order ODE-System withe the <I>method<\I>
*/
    public double [] Solve(ODESystem sys, double ta, double xa[], double te, double dt_start, double dt_max, double eps_abs, double eps_rel){
	int resultSize=sys.getResultSize();
	ABMTStep methode = new ABMTStep ();
	DynNewtonPolynomOrder3[] f_help = new DynNewtonPolynomOrder3[resultSize];
	EulerTStep emethode = new EulerTStep();

	double t=ta, dt=dt_start, diff;
	double s=2.;
	
	double[] x = new double[resultSize];
	double[] x_old = new double[resultSize];
        double[] x_bad = new double[resultSize];

	for(int i=0;i<resultSize;i++)
			x_old[i] = xa[i];
	        
//	Startstep
	double sysValue[];
	sysValue = new double[resultSize];
	double[][] w=new double [2][4];
	for(int i=0;i<resultSize;i++)
	    f_help[i]=new DynNewtonPolynomOrder3(w);
	    
	for (t=0.;t<=t+3.*dt_start;t+=dt_start){
	    sysValue = sys.getValue(t,x);
	    for(int i=0;i<resultSize;i++) f_help[i].addValue(t,sysValue[i]);
	    x = emethode.TimeStep(sys,t,dt,x);
	}
//	End Startstep
		
	do{
	    if((t+dt)>te) 
		dt = te - t;
	    x = methode.TimeStep(sys,t,dt,x_old,x_bad,f_help);
			
/*	    diff = norm(x_good,x_bad);
	    if ( diff < MACH_2 )
		s=2.;
	    else{
		double xmax = norm(x_good,x_null);
		s = Math.sqrt(dt *(eps_abs + eps_rel*xmax) /diff);
	}
*/	    if(s>1.){
		// Step Akzept step dt
		for(int i=0;i<resultSize;i++)
		    x_old[i] = x[i];
		t+=dt;
		dt*=Math.min(2.,0.98*s);
	    } else {
		dt*=Math.max(0.5,0.98*s);
	    }
//	    System.out.println(dt);
					
	} while (t<te);
		return x;
	}
	
}
