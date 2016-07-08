/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator;

import applications.transshipment.generator.projekte.ParameterInputFile;
import applications.transshipment.model.resources.conveyanceSystems.lcs.HandoverPoint;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class HandoverPointsGenerator {

 
    public static List<HandoverPoint> getHandoverPointsForLCSystem(Rectangle2D rLFA, ParameterInputFile parameters) {


        ArrayList<HandoverPoint> res = new ArrayList<>();
        double rackStart = (rLFA.getWidth() - parameters.getInt(ParameterInputFile.KEY_LCS_numberOfHandoverPoints)* parameters.getDouble(ParameterInputFile.KEY_LCS_lengthHandoverPoint)- (parameters.getInt(ParameterInputFile.KEY_LCS_numberOfHandoverPoints)- 1) * parameters.getDouble(ParameterInputFile.KEY_LCS_distanceHandoverPoints)) / 2;
        if (rackStart < 0) {
            throw new RuntimeException("Ungueltige LCS-Konfiguration: rackStart darf nicht negativ werden");
        }
        double pos = rackStart;
        for (int j = 0; j < parameters.getInt(ParameterInputFile.KEY_LCS_numberOfHandoverPoints); j++, pos += (parameters.getDouble(ParameterInputFile.KEY_LCS_lengthHandoverPoint)  + parameters.getDouble(ParameterInputFile.KEY_LCS_distanceHandoverPoints))) {
            for (int i = 0; i < 2; i++) {
                Rectangle2D rec = null;
                if (i == 0) {
                    rec = new Rectangle2D.Double(pos, rLFA.getMinY(), parameters.getDouble(ParameterInputFile.KEY_LCS_lengthHandoverPoint), parameters.getDouble(ParameterInputFile.KEY_LCS_widthHandoverPoint));
                }
                if (i == 1) {
                    rec = new Rectangle2D.Double(pos, rLFA.getMaxY() -  parameters.getDouble(ParameterInputFile.KEY_LCS_widthHandoverPoint), parameters.getDouble(ParameterInputFile.KEY_LCS_lengthHandoverPoint),  parameters.getDouble(ParameterInputFile.KEY_LCS_widthHandoverPoint));
                }
                res.add(new HandoverPoint(rec));
            }
        }
        return res;
    }
}
