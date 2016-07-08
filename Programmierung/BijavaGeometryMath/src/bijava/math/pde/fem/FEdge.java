package bijava.math.pde.fem;

/**
 * 
 * @author milbradt
 * @deprecated umstellen auf die Nutzung des allgemeinen FElement im makroskopischen Verkehrsmodell - insbesonder die koeffizientenmatrix 
 */
public class FEdge extends FElement {

    private double distance=0.;    
    private double koeffmat[][] = new double[2][2];
        
    
    //KONSTRUKTOREN
    //--------------------------------------------------------------------------
    /**
     * Creates a new instance of FEdge
     */           
    public FEdge(FEDOF b, FEDOF e) {
        super (new FEDOF[]{b,e});        
        
        distance = b.getPoint().distance(e.getPoint());        
        koeffmat[0][1] = - 1. / distance;
        koeffmat[1][1] = 1. / distance;
    }
    
    public double elm_size() {
        return distance;
    }

    public double[][] getkoeffmat() {
        double mat[][] = new double[2][2];
        for (int i=0;i<2;i++)
            for (int j=0;j<2;j++)
                mat[i][j]=koeffmat[i][j];
        return mat;
    }  
}