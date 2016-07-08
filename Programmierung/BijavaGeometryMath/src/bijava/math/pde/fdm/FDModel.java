package bijava.math.pde.fdm;
import bijava.math.pde.ModelData;

/** 
 * System of partial differential equations for FD-Approximation 
 * -------------------------------------------------------------
 */

public interface FDModel {
    public abstract ModelData genData(FDDOF dof);
    public abstract void setBoundaryCondition(FDDOF dof, double t);  
}
