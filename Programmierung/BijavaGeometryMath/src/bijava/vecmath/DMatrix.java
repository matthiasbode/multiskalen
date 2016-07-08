package bijava.vecmath;

import bijava.geometry.LinearPoint;

/**
 * Double Matrix Class
 * @author P. Milbradt
 */
//------------------------------------------------------------------------------
//  Deklaration der Klasse DVector
//------------------------------------------------------------------------------

public class DMatrix implements LinearPoint {
    // ---------------------- private variables ------------------
    private   int rows, cols; // Zeilen und Splalten
    
    private  double contents[][]; // matrix [rows][cols]
    
    // -------------------  Constructors ------------------------
    /** default constructor initializes matrix to zero dimensions */
    public DMatrix(){
        rows = 0;
        cols = 0;
    }
    
    /** initializes matrix to height 'row' and width 'col'. initializes data to 0 */
    public DMatrix(int row,int col){
        if (row >= 0 && col>= 0) {
            rows = row;
            cols = col;
            contents = new double[rows][cols];
            
            for (int i = 0; i< rows; i++) {
                for (int j=0; j<cols; j++)
                    contents[i][j] = 0; // initialize to zero.
            }
        } else {
            System.out.println("Matrix hat negative Dimension!");
        }
    }
    
    //------------------------------------------------------------------------------
    /** Kopierkonstruktor  */
    //------------------------------------------------------------------------------
    public DMatrix( DMatrix m){
        rows = m.rows;
        cols = m.cols;
        contents = new double[rows][cols];
        
        for (int i = 0; i< rows; i++) {
            for (int j=0; j<cols; j++)
                contents[i][j] = m.contents[i][j];
        }
    }
    
    /** convert a 2-dim.Array to a DMatrix */
    public DMatrix(double[][] mat) {
        rows = mat.length;
        cols = mat[0].length;
        contents = mat;
    }
    
    // ------------  Public methods: ------------------------
    
    // --- data methods: ---
    
    /** returns the item at (i,j) */
    public double getItem(int i, int j)  throws  ArrayIndexOutOfBoundsException {
        if ((i<rows) && (j < cols) && (i >= 0) && (j >= 0))
            return contents [i][j];
        else
            return 0;
    }
    
    /** sets the item at (i, j) */
    public void setItem(int i, int j, double val) {
        if ((i >= rows) || (j >= cols)) {
            int newrow = Math.max(rows, i+1);
            int newcol = Math.max(cols, j+1);
            
            DMatrix aux = new DMatrix(newrow, newcol);
            for (int ii=0; ii< rows; ii++)
                for (int jj=0; jj < cols; jj++)
                    aux.contents[ii][jj] = contents [ii][jj];
            contents = aux.contents;
            rows = newrow;
            cols = newcol;
        }
        contents[i][j] = val;
    }
    
    //** returns number of rows */
    /**
     * @return  */
    public int getRows() {
        return rows;
    }
    
    //** returns number of columns */
    /**
     * @return  */
    public int getCols() {
        return cols;
    }
    
    
    //-----Operations --------
    
    /** Adds a  matrices */
    public DMatrix add(DMatrix m) {
        if (!(m.rows == rows || m.cols == cols)) {
            return m;
        }
        
        DMatrix result = new DMatrix(rows,m.cols);
        for (int col=0;col<cols;col++)
            for (int row=0;row<rows;row++)
                result.contents[row][col] = contents[row][col] + m.contents[row][col];
        return result;
    }
    
    /** subtract a matrix */
    public DMatrix sub(DMatrix m){
        if (!(m.rows == rows || m.cols == cols)) {
            return m;
        }
        
        DMatrix result = new DMatrix(rows,m.cols);
        for (int col=0;col<cols;col++)
            for (int row=0;row<rows;row++)
                result.contents[row][col] = contents[row][col] - m.contents[row][col];
        return result;
    }
    
    
    
