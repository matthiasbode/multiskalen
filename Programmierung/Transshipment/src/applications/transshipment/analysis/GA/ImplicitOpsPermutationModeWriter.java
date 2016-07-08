/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.GA;

import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeImplicitOpsSuperIndividual;
import applications.transshipment.generator.json.JobExport;
import applications.transshipment.model.LoadUnitJob;
import ga.individuals.subList.SubListIndividual;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class ImplicitOpsPermutationModeWriter implements GAListener<PermutationModeImplicitOpsSuperIndividual> {

    File folder;

    public ImplicitOpsPermutationModeWriter(File folder) {
        this.folder = folder;
    }

    @Override
    public void nextGeneration(GAEvent<PermutationModeImplicitOpsSuperIndividual> event) {

    }

    @Override
    public void finished(GAEvent<PermutationModeImplicitOpsSuperIndividual> event) {
        File subFolder = new File(folder, "Optimization");
        subFolder.mkdir();

        int i = 0;
        for (PermutationModeImplicitOpsSuperIndividual e : event.population.getIndividualsSortedList()) {
            JSONSerialisierung.exportJSON(new File(subFolder, i + "indOp_" + e.getFitness().toString().replace(".", " _") + ".txt"), e.getOperationIndividual().getChromosome(), true);

            List<List<JobExport>> exportMode = new ArrayList<List<JobExport>>();
            for (SubListIndividual<LoadUnitJob> subListIndividual : e.getModeIndividual().getChromosome()) {
                List<JobExport> list = new ArrayList<>();
                for (LoadUnitJob loadUnitJob : subListIndividual.getChromosome()) {
                    JobExport jobExport = new JobExport(loadUnitJob);
                    list.add(jobExport);
                }

                exportMode.add(list);
            }

            JSONSerialisierung.exportJSON(new File(subFolder, i + "indMode_" + e.getFitness().toString().replace(".", " _") + ".txt"), exportMode, true);
        }
        i++;
    }

   
}
