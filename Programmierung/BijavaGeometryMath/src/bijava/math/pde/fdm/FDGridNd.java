package bijava.math.pde.fdm;

import bijava.structs.ArrayNd;



public class FDGridNd implements FDGrid{
    ArrayNd<FDDOF> x;
    double[] min;
    double[] max;
    int[] dim;
    
    public FDGridNd(int[] n, double[] min, double[] max){
        x = new ArrayNd<FDDOF>(n);
        this.min = new double[min.length];
        this.max = new double[max.length];
        System.arraycopy(min, 0, this.min, 0, min.length);
        System.arraycopy(max, 0, this.max, 0, max.length);
        dim=n;
    }
    
    public int[] getSize(){
        return x.getDimSize();
    }
    
    public double[] getLocation(int[] i){
        if(i.length != dim.length) return null;
        double[] rvalue=new double[dim.length];
        for (int j=0;j<dim.length;j++){
            rvalue[j]=min[j] + i[j]*(max[j]-min[j])/(dim[j]-1);
        }
        return rvalue;
    }
    
    public void setDOF(int[] n, FDDOF d){
        x.setElement(n,d);
    }
    
    public FDDOF getDOF(int[] n){
        return x.getElement(n);
    }
}
