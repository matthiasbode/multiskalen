package bijava.math.pde.fem;

import bijava.math.pde.ModelData;

/** 
 * System of partial differential equations for FE-Approximation 
 * -------------------------------------------------------------
 */

public interface FEModel {
    public abstract ModelData genData(FEDOF dof);
    //public abstract ModelData genData(FElement ele);
    public abstract void setBoundaryCondition(FEDOF dof, double t); 
    public abstract void ElementApproximation(FElement ele);  
}
