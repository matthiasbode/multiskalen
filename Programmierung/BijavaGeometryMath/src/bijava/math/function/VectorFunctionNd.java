package bijava.math.function;

import bijava.geometry.dimN.*;

public interface VectorFunctionNd {
        /** Gibt die Funktionswerte an der Stelle p zurueck*/
	VectorNd getValue (PointNd p); // Todo: allgemeinerer Parameter als PointNd (PointNd implements EuclidianPoint)
        /** Gibt die Raumdimension des Definitionsbereichs zurueck */
        public int getDim();
}
