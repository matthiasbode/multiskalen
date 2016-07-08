package bijava.geometry.dimN;

import bijava.vecmath.*;
import bijava.math.function.*;


public abstract class FormFunctionNd{
    
    /**Funktionswert der Funktion am Punkt p 
    * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
    * @return Funktionswert */
    public abstract PointNd getValue(ScalarFunctionNd[] coordinate, PointNd p);
    
    public abstract MatrixNd getJacobi(DifferentialScalarFunctionNd[] coordinate, PointNd p);

}
