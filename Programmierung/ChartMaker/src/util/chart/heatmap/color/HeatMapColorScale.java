/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap.color;

import java.awt.Color;

/**
 *
 * @author Philipp
 */
public interface HeatMapColorScale {
    public Color getColor(double value, double maxValue);
}
