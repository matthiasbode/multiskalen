package bijava.math.function.interpolation.meshless;

/**
 * @author kaapke
 * @since 12.02.2004
 * @version 12.02.2004
 */
public class RBF_Gaussians implements RadialBasisFunction {
	
	private double spread=1.; 
	
	public RBF_Gaussians() {
		this.spread = 1.;
	}
	
	public RBF_Gaussians(double spread) {
		this.spread = spread;
	}
	
	public void setSpread(double spread){
		this.spread = spread;
	}
	
	public double getSpread(){ return this.spread;}
	
	public double getValue(double r) {
		return Math.exp(-((r/spread)*(r/spread)));	
	}

}
