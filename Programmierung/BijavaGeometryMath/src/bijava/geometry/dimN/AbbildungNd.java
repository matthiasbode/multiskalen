package bijava.geometry.dimN;

import bijava.math.function.*;
import bijava.vecmath.*;
import java.util.*;
import bijava.geometry.dimN.MatrixNd;

/**
 * Die Klasse AbbildungNd stellt grundlegende Funktionen fuer die AbbildungNd einer Parametrischen Zelle bereit.
 * 
 * @author  schierbaum
 * @version 1.0
 * @since   1.0
 */

public class AbbildungNd {

    /* Attributliste */
    private Vector<FormFunctionNd> formfunction;  // Vektor der Formfunktionen
    
    
    public AbbildungNd() {
        formfunction=new Vector<FormFunctionNd>();
    }
    
    
    public PointNd getParametricPoint(ScalarFunctionNd[] coordinate, PointNd p){
        PointNd parampoint= new PointNd(p.dim());
        Enumeration formfunct = this.formfunction.elements();
        while (formfunct.hasMoreElements()) {
            FormFunctionNd ff = (FormFunctionNd) formfunct.nextElement();
            PointNd ffvalue=ff.getValue(coordinate, p);
            for (int i=0; i<parampoint.dim(); i++){
                parampoint.x[i]+=ffvalue.x[i];
            }
        }
        return parampoint;
    }
    
    public PointNd getReferencePoint(ScalarFunctionNd[] coordinate, PointNd p){
        return null;
    }
    
    
    public void addFormFunction(FormFunctionNd formfunct){
        formfunction.addElement(formfunct);
    }
    
    
    public FormFunctionNd getFormFunction(int i){
        return formfunction.elementAt(i);
    }
    
    public FormFunctionNd[] getFormFunctions(){
        return (FormFunctionNd[]) formfunction.toArray();
    }
        
    public void setFormFunction(int i, FormFunctionNd formfunct){
        formfunction.setElementAt(formfunct, i);
    }
    
    
    public MatrixNd getJacobi(DifferentialScalarFunctionNd[] coordinate, PointNd p){
        PointNd point=new PointNd(p.dim());
        MatrixNd jacobi=new MatrixNd(point.dim(), point.dim());
        jacobi.setZero();
        Enumeration formfunct = this.formfunction.elements();
        while (formfunct.hasMoreElements()) {
            FormFunctionNd ff = (FormFunctionNd) formfunct.nextElement();
            jacobi.add(ff.getJacobi(coordinate, p));
        }
        return jacobi;    
    }

    
}
