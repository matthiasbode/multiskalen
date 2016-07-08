package bijava.math.pde.fdm;

public class FDGrid2d implements FDGrid{
    private FDDOF[][] x;
    private int isize, jsize;
    private double xmin, xmax,ymin,ymax;
    
    public FDGrid2d(int i,int j, double xmin, double xmax, double ymin, double ymax){
        this.isize = i;
        this.jsize = j;
        this.xmin=xmin;
        this.xmax=xmax;
        this.ymin=ymin;
        this.ymax=ymax;
        x = new FDDOF[i][j];
    }
    
    public int[] getSize(){
        return new int[]{isize,jsize};
    }
    
    public double[] getLocation(int i, int j){
        return new double[]{xmin + i*(xmax-xmin)/(isize-1),ymin + j*(ymax-ymin)/(jsize-1)};
    }
    
    public double[] getLocation(int[] i){
        return new double[]{xmin + i[0]*(xmax-xmin)/(isize-1),ymin + i[1]*(ymax-ymin)/(jsize-1)};
    }
    
    public void setDOF(int i, int j, FDDOF d){
        x[i][j]=d;
    }
    
    public void setDOF(int[] n, FDDOF d){
        x[n[0]][n[1]]=d;
    }
    
    public FDDOF getDOF(int[] n){
        return x[n[0]][n[1]];
    }
}