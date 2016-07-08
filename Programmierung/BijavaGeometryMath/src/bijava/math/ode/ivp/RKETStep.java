package bijava.math.ode.ivp;

public interface RKETStep {
	double[] TimeStep(ODESystem sys, double t, double dt,  double x[], double bad[]);
}
