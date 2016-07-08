package bijava.graphics3d;

import java.awt.Color;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleFanArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Schoening
 */
public class Cylinder3D extends Shape3D {

    private Point3f mittelpkt;
    private float rx, dx;
    private float ry, dy;
    private float h;
    private Color4f color;
    private Point3f[] ptsBody;
    private Point3f[] p2;
    private Point3f[] p3;
    private Vector3f vec = new Vector3f(0, 0, 1);
    private Vector3f rtg = new Vector3f(0, 0, 1);
    private int tBody;
    private int t;
    private int form;
    private GeometryArray[] geom;
    public static final int ALLE_FLAECHEN = 0;
    public static final int NUR_DER_MANTEL = 1;
    public static final int NUR_MIT_BODEN = 2;
    public static final int NUR_MIT_DECKEL = 3;
    public static final int OHNE_MANTEL = 4;

//------------------------------------------------------------------------------//
    /**
     *
     * @param mittelpkt
     * @param rx
     * @param dx
     * @param ry
     * @param h
     * @param divisions
     * @param form
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(Point3f mittelpkt, float rx, float dx, float ry, float dy, float h, int form, int divisions, Color4f color) {
        this.mittelpkt = mittelpkt;
        this.color = color;
        this.rx = rx;
        this.ry = ry;
        this.dx = dx / 2;
        this.dy = dy / 2;
        this.h = h / 2;
        this.form = form;
//        if(divisions < 3)throw new IllegalArgumentException("Divisions muss groesser 3 sein");
        if (divisions < 3) {
            divisions = 3;
        }
        t = tBody;
        this.tBody = divisions;
        t = tBody / 2;

        init();
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param mittelpkt
     * @param rx
     * @param ry
     * @param h
     * @param form
     * @param divisions
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(Point3f mittelpkt, float rx, float ry, float h, int form, int divisions, Color4f color) {
        this(mittelpkt, rx, 0, ry, 0, h, divisions, form, color);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param mittelpkt
     * @param rx
     * @param ry
     * @param h
     * @param form
     * @param divisions
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(Point3f mittelpkt, float rx, float ry, float h, int form, int divisions, Color color) {
        this(mittelpkt, rx, ry, h, form, divisions, new Color4f(color));
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param rx
     * @param dx
     * @param ry
     * @param h
     * @param form
     * @param divisions
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(float rx, float dx, float ry, float dy, float h, int form, int divisions, Color4f color) {
        this(new Point3f(), rx, dx, ry, dy, h, form, divisions, color);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param rx
     * @param ry
     * @param h
     * @param form
     * @param divisions
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(float rx, float ry, float h, int form, int divisions, Color4f color) {
        this(new Point3f(), rx, ry, h, form, divisions, color);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param rx
     * @param ry
     * @param h
     * @param form
     * @param divisions
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(float rx, float ry, float h, int form, int divisions, Color color) {
        this(new Point3f(), rx, ry, h, form, divisions, new Color4f(color));
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param rx
     * @param dx
     * @param ry
     * @param h
     * @param form
     * @param divisions
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(float rx, float dx, float ry, float h, int form, int divisions, Color color) {
        this(new Point3f(0, 0, 0), rx, dx, ry, 0, h, form, divisions, new Color4f(color));
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param rx
     * @param ry
     * @param h
     * @param form
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(float rx, float ry, float h, int form, Color color) {
        this(new Point3f(), rx, ry, h, form, 46, new Color4f(color));
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param rx
     * @param dx
     * @param ry
     * @param h
     * @param form
     * @param color
     */
//------------------------------------------------------------------------------//
    public Cylinder3D(float rx, float dx, float ry, float h, int form, Color4f color) {
        this(new Point3f(), rx, dx, ry, 0, h, form, 46, color);
    }

    public Cylinder3D(Point3f mittelpkt, float rx, float dx, float ry, float dy, float h,
            int form, int divisions, Color4f color, Vector3f rtg) {
        this.mittelpkt = mittelpkt;
        this.rx = rx;
        this.dx = dx / 2;
        this.ry = ry;
        this.dy = dy / 2;
        this.h = h / 2;
        this.color = color;
        this.rtg = rtg;
        this.tBody = divisions;
        t = tBody / 2;
        this.form = form;

        init();
    }

