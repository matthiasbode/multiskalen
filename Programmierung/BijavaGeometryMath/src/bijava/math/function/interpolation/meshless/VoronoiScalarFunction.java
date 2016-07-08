package bijava.math.function.interpolation.meshless;

import bijava.geometry.MetricPoint;
import bijava.math.function.ScalarFunction2d;


/** Interpolation die einen gestuften Verlauf hat */
public class VoronoiScalarFunction {
    private MetricPoint[]p;
    private double[]f;
    public VoronoiScalarFunction(MetricPoint[] p, double[] f) {
        this.p= p;
        this.f = f;
    }
    
    public double getValue(MetricPoint p) {
        double rf = f[0];
        double d = this.p[0].distance(p);
        for(int i=1; i<this.p.length; i++)
            if(this.p[i].distance(p)<d) {
            d = this.p[i].distance(p);
            rf = f[i];
            }
        return rf;
    }
}
