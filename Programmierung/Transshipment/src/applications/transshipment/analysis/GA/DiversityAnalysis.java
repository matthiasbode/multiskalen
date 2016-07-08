/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.GA;

import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import ga.basics.Population;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import ga.mutation.Mutation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class DiversityAnalysis implements GAListener<ImplicitSuperIndividual> {

    File folder;

    public DiversityAnalysis(File folder) {
        this.folder = folder;
    }

    @Override
    public void nextGeneration(GAEvent<ImplicitSuperIndividual> event) {
        File subFolder = new File(folder, "Optimization");
        subFolder.mkdirs();

        Population<ImplicitSuperIndividual> population = event.population;
        String popTitle = "Population_" + population.numberOfGenerations;

        StringBuilder bf = new StringBuilder();
        bf.append(popTitle).append("\n");
        bf.append("##########################################\n");

        ArrayList<EnumMap<OperationPriorityRules.Identifier, Integer>> rates = new ArrayList<>();
        int size = population.getFittestIndividual().getOperationIndividual().size();

        for (int i = 0; i < size; i++) {
            EnumMap<OperationPriorityRules.Identifier, Integer> rate = new EnumMap<>(OperationPriorityRules.Identifier.class);
            for (ImplicitSuperIndividual s : population.getIndividualsSortedList()) {
                OperationPriorityRules.Identifier rule = s.getOperationIndividual().get(i);
                Integer anzahl = rate.get(rule);
                if (anzahl == null) {
                    rate.put(rule, 1);
                } else {
                    rate.put(rule, anzahl + 1);
                }
            }
            rates.add(rate);
        }

        OperationPriorityRules.Identifier[] best = new OperationPriorityRules.Identifier[size];

        for (int i = 0; i < rates.size(); i++) {
            EnumMap<OperationPriorityRules.Identifier, Integer> rate = rates.get(i);
            bf.append("Zeitintervall: ").append(i).append("\n");

            for (OperationPriorityRules.Identifier k : rate.keySet()) {
                bf.append(k).append(":\t").append(rate.get(k)).append("\n");
                if (best[i] == null || rate.get(best[i]) < rate.get(k)) {
                    best[i] = k;
                }
            }

            bf.append("-------------------------------------").append("\n");
        }

        bf.append("#######################################").append("\n");
        bf.append("#######################################").append("\n");
        bf.append("#######################################").append("\n");

        for (ImplicitSuperIndividual s : population.getIndividualsSortedList()) {
            bf.append(s).append("\n");
            ImplicitOperationIndividual operationIndividual = s.getOperationIndividual();
            bf.append("Operation mutiert: "+ operationIndividual.additionalObjects.get(Mutation.mutated)).append("\n");
            ImplicitModeIndividual modeIndividual = s.getModeIndividual();
            bf.append("Mode mutiert: "+ modeIndividual.additionalObjects.get(Mutation.mutated)).append("\n");
            int numberOfDiffs = 0;
            for (int i = 0; i < s.getOperationIndividual().getChromosome().size(); i++) {
                OperationPriorityRules.Identifier currentRule = s.getOperationIndividual().getChromosome().get(i);
                if (currentRule != best[i]) {
                    numberOfDiffs++;
                }
            }
            bf.append("Anzahl Abweichungen zur meistgewählten Rule: \t").append(numberOfDiffs).append("\n");
            bf.append("------------------------------------\n");
        }
        File file = new File(subFolder, popTitle+".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(bf.toString());
        } catch (IOException ex) {
            Logger.getLogger(DiversityAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void finished(GAEvent<ImplicitSuperIndividual> event) {

    }

     

}
