/*
 * MainMacroscopicTrafficModel1D.java
 *
 * Created on 20. April 2007, 08:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.pde.fem.model.traffic;

import javax.swing.*;
import bijava.math.ode.ivp.*;
import bijava.math.pde.fem.*;
/**
 *
 * @author milbradt
 */
public class MainMacroscopicTrafficModel1D extends JComponent {
    //ATTRIBUTE
    //--------------------------------------------------------------------------
    FEDecomposition fed = new FEDecomposition();
    JFrame frame;


    //Konstruktor
    //--------------------------------------------------------------------------
    /**
     * Creates a new MainMacroscopicTrafficModel1D
     */
    public MainMacroscopicTrafficModel1D () {
        //initialisieren des Fensters
        frame = new JFrame("MacroscopicTrafficModel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800,400);
        setSize(800,400);
        frame.getContentPane().add(this);
        frame.setVisible(true);

        //Setzen der Anzahl der Knoten
        int    knoten   = 301;
        
        //Setzen der Laenge
        double laenge   = 30000.0;
        
        //Laenge der Intervalle
        double dx       = laenge/(knoten-1);
        
        // Create FEDecomposition
        FEDOF b;
        FEDOF e = new FEDOF(0,0.,0.,0.);
        for (int m=1; m<knoten; m++) { 
            b = e;
            e = new FEDOF(m,m*dx,0,0); 
            fed.addFElement(new FEdge(b,e));
        }

        //erzeugt ein Stroemungsmodell
        MacroscopicTrafficModel1D  mtraffic1d  = new MacroscopicTrafficModel1D(fed);

        //Anfangswerte des TrafficModells
        double[] mtrafficerg  = mtraffic1d.initialSolution(0.);

        //RKETStep methode = new RK_2_3_TStep();
        //SimpleTStep methode = new EulerTStep();
        SimpleTStep methode = new HeunTStep();
        //ABMTStep   methode = new ABMTStep();

        //Setzt den maximalen Zeitschritt
        mtraffic1d.setMaxTimeStep(0.001);

        double startTime = 0.0;     // [sec]
        double endTime   = 44700;   // [sec]
        double dt        = 10.;     //0.1;

        //... Schleife ueber die Zeit ...........................................
        mtraffic1d.draw_it (getGraphics(), mtrafficerg,   startTime);
        for (double t = startTime; t < endTime; t += dt) {
            //sehe noch keinen Sinn drin
//            double ta = t;
//            double te = t+dt;
//            double ts = 0.;

            //Loest die DGL zu einem Zeitschritt t + dt
            mtrafficerg = IVP.solve(mtraffic1d,t,mtrafficerg,t+dt,methode);

            mtraffic1d.draw_it(getGraphics(), mtrafficerg,   t+dt);
            repaint();
        }
    }
    
    
    //METHODEN
    //--------------------------------------------------------------------------
    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        MainMacroscopicTrafficModel1D e = new MainMacroscopicTrafficModel1D();
    }
}