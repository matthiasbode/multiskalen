package bijava.vecmath;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
/**
 *
 * MatrixUtil.java
 * Description: is used to do matrix computations
 * Copyright: Copyright Byrge Birkeland (c) 2003
 * @author Agder University College
 * @author Byrge Birkeland
 * @version 1.0
 */
public class MatrixUtil {
    
    /** Creates a new instance of MatrixUtil */
    private MatrixUtil() {
    }
    
    public static GMatrix diag(double[] Vec) {
        int n=Vec.length;
        double[] V= new double[n*n];
        for (int i=0; i<n; i++) V[n*i+i]=Vec[i];
        return new GMatrix(n,n,V); }
    
    /**
     * augments two matrices, i.e. puts matrix B after matrix A
     * @param A the first matrix
     * @param B the second matrix
     * @return the augmented matrix (A|B)
     */
    
    public static GMatrix augment(GMatrix A, GMatrix B) {
        int RA=A.getNumRow(), CA=A.getNumCol(),
                RB=B.getNumRow(), CB=B.getNumCol(),
                R=RA<RB?RA:RB;
        GMatrix C=new GMatrix(R,CA+CB);
        for(int i=0; i<R; i++) {
            for (int j = 0; j < CA; j++)
                C.setElement(i, j, A.getElement(i, j));
            for (int j = CA; j < CA + CB; j++)
                C.setElement(i, j, B.getElement(i, j - CA));
        }
        return C; }
    
    /**
     * extends the matrix A  by adding a vector B
     * @param A the matrix to be extended
     * @param B the vector to add to A
     * @return the extended amtrix
     */
    
    public static GMatrix augment(GMatrix A, GVector V) {
        int R=A.getNumRow(),C=A.getNumCol();
        GMatrix B = new GMatrix(R,C+1);
        for(int i=0; i<R; i++){
            for(int j=0; j<C; j++) B.setElement(i,j,A.getElement(i,j));
            B.setElement(i,C,i<V.getSize()?V.getElement(i):0);}
        return B; }
    
    /**
     * converts the matrix this to a double[][]
     * @return the resulting double[][] array
     */
    
    public static double[][]  to2DArray(GMatrix A) {
        int R=A.getNumRow(),C=A.getNumCol();
        double[][] ans=new double[R][];
        for (int r=0; r<R; r++){
            ans[r]=new double[C];
            for (int c=0; c<C;c++)
                ans[r][c]=A.getElement(r,c); }
        return ans;
    } //Slutt to2DArray()
    
    public static GMatrix toGMatrix(double[][] arr) {
        int m= arr.length;
        int L= arr[0].length;
        for(int i=0; i<arr.length; i++)
            if(arr[i].length>L) L=arr[i].length;
        GMatrix M = new GMatrix(m,L);
        for(int i=0; i<arr.length; i++)
            for(int j=0; j<L; j++)
                M.setElement(i,j,arr[i][j]);
        return M;
    }
    
    
/////////////////////////////////////
// Elementary row operations
/////////////////////////////////////
    
    /** interchanges two rows in the matrix this
     * @param r1 the index of the first row
     * @param r2 the index of the second row */
    
    public static GMatrix rowInterchange(GMatrix M, int r1, int r2) {
        double temp=0.0;
        GMatrix MM=new GMatrix(M);
        int C = MM.getNumCol();
        GVector R1 = new GVector(C); MM.getRow(r1,R1);
        GVector R2 = new GVector(C); MM.getRow(r2,R2);
        MM.setRow(r1,R2); MM.setRow(r2,R1);
        return MM;}
    
    /** multiplies the r-th row of this by a given number
     * @param r the index of the row to multiply
     * @param a the factor to multiply with
     */
    
    public static GMatrix rowMultiply(GMatrix M, int r, double a) {
        GMatrix MM=new GMatrix(M);
        int C=M.getNumCol();
        GVector Rr=new GVector(C); M.getRow(r,Rr); Rr.scale(a);
        MM.setRow(r,Rr);
        return MM; }
    
    /**
     * add t times row r2 to row r1
     * @param r1 the index of the row that is to be changed
     * @param r2 the index of the row a multiple of which is to be added to row r1
     * @param t the factor to multiply row r2 with
     */
    
