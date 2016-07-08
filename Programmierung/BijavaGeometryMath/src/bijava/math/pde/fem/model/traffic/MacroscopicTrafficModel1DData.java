package bijava.math.pde.fem.model.traffic;

import bijava.geometry.LinearPoint;
import bijava.math.function.ScalarFunction1d;
import bijava.math.pde.ModelData;

/**
 *
 * @author milbradt
 */
public class MacroscopicTrafficModel1DData implements ModelData {
    //Attribute
    //--------------------------------------------------------------------------            
    //..Zustandsgroessen..
    public double v, dvdt, dvdx;
    public double rho, drhodt;
    //..Ergebnisvector..
    public double rv;
    public double rrho;
    //..Boundary conditions..
    public ScalarFunction1d bv=null;
    public ScalarFunction1d brho=null;
    
    
    //Methoden
    //--------------------------------------------------------------------------
    public ModelData clone() {
        return null;
    }

    public ModelData add(ModelData md) {
        return null;
    }

    public ModelData mult(double scalar) {
        return null;
    }

    public ModelData sub(ModelData md) {
        return null;
    }

    public LinearPoint add(LinearPoint point) {
        return null;
    }

    public LinearPoint sub(LinearPoint point) {
        return null;
    }

    public ModelData initialNew() {
        return null;
    }
}