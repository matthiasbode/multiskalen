package bijava.math.pde.fvm.model;

import bijava.math.ode.ivp.ODESystem;
import bijava.math.pde.fvm.FVDOF;
import bijava.math.pde.fvm.FVApproximation;
import bijava.math.pde.fvm.FVDecomposition;
import bijava.math.pde.fvm.FVModel;
import bijava.math.pde.ModelData;
import bijava.math.pde.fvm.FVolume;
import java.util.Enumeration;
import java.util.Iterator;

public class FVTransport1D extends FVApproximation implements FVModel, ODESystem
{
    private int resultsize = 1;
    private double u;
    private double mue;
    private double tstepmax = 1.0;

    private int numberofdofs = 0;
    private double[] resultvec;

    public FVTransport1D(FVDecomposition FVDecomp) 
    {
        fVDecomposition = FVDecomp;
        fvmodel = this;
        this.u = 0.0;
        this.mue = 0.0;
        this.tstepmax = 1.0;
        this.numberofdofs = FVDecomp.getNumberofDOFs();
        this.resultvec = new double[resultsize*numberofdofs];
    }

    public FVTransport1D(FVDecomposition FVDecomp, double u, double mue) 
    {
        fVDecomposition = FVDecomp;
        fvmodel = this;
        this.u = u;
        this.mue = mue;
        this.tstepmax = 1.0;
        this.numberofdofs = FVDecomp.getNumberofDOFs();
        this.resultvec = new double[resultsize*numberofdofs];
    }

    // initialSolution
    //   Anfangswertberechnung, Setzen/Interpolieren der Anfangsloesung
    public double[] initialSolution(double time){
	double firstresult[] = new double[getResultVectorSize()];
	System.out.println("Transport1D - Anfangswerte Initialisieren");
	for (Enumeration e = fVDecomposition.allDOFs(); e.hasMoreElements();){
	    FVDOF dof= (FVDOF) e.nextElement();
	    int i = dof.getNumber();
	    FVTransport1DData tmd = getFVTransport1DData(dof);
	    firstresult[i] = tmd.C;
	}
	return firstresult;
    }

    public double getMaxTimeStep() { return tstepmax;}
    
    public int getResultSize() { return resultsize; }
    
    public int getResultVectorSize() { return resultsize * numberofdofs; }
    
    public void setMaxTimeStep(double maxtimestep) { this.tstepmax = maxtimestep;}
    
    //------------------------------------------------------------------------
    // genData
    //------------------------------------------------------------------------
    public ModelData genData(FVDOF dof){
	FVTransport1DData md = new FVTransport1DData();
	md.C = 0.0;     // Standard Anfangswert, kann in initialSolution() noch ge�ndert werden
	md.dCdt = 0.0; md.dCdx = 0.0; md.rC = 0.0;
        return md;
    }

    //------------------------------------------------------------------------
    // setBoundaryCondition
    //------------------------------------------------------------------------
    public void setBoundaryCondition(FVDOF dof,double t) {
//        if (dof.getNumberofNeighbours() < 2) {
//            FVTransport1DData md = getFVTransport1DData(dof);
//            md.C = 0.0;
//        }
    }
    
    //------------------------------------------------------------------------
    // getValue
    //------------------------------------------------------------------------
    public double[] getValue(double time, double[] x)
    {	
        double[] res = new double[getResultSize()];
        for ( int i=0; i<x.length; i++)
        {
            resultvec[i] = 0.0;
        }
        return res;
    }
    
    
    
    
    private FVTransport1DData getFVTransport1DData(FVDOF dof){
	FVTransport1DData trans1dd=null;
	Iterator<ModelData> modeldatas = dof.allModelDatas();
	while (modeldatas.hasNext()) {
	    ModelData md = modeldatas.next();
	    if(md instanceof FVTransport1DData)  trans1dd = ( FVTransport1DData )md;
	}
	return trans1dd;
    }

    public void integrateVolume(FVolume volume) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
