package bijava.math.pde.fdm;

public class FDGrid1d implements FDGrid{
    double xmin, xmax;
    FDDOF[] x;
    int size;
    
    public FDGrid1d(int n, double xmin, double xmax){
        this.xmin=xmin;
        this.xmax=xmax;
        this.size=n;
        x = new FDDOF[n];
    }
    
    public int getLength(){
        return size;
    }
    
    public int[] getSize(){
        return new int[]{size};
    }
    
    public double getLocation(int i){
        return xmin + i*(xmax-xmin)/(size-1);
    }
    
    public double[] getLocation(int[] i){
        return new double[]{xmin + i[0]*(xmax-xmin)/(size-1)};
    }
    
    public void setDOF(int i, FDDOF d){
        x[i]=d;
    }
    
    public void setDOF(int[] n, FDDOF d){
        x[n[0]]=d;
    }
    
    public FDDOF getDOF(int[] n){
        return x[n[0]];
    }
}