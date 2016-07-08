package bijava.math.pde.fem;

import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;


public class EdgeQuadraticInterpolationFunction extends FEBasisFunction{
    
    private FEDOF DOF_i;
    private FEDOF DOF_j;
    private boolean referPoint;
    
    
    public EdgeQuadraticInterpolationFunction(NaturalElementCoordinateFunction[] l, FEDOF d, FEDOF d1, FEDOF d2, boolean rp){
        super.setLocalCoordinate(l);
        super.setDOF(d);
        super.setOrder(2);
        this.DOF_i=d1;
		this.DOF_j=d2;
		this.referPoint=rp;
    }
    
    
    public double getValue(PointNd p){
        double lambdaDOF=Double.NaN, lambdaDOF_i=Double.NaN, lambdaDOF_j=Double.NaN;
        int i=0;

        if (referPoint == true){
            while (i<super.getLocalCoordinate().length) {
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==super.getDOF().getPoint())  lambdaDOF =super.getLocalCoordinate()[i].getValue(p);
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_i.getPoint()) lambdaDOF_i=super.getLocalCoordinate()[i].getValue(p);
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_j.getPoint()) lambdaDOF_j=super.getLocalCoordinate()[i].getValue(p);
                i++;
            }
            return lambdaDOF*lambdaDOF-(lambdaDOF*lambdaDOF_i)-(lambdaDOF*lambdaDOF_j); 
        }
        if (referPoint == false){
            while (i<super.getLocalCoordinate().length) {
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_i.getPoint()) lambdaDOF_i=super.getLocalCoordinate()[i].getValue(p);
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_j.getPoint()) lambdaDOF_j=super.getLocalCoordinate()[i].getValue(p);
                i++;
            }
            return 4.*lambdaDOF_i*lambdaDOF_j;
        }
        return Double.NaN;
    }
    
    
    /** Ableitung der Ansatzfunktion ueber dem FE-Element am Punkt p
     * @param p Punkt, an dem die Ableitung berechnet werden soll
     * @return Feld der Ableitungsvektoren  */
    public VectorNd getGradient(PointNd p) {
        double lambdaDOF=Double.NaN;
        double lambdaDOF_i=Double.NaN;
        double lambdaDOF_j=Double.NaN;
        double[] grad_lambdaDOF=new double[2];
        double[] grad_lambdaDOF_i=new double[2];
        double[] grad_lambdaDOF_j=new double[2];
        double[] grad_Result=new double[2];
        
        int i=0;

        if (referPoint == true){
            while (i<super.getLocalCoordinate().length) {
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==super.getDOF().getPoint()){
                    lambdaDOF =super.getLocalCoordinate()[i].getValue(p);
                    grad_lambdaDOF=super.getLocalCoordinate()[i].getGradient(p).getCoords();
                }
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_i.getPoint()){
                    lambdaDOF_i=super.getLocalCoordinate()[i].getValue(p);
                    grad_lambdaDOF_i=super.getLocalCoordinate()[i].getGradient(p).getCoords();
                }
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_j.getPoint()){
                    lambdaDOF_j=super.getLocalCoordinate()[i].getValue(p);
                    grad_lambdaDOF_j=super.getLocalCoordinate()[i].getGradient(p).getCoords();
                }
                i++;
            }
            grad_Result[0]=2*lambdaDOF*grad_lambdaDOF[0]-(lambdaDOF*grad_lambdaDOF_i[0]+grad_lambdaDOF[0]*lambdaDOF_i)-(lambdaDOF*grad_lambdaDOF_j[0]+grad_lambdaDOF[0]*lambdaDOF_j);
            grad_Result[1]=2*lambdaDOF*grad_lambdaDOF[1]-(lambdaDOF*grad_lambdaDOF_i[1]+grad_lambdaDOF[1]*lambdaDOF_i)-(lambdaDOF*grad_lambdaDOF_j[1]+grad_lambdaDOF[1]*lambdaDOF_j);
            return new VectorNd(grad_Result);
        }
        if (referPoint == false){
            while (i<super.getLocalCoordinate().length) {
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_i.getPoint()){
                    lambdaDOF_i=super.getLocalCoordinate()[i].getValue(p);
                    grad_lambdaDOF_i=super.getLocalCoordinate()[i].getGradient(p).getCoords();
                }
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_j.getPoint()){
                    lambdaDOF_j=super.getLocalCoordinate()[i].getValue(p);
                    grad_lambdaDOF_j=super.getLocalCoordinate()[i].getGradient(p).getCoords();
                }
                i++;
            }
            grad_Result[0]=4.*(grad_lambdaDOF_i[0]*lambdaDOF_j+lambdaDOF_i*grad_lambdaDOF_j[0]);
            grad_Result[1]=4.*(grad_lambdaDOF_i[1]*lambdaDOF_j+lambdaDOF_i*grad_lambdaDOF_j[1]);
            return new VectorNd(grad_Result);
        }
        return new VectorNd(new double[] {Double.NaN, Double.NaN});
    }
}
