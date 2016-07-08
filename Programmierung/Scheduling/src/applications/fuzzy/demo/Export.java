/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.demo;

import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.operation.FuzzyWorkloadParameters;
import applications.fuzzy.operation.FuzzyOperation;
import applications.fuzzy.plotter.FuzzyFunctionPlotter;
import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyDemandUtilities;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.ResourceImplementation;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.DoubleValue;
import util.MathUtils;

/**
 *
 * @author Matthias
 */
public class Export {

    public static void main(String[] args) {
        int resolution = 2;
        double v1 = 4500.;
        double VF = 0.01;
        double VFab = 0.025;

        Nutzleistung qn = new Nutzleistung();
        DiscretizedFuzzyInterval Qn_I = qn.get_Qn_I(resolution);

        // Fuzzy-Volumen festlegen
        DiscretizedFuzzyInterval vFI1 = FuzzyFactory.createLinearInterval(v1 - v1 * VF, v1 + v1 * VF, v1 * VFab, v1 * VFab, resolution);

        DiscretizedFuzzyInterval duration = vFI1.div(Qn_I);
                
        duration = FuzzyFactory.createLinearInterval(30,40,5,5, resolution);
                
         DiscretizedFuzzyInterval start = FuzzyFactory.createLinearInterval(10, 15, 4,4, resolution);
        double c2 = start.add(duration).getC2();

        Resource r1 = new ResourceImplementation("Bagger");
        double rd1 = 1.0;
        FuzzyOperation o1 = new FuzzyOperation(duration, 0.5);
        o1.setDemand(r1, new DoubleValue(rd1));

        double dbeta = 0.01;
        double dt = 0.01;
        double[] ts = MathUtils.increment(0.0, dt, c2); // x = 0.0:0.1:1.0
        double[] betas = MathUtils.increment(0.0, dbeta, 1.0);// y = 0.0:0.05:1.0

        double[][] auslastungen = new double[betas.length][ts.length];

        LinearizedFunction1d function = FuzzyDemandUtilities.getPresenceFunctionAtPessimisticLevelWithLambda(o1,start, new FuzzyWorkloadParameters(0.5));
        DiscretizedFuzzyInterval end = start.add(duration);
        
        
        FuzzyFunctionPlotter plotter = new FuzzyFunctionPlotter("Operation");
//        plotter.addFunction(duration.getMembershipFunction(), 0, c2, dt, "Kurve");
        plotter.addFunction(FuzzyDemandUtilities.getNecessityFunction(o1, start), 0, end.getC2(), dt, "Notwendigkeit");
        plotter.addFunction(FuzzyDemandUtilities.getPossibilityFunction(o1, start), 0, end.getC2(), dt, "Möglichkeit");
        plotter.addFunction(start.getMembershipFunction(), 0, end.getC2(), dt, "Start");
        plotter.addFunction(end.getMembershipFunction(), 0, end.getC2(), dt, "Ende");
        plotter.plot();

//        for (int i = 0; i < betas.length; i++) {
//            double beta = betas[i];
////            LinearizedFunction1d presenceFunctionAtPessimisticLevel = FuzzyDemandUtilities.getPresenceFunctionAtPessimisticLevelWithLambda(o1,start, new FuzzyWorkloadParameters(start, beta));
//            o1.setBeta(beta);
//            LinearizedFunction1d presenceFunctionAtPessimisticLevel = FuzzyDemandUtilities.getPresenceFunctionAtPessimisticLevel(o1,start);
//            
//            for (int j = 0; j < ts.length; j++) {
//                double t = ts[j];
//                double value = presenceFunctionAtPessimisticLevel.getValue(t);
//                double supportDuration = duration.getC2()-duration.getC1();
//                double scale = duration.getC1() + supportDuration * beta;
//                double membership = duration.getMembership(scale);
//                auslastungen[i][j] = value   ;
//            }
//        }
//
//        MatFileWriter fw = new MatFileWriter();
//
//        MLDouble data = new MLDouble("Data", auslastungen);
//        MLDouble tdata = new MLDouble("t", ts, ts.length);
//        MLDouble betadata = new MLDouble("beta", betas, betas.length);
//
//        MLStructure DatArray = new MLStructure("UnScaled", new int[]{1, 1});
//        DatArray.setField("t", tdata);
//        DatArray.setField("beta", betadata);
//        DatArray.setField("Auslastung", data);
//
//        Collection<MLArray> datas = new ArrayList<>();
//        datas.add(DatArray);
//
//        try {
////            File f = new File("D:\\Eigene Dateien\\Desktop\\auslastungUnScaled.mat");
//            File f = new File("/home/bode/Arbeitsfläche/auslastungUnScaled.mat");
//            fw.write(f, datas);
//        } catch (IOException ex) {
//            Logger.getLogger(Export.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.out.println("Rausgeschrieben");
    }

}
