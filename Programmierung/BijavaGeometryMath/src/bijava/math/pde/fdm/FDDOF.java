/*
 * DOF.java
 *
 * Created on 10. Maerz 2002, 16:00
 */

package bijava.math.pde.fdm;

import bijava.math.pde.DOF;
import bijava.math.pde.ModelData;
import java.util.*;


/**
 * Die Klasse DOF stellt Funktionen und Methoden fuer Freiheitsgrade zur Verfuegung. 
 *
 * @author  milbradt
 * @version 0.1
 * @since   0.1
 */

public class FDDOF extends DOF{

	/** 
	 * Returns a String representation of the DOF object
	 *
	 * @return a String representation of the DOF object
	 */
	public String toString() {
		return "( DOF " + " " + super.toString() + ")";
	}

	/** 
	 * Append and inilize the Data For the spezified FEModel
	 *
	 * @param model a object witch implements the FEModel interface
	 */
	public void addModelData(FDModel model) {
		modelData.addElement(model.genData(this));
	}

}
