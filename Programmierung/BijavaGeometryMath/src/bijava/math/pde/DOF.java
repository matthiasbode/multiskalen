package bijava.math.pde;

import bijava.geometry.dimN.PointNd;
import java.util.*;


/** Klasse zur Beschreibung von Modelldaten an einem Ort
 *  Basisklasse fuer Finite Differenzen, Elemente und Volumen als auh fuer netzfreie Methoden
 * @author  Milbradt
 * @version 0.1
 */
public class DOF implements Cloneable{
    
    protected PointNd point;
    protected Vector<ModelData> modelData = new Vector<ModelData>();
    
    protected DOF(){};
    
    public DOF(PointNd p) {
        point = p;
    }
    
    /**
     * construct a DOF
     *
     * @param x a field of the coordinates of the location
     */
    public DOF(double[] x) {
        point = new PointNd(x);
    }
    
    /**
     * construct a DOF
     *
     * @param x the x-koordinate of the location
     * @param y the y-koordinate of the location
     * @param z the z-koordinate of the location
     */
    public DOF(double x, double y, double z) {
        point = new PointNd(x,y,z);
    }
    
    public DOF(DOF dof){
        point=dof.point;
        modelData = new Vector<ModelData>();
        Iterator<ModelData> modeldatas = dof.allModelDatas();
        while (modeldatas.hasNext()) {
            ModelData md = modeldatas.next();
            modelData.add(md.initialNew());
        }
    }
    
    /** clone den DOF mit der Lokation p und mit Null initialisierte Modelldaten */
    public DOF clone(PointNd p){
        DOF result = new DOF(p);
        result.modelData = new Vector<ModelData>();
        Iterator<ModelData> modeldatas = this.allModelDatas();
        while (modeldatas.hasNext()) {
            ModelData md = modeldatas.next();
            result.modelData.add(md.initialNew());
        }
        return result;
    }
    
    public DOF clone(){
        DOF result = new DOF(point);
        result.modelData = new Vector<ModelData>();
        Iterator<ModelData> modeldatas = this.allModelDatas();
        while (modeldatas.hasNext()) {
            ModelData md = modeldatas.next();
            result.modelData.add(md.clone());
        }
        return result;
    }
    
    
    public String toString() {
        String str;
        str="Modelldaten am Punkt: "+point+"\n";
        Iterator<ModelData> modeldatas = this.allModelDatas();
        while (modeldatas.hasNext()) {
            ModelData md = modeldatas.next();
            str+=md.toString();
        }
        return str;
    }
    
    
    public PointNd getPoint(){
        return point;
    }
    
    
    /** Append and inilize the Data For the spezified FEModel
     * @param modelData a object witch extends the ModelData class
     */
    public void addModelData(ModelData modeldata) {
        modelData.addElement(modeldata);
    }
    
    
    /** Return a Iterator of ModelData at the DOF
     * @return Iterator of ModelData
     */
    public Iterator<ModelData> allModelDatas(){
        return modelData.iterator();
    }
    
    
    public ModelData getModelData(Object obj){
        Iterator<ModelData> modeldatas = this.allModelDatas();
        while (modeldatas.hasNext()) {
            ModelData md = modeldatas.next();
            if(md.getClass().isInstance(obj)) return md;
        }
        return null;
    }
}
