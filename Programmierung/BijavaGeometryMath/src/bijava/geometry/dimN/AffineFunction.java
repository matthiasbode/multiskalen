package bijava.geometry.dimN;

import bijava.math.function.*;
import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;


public class AffineFunction extends FormFunctionNd{
    PointNd p_i;
    MatrixNd jacobi;
    MatrixNd transformation;
    
    
    public AffineFunction(PointNd p_i, double[] transformation){
        this.transformation=new MatrixNd(p_i.dim()+1,p_i.dim()+1,transformation);
        this.p_i=p_i;
    }
    
    
    public AffineFunction(PointNd pr_i, PointNd pp_i){
		this.transformation=new MatrixNd(pr_i.dim()+1,pr_i.dim()+1);
    	transformation.setIdentity();
    	for (int i = 0; i < pr_i.dim(); i++) {
			transformation.setElement(i, pr_i.dim(), - pr_i.x[i] + pp_i.x[i]);
		}
		this.p_i=pr_i;
    }
    
    
	/** Affine Funktionen zur Bildung eines Einheitsquadrates 
	  * @param p Array von Punkten, die auf ein Einheitsquadrat transformiert werden sollen,
	  * @return Array von Affinen Funktionen */
    public static AffineFunction[] getAffineFunctions(PointNd[] pr, PointNd[] pp){
    	AffineFunction[] af = new AffineFunction[pr.length];
    	af[0]=new AffineFunction(pr[0],pp[0]);
		af[1]=new AffineFunction(pr[1],pp[1]);
		af[2]=new AffineFunction(pr[2],pp[2]);
		af[3]=new AffineFunction(pr[3],pp[3]);
		return af;
    }
        
    
    /**Funktionswert der Funktion am Punkt p 
    * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
    * @return Funktionswert */
    public PointNd getValue(ScalarFunctionNd[] coordinate, PointNd p){
    	GMatrix coordTransMatr=new GMatrix(p.dim(), p.dim());
    	GVector coordTransVect=new GVector(p.dim());
    	for (int i = 0; i < p.dim() ; i++) {
    		for (int j = 0; j < p.dim(); j++) {
    			coordTransMatr.setElement(i,j,transformation.getElement(i,j));
			}
			coordTransVect.setElement(i,transformation.getElement(i, p.dim()));
		}
        int i=0;
        while (i<coordinate.length) {
            if (((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate()==this.p_i){
            	double coordvalue_i;
            	boolean bool=false;
            	for (int j=0; j<coordinate.length; j++){
            		if (((NaturalElementCoordinateFunction) coordinate[j]).getCoordinate().equals(p)) bool=true;
            	}
            	if (bool==true){
					if(p.equals(p_i)) coordvalue_i=1.;
					else coordvalue_i=0.;
				}
                else {
                	coordvalue_i=coordinate[i].getValue(p);
                }
                PointNd coordcoord_i=((NaturalElementCoordinateFunction) coordinate[i]).getCoordinate();
                GVector vec1=new GVector(coordcoord_i.x);
                //coordTransVect.setElement(0,-1.);
		//coordTransVect.setElement(1,0.);
                vec1.mul(coordTransMatr,vec1);
                vec1.add(coordTransVect);
                vec1.scale(coordvalue_i);
                int size = vec1.getSize();
                double[] pw = new double[size];
                for (i=0;i<size;i++) pw[i]=vec1.getElement(i);
                return new PointNd(pw);
            }
            i++;
        }
        return null;
    }  
    
    
    public MatrixNd getJacobi(DifferentialScalarFunctionNd[] coordinate, PointNd p){
		PointNd point=new PointNd(p.dim());
        MatrixNd jacobi=new MatrixNd(point.dim(), point.dim());
        jacobi.setZero();
        return jacobi;    
    }
    
}