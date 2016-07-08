/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.GA;

import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import java.io.File;
import java.util.Arrays;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class ImplicitWriter implements GAListener<ImplicitSuperIndividual> {

    File folder;

    public ImplicitWriter(File folder) {
        this.folder = folder;
    }

    @Override
    public void nextGeneration(GAEvent<ImplicitSuperIndividual> event) {

    }

    @Override
    public void finished(GAEvent<ImplicitSuperIndividual> event) {
        File subFolder = new File(folder, "Optimization");
        subFolder.mkdir();

        int i = 0;
        for (ImplicitSuperIndividual e : event.population.getIndividualsSortedList()) {
            JSONSerialisierung.exportJSON(new File(subFolder, i + "indOper_" + Arrays.toString(e.getFitnessVector()).replace(".", " _") + ".txt"), e.getOperationIndividual().getChromosome(), true);
            JSONSerialisierung.exportJSON(new File(subFolder, i + "indMode_" + Arrays.toString(e.getFitnessVector()).replace(".", " _") + ".txt"), e.getModeIndividual().getChromosome(), true);
            i++;
        }

    }

    

}
