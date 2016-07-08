package bijava.math.pde;

import bijava.geometry.LinearPoint;

//Parant-Class
public abstract class AbstractModelData implements ModelData {
    
    public abstract ModelData clone();
    /** erzeugt neue Modelldaten und initalisiert diese mit 0 */
    public abstract ModelData initialNew();
    /**  addieren als Voraussetung bei der Interpolation */
    public abstract ModelData add(ModelData md);
    /** skalar multiplizieren  als Voraussetung bei der Interpolation */
    public abstract ModelData mult(double scalar);
    
    public  abstract ModelData sub(ModelData md);
    
    public LinearPoint add(LinearPoint point){
        if(point instanceof ModelData)
            return add((ModelData) point);
        return null;
    }
    public LinearPoint sub(LinearPoint point){
        if(point instanceof ModelData)
            return sub((ModelData) point);
        return null;
    }
}