    /** multiplies with a scalar (double)
     * @param num
     * @return
     */
    public DMatrix mult(double num){
        DMatrix result = new DMatrix(rows,cols);
        for (int col=0;col<cols;col++)
            for (int row=0;row<rows;row++)
                result.contents[row][col] =  contents[row][col] * num;
        return result;
    }
    
    
    /**
     */
    public DMatrix mult(DMatrix m) throws ArithmeticException {
        if (cols != m.rows) {
            return m;
        }
        
        DMatrix result = new DMatrix(rows,m.cols);
        for (int row=0;row<result.rows;row++) {
            for (int col=0;col<result.cols;col++) {
                for (int n=0;n<cols;n++) {
                    result.contents[row][col] += contents[row][n] * m.contents[n][col];
                }
            }
        }
        return result;
    }
    
    
    /** Transponierte der Matrix
     * @return Transponierte der Matrix
     */
    public DMatrix trn() {
        DMatrix result = new DMatrix(cols,rows);
        
        for (int col=0;col<cols;col++) {
            for (int row=0;row<rows;row++) {
                result.contents[col][row] = contents[row][col];
            }
        }
        return result;
    }
    
    
    /**
     * @return  */
    public double det() {
        
        int[] indx = new int[rows];
        Flip parity = new Flip(true);
        if (rows != cols) System.out.println( "DMatrix.det(): matrix not quadratic.");
        
        //Place for LU decomposition
        DMatrix lud = new DMatrix(rows,rows);
        
        //Swap matrix for its LU decomposition
        try{
            lud = this.luDecompose(indx, parity);
        }
        catch ( ArithmeticException ae ) {
            return 0.;
        }
        
        
        double Det = 1;
        for(int i=0;i<rows;i++) Det *= lud.getItem(i,i);
        
        if ( !parity.getValue() ) Det = -Det;
        
        return Det;
    }
    
    
    /**
     * luDecomposition performs LU Decomposition on a matrix.
     * must be given an array to mark the row permutations and a flag
     * to mark whether the number of permutations was even or odd.
     */
    
    public DMatrix luDecompose( int indx[], Flip flip) throws  ArithmeticException {
        
        boolean parity = flip.getValue();
        
        double[][] tmatrix = new double[rows][cols];
        
        for (int i = 0; i< rows; i++) {
            for (int j=0; j<cols; j++)
                tmatrix[i][j] = contents[i][j];
        }
        
        
        //imax is position of largest element in the row. i,j,k, are counters
        int i,j,k,imax = 0;
        
        // amax is value of largest  element in the row.
        //dum is a temporary variable.
        double amax, dum = 0;
        
        //scaling factor for each row is stored here
        double scaling[] = new double [rows];
        
        // a small number != zero
        double tiny = 1.0E-20;
        
        // Is the number of pivots even?
        parity = true;
        
        //Loop over rows to get the scaling information
        //The largest element in the row is the inverse of the scaling factor.
        for (i = 0; i < rows; i++) {
            amax = tmatrix[i][0];
            for (j = 0; j < rows; j++) {
                amax = Math.max(amax, Math.abs(tmatrix[i][j]));
            }
            
            if ( amax <= tiny )
                throw new ArithmeticException("Singular Matrix");
            
            //Save the scaling
            scaling[i] = 1.0/amax;
        }
        
        //Loop over columns using Crout's Method.
        for (j = 0; j < rows; j++) {
            
            //lower left corner
            for (i = 0; i < j; i++) {
                
                dum = tmatrix[i][j];
                
                for (k = 0; k < i; k++) {
                    dum -= tmatrix[i][k] * tmatrix[k][j];
                }
                tmatrix[i][j] = dum;
            }
            
            //Initialize search for largest element
            amax = 0.0;
            
            //upper right corner
            for (i = j; i < rows; i++) {
                dum = tmatrix[i][j];
                for (k = 0; k < j; k++) {
                    dum -= tmatrix[i][k] * tmatrix[k][j];
                }
                tmatrix[i][j] = dum;
                if (scaling[i] * Math.abs(dum) > amax) {
                    amax = scaling[i]* Math.abs(dum);
                    imax = i;
                }
            }
            
            if ( amax <= tiny )
                throw new ArithmeticException("Singular Matrix");
            
            
            //Change rows if it is necessary
            if ( j != imax){
                for (k = 0; k < rows; k++) {
                    dum = tmatrix[imax][k];
                    tmatrix[imax][k] = tmatrix[j][k];
                    tmatrix[j][k] = dum;
                }
                //Change parity
                parity = !parity;
                scaling[imax] = scaling[j];
            }
            //Mark the column with the pivot row.
            indx[j] = imax;
            
            //replace zeroes on the diagonal with a small number.
            if (tmatrix[j][j] == 0.0) {
                tmatrix[j][j] = tiny;
            }
            //Divide by the pivot element
            if (j != rows) {
                dum = 1.0/tmatrix[j][j];
                for (i=j+1; i < rows; i++) {
                    tmatrix[i][j] *= dum;
                }
            }
            
        }
        
        flip.setValue(parity);
        return new DMatrix(tmatrix);
    }
    
