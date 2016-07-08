package bijava.math.pde.fem;

import java.util.*;

/**  FE-Approximation of a System of partial differential equations */
public abstract class FEApproximation {
  public FEDecomposition fenet;
  public FEModel femodel;
  private int numberOfThreads = 1;
  
  public void setNumberOfThreads(int i){
  	numberOfThreads = i;
  }
  
  public int getNumberOfThreads(){
  	return numberOfThreads;
  }
  
  /** DOFs initialisieren*/
  public void initialDOFs(){
        FEDOF dof;
        Enumeration _dofs = fenet.allDOFs();
        while (_dofs.hasMoreElements()) {
            dof = (FEDOF) _dofs.nextElement();
            dof.addModelData(femodel);
        }
  }
  
   /** perform Elementloop using the Method ElementApproximation  */
  public void performElementLoop(){
        if(numberOfThreads==1) {
            Iterator<FElement> elem = fenet.allFElements();
            while (elem.hasNext()) {
                femodel.ElementApproximation(elem.next());
            }
        } else {
            ElementLoop[] elemloop = new ElementLoop[numberOfThreads];
            int anzelem = fenet.getNumberofFElements();
            for (int ii=0; ii<numberOfThreads; ii++){
                elemloop[ii]= new ElementLoop(anzelem*ii/numberOfThreads, anzelem*(ii+1)/numberOfThreads);
                elemloop[ii].start();
            }
            for(int ii=0; ii<numberOfThreads; ii++)
                try{elemloop[ii].join();} catch(Exception e){}
        }
  }
  
  class ElementLoop extends Thread {
    int lo, hi;
    ElementLoop (int lo, int hi){
      this.lo=lo;
      this.hi=hi;
    }
    public void run(){
      for(int i=lo; i<hi; i++){
        femodel.ElementApproximation(fenet.getFElement(i));
      }
    }
  }
}
