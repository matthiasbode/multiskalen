package bijava.math.pde.fem.model;

import bijava.math.ode.ivp.ODESystem;
import java.util.*;
import bijava.math.pde.*;
import bijava.math.pde.fem.*;
import bijava.math.*;
import java.awt.*;

/** This class describe the stabilized finite element approximation
 * of the instationary 1-dimensional shallow water equation
 * @author Dr.-Ing. habil. Peter Milbradt
 * @version 1.2
 */
public class CurrentModel1D extends FEApproximation implements FEModel,ODESystem {
    static final double G = 9.81;
    static final double AST=0.0012;	     	//0.0012 Austauschkoeffizient fuer Stroemung
    
    private int n,U,H,numberofdofs;
    private double maxTimeStep=0.01;
    private double[] result;
    
    /** Creates new CurrentModel1D
     * @param fe a finite edge domain decomposition
     */
    public CurrentModel1D(FEDecomposition fe) {
        fenet = fe;
        femodel=this;
        // DOFs initialisieren
        initialDOFs();
        
        numberofdofs = fenet.getNumberofDOFs();
        U = 0;
        H = numberofdofs;
        n = 2 * numberofdofs;
        result = new double[n];
    }
    
    /** compute the initial solutions
     * @param time starttime
     * @return the result vector
     */
    public double[] initialSolution(double time){
        double x[] = new double[getResultSize()];
        
        System.out.println("CurrentModel - Werte Initialisieren");
        for (Enumeration e = fenet.allDOFs(); e.hasMoreElements();){
            FEDOF dof= (FEDOF) e.nextElement();
            int i = dof.getNumber();
            CurrentModel1DData cmd = getCurrentModel1DData(dof);
            x[H + i] = cmd.h;
            x[U + i] = 0.;
        }
        return x;
    }
    
    /**
     * @param dof
     * @return
     */
    public ModelData genData(FEDOF dof){
        CurrentModel1DData md = new CurrentModel1DData();
        if (dof.getPoint().x[0]<50) md.h=0.1;
        else md.h=0.;
        return md;
    }
    /** set the solving conditions at the DOF
     * @param dof degree of freedom to set
     * @param t actual time
     */
    public void setBoundaryCondition(FEDOF dof,double t) {
        if(dof.getNumber()==0) {CurrentModel1DData cmd = getCurrentModel1DData(dof); cmd.u=0.;}
        if(dof.getNumber()==numberofdofs-1) {CurrentModel1DData cmd = getCurrentModel1DData(dof); cmd.u=0.;}
    }
    
    public double elm_size(FElement ele) {
        return ele.getDOF(0).getPoint().distance(ele.getDOF(1).getPoint());
    }
    