    /**
     * Do the backsubstitution on matrix a which is the LU decomposition
     * of the original matrix. b is the right hand side vector which is NX1. b
     * is replaced by the solution. indx is the array that marks the row
     * permutations.
     */
    public double[] luBackSubstitute( double[] b,   int indx[]) {
        //counters
        int i, ip, j, ii = -1;
        double sum = 0;
        
        for (i = 0; i < rows; i++) {
            
            ip = indx[i];
            sum = b[ip];
            
            b[ip] = b[i];
            if (ii != -1) {
                for (j = ii; j < i; j++){
                    sum -= contents[i][j] * b[j];
                }
            }
            else {
                if ( sum != 0) {
                    ii = i;
                }
            }
            b[i] = sum;
            
        }
        for (i=rows-1; i >= 0; i--) {
            sum = b[i];
            for (j = i+1; j < rows; j++) {
                sum -= contents[i][j] * b[j];
            }
            b[i] = sum / contents[i][i];
        }
        return b;
    }
    
    
    /**
     * Invert a matrix.
     */
    public DMatrix invert() throws  ArithmeticException {
        
        Flip parity = new Flip(true);
        
        //temporary storage
        double col[] = new double [rows] ;
        double vecresult[] = new double [rows] ;
        
        //An array holding the permutations used by LU decomposition
        int indx[] = new int [rows];
        
        //Place for result
        double result[][] = new double[rows][rows];
        
        //Place for LU decomposition
        DMatrix lud = new DMatrix(rows,rows);
        
        //Swap matrix for its LU decomposition
        try {
            lud = this.luDecompose(indx, parity);
        } catch ( ArithmeticException ae ) {
            throw new ArithmeticException("Singular Matrix");
        }
        
        
        //Do backsubstitution with the b matrix being all zeros except for
        //a 1 in the row that matches the column we're in.
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < rows; i++) {
                col[i] = 0;
            }
            col[j] = 1;
            vecresult = lud.luBackSubstitute( col, indx);
            
            //plug values into result
            for (int i = 0; i < rows; i++) {
                result[i][j] = vecresult[i];
            }
        }
        return new DMatrix(result);
    }
    
    
    /**
     * Solve a set of linear equations. a is a square matrix of coefficients.
     * b is the right hand side. b is replaced by solution.
     * Target is replaced by its LU decomposition.
     */
    public double [] solve( double b[]) throws  ArithmeticException {
        
        double[] result = new double[rows];
        
        Flip parity = new Flip(true);
        int indx[] = new int [rows];
        
        DMatrix lud = this.luDecompose( indx, parity);
        
        try {
            result = lud.luBackSubstitute( b, indx);
        } catch ( ArithmeticException ae ) {
            throw new ArithmeticException("Singular Matrix");
        }
        
        return result;
    }
    
    
    /** converts the matrix to string - each row (seperated using
       a newline character) represents one row in the matrix. */
    public String toString() {
        String ret="";
        
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols-1; j++)
                ret = ret + contents[i][j] + "   ";
            ret = ret + contents [i][cols-1];
            ret = ret + "\n";
        }
        return ret;
    }  

    public LinearPoint add(LinearPoint point) {
        if(point instanceof DMatrix) return add((DMatrix) point);
        return null;
    }

    public LinearPoint sub(LinearPoint point) {
        if(point instanceof DMatrix) return sub((DMatrix) point);
        return null;
    }
}
