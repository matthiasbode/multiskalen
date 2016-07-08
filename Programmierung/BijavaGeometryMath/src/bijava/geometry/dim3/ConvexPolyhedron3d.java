package bijava.geometry.dim3;

import javax.media.j3d.BoundingBox;

//==========================================================================//
//  KLASSE ConvexPolyhedron3d                                                     //
//==========================================================================//
/**
 * Die Klasse <code>ConvexPolyhedron3d</code> beschreibt Objekte zur Darstellung
 *  von konvexen Zellen im dreidimensionalen euklidischen Raum. Sie ist eine
 *  abstrakte Basisklasse. Das heisst, es kann kein Objekt von 
 *  <code>ConvexPolyhedron3d</code> erzeugt werden.
 * 
 *  <p><strong>Version:</strong>
 *  <br><dd>1.0, Februar 2000
 *  <p><strong>Author:</strong>
 *  <br><dd>Institut f&uuml;r Bauinformatik
 *  <br><dd>Universit&auml;t Hannover
 *  <br><dd>Dipl.-Ing. Martin Rose                                          
 */
//==========================================================================//
public abstract class ConvexPolyhedron3d {
  int dimension;
  Point3d[] points;
  double epsilon;

//--------------------------------------------------------------------------//
//  DIMENSION LESEN                                                         //
//--------------------------------------------------------------------------//
/** Liefert die Diemnsion der konvexen Zelle.                               */
//--------------------------------------------------------------------------//
  public int getDimension () { return dimension; }  
 
//--------------------------------------------------------------------------//
//  ANZAHL DER RANDPUNKTE LESEN                                             //
//--------------------------------------------------------------------------//
/** Liefert die Anzahl der points der konvexen Hülle.                       */
//--------------------------------------------------------------------------//
  public int getPointSize () { return points.length; }  
  
//--------------------------------------------------------------------------//
//  RANDPUNKTE LESEN                                                        //
//--------------------------------------------------------------------------//
/** Liefert die points der konvexen Hülle.                                  */
//--------------------------------------------------------------------------//
  public Point3d[] getPoints () { return points; }   
  
//--------------------------------------------------------------------------//
//  RANDPUNKT LESEN                                                         //
//--------------------------------------------------------------------------//
/** Liefert einen Punkt der konvexen Hülle.
 *  <p>
 *  @param       index Position des pointss im Punktfeld
 *  <p>
 *  @throws      <code>ArrayIndexOutOfBoundsException</code> wenn der Index
 *               nicht im Bereiche der Punktanzahl liegt                    */
//--------------------------------------------------------------------------//
  public Point3d getPoint(int index) throws ArrayIndexOutOfBoundsException {
      if (index < 0 || index >= points.length)
          throw new ArrayIndexOutOfBoundsException();
      return new Point3d(points[index]);
  }
 
//--------------------------------------------------------------------------//
//  EPSILONUMGEBUNG HOLEN                                                    //
//--------------------------------------------------------------------------// 
/** Liefert die festgelegte Epsilon-Umgebung aller Zellpoints.              */
//--------------------------------------------------------------------------//
  public double getEpsilon () { return epsilon; }

//--------------------------------------------------------------------------//
//  EPSIONUMGEBUNG SETZEN                                                   //
//--------------------------------------------------------------------------// 
/** Setzt die Epsilon-Umgebung aller Zellpoints.
 *
 *  @param epsilon Neuer Wert der Epsilon-Umgebung                          */
//--------------------------------------------------------------------------//
  public void setEpsilon (double epsilon) {  this.epsilon = epsilon; }

//--------------------------------------------------------------------------//
//  ABSTAND EINES pointsS ZU EINER EBENE                                    //
//--------------------------------------------------------------------------// 
  private double distance(Point3d point, Point3d line1, Point3d line2) {
      Vector3d b = new Vector3d(); b.sub(line2, line1);
      Vector3d c = new Vector3d(); c.sub(point, line1);
      Vector3d kreuz = new Vector3d(); kreuz.cross(b, c);
      
      return (kreuz.length() / b.length());
  }
//--------------------------------------------------------------------------//
//  Bounding Box der konvexen Zelle                                         //
//--------------------------------------------------------------------------// 
  public abstract BoundingBox getBoundingBox();
  
  public abstract boolean contains(Point3d p);
  
  public abstract double[] getNaturefromCart(Point3d p);
  
//--------------------------------------------------------------------------//
//  AUSAGABE                                                                //  
//--------------------------------------------------------------------------//
/** Liefert die konvexe Zelle als Zeichenkette.                             */
//--------------------------------------------------------------------------//
  public String toString() {
      String wort = dimension + "D-Zelle: (" + points[0];
      for (int i=1; i<points.length; i++) wort += ", " + points[i];
      wort += ")";
      return wort;
  }
}