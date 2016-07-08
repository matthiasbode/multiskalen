

package bijava.math.pde;
import bijava.math.pde.ModelData;
import bijava.geometry.LinearPoint;

//Pick
//9.2.2005
public class ScalarModelData implements ModelData {
    private double wert=0;
    
    public ScalarModelData() {
        wert=0;
    }
    
    public ScalarModelData(double wert) {
        this.wert=wert;
    }
    
    public ScalarModelData(ScalarModelData sm) {
        this.wert=sm.wert;
    }
    
    public double getValue() {
        return wert;
    }
    public void setValue(double value) {
        this.wert=value;
    }
    
    /** erzeugt neue Modelldaten und initalisiert diese mit 0 */
    public ScalarModelData initialNew(){
        return new ScalarModelData();
    }
    public ScalarModelData clone(){
        return new ScalarModelData(this.wert);
    }
    
    public  ModelData add(ModelData md) {
        if(isThisModel(md)) {
            ScalarModelData sm=new ScalarModelData((ScalarModelData) md);
            sm.wert+=this.wert;
            return sm;
        }
        return null;
    }
    
    public  ModelData add(LinearPoint md) {
        if(md instanceof ScalarModelData) {
            ScalarModelData sm=new ScalarModelData((ScalarModelData) md);
            sm.wert+=this.wert;
            return sm;
        }
        return null;
    }
    
    public  ModelData sub(ModelData md) {
        if(isThisModel(md)) {
            ScalarModelData sm=new ScalarModelData((ScalarModelData) md);
            sm.wert-=this.wert;
            return sm;
        }
        return null;
    }
    
    public  ModelData sub(LinearPoint md) {
        if(md instanceof ScalarModelData) {
            ScalarModelData sm=new ScalarModelData((ScalarModelData) md);
            sm.wert-=this.wert;
            return sm;
        }
        return null;
    }
    
    public  ModelData mult(double scalar) {
        ScalarModelData sm=new ScalarModelData();
        sm.wert=this.wert*scalar;
        
        return sm;
    }
    
    //Teste ob das Model aus dier Klasse kommt
    public  boolean isThisModel(ModelData md) {
        
        if(md instanceof ScalarModelData)
            return true;
        return false;
    }
    
    public String toString() {
        String erg="ScalarModelData mit Wert="+wert;
        return erg;
    }
    
}
