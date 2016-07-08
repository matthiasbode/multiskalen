/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.GA;

import applications.transshipment.multiscale.model.Scale;
import ga.individuals.Individual;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bode
 */
public class MultiScaleFitnessWriter<I extends Individual> implements GAListener<I> {

    Scale currentScale = Scale.macro;
    BufferedWriter bw;
    FileWriter fw;

    public MultiScaleFitnessWriter(File folder) {
        File f = new File(folder, "Verlauf.txt");
        try {
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
        } catch (IOException ex) {
            Logger.getLogger(MultiScaleFitnessWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void nextGeneration(GAEvent<I> event) {
        double[] fitness = event.population.getFittestIndividual().getFitnessVector();
        for (double d : fitness) {
            try {
                bw.write(Double.toString(d));
                bw.write(",");
            } catch (IOException ex) {
                Logger.getLogger(MultiScaleFitnessWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(MultiScaleFitnessWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        event = null;

    }

    @Override
    public void finished(GAEvent<I> event) {
        if (currentScale.equals(Scale.macro)) {
            try {
                bw.write("micro");
                bw.newLine();
                currentScale = Scale.micro;
            } catch (IOException ex) {
                Logger.getLogger(MultiScaleFitnessWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (currentScale.equals(Scale.micro)) {
            try {
                bw.write("macro");
                bw.newLine();
                currentScale = Scale.macro;
            } catch (IOException ex) {
                Logger.getLogger(MultiScaleFitnessWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void close() {
        try {
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(MultiScaleFitnessWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
