/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeDirectJob;

import applications.transshipment.model.LoadUnitJob;
import ga.Parameters;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author bode
 */
public class PermutationJobStartPopulationGenerator implements StartPopulationGenerator<PermutationJobIndividual> {

    Collection<LoadUnitJob> jobs;

    public PermutationJobStartPopulationGenerator(Collection<LoadUnitJob> jobs) {
        this.jobs = jobs;
    }

    /**
     * Erzeugt eine Startpopulation indem zufällig für jede
     * Zusammenhangskomponente eine Strategie gewählt wird.
     *
     * @param anzahl
     * @return
     */
    @Override
    public Population<PermutationJobIndividual> generatePopulation(int anzahl, Object... additionalObjects) {
        ArrayList<PermutationJobIndividual> res = new ArrayList<>();

        for (int i = 0; i < anzahl; i++) {
            ArrayList<LoadUnitJob> jobForInd = new ArrayList<>();
            Collections.shuffle(jobForInd, Parameters.getRandom());
            PermutationJobIndividual ind = new PermutationJobIndividual(jobForInd);
            res.add(ind);
        }
        Population<PermutationJobIndividual> pop = new Population<>(PermutationJobIndividual.class, res);

        return pop;
    }

}
