package bijava.math.function.interpolation;

public class NewtonPoly03 extends NewtonPoly{

	public NewtonPoly03(double w[][]) {
	    super(w);
	    if (w[0].length != 3) System.out.println("Kein Polynom 3. Ord. !");
		
	}
	
	public double getDifferential(double t) {
		return 	(-b[0]*(werte[0][1]+werte[0][0]) + b[3]*(werte[0][0]*werte[0][1]+werte[0][1]*werte[0][2]+werte[0][0]*werte[0][2])) +
			.5 * t * (b[2] - b[3] * (werte[0][0] + werte[0][1] + werte[0][2]) +
			1/3. * t * t * b[3]);
	}
	
	public double getIntegral(double ta,double tb) {
		return 	((tb * (b[0] - b[1] * werte[0][0] + b[2] * werte[0][0] * werte[0][1] + b[2] * werte[0][0] * werte[0][1] * werte[0][2]) +
			Math.pow(tb,2)/2. +
			Math.pow(tb,3)/3. +
			Math.pow(tb,4)/4.) -
			(ta * (b[0] - b[1] * werte[0][0] + b[2] * werte[0][0] * werte[0][1] + b[2] * werte[0][0] * werte[0][1] * werte[0][2]) +
			Math.pow(ta,2)/2. +
			Math.pow(ta,3)/3. +
			Math.pow(ta,4)/4.));
	}

}
