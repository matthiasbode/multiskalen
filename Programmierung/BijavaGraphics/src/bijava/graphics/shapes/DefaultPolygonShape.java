package bijava.graphics.shapes;

import bijava.geometry.dim2.Polygon2d;
import bijava.graphics.canvas2D.ObjectShape;
import bijava.graphics.canvas2D.Polygon2D;

//==============================================================================//
/** Ein DefaultPolygonShape ist ein Objekt, das sowohl das eigentliche Objekt
 *  Polygon2d enthaelt, als auch die durch Graphics2D darstellbare Form
 *  dieses Polygons, ein Polygon2D-Objekt.
 *
 *  @author berthold                                                            */
//==============================================================================//
public class DefaultPolygonShape extends ObjectShape {


//------------------------------------------------------------------------------//
/** Konstruktor, dem das Modellobjekt (Polygon2d) uebergeben wird. Hierbei wird
 *  automatisch ein <code>Polygon2D</code>-Shape erzeugt, das dargestellt werden
 *  kann.
 *
 *  @param  poly    Polygon2d, zu dem ein darstellbares Polygon erzeugt wird. Ein
 *                  Verweis auf <code>poly</code> wird gespeichert.             */
//------------------------------------------------------------------------------//
    public DefaultPolygonShape(Polygon2d poly) {
        super(new Polygon2D(poly), poly);
    }

}
