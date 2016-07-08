package bijava.math.pde.fem;

import bijava.geometry.*;
import bijava.geometry.dimN.*;
import bijava.math.pde.DOF;
import bijava.math.pde.ModelData;
import java.util.Iterator;

public class FElement {
    
    private NaturalElement element = null;
    private NaturalElementCoordinateFunction[] coordinates = null;  // Speicherung der anonymen lokalen Funktionen
    private FEDOF[] dofs = null;
    private FEBasisFunction[] interpolationFunction = null;
    
    
    public FElement(NaturalElement e, FEDOF[] d, FEBasisFunction[] f){
        element = e;
        dofs = d;
        interpolationFunction = f;
    }
    
    public FElement(FEDOF[] d) {
        PointNd[] point = new PointNd[d.length];
        for (int i = 0; i < d.length; i++) {
            point[i] = d[i].getPoint();
        }
        
        if (point.length==2)
            element = new EdgeNd(point);
        else
            try{
                element = new SimplexNd(point);
            }catch(Exception e){
                element = new ConvexPolyhedronNd(point);
            }
        
        PointNd[] p = ((ConvexPolyhedronNd)element).getNodes();
        coordinates = element.getLocalCoordinateFunction();
        
        double volume = ((ConvexPolyhedronNd)element).getVolume();
        
        dofs = d;
        
        for (int i = 0; i < d.length; i++) {
            addInterpolationFunction(new EdgeLinearInterpolationFunction(coordinates, d[i]));
        }
    }
    
    //**************************
    //* get- und set- Methoden *
    //**************************
    
    public NaturalElement getGeomElement() {
        return element;
    }
    
    public void setGeomElement(NaturalElement e) {
        this.element = e;
    }
    
    public NaturalElementCoordinateFunction[] getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(NaturalElementCoordinateFunction[] f) {
        this.coordinates = f;
    }
    
    public FEDOF[] getDOFs() {
        return dofs;
    }
    
    public FEDOF getDOF(int i){
        return dofs[i];
    }
    
    public void setDOFs(FEDOF[] d) {
        this.dofs = d;
    }
    
    public FEBasisFunction[] getInterpolationFunctions() {
        return interpolationFunction;
    }
    
    public void setInterpolationFunctions(FEBasisFunction[] f) {
        this.interpolationFunction = f;
    }
    
    //*****************************
    //* add- und remove- Methoden *
    //*****************************
    
    public void addDOF(FEDOF d) {
        FEDOF[] tmp = new FEDOF[dofs.length + 1];
        for (int i = 0; i < dofs.length; i++)
            tmp[i] = dofs[i];
        tmp[tmp.length - 1] = d;
        dofs = tmp;
    }
    
    public void addInterpolationFunction(FEBasisFunction f) {
        if(interpolationFunction==null){
            interpolationFunction = new FEBasisFunction[]{f};
        }else{
        FEBasisFunction[] tmp = new FEBasisFunction[interpolationFunction.length + 1];
        for (int i = 0; i < interpolationFunction.length; i++)
            tmp[i] = interpolationFunction[i];
        tmp[tmp.length - 1] = f;
        interpolationFunction = tmp;
        }
    }
    
    /** erzeugt einen DOF mit den zugeh�rigen Modelldaten und belegt diese mit
     *  den Interpolierten Werten
     */
    public DOF Interpolate(PointNd point){
        DOF result = null;
        if (element.contains(point)){
            result=new DOF(point);  //diesmal ohne Modelldaten 
            Iterator<ModelData> modeldatas = dofs[0].allModelDatas();
            
            while (modeldatas.hasNext()) {
                ModelData md = modeldatas.next();
                ModelData mdres = md.initialNew();
                for (int i=0; i<interpolationFunction.length;i++){                               
                     mdres = mdres.add(interpolationFunction[i].getDOF().getModelData(md).mult(interpolationFunction[i].getValue(point)));
                }
                result.addModelData(mdres);
            }
        }
        return result;
    }
}
