package bijava.geometry.dimN;

import bijava.geometry.dimN.MatrixNd;
import bijava.geometry.dimN.PointNd;
import bijava.math.function.DifferentialScalarFunctionNd;
import bijava.math.function.ScalarFunctionNd;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;


public class IdentitivFunction extends FormFunctionNd{
    int i;
    
    
    /**
     * 
     * @param i 
     */
    public IdentitivFunction(int i){
        this.i=i;
    }
        
    
    /**
     * Funktionswert der Funktion am Punkt p 
     * @return Funktionswert
     * @param coordinate 
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     */
    public PointNd getValue(ScalarFunctionNd[] coordinate, PointNd p){
        PointNd point=new PointNd(p.dim());
        for (int k=0; k<p.dim(); k++){
              point.x[k]=coordinate[i].getValue(p)*((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate().x[k]-((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate().x[k];
        }
        return point;
    }
    
    
    /**
     * 
     * @param coordinate 
     * @param p 
     * @return 
     */
    public MatrixNd getJacobi(DifferentialScalarFunctionNd[] coordinate, PointNd p){
        PointNd point=new PointNd(p.dim());
        MatrixNd jacobi=new MatrixNd(p.dim(), p.dim());
        jacobi.setZero();
        return jacobi;    
    }
    
}
