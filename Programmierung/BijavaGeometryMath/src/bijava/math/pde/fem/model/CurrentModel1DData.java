package bijava.math.pde.fem.model;
import bijava.geometry.LinearPoint;
import bijava.math.function.ScalarFunction1d;
import bijava.math.pde.ModelData;

/*
 * Current1DModelData.java
 *
 * Created on 26. April 2000, 21:39
 */


/**
 *
 * @author  Peter Milbradt
 * @version
 */
public class CurrentModel1DData implements ModelData {
    
    // Zustandsgroessen
    double u, dudt, dudx;
    double h, dhdt, dhdx;
    // Ergebnisvector
    double ru;
    double rh;
    
    // boudary conditions
    ScalarFunction1d bqx=null;
    ScalarFunction1d bu=null;
    ScalarFunction1d bh=null;
    
    public ModelData clone(){
        CurrentModel1DData r = new CurrentModel1DData();
        r.u=u; r.dudt=dudt; r.dudx=dudx;
        r.h=h; r.dhdt=dhdt; r.dhdx=dhdx;
        r.ru=ru; r.rh=rh;
        r.bqx=bqx; r.bu=bu; r.bh=bh;
        return r;
    }
    /** erzeugt neue Modelldaten und initalisiert diese mit 0 */
    public ModelData initialNew(){
        return new CurrentModel1DData();
    }
    /**  addieren als Voraussetung bei der Interpolation */
    public ModelData add(ModelData md){
        CurrentModel1DData r=null;
        if(md instanceof CurrentModel1DData){
            CurrentModel1DData m = (CurrentModel1DData)md;
            r = (CurrentModel1DData) this.clone();
            r.u+=m.u; r.dudt+=m.dudt; r.dudx+=m.dudx;
            r.h+=m.h; r.dhdt+=m.dhdt; r.dhdx+=m.dhdx;
            r.ru+=m.ru; r.rh+=m.rh;
        }
        return r;
    }
    /** skalar multiplizieren  als Voraussetung bei der Interpolation */
    public ModelData mult(double scalar){
        CurrentModel1DData r = (CurrentModel1DData) this.clone();
        r.u*=scalar; r.dudt*=scalar; r.dudx*=scalar;
        r.h*=scalar; r.dhdt*=scalar; r.dhdx*=scalar;
        r.ru*=scalar; r.rh*=scalar;
        return r;
    }

    public ModelData sub(ModelData md) {
        CurrentModel1DData r=null;
        if(md instanceof CurrentModel1DData){
            CurrentModel1DData m = (CurrentModel1DData)md;
            r = (CurrentModel1DData) this.clone();
            r.u-=m.u; r.dudt-=m.dudt; r.dudx-=m.dudx;
            r.h-=m.h; r.dhdt-=m.dhdt; r.dhdx-=m.dhdx;
            r.ru-=m.ru; r.rh-=m.rh;
        }
        return r;
    }

    public LinearPoint add(LinearPoint point) {
        if (point instanceof ModelData ) return this.add((ModelData)point);
        return null;
    }

    public LinearPoint sub(LinearPoint point) {
        if (point instanceof ModelData ) return this.sub((ModelData)point);
        return null;
    }
    
}
