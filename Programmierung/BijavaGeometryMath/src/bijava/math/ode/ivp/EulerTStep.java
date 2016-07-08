package bijava.math.ode.ivp;

public class EulerTStep implements SimpleTStep {
	int resultSize=0;
	double result[], sysValue[], x[];
	double dt;
	private int NumberOfThreads = 1;
	
	/** Anzahl der Threads Festlegen, die zur Loesung erzeugt werden sollen,
	Defaultwert ist 1 */
	public void setNumberOfThreads(int i){
  		NumberOfThreads = i;
  	}
  
  	public int getNumberOfThreads(){
  		return NumberOfThreads;
  	}
	
	public double[] TimeStep(ODESystem sys, double t, double dt,  double x[]){
		resultSize=sys.getResultSize();
		this.dt = dt;
		this.x = x;
		result = new double[resultSize];
		
		sysValue = sys.getValue(t, x);
		
		if(NumberOfThreads==1) {
            		for(int i=0;i<resultSize;i++){
				result[i] = x[i] + dt * sysValue[i];
			}
        	} else {
            		ParallelLoop[] ploop = new ParallelLoop[NumberOfThreads];
            		for (int ii=0; ii<NumberOfThreads; ii++){
                		ploop[ii]= new ParallelLoop(resultSize*ii/NumberOfThreads, resultSize*(ii+1)/NumberOfThreads);
                		ploop[ii].start();
            		}
            		for(int ii=0; ii<NumberOfThreads; ii++)
                		try{ploop[ii].join();} catch(Exception e){}
        	}

		return result;
		
	}
	
	class ParallelLoop extends Thread {
    		int lo, hi;
    		ParallelLoop (int lo, int hi){
      			this.lo=lo;
      			this.hi=hi;
    		}
    		public void run(){
      			for(int i=lo; i<hi; i++){
        			result[i] = x[i] + dt * sysValue[i];
      			}
    		}
  	}
}
