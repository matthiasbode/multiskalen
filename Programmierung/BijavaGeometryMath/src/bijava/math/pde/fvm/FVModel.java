package bijava.math.pde.fvm;

import bijava.math.pde.ModelData;

/** 
 * System of partial differential equations for FE-Approximation 
 * -------------------------------------------------------------
 */

public interface FVModel {
    ModelData genData(FVDOF dof);
    void setBoundaryCondition(FVDOF dof, double t);  
    void integrateVolume(FVolume volume);
//    void correctVolume(FVolume volume);
}
