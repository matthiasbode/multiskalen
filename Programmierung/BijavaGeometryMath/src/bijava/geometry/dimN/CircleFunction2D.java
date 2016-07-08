package bijava.geometry.dimN;

import bijava.math.function.DifferentialScalarFunctionNd;
import bijava.math.function.ScalarFunctionNd;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;




public class CircleFunction2D extends FormFunctionNd{
    PointNd p_i, p_j;
    double phi_i, phi_j;
    double R;
    
    
    public CircleFunction2D(PointNd p_i, PointNd p_j, double phi_i, double phi_j, double R){
        this.p_i=p_i;
        this.p_j=p_j;
        this.phi_i=phi_i;
        this.phi_j=phi_j;
        this.R=R;
    }
        
    
    /**Funktionswert der Funktion am Punkt p 
    * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
    * @return Funktionswert */
    public PointNd getValue(ScalarFunctionNd[] coordinate, PointNd p){
        PointNd point=new PointNd(p.dim());
        double coordinate_i=0.0;
        double coordinate_j=0.0;
        double s_i=0.0;
        double s_j=0.0; 
        int i=0;
        while (i<coordinate.length) {
            if (((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate()==this.p_i) coordinate_i=coordinate[i].getValue(p);
            if (((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate()==this.p_j) coordinate_j=coordinate[i].getValue(p);
            i++;
        }
        if (coordinate_i>0) s_i=coordinate_i/(coordinate_j+coordinate_i);
        if (coordinate_j>0) s_j=coordinate_j/(coordinate_i+coordinate_j);
        point.x[0]=(coordinate_i+coordinate_j)*this.R*(Math.cos(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.cos(this.phi_i)-s_j*Math.cos(this.phi_j));
        point.x[1]=(coordinate_i+coordinate_j)*this.R*(Math.sin(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.sin(this.phi_i)-s_j*Math.sin(this.phi_j));
        return point;
    }
    
    
    public MatrixNd getJacobi(DifferentialScalarFunctionNd[] coordinate, PointNd p){
        PointNd point=new PointNd(p.dim());
        double coordinate_i=0.0;
        double coordinate_j=0.0;
        double[] coordinate_gradient_i=new double[2];
        double[] coordinate_gradient_j=new double[2];
        double s_i=0.0;
        double s_j=0.0; 
        double s_i_r=0.0;
        double s_j_r=0.0; 
        double s_i_s=0.0;
        double s_j_s=0.0; 
        int i=0;
        while (i<coordinate.length) {
            if (((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate()==this.p_i) coordinate_i=coordinate[i].getValue(p);
            if (((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate()==this.p_j) coordinate_j=coordinate[i].getValue(p);
            if (((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate()==this.p_i) coordinate_gradient_i=coordinate[i].getGradient(p).getVector();
            if (((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate()==this.p_j) coordinate_gradient_j=coordinate[i].getGradient(p).getVector();
            i++;
        }
        if (coordinate_i>0) s_i=coordinate_i/(coordinate_j+coordinate_i);
        if (coordinate_j>0) s_j=coordinate_j/(coordinate_i+coordinate_j);
        if (coordinate_i>0) s_i_r=(coordinate_gradient_i[0]*(coordinate_j+coordinate_i)-coordinate_i*(coordinate_gradient_i[0]+coordinate_gradient_j[0]))/((coordinate_j+coordinate_i)*(coordinate_j+coordinate_i));
        if (coordinate_j>0) s_j_r=(coordinate_gradient_j[0]*(coordinate_j+coordinate_i)-coordinate_j*(coordinate_gradient_i[0]+coordinate_gradient_j[0]))/((coordinate_j+coordinate_i)*(coordinate_j+coordinate_i));
        if (coordinate_i>0) s_i_s=(coordinate_gradient_i[1]*(coordinate_j+coordinate_i)-coordinate_i*(coordinate_gradient_i[1]+coordinate_gradient_j[1]))/((coordinate_j+coordinate_i)*(coordinate_j+coordinate_i));
        if (coordinate_j>0) s_j_s=(coordinate_gradient_j[1]*(coordinate_j+coordinate_i)-coordinate_j*(coordinate_gradient_i[1]+coordinate_gradient_j[1]))/((coordinate_j+coordinate_i)*(coordinate_j+coordinate_i));
        point.x[0]=(coordinate_i+coordinate_j)*this.R*(Math.cos(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.cos(this.phi_i)-s_j*Math.cos(this.phi_j));
        point.x[1]=(coordinate_i+coordinate_j)*this.R*(Math.sin(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.sin(this.phi_i)-s_j*Math.sin(this.phi_j));
        MatrixNd jacobi=new MatrixNd(point.dim(), point.dim());
        jacobi.setZero();
        jacobi.setElement(0,0,(coordinate_gradient_i[0]+coordinate_gradient_j[0])*this.R*(Math.cos(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.cos(this.phi_i)-s_j*Math.cos(this.phi_j))+(coordinate_i+coordinate_j)*this.R*(-1.*Math.sin(this.phi_i*s_i+this.phi_j*s_j)*(phi_i*s_i_r+phi_j*s_j_r)-s_i_r*Math.cos(this.phi_i)-s_j_r*Math.cos(this.phi_j)));
        jacobi.setElement(0,1,(coordinate_gradient_i[0]+coordinate_gradient_j[0])*this.R*(Math.sin(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.sin(this.phi_i)-s_j*Math.sin(this.phi_j))+(coordinate_i+coordinate_j)*this.R*(    Math.cos(this.phi_i*s_i+this.phi_j*s_j)*(phi_i*s_i_r+phi_j*s_j_r)-s_i_r*Math.sin(this.phi_i)-s_j_r*Math.sin(this.phi_j)));
        jacobi.setElement(1,0,(coordinate_gradient_i[1]+coordinate_gradient_j[1])*this.R*(Math.cos(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.cos(this.phi_i)-s_j*Math.cos(this.phi_j))+(coordinate_i+coordinate_j)*this.R*(-1.*Math.sin(this.phi_i*s_i+this.phi_j*s_j)*(phi_i*s_i_s+phi_j*s_j_s)-s_i_s*Math.cos(this.phi_i)-s_j_s*Math.cos(this.phi_j)));
        jacobi.setElement(1,1,(coordinate_gradient_i[1]+coordinate_gradient_j[1])*this.R*(Math.sin(this.phi_i*s_i+this.phi_j*s_j)-s_i*Math.sin(this.phi_i)-s_j*Math.sin(this.phi_j))+(coordinate_i+coordinate_j)*this.R*(    Math.cos(this.phi_i*s_i+this.phi_j*s_j)*(phi_i*s_i_s+phi_j*s_j_s)-s_i_s*Math.sin(this.phi_i)-s_j_s*Math.sin(this.phi_j)));
        return jacobi;    
    }
}
