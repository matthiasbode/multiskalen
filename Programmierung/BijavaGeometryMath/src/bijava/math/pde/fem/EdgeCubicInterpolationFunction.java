package bijava.math.pde.fem;

import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;
import bijava.math.function.DifferentialScalarFunctionNd;


/**
 * Die Klasse LinearQuadraticTrialFunction stellt Funktionen fuer Linear-Quadratische Ansatzfunktionen zur Verfuegung.
 *
 * @author  Institut fuer Bauinformatik
 */

public class EdgeCubicInterpolationFunction extends FEBasisFunction {
    private FEDOF DOF_i;
    private FEDOF DOF_j;
    private boolean referPoint;
    
    /** Konstruktor einer Linear-Quadratischen Ansatzfunktion
     * @param l: Lokale Koordinaten
     * @param d: Freiheitsgrad der Ansatzfunktion 
     * @param d_i: 1. Freiheitsgrad der Kante, auf dem die Ansatzfunktion gebildet werden soll
     * @param d_j: 2. Freiheitsgrad der Kante, auf dem die Ansatzfunktion gebildet werden soll
     * @param rp: true, wenn Freiheitsgrad auch Referenzpunktist; false, wenn Freiheitsgrad kein Referenzpunkt ist */
    public EdgeCubicInterpolationFunction(DifferentialScalarFunctionNd[] l, FEDOF d, FEDOF d_i, FEDOF d_j, boolean rp){
		super.setLocalCoordinate(l);
        super.setDOF(d);
        super.setOrder(3);
        this.DOF_i=d_i;
        this.DOF_j=d_j;
        this.referPoint=rp;
    }
    
