/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Matthias
 */
public class GraphicTools {

    static StandardColors standardColors = new StandardColors();
    static int counter;

    static class StandardColors {

        ArrayList<Color> colors = new ArrayList<Color>();

        public StandardColors() {
            colors.add(Color.MAGENTA);
            colors.add(Color.CYAN);
            colors.add(Color.PINK);
            colors.add(Color.WHITE);
            colors.add(Color.LIGHT_GRAY);
            colors.add(Color.MAGENTA);
            colors.add(Color.CYAN);
            colors.add(Color.PINK);
            colors.add(Color.WHITE);
            colors.add(Color.LIGHT_GRAY);

            counter = 0;
        }
    }

    public static AffineTransform getW2SXScaled(Rectangle2D box, int width, int xversatz, int yversatz, double sy) {
        AffineTransform transform = new AffineTransform();

        double sx = width / box.getWidth();
        double tx = -box.getMinX() * sx,
                ty = -Math.abs(box.getMinY());
        transform = new AffineTransform(sx, 0.0, 0.0, sy, tx, ty);
        AffineTransform translation = AffineTransform.getTranslateInstance(xversatz, yversatz);
        translation.concatenate(AffineTransform.getScaleInstance(1.0, -1.0));
        transform.preConcatenate(translation);
        return transform;
    }

    public static AffineTransform getW2SNonEqual(Rectangle2D box,
            int width,
            int height,
            Insets insets/*,
     Point offset*/) {
        int deg = 0;
        double theta = deg * Math.PI / 180.;
//        double theta = (1+2./9)*Math.PI;
        theta = theta % (2 * Math.PI);
        if (theta < 0) {
            theta += 2 * Math.PI;
        }

        int h = height - insets.top - insets.bottom;
        int w = width - insets.left - insets.right;

        AffineTransform transform = new AffineTransform();

        double a = box.getWidth(), b = box.getHeight();
        double sin = Math.sin(theta), cos = Math.cos(theta);
        double absin = Math.abs(sin), abcos = Math.abs(cos);
        double sx, sy;
        if (deg == 45) {
            double s1 = Math.sin(0.768), s2 = Math.sin(0.803),
                    c1 = Math.cos(0.768), c2 = Math.cos(0.803);
            sx = (Math.abs((w * c1 - h * s1) / (a * (c1 * c1 - s1 * s1)))
                    + Math.abs((w * c2 - h * s2) / (a * (c2 * c2 - s2 * s2)))) / 2;
            sy = (Math.abs((h * c1 - w * s1) / (b * (c1 * c1 - s1 * s1)))
                    + Math.abs((h * c2 - w * s2) / (b * (c2 * c2 - s2 * s2)))) / 2;
        } else {
//        double sx = (width - insets.left - insets.right) / (Math.abs(b*sin) + Math.abs(a*cos)),
//               sy = (height - insets.bottom - insets.top) / (Math.abs(a*sin) + Math.abs(b*cos));
            sx = Math.abs((w * cos - h * sin) / a / (cos * cos - sin * sin));
            sy = Math.abs((h * cos - w * sin) / b / (cos * cos - sin * sin));
        }
        sx = Math.max(sx, .15);
        sy = Math.max(sy, .15);
        AffineTransform scale = new AffineTransform(sx, 0.0, 0.0, sy, 0, 0);

//        AffineTransform translate = AffineTransform.getTranslateInstance(width/2, 0);
        AffineTransform translate = AffineTransform.getTranslateInstance(-box.getMinX(), -box.getMinY());

        AffineTransform mirror = AffineTransform.getScaleInstance(1.0, -1.0);

        double tx = insets.left, ty = height - insets.bottom;
        if (theta < Math.PI / 2) {
            ty += -a * sin * sx;
        } else if (theta < Math.PI) {
            ty += b * cos * sy - a * sin * sx;
            tx += -a * cos * sx;
        } else if (theta < 3 * Math.PI / 2) {
            tx += -a * cos * sx - b * sin * sy;
            ty += b * cos * sy;
        } else if (theta < 2 * Math.PI) {
            tx += -b * sin * sy;
        }

//        tx += offset.x;
//        ty += offset.y;
        AffineTransform translation = AffineTransform.getTranslateInstance(tx, ty);

        AffineTransform rotate = AffineTransform.getRotateInstance(theta);

        transform.concatenate(translate);
        transform.preConcatenate(mirror);
        transform.preConcatenate(scale);
        transform.preConcatenate(rotate);
        transform.preConcatenate(translation);
//        transform.preConcatenate(AffineTransform.getTranslateInstance(width/2, height/2));

        return transform;
    }

