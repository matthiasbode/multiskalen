package bijava.graphics3d;

import java.awt.Color;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

//------------------------------------------------------------------------------//
/**
 *
 * Klasse erzeugt ein IndexedQuadArray
 * @author Schoening
 */
//------------------------------------------------------------------------------//
public class Cube3D extends Shape3D {

    protected Point3f center_point;
    protected Color4f color;
    protected float dx;
    protected float dy;
    protected float dz;

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen rechteckigen Kasten an dem Mittelpunkt <tt>m</tt> mit den Kantenl&auml;ngen
     * <tt>dx</tt>,<tt>dy</tt> und <tt>dz</tt> in der Farbe <tt>c</tt>
     * @param m   Mittelpunkt
     * @param dx  Kantenl&auml;ge in x-Richtung
     * @param dy  Kantenl&auml;ge in y-Richtung
     * @param dz  Kantenl&auml;ge in z-Richtung
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(Point3f m, float dx, float dy, float dz, Color4f c) {
        this.center_point = m;
        this.color = c;
        this.dx = dx / 2;
        this.dy = dy / 2;
        this.dz = dz / 2;
        initGeom();
    }

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen rechteckigen Kasten an dem Mittelpunkt <tt>m</tt> mit den Kantenl&auml;ngen
     * <tt>dx</tt>,<tt>dy</tt> und <tt>dz</tt> in der Farbe <tt>c</tt>
     * @param m   Mittelpunkt
     * @param dx  Kantenl&auml;ge in x-Richtung
     * @param dy  Kantenl&auml;ge in y-Richtung
     * @param dz  Kantenl&auml;ge in z-Richtung
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(Point3f m, float dx, float dy, float dz, Color c) {
        this(m, dx, dy, dz, new Color4f(c));
    }

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen W&uuml;rfel an dem Mittelpunkt <tt>m</tt> mit der Kantenl&auml;nge
     * <tt>s</tt> in der Farbe <tt>c</tt>
     * @param m   Mittelpunkt
     * @param s   Kantenl&auml;ge
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(Point3f m, float s, Color4f c) {
        this(m, s, s, s, c);
    }

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen W&uuml;rfel an dem Mittelpunkt <tt>m</tt> mit der Kantenl&auml;nge
     * <tt>s</tt> in der Farbe <tt>c</tt>
     * @param m   Mittelpunkt
     * @param s   Kantenl&auml;ge
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(Point3f m, float s, Color c) {
        this(m, s, s, s, new Color4f(c));
    }

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen rechteckigen Kasten an dem Mittelpunkt <tt>( 0, 0, 0 )</tt> mit den Kantenl&auml;ngen
     * <tt>dx</tt>,<tt>dy</tt> und <tt>dz</tt> in der Farbe <tt>c</tt>
     * @param dx  Kantenl&auml;ge in x-Richtung
     * @param dy  Kantenl&auml;ge in y-Richtung
     * @param dz  Kantenl&auml;ge in z-Richtung
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(float dx, float dy, float dz, Color4f c) {
        this(new Point3f(), dx, dy, dz, c);
    }

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen rechteckigen Kasten an dem Mittelpunkt <tt>( 0, 0, 0 )</tt> mit den Kantenl&auml;ngen
     * <tt>dx</tt>,<tt>dy</tt> und <tt>dz</tt> in der Farbe <tt>c</tt>
     * @param dx  Kantenl&auml;ge in x-Richtung
     * @param dy  Kantenl&auml;ge in y-Richtung
     * @param dz  Kantenl&auml;ge in z-Richtung
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(float dx, float dy, float dz, Color c) {
        this(new Point3f(), dx, dy, dz, new Color4f(c));
    }

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen W&uuml;rfel an dem Mittelpunkt <tt>( 0, 0, 0 )</tt> mit der Kantenl&auml;nge
     * <tt>s</tt> in der Farbe <tt>c</tt>
     * @param s   Kantenl&auml;ge
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(float s, Color4f c) {
        this(new Point3f(), s, s, s, c);
    }

//------------------------------------------------------------------------------//
    /**
     * Erzeugt einen W&uuml;rfel an dem Mittelpunkt <tt>( 0, 0, 0 )</tt> mit der Kantenl&auml;nge
     * <tt>s</tt> in der Farbe <tt>c</tt>
     * @param s   Kantenl&auml;ge
     * @param c   Farbe
     */
//------------------------------------------------------------------------------//
    public Cube3D(float s, Color c) {
        this(new Point3f(), s, s, s, new Color4f(c));
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    private void initGeom() {
        Point3f[] p = new Point3f[8];
        p[0] = new Point3f(center_point.x + dx, center_point.y + dy, center_point.z + dz);
        p[1] = new Point3f(center_point.x - dx, center_point.y + dy, center_point.z + dz);
        p[2] = new Point3f(center_point.x - dx, center_point.y - dy, center_point.z + dz);
        p[3] = new Point3f(center_point.x + dx, center_point.y - dy, center_point.z + dz);

        p[4] = new Point3f(center_point.x + dx, center_point.y + dy, center_point.z - dz);
        p[5] = new Point3f(center_point.x - dx, center_point.y + dy, center_point.z - dz);
        p[6] = new Point3f(center_point.x - dx, center_point.y - dy, center_point.z - dz);
        p[7] = new Point3f(center_point.x + dx, center_point.y - dy, center_point.z - dz);
        int[] indices = new int[24];
        IndexedQuadArray quad = new IndexedQuadArray(p.length, GeometryArray.COORDINATES | GeometryArray.COLOR_4
                | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2,
                indices.length);

        indices[0] = 0;
        indices[1] = 1;
        indices[2] = 2;
        indices[3] = 3;
        indices[4] = 7;
        indices[5] = 6;
        indices[6] = 5;
        indices[7] = 4;
        indices[8] = 0;
        indices[9] = 3;
        indices[10] = 7;
        indices[11] = 4;
        indices[12] = 5;
        indices[13] = 6;
        indices[14] = 2;
        indices[15] = 1;
        indices[16] = 0;
        indices[17] = 4;
        indices[18] = 5;
        indices[19] = 1;
        indices[20] = 6;
        indices[21] = 7;
        indices[22] = 3;
        indices[23] = 2;

        TexCoord2f texCoord[] = new TexCoord2f[4];
        texCoord[0] = new TexCoord2f(0.0f, 0.0f);
        texCoord[1] = new TexCoord2f(1.0f, 0.0f);
        texCoord[2] = new TexCoord2f(1.0f, 1.0f);
        texCoord[3] = new TexCoord2f(0.0f, 1.0f);

//        Vector3f[] normals = new Vector3f[8];
//        normals[0] = new Vector3f(1.0f, 1.0f, 1.0f);
//        normals[1] = new Vector3f(-1.0f, 1.0f, 1.0f);
//        normals[2] = new Vector3f(-1.0f, -1.0f, 1.0f);
//        normals[3] = new Vector3f(1.0f, -1.0f, 1.0f);
//        normals[4] = new Vector3f(1.0f, 1.0f, -1.0f);
//        normals[5] = new Vector3f(-1.0f, 1.0f, -1.0f);
//        normals[6] = new Vector3f(-1.0f, -1.0f, -1.0f);
//        normals[7] = new Vector3f(1.0f, -1.0f, -1.0f);
//
//        for (int i = 0; i < normals.length; i++) {
//            normals[i].normalize();
//        }
//        quad.setNormals(0, normals);
//        quad.setNormalIndices(0, indices);

        quad.setCoordinates(0, p);
        quad.setCoordinateIndices(0, indices);

        quad.setTextureCoordinates(0, 0, texCoord);
        quad.setTextureCoordinateIndices(0, 0, indices);

        for (int i = 0; i < p.length; i++) {
            quad.setColor(i, color);
        }
        setGeometry(quad);
    }
}