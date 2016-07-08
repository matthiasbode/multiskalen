/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package math.geometry;

import java.io.Serializable;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */

//==========================================================================//
//  INTERFACE Curve2d                                                       //
//==========================================================================//
/** "Curve2d" is an interface for a curve in a twodimensional space.
 *
 *  <p><strong>Version:</strong><br>
 *  <dd>1.0, july 2003</dd>
 *  <p><strong>Author:</strong><br>
 *  <dd>Dr.-Ing. Martin Rose</dd>                                           */
//==========================================================================//
public interface Curve2d extends Serializable
{

//--------------------------------------------------------------------------//
//  GET A POINT                                                             //
//--------------------------------------------------------------------------//
/** Get the point to an argument s.                                         */
//--------------------------------------------------------------------------//
  public Point2d getPoint (double s);

//--------------------------------------------------------------------------//
//  GET THE LENGTH                                                          //
//--------------------------------------------------------------------------//
/** Get the length of the curve.                                            */
//--------------------------------------------------------------------------//
  public double getLength ();

}
