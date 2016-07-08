package bijava.math.pde.fem.model.traffic;

import java.util.*;
import bijava.math.*;
import java.awt.*;
import bijava.math.ode.ivp.ODESystem;
import bijava.math.pde.ModelData;
import bijava.math.pde.fem.*;

/** This class describe the stabilized finite element approximation
 * of the instationary 1-dimensional Macroscopic Traffic Model (Kerner und Kohnheuser)
 * @author Dr.-Ing. habil. Peter Milbradt
 * @version 1.1
 */
public class MacroscopicTrafficModel1D extends FEApproximation implements FEModel,ODESystem {
    //Attribute
    //--------------------------------------------------------------------------
    public int n,V,RHO,numberofdofs;
    private double maxTimeStep;
    private double[] result;

    
    //Konstruktoren
    //--------------------------------------------------------------------------
    /** Creates new MacroscopicTrafficModel1D
    * @param fe a finite edge domain decomposition
    */
    public MacroscopicTrafficModel1D(FEDecomposition fe) {
        fenet = fe;
        femodel = this;

        //DOFs initialisieren
        initialDOFs();
        numberofdofs = fenet.getNumberofDOFs();
        
        V = 0;
        RHO = numberofdofs;
        n = 2 * numberofdofs;
        result = new double[n];
    }
    
    //Methoden
    //--------------------------------------------------------------------------

    /** compute the initial solutions
    * @param time starttime
    * @return the result vector
    */
    public double[] initialSolution(double time){
        double x[] = new double[getResultSize()];

        System.out.println("MacroscopicTrafficModel - Werte Initialisieren");

        for (Enumeration<FEDOF> dofs = fenet.allDOFs(); dofs.hasMoreElements();) {
            FEDOF dof = dofs.nextElement();
            int i = dof.getNumber();
            MacroscopicTrafficModel1DData mtmd = getMacroscopicTrafficModel1DData(dof);
            x[RHO + i] = mtmd.rho;
            x[V + i] = mtmd.v;
        }
        return x;
    }

    // -------------------------------------------------------------------------
    // ToDO
    // -------------------------------------------------------------------------
    public ModelData genData(FElement felement) {
        return null;
    }

    /**
     * Setzt die Dichte und das Volumen in Modeldata
     * @param dof
     * @return modeldata
     */
    public ModelData genData(FEDOF dof){
        MacroscopicTrafficModel1DData mtd = new MacroscopicTrafficModel1DData();

        double laenge=30000.;
        double deltaRho =   0.01;
        //..Beispiel 1: rhoH = 0.023, Beispiel 2: rhoH = 0.063.....................//
        double cosh1 = 0.5 * ( Math.exp( 0.004*(dof.getPoint().x[0]+100.0-laenge/2.0))
                             + Math.exp(-0.004*(dof.getPoint().x[0]+100.0-laenge/2.0)));
        double cosh2 = 0.5 * ( Math.exp( 0.002*(dof.getPoint().x[0]-100.0-laenge/2.0))
                             + Math.exp(-0.002*(dof.getPoint().x[0]-100.0-laenge/2.0)));
        mtd.rho = MacroFD.rhoH + deltaRho * (1.0/(cosh1*cosh1) - 0.5/(cosh2*cosh2));  
//        e.rho = rhoH + deltaRho * Math.cos(2.0*Math.PI* e.x/laenge - 1.42);
        mtd.v   = MacroFD.getVelocity(mtd.rho);

        return mtd;
    }
    
    
    /** set the solving conditions at the DOF
    * @param dof degree of freedom to set
    * @param t actual time
    */
    public void setBoundaryCondition(FEDOF dof, double t) {
        //..Randwerte setzen..................................................//
        if(dof.getNumber()==0) {
            MacroscopicTrafficModel1DData cmd = getMacroscopicTrafficModel1DData(dof);
            MacroscopicTrafficModel1DData cmdlast = getMacroscopicTrafficModel1DData(fenet.getDOF(numberofdofs-1));
            cmd.rho = cmdlast.rho;	
            cmd.v   = cmdlast.v;
            cmd.drhodt = cmdlast.drhodt;	
            cmd.dvdt   = cmdlast.dvdt;
        }
    }


