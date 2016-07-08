package bijava.math.pde.fvm.model;

import bijava.geometry.LinearPoint;
import bijava.math.function.AbstractScalarFunction1d;
import bijava.math.function.ScalarFunction1d;
import bijava.math.pde.ModelData;

public class FVTransport1DData implements ModelData {
    
    // Zustandsgroessen
    double C; double dCdt; double dCdx;
    // Ergebnisvector der Ortsdiskretisierung
    double rC;
    
    // boudary conditions
//    ScalarFunction1d bqx=null;
    ScalarFunction1d bC=null;
    
    /** Creates new FVTransport1DData */
    public FVTransport1DData() {
    }
    
    public ModelData clone(){
        FVTransport1DData rvalue = new FVTransport1DData();
        rvalue.C = C;
        rvalue.dCdt = dCdt;
        rvalue.dCdx = dCdx;
        rvalue.rC = rC;
        rvalue.bC = bC;
        return rvalue;
    }
    /** erzeugt neue Modelldaten und initalisiert diese mit 0 */
    public ModelData initialNew(){
        return new FVTransport1DData();
    }
    /**  addieren als Voraussetung bei der Interpolation */
    public ModelData add(ModelData md){
        FVTransport1DData rvalue = null;
        if(md instanceof FVTransport1DData){
            rvalue = (FVTransport1DData)md;
            rvalue.C += C;
            rvalue.dCdt += dCdt;
            rvalue.dCdx += dCdx;
            rvalue.rC += rC;
        }
        return rvalue;
    }
    /** skalar multiplizieren  als Voraussetung bei der Interpolation */
    public ModelData mult(final double scalar){
        FVTransport1DData rvalue = new FVTransport1DData();
        rvalue.C = C*scalar;
        rvalue.dCdt = dCdt*scalar;
        rvalue.dCdx = dCdx *scalar;
        rvalue.rC = rC *scalar;
        if(bC!=null)
            rvalue.bC = new AbstractScalarFunction1d(){
                public double getValue(double p){
                    return scalar*bC.getValue(p);
                };
            };
            return rvalue;
    }

    public ModelData sub(ModelData md) {
        FVTransport1DData rvalue = null;
        if(md instanceof FVTransport1DData){
            rvalue = (FVTransport1DData)md;
            rvalue.C -= C;
            rvalue.dCdt -= dCdt;
            rvalue.dCdx -= dCdx;
            rvalue.rC -= rC;
        }
        return rvalue;
    }

    public LinearPoint add(LinearPoint point) {
        if(point instanceof ModelData) return this.add((ModelData) point);
        return null;
    }

    public LinearPoint sub(LinearPoint point) {
        if(point instanceof ModelData) return this.sub((ModelData) point);
        return null;
    }
    
    
    
}