    public double getWinkel(Vector3f rtg) {
        double links = vec.x * rtg.x + vec.y * rtg.y + vec.z * rtg.z;
        double betrag = Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z)
                * Math.sqrt(rtg.x * rtg.x + rtg.y * rtg.y + rtg.z * rtg.z);
        return 2 * Math.PI - Math.acos(links / betrag);
    }

    public void init() {

        double a = getWinkel(rtg);
        System.out.println(a / Math.PI);
        if (a == 0 || a == Math.PI || a == 2 * Math.PI) {
            System.out.println("wbbbbbbbbbbbb");
            initPoints();
            if (form != NUR_DER_MANTEL) {
                copyPoints(mittelpkt.x + dx, mittelpkt.y + dy, h,
                        mittelpkt.x - dx, mittelpkt.y - dy, -h);
            }
        } else if (a == Math.PI / 2 || a == 3 * Math.PI / 2) {
            System.out.println("waaaa");
            initPoints_waagerecht();
            if (form != NUR_DER_MANTEL) {
                copyPoints(mittelpkt.x + dx, h, mittelpkt.y + dy,
                        mittelpkt.x - dx, -h, mittelpkt.y - dy);
            }
        } else if (a == Math.PI) {
        }

        switch (form) {
            case ALLE_FLAECHEN:
                geom = new GeometryArray[3];
                initMantel(0);
                initDeckel(1);
                initBoden(2);
                break;
            case NUR_DER_MANTEL:
                geom = new GeometryArray[1];
                initMantel(0);
                break;
            case NUR_MIT_BODEN:
                geom = new GeometryArray[2];
                initMantel(0);
                initBoden(1);
                break;
            case NUR_MIT_DECKEL:
                geom = new GeometryArray[2];
                initMantel(0);
                initDeckel(1);
                break;
            case OHNE_MANTEL:
                geom = new GeometryArray[2];
                initDeckel(0);
                initBoden(1);
                break;
            default:
                break;
        }
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    private void initPoints() {


        ptsBody = new Point3f[tBody];
        for (int i = 0, j = 1; i < (tBody - 2); i += 2, j += 2) {
            float x = mittelpkt.x + rx * (float) Math.cos((j / 2) * 2 * Math.PI / (tBody / 2 - 2));
            float y = mittelpkt.y + ry * (float) Math.sin((j / 2) * 2 * Math.PI / (tBody / 2 - 2));
            ptsBody[i] = new Point3f(x + dx, y + dy, h);
            ptsBody[i + 1] = new Point3f(x - dx, y - dy, -h);
        }
        float x = mittelpkt.x + rx * (float) Math.cos((1 / 2) * 2 * Math.PI / (tBody / 2 - 2));
        float y = mittelpkt.y + ry * (float) Math.sin((1 / 2) * 2 * Math.PI / (tBody / 2 - 2));
        ptsBody[tBody - 2] = new Point3f(x + dx, y + dy, h);
        ptsBody[tBody - 1] = new Point3f(x - dx, y - dy, -h);
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//    
    private void initPoints_waagerecht() {

        ptsBody = new Point3f[tBody];
        for (int i = 0, j = 1; i < (tBody - 2); i += 2, j += 2) {
            float x = mittelpkt.x + rx * (float) Math.cos((j / 2) * 2 * Math.PI / (tBody / 2 - 2));
            float y = mittelpkt.y + ry * (float) Math.sin((j / 2) * 2 * Math.PI / (tBody / 2 - 2));
            ptsBody[i] = new Point3f(x + dx, h, y + dy);
            ptsBody[i + 1] = new Point3f(x - dx, -h, y - dy);
        }
        float x = mittelpkt.x + rx * (float) Math.cos((1 / 2) * 2 * Math.PI / (tBody / 2 - 2));
        float y = mittelpkt.y + ry * (float) Math.sin((1 / 2) * 2 * Math.PI / (tBody / 2 - 2));
        ptsBody[tBody - 2] = new Point3f(x + dx, h, y + dy);
        ptsBody[tBody - 1] = new Point3f(x - dx, -h, y - dy);
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    private void copyPoints(float x, float y, float z, float xx, float yy, float zz) {
        p2 = new Point3f[t];
        p3 = new Point3f[t];
        p2[0] = new Point3f(x, y, z);
        p3[0] = new Point3f(xx, yy, zz);

        int j = 1;
        for (int i = 0; i < ptsBody.length - 2; i += 2) {
            p2[j] = ptsBody[i];
            p3[j] = ptsBody[ptsBody.length - 3 - i];
            j++;
        }
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param nu
     */
//------------------------------------------------------------------------------//
    private void initDeckel(int nu) {
        geom[nu] = new TriangleFanArray(t, GeometryArray.COORDINATES | GeometryArray.COLOR_4, new int[]{t});
        geom[nu].setCoordinates(0, p2);
        for (int i = 0; i < t; i++) {
            geom[nu].setColor(i, color);
        }
        addGeometry(geom[nu]);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param nu
     */
//------------------------------------------------------------------------------//
    private void initBoden(int nu) {
        geom[nu] = new TriangleFanArray(t, GeometryArray.COORDINATES | GeometryArray.COLOR_4, new int[]{t});
        geom[nu].setCoordinates(0, p3);
        for (int i = 0; i < t; i++) {
            geom[nu].setColor(i, color);
        }
        addGeometry(geom[nu]);
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param nu
     */
//------------------------------------------------------------------------------//
    private void initMantel(int nu) {

        geom[nu] = new TriangleStripArray(tBody, GeometryArray.COORDINATES | GeometryArray.COLOR_4, new int[]{tBody});
        geom[nu].setCoordinates(0, ptsBody);
        for (int i = 0; i < tBody; i++) {
            geom[nu].setColor(i, color);
        }
        setGeometry(geom[nu]);
    }
}
