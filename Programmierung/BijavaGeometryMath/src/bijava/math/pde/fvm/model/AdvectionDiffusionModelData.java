/*
 * AdvectionDiffusionModelData.java
 *
 * Created on 8. Juni 2007, 13:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.pde.fvm.model;

import bijava.geometry.LinearPoint;
import bijava.geometry.dimN.VectorNd;
import bijava.math.pde.ModelData;

/**
 *
 * @author abuabed
 */
public class AdvectionDiffusionModelData implements ModelData {
    
    double c;
    
    double rc;
    
    double rcOld;

    VectorNd u = new VectorNd(new double[]{0.02});
    
    /** Creates a new instance of AdvectionDiffusionModelData */
    public AdvectionDiffusionModelData() {
        
    }
    
    public ModelData clone(){
        AdvectionDiffusionModelData rvalue = new AdvectionDiffusionModelData();
//        rvalue.C = C;
//        rvalue.dCdt = dCdt;
//        rvalue.dCdx = dCdx;
//        rvalue.rC = rC;
//        rvalue.bC = bC;
        return rvalue;
    }
    
    public ModelData initialNew() {
        return new AdvectionDiffusionModelData();
    }

    public ModelData add(ModelData md) {
        AdvectionDiffusionModelData rvalue = null;
        if(md instanceof AdvectionDiffusionModelData){
//            rvalue = (FVTransport1DData)md;
//            rvalue.C += C;
//            rvalue.dCdt += dCdt;
//            rvalue.dCdx += dCdx;
//            rvalue.rC += rC;
        }
        return rvalue;
    }

    public ModelData mult(double scalar) {
        AdvectionDiffusionModelData rvalue = new AdvectionDiffusionModelData();
//        rvalue.C = C*scalar;
//        rvalue.dCdt = dCdt*scalar;
//        rvalue.dCdx = dCdx *scalar;
//        rvalue.rC = rC *scalar;
//        if(bC!=null)
//            rvalue.bC = new AbstractScalarFunction1d(){
//                public double getValue(double p){
//                    return scalar*bC.getValue(p);
//                };
//            };
        return rvalue;
    }

    public ModelData sub(ModelData md) {
        AdvectionDiffusionModelData rvalue = null;
//        if(md instanceof FVTransport1DData){
//            rvalue = (FVTransport1DData)md;
//            rvalue.C -= C;
//            rvalue.dCdt -= dCdt;
//            rvalue.dCdx -= dCdx;
//            rvalue.rC -= rC;
//        }
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
