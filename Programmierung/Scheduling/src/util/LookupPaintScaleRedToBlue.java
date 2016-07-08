/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Paint;
import org.jfree.chart.renderer.LookupPaintScale;

/**
 *
 * @author Philipp
 */
public class LookupPaintScaleRedToBlue extends LookupPaintScale{
    @Override
    public Paint getPaint(double value){
        return ColorScale.getColorInverted(value,this.getUpperBound());
    }
}
