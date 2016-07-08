/*
 * DOF.java
 *
 * Created on 10. Maerz 2002, 16:00
 */

package bijava.math.pde.fem;

import java.util.*;
import bijava.geometry.dimN.PointNd;
import bijava.math.pde.DOF;
import bijava.math.pde.ModelData;

/**
 * Die Klasse DOF stellt Funktionen und Methoden fuer Freiheitsgrade zur Verfuegung. 
 *
 * @author  milbradt, schierbaum
 * @version 1.0
 */

public class FEDOF extends DOF{
	private int number = -1;
	private Vector<FElement> aelements = new Vector<FElement>();

	/** 
	 * construct a DOF
	 *
	 * @param nr a global number
	 * @param p a Point of the location
	 */
	public FEDOF(int nr, PointNd p) {
		super(p);
		number = nr;
	}
        
        /** 
	 * construct a DOF
	 *
	 * @param p a Point of the location
	 */
	public FEDOF(PointNd p) {
		super(p);
	}

	/** 
	 * construct a DOF
	 *
	 * @param nr a global number
	 * @param x a field of the coordinates of the location
	 */
	public FEDOF(int nr, double[] x) {
		super(new PointNd(x));
		number = nr;
	}

	/** 
	 * construct a DOF
	 *
	 * @param nr a global number
	 * @param x the x-koordinate of the location
	 * @param y the y-koordinate of the location
	 * @param z the z-koordinate of the location
	 */
	public FEDOF(int nr, double x, double y, double z) {
		super(new PointNd(x,y,z));
		number = nr;
	}


	/** 
	 * return the Number of the DOF
	 *
	 * @return the global number
	 */
	public int getNumber() {
		return number;
	}

	/** 
	* set the Number of the DOF
	*
	* @param i the global number
	*/
	public void setNumber(int i) {
		number = i;
	}

	/** 
	 * Returns a String representation of the DOF object
	 *
	 * @return a String representation of the DOF object
	 */
	public String toString() {
		return "( DOF " + number + " " + super.toString() + ")";
	}

	/** 
	 * Returns the Point of the DOF object
	 *
	 * @return a Point representation of the DOF object
	*/
	public PointNd getPoint() {
		return point;
	}

	public void addAElement(FElement element) {
		if (!aelements.contains(element))
			aelements.addElement(element);
	}

	public int getNumberofAElements() {
		return aelements.size();
	}

	/** 
	 * Returns a Enumeration of FElements appending to this DOF
	 *
	 * @return Enumeration of FElements appending to this DOF
	 */
	public Enumeration getAElements() {
		return aelements.elements();
	}

	/** 
	 * Append and inilize the Data For the spezified FEModel
	 *
	 * @param model a object witch implements the FEModel interface
	 */
	public void addModelData(FEModel model) {
		modelData.addElement(model.genData(this));
	}

	/** 
	 * Append and inilize the Data For the spezified FEModel
	 *
	 * @param modelData a object witch extends the ModelData class
	 */
	public void addModelData(ModelData modeldata) {
		modelData.addElement(modeldata);
	}

	/** 
	* Return the Modeldata at the DOF
	*
	* @param obj a object witch extends the ModelData class
	* @return Modeldata of DOF
	*/
	public ModelData getModelData(Object obj) {
		Iterator modeldatas = this.allModelDatas();
		while (modeldatas.hasNext()) {
			ModelData md = (ModelData) modeldatas.next();
			if (md.getClass().isInstance(obj))
				return md;
		}
		return null;
	}

}