    /**
     * @param element  */
    public void ElementApproximation(FElement ele) {
//        FEdge ele = (FEdge)element;
//        double[][] koeffmat = ele.getkoeffmat();
        double[] u = new double[2];
        double[] h = new double[2];
        double[] absdepth = new double[2];
        // compute element derivations
        double dudx=0.;
        double dhdx=0.;
        double depthdx=0.;
        double du2dx2 = 0.;
        double u_mean = 0.;
        double absdepth_mean = 0.;
        
        for ( int j = 0; j < 2; j++) {
            FEDOF dof = ele.getDOF(j);
            CurrentModel1DData cmd = getCurrentModel1DData(dof);
            
            u[j]=cmd.u;
            u_mean+=u[j]/2.;
            h[j]=cmd.h;
            absdepth[j] = Math.max( 0.00001 ,dof.getPoint().x[2] + cmd.h );
            dudx += cmd.u * ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0);
            dhdx += cmd.h * ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0);
            absdepth_mean += Math.max( 0.00001 ,dof.getPoint().x[2] +h[j] ) /2.;
            depthdx += Math.max( 0.00001 ,dof.getPoint().x[2] +h[j] ) * ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0);
            System.out.println(ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0));
        }
        double elementsize=elm_size(ele);
        double ast = AST;
	// Smagorinsky-Ansatz
	ast += Math.pow(ast*elementsize,2.)*Math.abs(dudx);
        
        double operatornorm = Math.abs(u_mean)+Math.sqrt(G*absdepth_mean);
        
        double tau_cur = 0.5 * elementsize / operatornorm;
        
        maxTimeStep = Math.min(maxTimeStep, tau_cur);
	
	double a_opt = 1.;
        if(ast>0.00001) {
            double peclet = operatornorm * elementsize / ast;
            a_opt = Function.coth(peclet) - 1.0 / peclet;
        }
        tau_cur *= a_opt;
        
        double cureq1_mean = 0.;
        double cureq2_mean = 0.;
        
        for ( int j = 0; j < 2; j++) {
            DOF dof = ele.getDOF(j);
            CurrentModel1DData cmd = getCurrentModel1DData(dof);
            
            cureq1_mean += 1./2. * ( cmd.dhdt + dof.getPoint().x[2] * dudx + cmd.u * depthdx );
            cureq2_mean += 1./2. * ( cmd.dudt + G * dhdx + cmd.u * dudx );
        }
        //    System.out.println("Fehler = "+cureq1_mean+" "+cureq2_mean);
        
        for (int j=0;j<2;j++){
            DOF dof = ele.getDOF(j);
            CurrentModel1DData cmd = getCurrentModel1DData(dof);
            
            cmd.rh -= tau_cur * ( ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0) * absdepth_mean * cureq2_mean +
                                  ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0) * u_mean * cureq1_mean
                                );
            cmd.ru -= tau_cur * ( ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0) * u_mean * cureq2_mean +
                                  ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0) * G * cureq1_mean
                                );
            
            double vorfaktor;
            for (int l=0;l<2;l++){
                if(j==l) vorfaktor=1./3.;
                else vorfaktor=1./6.;
               
                cmd.rh -= vorfaktor * ( absdepth[l] * dudx + u[l] * depthdx );
                cmd.ru -= vorfaktor * ( G * dhdx + u[l] * dudx + 2.* ast*dudx*ele.getInterpolationFunctions()[j].getGradient(dof.getPoint()).getCoord(0));
            }
            cmd.dudx += 0.5 * dudx;
        }
    }
    
    private CurrentModel1DData getCurrentModel1DData(DOF dof){
        CurrentModel1DData cmd=null;
        Iterator<ModelData> modeldatas = dof.allModelDatas();
        while (modeldatas.hasNext()) {
            ModelData md = (ModelData) modeldatas.next();
            if(md instanceof CurrentModel1DData)  cmd = ( CurrentModel1DData )md;
        }
        return cmd;
    }
    
    /**
     * @return  */
    public int getResultSize() {
        return n;
    }
    
    /**
     * @return  */
    public double getMaxTimeStep() {
        return maxTimeStep;
    }
    
    /** getValue
     * @return  */
    public double[] getValue(double p1,double[] x) {
        
        for (Enumeration e = fenet.allDOFs(); e.hasMoreElements();){
            FEDOF dof= (FEDOF) e.nextElement();
            int i = dof.getNumber();
            CurrentModel1DData current = getCurrentModel1DData(dof);
            
            current.h = x[H + i];
            current.u = x[U + i];
            
            setBoundaryCondition( dof, p1);
            
            x[H + i]=current.h;
            x[U + i]=current.u;
            
            current.dudx = 0.;
            // set Results to zero
            current.ru=0.;
            current.rh=0.;
        }
        
        maxTimeStep = 10000.;
        
        // Elementloop
        performElementLoop();
        
        for (Enumeration e = fenet.allDOFs(); e.hasMoreElements();){
            FEDOF dof= (FEDOF) e.nextElement();
            int i = dof.getNumber();
            CurrentModel1DData current = getCurrentModel1DData(dof);
            
            result[U+i] = current.ru;
            result[H+i] = current.rh;
            
            // WATT - Strategie
            if ( ((dof.getPoint().x[2] + current.h) <= 0.) && (result[H+i] <= 0.)) {
                result[H+i] = 0.;
            }
            current.dudt = result[U+i];
            current.dhdt = result[H+i];
        }
        
        return result;
    }
    
    public void setMaxTimeStep(double p1) {
        maxTimeStep=p1;
    }
    
    
    public void draw_it( Graphics g, double[] x, double time) {
        
        g.clearRect(0,0,800,400);
        g.setColor( Color.white);
        g.fillRect(0,0,800,400);
        
        g.setColor( Color.green );
        g.drawLine(100,300,700,300);
        g.setColor( Color.black );
        g.drawLine(100,200,700,200);
        
        int anz=fenet.getNumberofDOFs();
        
        g.setColor( Color.blue );
        for( int k=0;k<anz-1;k++) {
            g.drawLine((int)(5*fenet.getDOF(k).getPoint().x[0])+100, 400 - ((int)(500.*x[H+k])+200),
            (int)(5*fenet.getDOF(k+1).getPoint().x[0]+100), 400 - ((int)(500.*x[H+k+1])+200));
        }
        
        g.setColor( Color.red );
        for( int k=0;k<anz-1;k++) {
            g.drawLine((int)(5*fenet.getDOF(k).getPoint().x[0]+100), 400 - ((int)(500.*x[U+k])+200),
            (int)(5*fenet.getDOF(k+1).getPoint().x[0]+100), 400 - ((int)(500.*x[U+k+1])+200));
        }
        g.setColor( Color.blue );
    }
}
