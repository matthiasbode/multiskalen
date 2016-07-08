package bijava.graphics3d.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.media.j3d.*;
import javax.vecmath.*;
//import com.sun.image.codec.jpeg.*;

/** Class CapturingCanvas3D, using the instructions from the Java3D FAQ pages on 
 *  how to capture a still image in jpeg format.
 *  Original Version: Peter Z. Kunszt, Johns Hopkins University, Dept of Physics and Astronomy, Baltimore MD
 */
public class CapturingCanvas3D extends Canvas3D  {
	
	public boolean writeJPEG_;
	private int postSwapCount_;
	
	public CapturingCanvas3D(GraphicsConfiguration gc) {
		super(gc);
		postSwapCount_ = 0;
	}
	
	public void postSwap() {
		if(writeJPEG_) {
			System.out.println("Writing JPEG");
			GraphicsContext3D  ctx = getGraphicsContext3D();
			// The raster components need all be set!
			Raster ras = new Raster(
					new Point3f(-1.0f,-1.0f,-1.0f),
					Raster.RASTER_COLOR,
					0,0,
					this.getSize().width, this.getSize().height,
					new ImageComponent2D(
							ImageComponent.FORMAT_RGB,
							new BufferedImage(this.getSize().width, this.getSize().height,
									BufferedImage.TYPE_INT_RGB)),
									null);
			
			ctx.readRaster(ras);
			
			// Now strip out the image info
			BufferedImage img = ras.getImage().getImage();
			
			// write that to disk....
//			try {
//				FileOutputStream out = new FileOutputStream("Capture"+postSwapCount_+".jpg");
//				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//				JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
//				param.setQuality(1.0f,false); // 90% qualith JPEG
//				encoder.setJPEGEncodeParam(param);
//				encoder.encode(img);
//				writeJPEG_ = false;
//				out.close();
//			} catch ( IOException e ) {
//				System.out.println("I/O exception!");
//			}
			postSwapCount_++;
                        throw new UnsupportedOperationException("Implementierung unabhaengig von sun-Paketen anstreben!");
		}
	}
}
