package applications.functions.graphics3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;


/**
 * This class creates a right-handed 3D coordinate system.
 *
 * @author Jan Stilhammer
 */
public class AxisSystem3D extends Shape3D {
    /**
     * Definition of the geometry of the three axis.
     */
    private static final float[] EXTREMITES = {

            // x-axis
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

            // y-axis
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            // z-axis
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
    };

    /**
     * Colors of the three axis.
     */
    private static final float[] COLOR = {
            // x-axis
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            // y-axis
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            // z-axis
            1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f
    };

    /**
     * The scale factor
     */
    private static final float SCALE = 1.0f;
    
    /**
     * The scale factor
     */
    private static final float LINE_WIDTH = 3.0f;

    /**
     * DOCUMENT ME!
     */
    private float scale;
    
    private float width;

    /**
     * Initializes a new coordinate system with the length of the axis to one
     * meter and a line-width of 4.
     */
    public AxisSystem3D() {
        this(SCALE, LINE_WIDTH);
    }

    /**
     * Initializes a new coordinate system with a line-width of 4.
     *
     * @param scale the scale factor to adjust the axis's length in meter.
     */
    public AxisSystem3D(float scale) {
        this(scale,LINE_WIDTH);
    }
    
    /**
     * Initializes a new coordinate system.
     *
     * @param scale the scale factor to adjust the axis's length in meter.
     * @param lineWidth the width of the axis-lines.
     */
    public AxisSystem3D(float scale, float lineWidth) {
        this.scale = scale;
        this.width=lineWidth;
        this.setGeometry(createGeometry());
        this.setAppearance(createAppearence());
    }

    /**
     * Returns the Geometry of the coordinate system.
     *
     * @return the Geometry of a coordinate system. The axis has three
     *         different colors and is scaled.
     */
    private Geometry createGeometry() {
        // Construction of the axis (LineArray).
        LineArray axis = new LineArray(6,
                LineArray.COORDINATES | LineArray.COLOR_3);

        // Scalling of the vertices of the 3 axis using scale.
        float[] scaledExtremites = new float[EXTREMITES.length];

        for (int i = 0; i < EXTREMITES.length; i++) {
            scaledExtremites[i] = EXTREMITES[i] * scale;
        }

        axis.setCoordinates(0, scaledExtremites);
        axis.setColors(0, COLOR);

        return axis;
    }
    
    
    /**
     * Returns the Appearance of the coordinate system.
     *
     * @return the Geometry of a coordinate system. The axis has three
     *         different colors and is scaled.
     */
    private Appearance createAppearence() {
        Appearance app = new Appearance();
        app.setLineAttributes(new LineAttributes(width,LineAttributes.PATTERN_SOLID,true));
        return app;
    }
}