    public void ElementApproximation(FElement element) {
        FEdge ele = (FEdge)element;
        double[][] koeffmat = ele.getkoeffmat();
        double[] v = new double[2];
        double[] rho = new double[2];
        // compute element derivations
        double dvdx=0.;
        double dv2dx2 = 0.;
        double drhodx=0.;
        double v_mean = 0.;
        double rho_mean = 0.;

        for ( int j = 0; j < 2; j++) {
            FEDOF dof = ele.getDOF(j);
            MacroscopicTrafficModel1DData cmd = getMacroscopicTrafficModel1DData(dof);

            v[j]=cmd.v;
            v_mean+=v[j]/2.;
            dvdx += cmd.v * koeffmat[j][1];
            rho[j]=cmd.rho;
            drhodx += cmd.rho * koeffmat[j][1];
            rho_mean += rho[j] /2.;
        }

        double A1= Math.abs(v_mean) + MacroFD.c0;
        double tauupwind = 0.5 * ele.elm_size() / A1;
        maxTimeStep = Math.min(maxTimeStep,tauupwind );

        double a_opt = 1.;
        double A2 = 1.;
        if ((MacroFD.mu > 0.0) && (rho_mean>0.01)){ 
                A2 = MacroFD.mu / rho_mean;
                double     peclet = A1 * ele.elm_size() / A2;
                a_opt = Function.coth(Math.abs(peclet)) - 1.0 / Math.abs(peclet);
        }

        tauupwind *= a_opt; 

        double residum1_mean = 0.;
        double residum2_mean = 0.;

        for ( int j = 0; j < 2; j++) {
            FEDOF dof = ele.getDOF(j);
            MacroscopicTrafficModel1DData cmd = getMacroscopicTrafficModel1DData(dof);

            residum1_mean += 0.5 * (cmd.drhodt + cmd.v * drhodx + cmd.rho * dvdx
        //                              - auffahrt(cmd.x-2000.0,t)	
                                    );	
            residum2_mean += 0.5 * (cmd.dvdt + cmd.v * dvdx 
                                          - (MacroFD.getVelocity(cmd.rho)-cmd.v)/MacroFD.tau
                                          + MacroFD.c02/cmd.rho * drhodx
                                          - MacroFD.mu/cmd.rho * dv2dx2
        //                                    - zufall.nextGaussian(0.0, 0.063*0.063)
                                    );
        }

        for (int j=0;j<2;j++) {
            FEDOF dof = ele.getDOF(j);
            MacroscopicTrafficModel1DData cmd = getMacroscopicTrafficModel1DData(dof);

            cmd.rrho -= tauupwind * koeffmat[j][1] * (v_mean * residum1_mean + rho_mean * residum2_mean);
            cmd.rv   -= tauupwind * koeffmat[j][1] * (MacroFD.c02/rho_mean * residum1_mean +   v_mean * residum2_mean);

            double vorfaktor;
            for (int l=0;l<2;l++){
                if(j==l) vorfaktor=1./3.;
                else vorfaktor=1./6.;
                cmd.rrho -= vorfaktor* (v[l] * drhodx + rho[l] * dvdx
        //                        - auffahrt(dof.x-2000.0,t)
                                        );
                cmd.rv   -= vorfaktor* (v[l] * dvdx
                              - (MacroFD.getVelocity(rho[l])-v[l])/MacroFD.tau
                              +  MacroFD.c02/rho[l] * drhodx
                              + 2.0 * MacroFD.mu/rho[l] * dvdx * koeffmat[j][1]
        //                        + zufall.nextGaussian(0.0, 0.063*0.063)
                             );	
        //      cmd.dvdx += 0.5 * dvdx;
            }
        }
    }

    
    /**
     * Gibt die Data eines Dof's zurueck
     */
    private MacroscopicTrafficModel1DData getMacroscopicTrafficModel1DData(FEDOF dof){
        MacroscopicTrafficModel1DData cmd=null;
        for (Iterator<ModelData> it = dof.allModelDatas(); it.hasNext();) {            
            ModelData md = it.next();
            if (md instanceof MacroscopicTrafficModel1DData)
                cmd = (MacroscopicTrafficModel1DData)md;
        }
        return cmd;
    }


