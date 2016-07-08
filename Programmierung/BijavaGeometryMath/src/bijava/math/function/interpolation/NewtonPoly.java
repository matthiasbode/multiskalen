package bijava.math.function.interpolation;

import bijava.math.function.ScalarFunction1d;

public class NewtonPoly implements ScalarFunction1d {
	double werte[][];
	double b[];
	int anz;
	
	public NewtonPoly(double w[][]){
		anz = w[0].length;
		werte = new double[2][anz];
		b = new double[anz];
		double h=0.;
		
		for (int i=0;i<anz;i++) {
			werte[0][i]= w[0][i];
			werte[1][i]= w[1][i];
			b[i] = werte[1][i];
		}
		for (int i=1; i<=anz;i++) {
			for (int k=anz;k>=i;k--) {
				h = werte[0][k]-werte[0][k-i];
				if (h == 0.) break;
				b[k] =(b[k]-b[k-1])/h;
			}
		}
	}


	public double getValue(double t) {
		double h = 0.;
		int i = 0;
		for (h=b[anz],i=anz-1;i>=0;i--) 
			h = h * (t - werte[0][i]) +b[i];
		return h;
	}
	
	public double getDifferential(double t) {
		return 0.;
	}
	
	
	public  String toString(){
		String s="";
			
		for (int i=0;i<anz;i++) {
			s+=" "+ werte[0][i] + " "+ werte[1][i];
		}
		return s;
	}
}
