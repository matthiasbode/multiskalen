package bijava.math.function.interpolation;

import bijava.geometry.dim2.Point2d;
import bijava.geometry.dimN.VectorNd;
import bijava.math.function.VectorFunction2d;

public class RasterVectorFunction2d implements VectorFunction2d{

	private RasterScalarFunction2d[] func;
	
	public RasterVectorFunction2d(RasterScalarFunction2d[] func){
		this.func = func;
	}
	
	public VectorNd getValue(Point2d p) {
		VectorNd result = new VectorNd(func.length);
		for (int i = 0; i < result.dim(); i++)
			result.x[i]=func[i].getValue(p);
		return result;
	}
}
