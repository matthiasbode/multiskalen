package bijava.math.ode.ivp;

public class HeunTStep implements SimpleTStep {
	int resultSize=0;
	double t1_result[],result[], sysValue[], x[];
	double dt;
	private int NumberOfThreads = 1;
	
	/** Anzahl der Threads Festlegen, die zur L�sung erzeugt werden sollen,
	Defaultwert ist 1. */
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
		t1_result = new double[resultSize];
		
		sysValue = sys.getValue(t, x);
		if(NumberOfThreads==1) {
            		for(int i=0;i<resultSize;i++){
				t1_result[i] = x[i] + dt/2. * sysValue[i];
			}
        	} else {
            		ParallelLoop1[] ploop1 = new ParallelLoop1[NumberOfThreads];
            		for (int ii=0; ii<NumberOfThreads; ii++){
                		ploop1[ii]= new ParallelLoop1(resultSize*ii/NumberOfThreads, resultSize*(ii+1)/NumberOfThreads);
                		ploop1[ii].start();
            		}
            		for(int ii=0; ii<NumberOfThreads; ii++)
                		try{ploop1[ii].join();} catch(Exception e){}
        	}

		
		sysValue = sys.getValue(t+dt/2., t1_result);
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
	
	class ParallelLoop1 extends Thread {
    		int lo, hi;
    		ParallelLoop1 (int lo, int hi){
      			this.lo=lo;
      			this.hi=hi;
    		}
    		public void run(){
      			for(int i=lo; i<hi; i++){
        			t1_result[i] = x[i] + dt/2. * sysValue[i];
      			}
    		}
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
