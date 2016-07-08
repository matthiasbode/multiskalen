/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap.color;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Philipp
 */
public class RedHeatMapColorScale implements HeatMapColorScale{

    @Override
    public Color getColor(double value, double maxValue) {
        float H = 0.99f;
        float S = maxValue == 0.0 ? 0 : (float) (value/maxValue);
        float V = 0.79f;
        return Color.getHSBColor(H, S, V);
    }
    public static void main(String[] args) {
        RedHeatMapColorScale scale = new RedHeatMapColorScale();
        JFrame f = new JFrame();
        f.setSize(500,500);
        f.setLayout(new GridLayout(12,30));
        for(float i=0f;i<=1f;i=i+0.01f){
            JLabel label = new JLabel();
            label.setForeground(scale.getColor(i,1));
            label.setText(""+i);
            f.add(label);
        }
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
