package bijava.math.ode.ivp;

/** System of ordinary differential equations */
public interface ODESystem  {
	/** give the dimension of the System (number of equations) */
	int  getResultSize();
	/** give the values of each equation at the  Point (time, x[]) */
	double[] getValue(double time, double x[]);
	/** set the System depend maximal time step */
	void setMaxTimeStep(double maxtimestep);
	/** read the System depend maximal time step */
        double getMaxTimeStep();
}
