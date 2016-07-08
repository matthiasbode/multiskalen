package bijava.math.pde.fdm;

public class FDGrid3d implements FDGrid{
    FDDOF[][][] x;
    private int isize, jsize, ksize;
    private double xmin, xmax,ymin,ymax, zmin,zmax;
    
    public FDGrid3d(int i,int j,int k, double xmin, double xmax, double ymin, double ymax, double zmin, double zmax){
        this.isize = i;
        this.jsize = j;
        this.ksize = k;
        this.xmin=xmin;
        this.xmax=xmax;
        this.ymin=ymin;
        this.ymax=ymax;
        this.zmin=zmin;
        this.zmax=zmax;
        x = new FDDOF[i][j][k];
    }
    
    public FDGrid3d(int[] n, double[] min, double[] max){
        this.isize = n[0];
        this.jsize = n[1];
        this.ksize = n[2];
        this.xmin=min[0];
        this.xmax=max[0];
        this.ymin=min[1];
        this.ymax=max[1];
        this.zmin=min[2];
        this.zmax=max[2];
        x = new FDDOF[isize][jsize][ksize];
    }
    
    public int[] getSize(){
        return new int[]{isize,jsize,ksize};
    }
    
    public double[] getLocation(int i, int j, int k){
        return new double[]{xmin + i*(xmax-xmin)/(isize-1),ymin + j*(ymax-ymin)/(jsize-1),zmin + k*(zmax-zmin)/(ksize-1)};
    }
    
    public double[] getLocation(int[] i){
        return new double[]{xmin + i[0]*(xmax-xmin)/(isize-1),ymin + i[1]*(ymax-ymin)/(jsize-1),zmin + i[2]*(zmax-zmin)/(ksize-1)};
    }
    
    public void setDOF(int i, int j, int k, FDDOF d){
        x[i][j][k]=d;
    }
    
    public void setDOF(int[] n, FDDOF d){
        x[n[0]][n[1]][n[2]]=d;
    }
    
    public FDDOF getDOF(int[] n){
        return x[n[0]][n[1]][n[2]];
    }
    
    
}