    /** Wert der Ansatzfunktion ueber dem FE-Element am Punkt p
     * @param p Punkt, an dem der Wert der Ansatzfunktion berechnet werden soll
     * @return Wert der Ansatzfunktion am Punkt p  
     */
    public double getValue(PointNd p){
        double lambdadof=Double.NaN, lambdaDOF_i=Double.NaN, lambdaDOF_j=Double.NaN;
        int i=0;

        if (referPoint == true){
            while (i<super.getLocalCoordinate().length) {
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==super.getDOF().getPoint())  lambdadof =super.getLocalCoordinate()[i].getValue(p);
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_i.getPoint()) lambdaDOF_i=super.getLocalCoordinate()[i].getValue(p);
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_j.getPoint()) lambdaDOF_j=super.getLocalCoordinate()[i].getValue(p);
                i++;
            }
            double phi_1=9.*lambdadof*lambdadof*lambdaDOF_i-9./2.*lambdadof*lambdaDOF_i*lambdaDOF_i;
            double phi_2=9.*lambdaDOF_i*lambdaDOF_i*lambdadof-9./2.*lambdaDOF_i*lambdadof*lambdadof;
            double phi_3=9.*lambdadof*lambdadof*lambdaDOF_j-9./2.*lambdadof*lambdaDOF_j*lambdaDOF_j;
            double phi_4=9.*lambdaDOF_j*lambdaDOF_j*lambdadof-9./2.*lambdaDOF_j*lambdadof*lambdadof;
            return lambdadof-2./3.*phi_1-1./3.*phi_2-2./3.*phi_3-1./3.*phi_4; 
        }
        if (referPoint == false){
            while (i<super.getLocalCoordinate().length) {
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_i.getPoint()) lambdaDOF_i=super.getLocalCoordinate()[i].getValue(p);
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==DOF_j.getPoint()) lambdaDOF_j=super.getLocalCoordinate()[i].getValue(p);
                i++;
            }
            return (9.*lambdaDOF_i*lambdaDOF_i*lambdaDOF_j-9./2.*lambdaDOF_i*lambdaDOF_j*lambdaDOF_j);
        }
        return Double.NaN;
    }
    
    
    /** Ableitung der Ansatzfunktion ueber dem FE-Element am Punkt p
     * @param p Punkt, an dem die Ableitung berechnet werden soll
     * @return Feld der Ableitungsvektoren  */
    public VectorNd getGradient(PointNd p) {
        
        double lambdadof=Double.NaN;
        double lambdaDOF_i=Double.NaN;
        double lambdaDOF_j=Double.NaN;
        double[] grad_lambdadof=new double[2];
        double[] grad_lambdaDOF_i=new double[2];
        double[] grad_lambdaDOF_j=new double[2];
        VectorNd grad_Result=new VectorNd(2);
        
        int i=0;

        if (referPoint == true){
            while (i<super.getLocalCoordinate().length) {
                if (((NaturalElementCoordinateFunction) super.getLocalCoordinate()[i]).getCoordinate()==super.getDOF().getPoint()){
                    lambdadof =super.getLocalCoordinate()[i].getValue(p);
                    grad_lambdadof=super.getLocalCoordinate()[i].getGradient(p).getCoords();
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
            double[] grad_phi_1=new double[2];
            double[] grad_phi_2=new double[2];
            double[] grad_phi_3=new double[2];
            double[] grad_phi_4=new double[2];
            grad_phi_1[0]=9.*(2.*grad_lambdadof[0])*lambdaDOF_i+9.*lambdadof*lambdadof*grad_lambdaDOF_i[0]-9./2.*grad_lambdadof[0]*lambdaDOF_i-9./2.*lambdadof*(2.*grad_lambdaDOF_i[0]);
            grad_phi_1[1]=9.*(2.*grad_lambdadof[1])*lambdaDOF_i+9.*lambdadof*lambdadof*grad_lambdaDOF_i[1]-9./2.*grad_lambdadof[1]*lambdaDOF_i-9./2.*lambdadof*(2.*grad_lambdaDOF_i[1]);
            grad_phi_2[0]=9.*(2.*grad_lambdaDOF_i[0])*lambdadof+9.*lambdaDOF_i*lambdaDOF_i*grad_lambdadof[0]-9./2.*grad_lambdaDOF_i[0]*lambdadof-9./2.*lambdaDOF_i*(2.*grad_lambdadof[0]);
            grad_phi_2[1]=9.*(2.*grad_lambdaDOF_i[1])*lambdadof+9.*lambdaDOF_i*lambdaDOF_i*grad_lambdadof[1]-9./2.*grad_lambdaDOF_i[1]*lambdadof-9./2.*lambdaDOF_i*(2.*grad_lambdadof[1]);
            grad_phi_3[0]=9.*(2.*grad_lambdadof[0])*lambdaDOF_j+9.*lambdadof*lambdadof*grad_lambdaDOF_j[0]-9./2.*grad_lambdadof[0]*lambdaDOF_j-9./2.*lambdadof*(2.*grad_lambdaDOF_j[0]);
            grad_phi_3[1]=9.*(2.*grad_lambdadof[1])*lambdaDOF_j+9.*lambdadof*lambdadof*grad_lambdaDOF_j[1]-9./2.*grad_lambdadof[1]*lambdaDOF_j-9./2.*lambdadof*(2.*grad_lambdaDOF_j[1]);
            grad_phi_4[0]=9.*(2.*grad_lambdaDOF_j[0])*lambdadof+9.*lambdaDOF_j*lambdaDOF_j*grad_lambdadof[0]-9./2.*grad_lambdaDOF_j[0]*lambdadof-9./2.*lambdaDOF_j*(2.*grad_lambdadof[0]);
            grad_phi_4[1]=9.*(2.*grad_lambdaDOF_j[1])*lambdadof+9.*lambdaDOF_j*lambdaDOF_j*grad_lambdadof[1]-9./2.*grad_lambdaDOF_j[1]*lambdadof-9./2.*lambdaDOF_j*(2.*grad_lambdadof[1]);
            grad_Result.setCoord(0, lambdadof-2./3.*grad_phi_1[0]-1./3.*grad_phi_2[0]-2./3.*grad_phi_3[0]-1./3.*grad_phi_4[0]);
            grad_Result.setCoord(1, lambdadof-2./3.*grad_phi_1[1]-1./3.*grad_phi_2[1]-2./3.*grad_phi_3[1]-1./3.*grad_phi_4[1]);
            return grad_Result;
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
            grad_Result.setCoord(0, 9.*(2.*grad_lambdaDOF_i[0])*lambdaDOF_j+9.*lambdaDOF_i*lambdaDOF_i*grad_lambdaDOF_j[0]-9./2.*grad_lambdaDOF_i[0]*lambdaDOF_j-9./2.*lambdaDOF_i*(2.*grad_lambdaDOF_j[0]));
            grad_Result.setCoord(1, 9.*(2.*grad_lambdaDOF_i[1])*lambdaDOF_j+9.*lambdaDOF_i*lambdaDOF_i*grad_lambdaDOF_j[1]-9./2.*grad_lambdaDOF_i[1]*lambdaDOF_j-9./2.*lambdaDOF_i*(2.*grad_lambdaDOF_j[1]));
            return grad_Result;
        }
        return null;
    }
}