    public static GMatrix rowPlus(GMatrix M, int r1, int r2, double t) {
        int C=M.getNumCol();
        GVector R1 = new GVector(C); M.getRow(r1,R1);
        GVector R2 = new GVector(C); M.getRow(r2,R2);
        GVector R3 = new GVector(C); R3.scaleAdd(t,R2,R1);
        GMatrix MM= new GMatrix(M);
        MM.setRow(r1,R3);
        return MM; }
    
///////////////////////////////////////////////////////////////
// Solving linear systems by Gauss elimination //
///////////////////////////////////////////////////////////////
    
    /**
     * performs the forward elimination process of the naiv Gauss elimination
     * of the matrix this
     */
    
    public static GMatrix forwardElim(GMatrix A) {
        GMatrix B= new GMatrix(A);
        int R=A.getNumRow();
        for (int r=0;r<R-1;r++)
            for (int i=r+1;i<R;i++)
                B=rowPlus(B,i,r,-B.getElement(i,r)/B.getElement(r,r));
        return B; }
    
    /**
     * performs the backwards substitution process in the naiv Gauss elimination process
     * for the upper triagular matrix argument
     * @param C the upper triangular matrix
     * @return the solution resulting from the process
     */
    
    public static GVector backSubst(GMatrix C) {
        int n =  C.getNumRow();
        GVector x = new GVector(n);
        x.setElement(n-1,C.getElement(n-1,n)/C.getElement(n-1,n-1));
        for (int i=n-1;i>=0;i--) {
            double sum=C.getElement(i,n);
            for(int j=i+1;j<n;j++) sum-=C.getElement(i,j)*x.getElement(j);
            x.setElement(i,sum/C.getElement(i,i));}
        return x; }
    
    /**
     * solves a linear system A X=B by naiv Gauss elimination
     * @param A the coefficient matrix
     * @param B the vector of right-hand sides
     * @return the vector of solutions to the system
     */
    
    public static GVector naivGauss(GMatrix A, GVector B) {
        GMatrix C = forwardElim(augment(A,B));
        GVector x=backSubst(C);
        return x; }
    
    public static GVector naivGauss(GMatrix A) {
        GMatrix C = forwardElim(A);
        GVector x=backSubst(C);
        return x; }
    
    /**
     * normalises a row in a coefficient matrix
     * @param i the number of the row to be normalised
     * @param n the number of unknown in the system
     */
    
    public static GMatrix normalise(GMatrix A, int i,  int n) {
        double Bij=A.getElement(i,0), MM=Bij, Max=Math.abs(MM);
        GMatrix B= new GMatrix(A);
        for (int j=0;j<n;j++) {
            Bij=A.getElement(i,j);
            double M=Math.abs(Bij);
            if(M>Max) {Max=M; MM=Bij;}}
        if (Max>0) {
            for(int j=0;j<A.getNumCol();j++)
                B.setElement(i,j,A.getElement(i,j)/MM);}
        return B;}
    
    /**
     * sorts the rows in the matrix this in a the way that of all the rows with index m
     * or higher, the m-th row is the one that has the element with greatest absolute
     * value in the k-th column
     * @param k the index of the column to find the biggest absolute value in
     * @param m the index of the first row to include in the comparison operation
     */
    
    public static GMatrix columnOrder(GMatrix A, int k, int m) {
        GMatrix B = new GMatrix(A);
        for(int i=m+1;i<B.getNumRow();i++)
            if(Math.abs(B.getElement(i,k))>Math.abs(B.getElement(m,k)))
                B=rowInterchange(B,i,m);
        return B;}
    
    /**
     * solves the linear equation system with n unknowns and with extended coefficient matrix
     * this using Gauss elimination with pivoting
     * @param n the number of unknown
     */
    
    public static GMatrix columnPivot(GMatrix A, int n) {
        GMatrix B = new GMatrix(A);
        int M=B.getNumRow();
        for (int m=0;m<M;m++) {
            for (int i=m;i<M;i++) B=normalise(B,i,n);
            B=columnOrder(B,m,m);
            if (B.getElement(m,m)!=0.0) B=rowMultiply(B,m,1.0/B.getElement(m,m));
            for (int i=0;i<M;i++) if(i!=m) B=rowPlus(B,i,m,-B.getElement(i,m));
        }
        if (B.getElement(M-1,n-1)!=0) {
            B=rowMultiply(B,M-1,1.0/B.getElement(M-1,n-1));
            for (int i=0; i<M-1;i++) B=rowPlus(B,i,M-1,-B.getElement(i,n-1));
        }
        return B;
    }
    
