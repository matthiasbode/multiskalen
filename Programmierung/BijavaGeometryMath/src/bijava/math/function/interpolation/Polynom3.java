package bijava.math.function.interpolation;

public class Polynom3 {
	double werte[][];
	int anz;
	
	public Polynom3(double w[][]){
		werte = w;
		anz = w[0].length;
	}

	public void initial(double w[][]) {
		werte = w;
		anz = w[0].length;
	}
	
	public double[][] getmin() {
		double y[][] = new double[2][1];
		y[0][0] = werte[0][0];
		y[1][0] = werte[1][0];
		for (int i=1;i<anz;i++) {
			if (werte[1][i] < y[1][0]) {
				y[0][0] = werte[0][i];
				y[1][0] = werte[1][i];
			}
		}
		return y;
	}
	public double[][] getmax() {
		double y[][] = new double[2][1];
		y[0][0] = werte[0][0];
		y[1][0] = werte[1][0];
		for (int i=1;i<anz;i++) {
			if (werte[1][i] > y[1][0]) {
				y[0][0] = werte[0][i];
				y[1][0] = werte[1][i];
			}
		}
		return y;
	}
	public double getValue(double t) {
		double y = 10000.;
		double t1=0.,t2=0.,y1=0.,y2=0.;
		double w[][] = new double[2][2];
		int i,j,pos;
		w[0][0] = werte[0][0];
		w[1][0] = werte[1][0];
		pos = anz;
		for (i=0;i<anz;i++) {
			if (werte[0][i]>t) {
				pos = i-1;
				i = anz;
			}

		}
		if (pos<anz) {
			t1 = werte[0][pos];
			y1 = werte[1][pos];
			t2 = werte[0][pos];
			y2 = werte[1][pos];
			for (j=pos;j<anz;j++) {
				if (werte[0][j]>t) {
					t2 = werte[0][j];
					y2 = werte[1][j];
					j = anz;
				}

			}
			if (t1 != t2)
				y = y1 + ((t-t1)/(t2-t1))*(y2-y1);	
			else
				y = y1;
		}
		return y;
	}
	
	public double getDifferential(double t) {
		double y = 10000.;
		double t1=0.,t2=0.,y1=0.,y2=0.;
		double w[][] = new double[2][2];
		int i,j,pos;
		w[0][0] = werte[0][0];
		w[1][0] = werte[1][0];
		pos = anz;
		for (i=0;i<anz;i++) {
			if (werte[0][i]>t) {
				pos = i-1;
				i = anz;
			}

		}
		if (pos<anz) {
			t1 = werte[0][pos];
			y1 = werte[1][pos];
			t2 = werte[0][pos];
			y2 = werte[1][pos];
			for (j=pos;j<anz;j++) {
				if (werte[0][j]>t) {
					t2 = werte[0][j];
					y2 = werte[1][j];
					j = anz;
				}

			}
			if (t1 != t2)
				y = (y2-y1)/(t2-t1);	
			else
				y = 1.;
		}
		return y;
	}
	
	public  String toString(){
		String s="";
			
		for (int i=0;i<anz;i++) {
			s+=" "+ werte[0][i] + " "+ werte[1][i];
		}
		return s;
	}
	

}
