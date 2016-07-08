package bijava.math.ode.ivp;

public interface SimpleTStep {
	double[] TimeStep(ODESystem sys, double t, double dt,  double x[]);
}
