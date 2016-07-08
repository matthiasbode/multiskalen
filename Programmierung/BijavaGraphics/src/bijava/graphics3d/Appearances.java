package bijava.graphics3d;

import com.sun.j3d.utils.image.TextureLoader;
import java.awt.Container;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Texture;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

/**
 *
 * @author Schoening
 */
public abstract class Appearances {

    public static Appearance getAppearance0() {
        Appearance appear = new Appearance();
        PolygonAttributes pattrib = new PolygonAttributes();
        pattrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        pattrib.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pattrib.setCullFace(PolygonAttributes.CULL_NONE);
        pattrib.setBackFaceNormalFlip(true);
        appear.setPolygonAttributes(pattrib);
        ColoringAttributes cattrib = new ColoringAttributes(1.f, 1.f, 1.f,
                ColoringAttributes.NICEST);
        appear.setColoringAttributes(cattrib);
        appear.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.6f));
        appear.setMaterial(new Material(new Color3f(0.2f, 0.2f, 0.2f),
                new Color3f(0f, 0f, 0f),
                new Color3f(1f, 1f, 1f),
                new Color3f(1f, 1f, 1f),
                100f));
        return appear;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @return
     */
//------------------------------------------------------------------------------//
    public static Appearance getAppearance1() {
        Appearance appear1 = new Appearance();
        PolygonAttributes pattrib = new PolygonAttributes();
        pattrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        pattrib.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pattrib.setCullFace(PolygonAttributes.CULL_NONE);
        pattrib.setBackFaceNormalFlip(true);
        appear1.setPolygonAttributes(pattrib);
        ColoringAttributes cattrib = new ColoringAttributes(new Color3f(153f, 126f, 115f),
                ColoringAttributes.NICEST);
        appear1.setColoringAttributes(cattrib);
        appear1.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NONE, 0.001f));
        appear1.setMaterial(new Material(new Color3f(0.2f, 0.2f, 0.2f),
                new Color3f(0f, 0f, 0f),
                new Color3f(1f, 1f, 1f),
                new Color3f(1f, 1f, 1f),
                100f));
        return appear1;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @return
     */
//------------------------------------------------------------------------------//
    public static Appearance getAppearance2() {
        Appearance appear = new Appearance();
        PolygonAttributes pattrib = new PolygonAttributes();
        pattrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        pattrib.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pattrib.setCullFace(PolygonAttributes.CULL_NONE);
        pattrib.setBackFaceNormalFlip(false);
        appear.setPolygonAttributes(pattrib);
        appear.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.2f));
        return appear;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @return
     */
//------------------------------------------------------------------------------//
    public static Appearance getAppearance3() {
        Appearance appear = new Appearance();
        PolygonAttributes pattrib = new PolygonAttributes();
        pattrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        pattrib.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pattrib.setCullFace(PolygonAttributes.CULL_NONE);
        pattrib.setBackFaceNormalFlip(false);
        appear.setPolygonAttributes(pattrib);
        appear.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.f));
        return appear;
    }

    //------------------------------------------------------------------------------//
    /**
     *
     * @return
     */
//------------------------------------------------------------------------------//
    public static Appearance getAppearance4() {
        Appearance appear = new Appearance();
//        PolygonAttributes pattrib = new PolygonAttributes();
//        pattrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
//        pattrib.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
//        pattrib.setCullFace(PolygonAttributes.CULL_NONE);
//        pattrib.setBackFaceNormalFlip(false);
//        appear.setPolygonAttributes(pattrib);
//        appear.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.2f));
//        Material material = new Material();
//        material.setShininess(128f);
//        appear.setMaterial(material);
//        TextureLoader loader = new TextureLoader("gesicht.jpg", new Container());
//        Texture texture = loader.getTexture();
//        texture.setBoundaryModeS(Texture.WRAP);
//        texture.setBoundaryModeT(Texture.WRAP);
//        texture.setBoundaryColor( new Color4f( 0.0f, 1.0f, 0.0f, 0.0f ) );
//        TexCoordGeneration tex = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,TexCoordGeneration.TEXTURE_COORDINATE_3);
//        tex.setPlaneS(new Vector4f(1f, 0f, 0f, 0f));
//        tex.setPlaneT(new Vector4f(0f, 1f, 0f, 0f));
//        tex.setPlaneR(new Vector4f(0f, 0f, 0f, 0f));
//        appear.setTexCoordGeneration(tex);
//        ImageComponent2D image = loader.getImage();
//        System.out.println("bbbbbbbbbb");
//        if(image == null) {
//            System.out.println("load failed for texture: ");
//        }
//
//        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
//                                      image.getWidth(), image.getHeight());
//        texture.setImage(0, image);
//        texture.setEnable(true);

//        texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
//        texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
//
// texture.setBoundaryModeS(Texture.WRAP);
//
//   texture.setBoundaryModeT(Texture.WRAP);

        // Set up the texture attributes
        //could be REPLACE, BLEND or DECAL instead of MODULATE
//        TextureAttributes texAttr = new TextureAttributes();
//        texAttr.setTextureMode(TextureAttributes.REPLACE);
//        appear.setTexture(texture);
//        appear.setTextureAttributes(texAttr);
        return appear;
    }
}
