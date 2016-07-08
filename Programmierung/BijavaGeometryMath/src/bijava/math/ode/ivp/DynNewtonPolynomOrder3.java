package bijava.math.ode.ivp;

class DynNewtonPolynomOrder3 extends DynNewtonPolynom {

	DynNewtonPolynomOrder3(double w[][]) {
		super(w);
		if (w[0].length != 4) System.out.println("Kein Polynom 3. Ord. !");
	}
	
	public double getDifferential(double t) {
		return 	(-b[0]*(werte[0][1]+werte[0][0]) + b[3]*(werte[0][0]*werte[0][1]+werte[0][1]*werte[0][2]+werte[0][0]*werte[0][2])) +
			.5 * t * (b[2] - b[3] * (werte[0][0] + werte[0][1] + werte[0][2]) +
			1/3. * t * t * b[3]);
	}
	
	public double getIntegral(double ta,double tb) {
		
		 return (  (b[0] - b[1] * werte[0][0] + b[2] * werte[0][0] * werte[0][1] - b[3] * werte[0][0] * werte[0][1] * werte[0][2])*(tb-ta)
			+ 1./2.*(b[1] - b[2]*(werte[0][0]+werte[0][1]) + b[3]*(werte[0][0]*werte[0][1] + werte[0][1]*werte[0][2] + werte[0][0]*werte[0][2]))*(Math.pow(tb,2)-Math.pow(ta,2))
			+ 1./3.*(b[2] - b[3]*(werte[0][0]+werte[0][1]+werte[0][2]))*(Math.pow(tb,3)-Math.pow(ta,3))
			+ 1./4.* b[3] *(Math.pow(tb,4)-Math.pow(ta,4)));
	}
	
	public void addValue(double t, double w){
		double[][] w2=new double [2][werte[0].length];

		for (int i=1;i<werte[0].length;i++) {
			
				w2[0][i-1] = werte[0][i];
				w2[1][i-1] = werte[1][i];
			
			
		}
		w2[0][werte[0].length-1] = t;
		w2[1][werte[0].length-1] = w;
		werte = w2;

		update_b();
	}
	public Object clone(){
		return  new DynNewtonPolynomOrder3(super.werte);
	}
}