    /**
     * finds the matrix obtained by cutting the R-th row and the C-th column in
     * the matrix this
     * @param R the index of the row to be cut
     * @param C the index of the column to be deleted
     * @return the matrix resulting by deleting row R and column C
     */
    
    public static GMatrix complement(GMatrix A, int R, int C) {
        int K=A.getNumCol();
        GMatrix B=new GMatrix(K-1,K-1);
        for (int r=0;r<K-1;r++)
            for (int c=0;c<K-1;c++) {
            int rr=r, cc=c;
            if(r>=R) rr=r+1;
            if(c>=C) cc=c+1;
            B.setElement(r,c,A.getElement(rr,cc)); }
        return B; }
    
    /**
     * finds the determinant of the matrix this
     * @return the determinant of the matrix this
     */
    
    public static double determinant(GMatrix A) {
        if(A.getNumCol()>2) {
            double sum=0.0;
            for (int i=0;i<A.getNumCol();i++) {
                double sgn = (i%2==0) ? 1.0 : -1.0;
                sum+=sgn*A.getElement(i,0)*determinant(complement(A,i,0)); }
            return sum; } else
                return A.getElement(0,0)*A.getElement(1,1)-A.getElement(1,0)*A.getElement(0,1);
    }
    
    /**
     * solves a triangular system of linear equations
     * @param a is the diagoanl below the main diagonal
     * @param c is the main diagonal
     * @param d is the diagonal above the main diagonal
     * @param b is the vector of right-hand sides
     * @return the solution of the triangular system
     */
    
    public static double[] tri(double[] a, double[] c, double[] d, double[] b) {
        int n=b.length;
        double[] x = new double[n];
        double xmult;
        try {
            for(int i=1;i<n;i++) {
                xmult = a[i-1]/d[i-1];
                d[i]=d[i]-xmult*c[i-1];
                b[i]=b[i]-xmult*b[i-1]; }
            x[n-1]=b[n-1]/d[n-1];
            for (int i=n-2; i>=0; i--)
                x[i]=(b[i]-c[i]*x[i+1])/d[i];} catch(IndexOutOfBoundsException e) {
                    System.out.println("Vektorene b og d skal ha lengde n, a og c skal ha lengde n-1.");}
        return x; }
    
    /**
     * solves a pentadiagonal system of linear equations
     * @param e the diagonal two below the main diagonal
     * @param a the diagonal just below the main diagonal
     * @param d the main diagonal
     * @param c the diagonal just above the main diagonal
     * @param f the diagonal two above the main diagonal
     * @param b the vector of right-hand sides
     * @return the solution vector
     */
    
    public static double[] penta(double[] e, double[] a, double[] d, double[] c,
            double[] f, double[] b) {
        int n=b.length;
        double[] x=new double[n];
        double xmult;
        try {
            for(int i=1;i<n-1;i++) {
                xmult=a[i-1]/d[i-1];
                d[i]=d[i]-xmult*c[i-1];
                c[i]=c[i]-xmult*f[i-1];
                b[i]=b[i]-xmult*b[i-1];
                xmult=e[i-1]/d[i-1];
                a[i]=a[i]-xmult*c[i-1];
                d[i+1]=d[i+1]-xmult*f[i-1];
                b[i+1]=b[i+1]-xmult*b[i-1]; }
            xmult=a[n-2]/d[n-2];
            d[n-1]=d[n-1]-xmult*c[n-2];
            x[n-1]=(b[n-1]-xmult*b[n-2])/d[n-1];
            x[n-2]=(b[n-2]-c[n-2]*x[n-1])/d[n-2];
            for(int i=n-3;i>=0;i--)
                x[i]=(b[i]-f[i]*x[i+2]-c[i]*x[i+1])/d[i];
        } catch(IndexOutOfBoundsException ex) {
            System.out.println("d og b har lengde n, c og a har lengde n-1,"+
                    "og f og e har lengde n-2.");}
        return x; }
    
}
