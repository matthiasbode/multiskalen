package bijava.geometry.dimN;

//==========================================================================//
//  CLASS PolygonCurve3D                                                    //
//==========================================================================//
/** "PolygonCurve3D" is a class for a curve as a polygon
 *  in a threedimensional space.
 *
 *  <p><strong>Version:</strong><br>
 *  <dd>1.0, july 2003</dd>
 *  <p><strong>Author:</strong><br>
 *  <dd>Dr.-Ing. Martin Rose</dd>                                           */
//==========================================================================//
public class PolygonNd implements CurveNd
{  private PointNd[] polygon;                  // array of points on the curve

//--------------------------------------------------------------------------//
//  TRANSFORMCONSTRUCTOR                                                    //
//--------------------------------------------------------------------------//
/** Create a polygonal curve.
 *
 *  @param polygon points on the curve                                      */
//--------------------------------------------------------------------------//
  public PolygonNd (PointNd[] polygon)
  { this.polygon = polygon; }

//--------------------------------------------------------------------------//
//  EQUALITY                                                                //
//--------------------------------------------------------------------------//
/** Test the curve for equality.
 *
 *  @return <code>true</code>, if <code>object</code> is an instance of the
 *	  class <code>PolygonCurve3D</code> and if all points of both
 *        curves are equal.                                                 */
//--------------------------------------------------------------------------//
  public boolean equals (Object object)
  { if (!(object instanceof PolygonNd)) return false;
    PolygonNd curve = (PolygonNd) object;
    for (int i = 0; i < polygon.length; i++)
      if (this.polygon[i].equals (curve.polygon[i])) return false;
    return true;
  }

//--------------------------------------------------------------------------//
//  GET A POINT                                                             //
//--------------------------------------------------------------------------//
/** Get the point to an argument s.
 *
 *  @return If s is negativ or greater than the length of the curve,
 *          the method returns <code>null</code>.                           */
//--------------------------------------------------------------------------//
  public PointNd getPoint (double s)
  { if (polygon.length == 1 & s == 0.0) return polygon[0]; 
    if (s < 0.0 | polygon.length < 2)   return null;

    double l = 0.0;
    double d = 0.0;
    for (int i = 1; i < polygon.length; i++)
    { d  = polygon[i-1].distance (polygon[i]);
      l += d;
      if (l >= s)
      { double  sf  = 1.0 - ((l - s) / d);
	PointNd p1  = polygon[i-1];
	PointNd p2  = polygon[i];
	PointNd p   = new PointNd (p1);
//        p.interpolate(p2,sf);
//	return p;
        return (p1.mult(sf)).add(p2.mult(1.-sf));
      }
    }
    return null;
  }

//--------------------------------------------------------------------------//
//  GET THE LENGTH                                                          //
//--------------------------------------------------------------------------//
/** Get the length of the curve.                                            */
//--------------------------------------------------------------------------//
  public double getLength ()
  { double length = 0.0;
    if (polygon.length < 2) return 0.0;
    for (int i = 1; i < polygon.length; i++)
      length += polygon[i-1].distance (polygon[i]);
    return length;
  }

//--------------------------------------------------------------------------//
//  CONVERSION TO A STRING                                                  //
//--------------------------------------------------------------------------//
/** Convert the curve into a string.                                        */
//--------------------------------------------------------------------------//
  public String toString ()
  { String s = "PolygonCurve3D: ";
    for (int i = 0; i < polygon.length; i++) s += polygon[i] + ", ";
    return s;
  }
}
