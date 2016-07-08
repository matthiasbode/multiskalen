package bijava.math.pde.fem;

import java.util.*;


public class FEDecomposition {
  
    private Vector<FElement> feelem =new Vector<FElement>();
    private Vector<FEDOF> dofs =new Vector<FEDOF>();

  
   /** @param element */
    public void addFElement(FElement element) {
        
        for (int i = 0; i < element.getDOFs().length; i++) {
			if (!dofs.contains(element.getDOFs()[i])) dofs.addElement(element.getDOFs()[i]);
				element.getDOFs()[i].addAElement(element);
		}
        
        /*
        Enumeration elementdofs = element.allDOFs();
        while (elementdofs.hasMoreElements()) {
            DOF dof = (DOF) elementdofs.nextElement();
            if (!dofs.contains(dof)) dofs.addElement(dof);
            dof.addAElement(element);
        }*/
        if (!feelem.contains(element)) feelem.addElement(element);
    }
  
    
    public FElement getFElement(int i){
        return feelem.elementAt(i);
    }
  
    
    public Iterator<FElement> allFElements() {
        return feelem.iterator();
    }

    
    public int getNumberofFElements() {
        return feelem.size();
    }
  
    
    public void addDOF(FEDOF dof){
        if (!dofs.contains(dof)) dofs.addElement(dof);
    }
    
	  
    public void addDOF(int i, FEDOF dof){
        if (!dofs.contains(dof)) dofs.addElement(dof);
    }

  
    public FEDOF getDOF(int i) {
        return (FEDOF) dofs.elementAt(i);
    }
    
    
    public Enumeration allDOFs() {
        return dofs.elements();
    }

    
    public int getNumberofDOFs() {
        return dofs.size();
    }
}
