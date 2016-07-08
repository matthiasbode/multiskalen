package bijava.math.pde.fvm;

import java.util.Enumeration;
import java.util.Iterator;

/**  FV-Approximation of a System of partial differential equations */
public abstract class FVApproximation {
    /**
     * Finite Volume Decompostion of problem domain
     */
    public FVDecomposition fVDecomposition;
    public FVModel fvmodel;
    
    /** DOFs initialisieren*/
    public void initialDOFs(){
        FVDOF dof;
        Enumeration<FVDOF> dofs = fVDecomposition.allDOFs();
        while (dofs.hasMoreElements()) {
            dof = dofs.nextElement();
            dof.addModelData(fvmodel);
        }
    }
    
    /**
     * perform an iteration over the finite control volumes of the FVDecompostion
     */
    public void performVolumeLoop(){
        Iterator<FVolume> volumes = fVDecomposition.allFVolumes();
        while (volumes.hasNext()) {
            fvmodel.integrateVolume(volumes.next());
        }
    }
}
