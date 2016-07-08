package bijava.graphics3d.io;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author RedEye
 */
public class SerializableImages implements Serializable {

    int width;
    int height;
    int[] pixels;

    public SerializableImages(BufferedImage bi) {
        width = bi.getWidth();
        height = bi.getHeight();
        pixels = new int[width * height];
        int[] tmp = bi.getRGB(0, 0, width, height, pixels, 0, width);
    }

    public BufferedImage getImage() {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0, 0, width, height, pixels, 0, width);
        return bi;
    }
}
