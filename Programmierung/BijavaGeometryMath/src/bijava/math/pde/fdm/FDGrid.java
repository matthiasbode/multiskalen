package bijava.math.pde.fdm;

public interface FDGrid {
    public int[] getSize();
    public double[] getLocation(int[] i);
    public void setDOF(int[] n, FDDOF d);
    public FDDOF getDOF(int[] n);
}
