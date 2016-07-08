package bijava.geometry;

/** a point in a linear vectorspace */
public interface VectorPoint<E extends VectorPoint> extends LinearPoint<E>{
	/** dimension of the linear space */
	public int dim();
	public double getCoord(int i);
        public double[] getCoords();
	public void setCoord(int i,double d);
}