    /**
     * Gibt die Groesse des resultierenden Feldes zurueck
     */
    public int getResultSize() {
        return n;
    }

    /**
     * Gibt den maximal m&ouml;glichen Zeitschritt zur&uuml;ck
     */
    public double getMaxTimeStep() {
        return maxTimeStep;
    }
    
    
    /**
     *
     */
    public double[] getValue(double p1,double[] x) {
        for (Enumeration<FEDOF> dofs = fenet.allDOFs(); dofs.hasMoreElements();) {
            FEDOF dof = dofs.nextElement();
            int i = dof.getNumber();
            MacroscopicTrafficModel1DData mtmd = getMacroscopicTrafficModel1DData(dof);

            mtmd.rho = Math.min(MacroFD.rhoMax,x[RHO + i]);
            mtmd.v = Math.max(0.,x[V + i]);
            setBoundaryCondition( dof, p1);

            x[RHO + i]=mtmd.rho;
            x[V + i]=mtmd.v;

            mtmd.drhodt=mtmd.rrho;
            mtmd.dvdt=mtmd.rv;

            mtmd.dvdx = 0.;
            // set Results to zero
            mtmd.rv=0.;
            mtmd.rrho=0.;
        }

        maxTimeStep = 10000.;

        // Elementloop
        performElementLoop();

        for (Enumeration<FEDOF> dofs = fenet.allDOFs(); dofs.hasMoreElements();) {
            FEDOF dof = dofs.nextElement();
            int i = dof.getNumber();
            MacroscopicTrafficModel1DData mtmd = getMacroscopicTrafficModel1DData(dof);

            result[V+i] = mtmd.rv;
            result[RHO+i] = (3.*mtmd.rrho-mtmd.drhodt)/2.;
        }
        return result;
    }

    
    /**
     * Setzt den maximalen Zeitschritt
     */
    public void setMaxTimeStep(double p1) {
        maxTimeStep=p1;
    }

    
    /**
     * malt
     */
    public void draw_it (Graphics g, double[] x, double t) {
        g.clearRect(0,0,800,400);
        g.setColor( Color.green );
        g.drawLine(100,300,700,300);
        int knoten=fenet.getNumberofDOFs();

        g.setColor( Color.yellow );
        g.drawLine(100,300 - (int)(1200.00*MacroFD.rhoMax), 
                   700,300 - (int)(1200.00*MacroFD.rhoMax));

        g.setColor( Color.black );
        for (int k=0;k<knoten-1;k++)
            g.drawLine(100 + (int)(   0.02*fenet.getDOF(k).getPoint().x[0]), 
                       300 - (int)(1200.00*x[RHO+k]), 
                       100 + (int)(   0.02*fenet.getDOF(k+1).getPoint().x[0]),
                       300 - (int)(1200.00*x[RHO+k+1]));	

        g.setColor( Color.red );
        for (int k=0;k<knoten-1;k++) 
            g.drawLine(100 + (int)(0.02*fenet.getDOF(k).getPoint().x[0]),
                       300 - (int)(3.60*x[V+k]  ), 
                       100 + (int)(0.02*fenet.getDOF(k+1).getPoint().x[0]),
                       300 - (int)(3.60*x[V+k+1]));	

        g.setColor( Color.blue );
        g.drawString("Zeit: "        + new Integer((int)t).toString()     + " s", 100, 350);
        g.drawString("Zeitschritt: " + new Double(maxTimeStep).toString() + " s", 500, 350);		
    }
}