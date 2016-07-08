package bijava.math.pde.fem;

import bijava.geometry.dimN.PointNd;
import bijava.math.pde.DOF;
import java.util.*;

/**
 * FE-Interpolation
 * @author Peter Milbradt
 */
public class FEInterpolation {
    private FEDecomposition fenet;
    private boolean optimizedData=false;
    
    /**
     * 
     * @param fenet 
     */
    public FEInterpolation(FEDecomposition fenet){
        this.fenet=fenet;
    }
                
    /**
     * 
     * @param point 
     * @return 
     */
    public DOF Interpolate(PointNd point){
        Iterator<FElement> elem = fenet.allFElements();
        while (elem.hasNext()){
            FElement felem = elem.next();
            if ((felem).getGeomElement().contains(point)){
                return felem.Interpolate(point);
            }
        }
        return null;
    }
    
    public FEDecomposition getFEDecomposition()
    {
    	return fenet;
    }
}
