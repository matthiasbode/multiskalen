package bijava.math.ode.ivp;

class DynNewtonPolynom {
	double werte[][];
	double b[];
	int order;
	
	DynNewtonPolynom(double w[][]){
		order = w[0].length-1;
		werte = new double[2][order+1];
		for (int i=0;i<=order;i++) {
			werte[0][i]= w[0][i];
			werte[1][i]= w[1][i];
		}
		update_b();
	}

	public void update_b() {
		b = new double[order+1];
		double h=0.;
		
		for (int i=0;i<=order;i++) {
			b[i] = werte[1][i];
		}
		for (int i=1; i<=order;i++) {
			for (int k=order;k>=i;k--) {
				h = werte[0][k]-werte[0][k-i];
				if (h == 0.) break;
				b[k] =(b[k]-b[k-1])/h;
			}
		}
	}
	
	public double getValue(double t) {
		double h = 0.;
		int i = 0;
		for (h=b[order],i=order-1;i>=0;i--) 
			h = h * (t - werte[0][i]) +b[i];
		return h;
	}
	
	public double getDifferential(double t) {
		return 0.;
	}
	
	public  String toString(){
		String s="";
			
		for (int i=0;i<order;i++) {
			s+=" "+ werte[0][i] + " "+ werte[1][i];
		}
		return s;
	}
}
