package bijava.math.pde.fvm;

import bijava.geometry.dimN.ConvexPolyhedronNd;
import java.util.*;


/**
 * This class provides the finite control volume decomposition of the problem domain to be solved
 */
public class FVDecomposition {

    /**
     * The control volumes, of which the FVDecompostion consists of.
     */
private Vector<FVolume> fVolumes =new Vector();
    /**
     * The FVDOFs that belongs to the finite control volumes in fVolumes.
     */
private Vector<FVDOF> dofs =new Vector();

    /**
     * Contruct a new FVDecomposition
     */
    public FVDecomposition() {

    }
    /**
     * Construct a FVDecompostion instance
     * @param fVolumes finite control volumes, of which the FVDecomposition consists
     */
    public FVDecomposition(Vector<FVolume> fVolumes) {
      this.setFVolumes(fVolumes);
    }
    /**
     * add a FVolume to the FVDecomposition, if the FVDecomposition does already have this FVolume.
     * @param element finite control volume to be add to the FVDecomposition
     */
    public void addFVolume(FVolume element) {

        if (!dofs.contains(element.getDOF())) dofs.addElement(element.getDOF());

        if (!fVolumes.contains(element)) fVolumes.addElement(element);
    }
    
    /**
     * initialize the adjacencies relations between the finite volumes in the FVDecomposition
     */
    public void initialNeighbourhoodRelations(){
        
        Enumeration<FVolume> volumes = fVolumes.elements();
        
        while (volumes.hasMoreElements()){
            
            FVolume fVolume = volumes.nextElement();
            FVFacet[] fVFacets = fVolume.getFVFacetsArray();
            
            for(int i=0; i < fVFacets.length; i++){
                
                Enumeration<FVolume> volumes1 = fVolumes.elements();
                
                while (volumes1.hasMoreElements()){
                    
                    FVolume fVolume1 = volumes1.nextElement();
                    FVFacet[] fVFacets1 = fVolume1.getFVFacetsArray();
                    
                    for(int j=0; j < fVFacets1.length; j++){
                        if ( fVFacets[i].geometryEquals(fVFacets1[j]) && !(fVFacets[i].equals(fVFacets1[j])) )
                        fVFacets[i].setNeighbourDof(fVFacets1[j].getThisDof());
                    }  
                }
            }
        }
    }
    
    /**
     * 
     * @param fVolumes 
     */
    private void setFVolumes(Vector<FVolume> fVolumes) {
        this.fVolumes = fVolumes;
        Enumeration<FVolume> volumes = fVolumes.elements();
        
        while (volumes.hasMoreElements()){
            FVolume fVolume = volumes.nextElement();
            this.addDOF(fVolume.getDOF());
        }
    }
    
    /**
     * Returns the i-th finite volume of the FVDecomposition
     * @param i number of the requested finite
     * @return 
     */
    public FVolume getFVolume(int i){
        return fVolumes.elementAt(i);
    }
  
    
    /**
     * Retrun all the finite control volumes of the FVDecomposition
     * @return a Iterator over all of FVolume objects.
     */
    public Iterator<FVolume> allFVolumes() {
        return fVolumes.iterator();
    }

    
    /**
     * 
     * @return 
     */
    public int getNumberofFElements() {
        return fVolumes.size();
    }
  
    
    /**
     * 
     * @param dof 
     */
    public void addDOF(FVDOF dof){
        if (!dofs.contains(dof)) dofs.addElement(dof);
    }
    
	  
    /**
     * 
     * @param i 
     * @param dof 
     */
    public void addDOF(int i, FVDOF dof){
        if (!dofs.contains(dof)) dofs.addElement(dof);
    }

  
    /**
     * 
     * @param i 
     * @return 
     */
    public FVDOF getDOF(int i) {
        return (FVDOF) dofs.elementAt(i);
    }
    
    
    /**
     * 
     * @return 
     */
    public Enumeration<FVDOF> allDOFs() {
        return dofs.elements();
    }

    
    /**
     * 
     * @return 
     */
    public int getNumberofDOFs() {
        return dofs.size();
    }
}
