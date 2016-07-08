package bijava.geometry.dimN;

import bijava.math.function.AbstractDifferentialFunctionNd;

public abstract class LocalCoordinateFunctionNd extends AbstractDifferentialFunctionNd {

	protected ConvexPolyhedronNd element; //Referenz auf das geometrische Element
	private PointNd coordinate; //zugeordnete Koordinate

	public ConvexPolyhedronNd getElement() {
		return element;
	}

	public void setElement(ConvexPolyhedronNd e) {
		this.element = e;
	}

	public PointNd getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(PointNd p) {
		this.coordinate = p;
	}
}
