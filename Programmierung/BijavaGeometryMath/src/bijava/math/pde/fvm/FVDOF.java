package bijava.math.pde.fvm;

import java.util.*;
import bijava.geometry.dimN.PointNd;
import bijava.math.pde.ModelData;
import bijava.math.pde.fvm.FVModel;
import bijava.math.pde.DOF;

/**
 * This class manages the degrees of freedom in a finite volume apporoximation.
 * @author abu abed
 * @version 1.0
 */

public class FVDOF extends DOF{
    /**
     * The number of the FVDOF
     */
    private int number = -1;
    /**
     * The finite contorl volume, to which this FVDOF belongs.
     */
    private FVolume fVolume = null;
    
    /**
     * construct a FVDOF
     *
     * @param nr a global number
     * @param p a Point of the location
     */
    public FVDOF(int nr, PointNd p) {
        super(p);
        this.number = nr;
    }
    
    /**
     * construct a FVDOF
     *
     * @param p a Point of the location
     */
    public FVDOF(PointNd p) {
        super(p);
    }
    
    /**
     * construct a DOF
     * @param nr a global number
     * @param x a field of the coordinates of the location
     */
    public FVDOF(int nr, double[] x) {
        super(new PointNd(x));
        this.number = nr;
    }
    
    /**
     * construct a DOF
     *
     * @param x a field of the coordinates of the location
     */
    public FVDOF(double[] x) {
        super(new PointNd(x));
    }
    
    /**
     * construct a DOF
     *
     * @param nr a global number
     * @param x the x-koordinate of the location
     * @param y the y-koordinate of the location
     * @param z the z-koordinate of the location
     */
    public FVDOF(int nr, double x, double y, double z) {
        super(new PointNd(x,y,z));
        this.number = nr;
    }
    
    /**
     * construct a DOF
     *
     * @param x the x-koordinate of the location
     * @param y the y-koordinate of the location
     * @param z the z-koordinate of the location
     */
    public FVDOF(double x, double y, double z) {
        super(new PointNd(x,y,z));
    }
    
    /**
     * return the Number of the DOF
     *
     * @return the global number
     */
    public int getNumber() {
        return this.number;
    }
    
    /**
     * set the Number of the DOF
     *
     * @param i the global number
     */
    public void setNumber(int i) {
        this.number = i;
    }
    
    /**
     * set the finite Volume of the DOF
     * @param fVolume the finite Volume to be set
     */
    public void setFVolume(FVolume fVolume) {
        this.fVolume = fVolume;
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
     * Returns the finite Volume of the DOF
     *
     * @return a FVolume representation of the finite Volume the DOF is placed in
     */
    public FVolume getFVolume() {
        return this.fVolume;
    }
    
    /**
     * Returns the Point of the DOF object
     *
     * @return a Point representation of the DOF object
     */
    public PointNd getPoint() {
        return this.point;
    }
    
    /**
     * Append and inilize the Data For the spezified FEModel
     *
     * @param model a object witch implements the FEModel interface
     */
    public void addModelData(FVModel model) {
        this.modelData.addElement(model.genData(this));
    }
    
    /**
     * Append and inilize the Data For the spezified FEModel
     * @param modeldata an object which extends the ModelData class
     */
    public void addModelData(ModelData modeldata) {
        this.modelData.addElement(modeldata);
    }
    
    /**
     * Return the Vector of ModelData at the DOF
     *
     * @return Vector of ModelData
     */
    public Iterator allModelDatas() {
        return this.modelData.iterator();
    }
    
    /**
     * Return the Modeldata at the DOF
     *
     * @param obj a object witch extends the ModelData class
     * @return VModeldata of DOF
     */
    public ModelData getModelData(Object obj) {
        Iterator<ModelData> modeldatas = this.allModelDatas();
        while (modeldatas.hasNext()) {
            ModelData md = (ModelData) modeldatas.next();
            if (md.getClass().isInstance(obj))
                return md;
        }
        return null;
    }
    
}
