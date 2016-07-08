/*
 * VectorNd.java
 *
 * Created on 28. Maerz 2003, 11:02
 */

package bijava.geometry.dimN;

import javax.vecmath.*;

/**
 *
 * @author  JochenSchierbaum
 */
public class MatrixNd extends GMatrix {
    
    /** Creates a new instance of MatrixNd */
    public MatrixNd(int i, int j) {
        super(i,j);
    }
    
    
    public MatrixNd(int i, int j, double[] d){
        super(i,j,d);
    }
    
    public MatrixNd(MatrixNd matrixnd){
        super(matrixnd);
    }
    
}