    public static AffineTransform getW2SNonEqual(Rectangle2D box,
            int width,
            int height,
            Insets insets,
            Point offset,
            double asf,
            int rot) {
        double theta = rot * Math.PI / 180.;
        theta = theta % (2 * Math.PI);
        if (theta < 0) {
            theta += 2 * Math.PI;
        }

        int h = height - insets.top - insets.bottom;
        int w = width - insets.left - insets.right;

        AffineTransform transform = new AffineTransform();

        double a = box.getWidth(), b = box.getHeight();
        double sin = Math.sin(theta), cos = Math.cos(theta);
        double sx = (width - insets.left - insets.right) / box.getWidth();
        double sy = (height - insets.bottom - insets.top) / box.getHeight();
        sx = Math.max(sx, .15) * asf;
        sy = Math.max(sy, .15) * asf;

        /**
         * voruebergehend hier dirty iso-Transformation gesetzt
         */
        sx = Math.min(sx, sy);
        sy = sx;

        AffineTransform scale = new AffineTransform(sx, 0.0, 0.0, sy, 0, 0);

        AffineTransform translate = AffineTransform.getTranslateInstance(-box.getMinX(), -box.getMinY());

        AffineTransform mirror = AffineTransform.getScaleInstance(1.0, -1.0);

        double tx = insets.left, ty = height - insets.bottom;
        if (theta < Math.PI / 2) {
            ty += -a * sin * sx;
        } else if (theta < Math.PI) {
            ty += b * cos * sy - a * sin * sx;
            tx += -a * cos * sx;
        } else if (theta < 3 * Math.PI / 2) {
            tx += -a * cos * sx - b * sin * sy;
            ty += b * cos * sy;
        } else if (theta < 2 * Math.PI) {
            tx += -b * sin * sy;
        }

        tx -= offset.x;
        ty -= offset.y;
        AffineTransform translation = AffineTransform.getTranslateInstance(tx, ty);

        AffineTransform rotate = AffineTransform.getRotateInstance(theta);

        transform.concatenate(translate);
        transform.preConcatenate(mirror);
        transform.preConcatenate(scale);
        transform.preConcatenate(rotate);
        transform.preConcatenate(translation);

        return transform;
    }

    public static Color getRandomStandardColor() {

        int i = (int) (Math.random() * standardColors.colors.size() - 1 + 0.5);
        Color c = standardColors.colors.remove(i);

        return c;
    }

    public static Color getStandardColor() {

        Color c = standardColors.colors.get(counter);
        counter++;
        if (counter >= standardColors.colors.size()) {
            counter = 0;
        }
        return c;

    }

    public static Shape getTextShape(FontRenderContext frc, String str, Font font) {
        TextLayout tl = new TextLayout(str, font, frc);
        return tl.getOutline(null);
    }

    /**
     * Convenience method that returns a scaled instance of the provided
     * {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance, in pixels
     * @param targetHeight the desired height of the scaled instance, in pixels
     * @param hint one of the rendering hints that corresponds to
     * {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     * {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     * {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     * {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step scaling
     * technique that provides higher quality than the usual one-step technique
     * (only useful in downscaling cases, where {@code targetWidth} or
     * {@code targetHeight} is smaller than the original dimensions, and
     * generally only when the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public static BufferedImage getScaledInstance(BufferedImage img,
            int targetWidth,
            int targetHeight,
            Object hint,
            boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

}
