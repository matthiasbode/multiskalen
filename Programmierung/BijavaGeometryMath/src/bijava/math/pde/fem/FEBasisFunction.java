package bijava.math.pde.fem;

import bijava.geometry.dimN.PointNd;
import bijava.math.function.AbstractDifferentialFunctionNd;
import bijava.math.function.DifferentialScalarFunctionNd;


/**
* Die abstrakte Klasse TrialFunction abgeleitet von Function 
* ----------------------------------------------------------
* stellt grundlegende Funktionen fuer Ansatzfunktionen 
* zur Verfuegung
* ----------------------------------------------------------
* @author  
* @version 0.1
*/

public abstract class FEBasisFunction extends AbstractDifferentialFunctionNd{
	private int order; //Ordnung der Ansatzfunktion
	private FEDOF dof; //Freiheitsgrad an dem die Funktion 1 betraegt
	private DifferentialScalarFunctionNd[] localCoordinate; //lokale Koordinaten

	public FEDOF getDOF() {
		return dof;
	}

	public void setDOF(FEDOF d) {
		this.dof = d;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int o) {
		this.order = o;
	}

	public DifferentialScalarFunctionNd[] getLocalCoordinate() {
		return localCoordinate;
	}

	public void setLocalCoordinate(DifferentialScalarFunctionNd[] f) {
		this.localCoordinate = f;
	}
        
        public int getDim(){
            return dof.getPoint().dim();
        }
        
        public abstract double getValue(PointNd p);
}
